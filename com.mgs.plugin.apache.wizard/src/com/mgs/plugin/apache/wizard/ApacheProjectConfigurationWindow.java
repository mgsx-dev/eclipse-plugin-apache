package com.mgs.plugin.apache.wizard;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.console.ConsolePlugin;

import com.mgs.plugin.apache.wizard.data.ApacheConfiguration;
import com.mgs.plugin.apache.wizard.data.ApacheProjectConfiguration;
import com.mgs.plugin.apache.wizard.external.AdminFactory;
import com.mgs.plugin.apache.wizard.external.ApacheAdmin;
import com.mgs.plugin.apache.wizard.external.ApacheVirtualHostGenerator;
import com.mgs.plugin.apache.wizard.ui.BridgeProcessToConsole;
import com.mgs.plugin.apache.wizard.ui.ConsoleUtils;

// TODO 1 report bugs on the project site.
// TODO 1 report explanations on the project site.
// TODO 1 publish version 2 with comment about new features.
// TODO 1 faire un chmod ou chown ou chgrp récursif si besoin sur le dossier
// TODO 2 faire un update site pour le feature lorsqu'il y aura une version stable.
// TODO 3 utiliser des listeners sur les données pour une bonne actualisation sans effet de bords.
// TODO 3 faire une fenetre de suivi d'avancement des manips (logs des appels system)
// TODO 3 gérer windows ...
// TODO 3 gérer un meilleur rendu graphique.
// TODO 2 donner la possibilité d'overrider la conf par défaut (répertoire apache, ...Etc)
// TODO 3 ajouter d'autres options (allow override, ...etc).

public class ApacheProjectConfigurationWindow extends ShellAdapter {
	
	private Shell window;
	
	private Text txServerName;
	private Button btSymbolicLinks;
	private Button btLocalOnly;
	private Button btLogs;
	private Button btIndexes;

	
	private Link link;
	
	private IProject project;
	
	private ExlusiveChecker checker;
	
	private ApacheProjectConfiguration configuration;
	
	private ApacheConfiguration globalConfiguration;
	
	private ApacheAdmin admin;
	
	private BridgeProcessToConsole bridge;
	
	public ApacheProjectConfigurationWindow(Shell parentWindow, IProject project) {
		
		this.project = project;
		
		ConsolePlugin.getDefault();
		this.bridge = new BridgeProcessToConsole(ConsoleUtils.getConsole("com.mgs.plugin.apache"));
		
		this.admin = AdminFactory.createApacheAdmin();
		this.admin.setStdOutputHandler(bridge);
		
		this.configuration = new ApacheProjectConfiguration();
		this.configuration.setProject(project);
		
		try {
			configuration.load();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		globalConfiguration = new ApacheConfiguration();
		globalConfiguration.load();
		
		window = new Shell(parentWindow, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.BORDER);
		window.addShellListener(this);
		window.setText("Apache Configuration for Project " + project.getName());
		
		createConfigurationPanel(window);
	}
	
	@Override
	public void shellClosed(ShellEvent e) {
		try {
			configuration.save();
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}
	
	public void setVisible(boolean state){
		window.setVisible(state);
	}
	
	private Button createButtonField(Composite parent, String name, int style, String info){
		Button bt = new Button(parent, style);
		bt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		bt.setText(name);
		bt.setToolTipText(info);
		return bt;
	}
	private Label lbApacheDirectory;
	private void createConfigurationPanel(Composite parent){
		
		GridLayout layout = new GridLayout(1, true);

		parent.setLayout(layout);
		
		Group cpGlobal = new Group(parent, SWT.BORDER);
		cpGlobal.setText("Apache Install Directory");
		cpGlobal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cpGlobal.setLayout(new GridLayout(2, false));

		lbApacheDirectory = new Label(cpGlobal, SWT.BORDER);
		lbApacheDirectory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Button btChooseDir = new Button(cpGlobal, SWT.PUSH);
		btChooseDir.setText("...");
		btChooseDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(window);
			    dialog.setFilterPath(globalConfiguration.getInstallDirectory());
			    dialog.setMessage("Select the apache installation directory");
			    String newDirectory = dialog.open();
			    globalConfiguration.setInstallDirectory(newDirectory);
			    globalConfiguration.save();
			    validate();
			}
		});
		
		Group cpMain = new Group(parent, SWT.BORDER);
		cpMain.setText("Main Configuration");
		cpMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cpMain.setLayout(new GridLayout(2, true));
		
		Label lbServerName = new Label(cpMain, SWT.NONE);
		lbServerName.setText("ServerName");
		lbServerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		txServerName = new Text(cpMain, SWT.NONE);
		txServerName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txServerName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(!configuration.getServerName().equals(txServerName.getText())){
					configuration.setServerName(txServerName.getText());
					validate();
				}
			}
		});
		
		btSymbolicLinks = createButtonField(cpMain, "FollowSymbolicLinks", SWT.CHECK, "Allow apache to follow symbolic links.");
		btSymbolicLinks.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configuration.getRootDocument().setSymbolicLinks(btSymbolicLinks.getSelection());
			}
		});
		
		btIndexes = createButtonField(cpMain, "Indexes", SWT.CHECK, "Allow browsing directory if no index is present.");
		btIndexes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configuration.getRootDocument().setIndexes(btIndexes.getSelection());
			}
		});
		
		btLocalOnly = createButtonField(cpMain, "Local Only", SWT.CHECK, "Prevent non local access.");
		btLocalOnly.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configuration.getRootDocument().setLocalOnly(btLocalOnly.getSelection());
			}
		});
		
		btLogs = createButtonField(cpMain, "Logs Enabled", SWT.CHECK, "Enable apache logs. (inside the project)");
		btLogs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configuration.setLogs(btLogs.getSelection());
			}
		});
		
		Group cpRoot = new Group(parent, SWT.BORDER);
		cpRoot.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cpRoot.setLayout(new FillLayout());
		cpRoot.setText("Document Root Selection");

		CheckboxTreeViewer treeViewer = new CheckboxTreeViewer(cpRoot, SWT.TOGGLE);
		treeViewer.setContentProvider(new ContainerContentProvider());
		treeViewer.setLabelProvider(new ContainerLabelProvider());
		treeViewer.setInput(project);
		
		checker = new ExlusiveChecker(treeViewer, configuration.getRootDocument().getContainer());
		
		treeViewer.addCheckStateListener(checker);
		treeViewer.setCheckStateProvider(checker);
		treeViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				IContainer newContainer = (IContainer)checker.getCurrentValue();
				if(newContainer == null){
					newContainer = project;
				}
				configuration.getRootDocument().setContainer(newContainer);
				validate();
			}
		});
		
		Group cpPreview = new Group(parent, SWT.BORDER);
		cpPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cpPreview.setLayout(new GridLayout(1, true));
		cpPreview.setText("Preview");
		
		link = new Link(cpPreview, SWT.BORDER);
		link.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://" + configuration.getServerName() + "/"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});

		Button btGen = new Button(parent, SWT.PUSH);
		btGen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btGen.setText("Preview");
		btGen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new MessageDialog(window, "Preview", null, generateConfiguration(), MessageDialog.INFORMATION, new String[]{"OK"}, 0).open();
			}
		});
		
		Button btApply = new Button(parent, SWT.PUSH);
		btApply.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btApply.setText("Apply Configuration");
		btApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				applyConfiguration();
			}
		});
		
		window.pack();
		window.setSize(400, 500);
		
		validate();
	}
	
	private void validate(){
		if(!txServerName.getText().equals(configuration.getServerName())){
			txServerName.setText(configuration.getServerName());
		}
		link.setText("<A>http://" + configuration.getServerName() + "/</A>");
		btIndexes.setSelection(configuration.getRootDocument().isIndexes());
		btLocalOnly.setSelection(configuration.getRootDocument().isLocalOnly());
		btSymbolicLinks.setSelection(configuration.getRootDocument().isSymbolicLinks());
		btLogs.setSelection(configuration.isLogs());
		lbApacheDirectory.setText(globalConfiguration.getInstallDirectory());
	}
	
	private void applyConfiguration(){
		
		bridge.getConsole().clearConsole();
		
		// on demande le mot de passe root si besoin.
		if(admin.adminPasswordRequired()){
			// TODO 3 cacher la saisie. (text.setEchoChar ('*'); or  | SWT.PASSWORD)
			InputDialog login = new InputDialog(window, "Admin Password", "Enter system admin password", "", null);
			if(login.open() == Dialog.OK){
				admin.setAdminPassword(login.getValue());
			}
		}
		
		// disable IHM
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(window);

		window.setEnabled(false);
		try {
			progressDialog.run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					admin.createConfiguration("eclipse-" + project.getName(), configuration.getServerName(), generateConfiguration(), monitor);
				}
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			window.setEnabled(true);
			if(admin.isError()){
				new MessageDialog(window, "Error", null, "Error occuring during configuration application.", MessageDialog.ERROR, new String[]{"OK"}, 0).open();
			}
			admin.clear();
		}
	}
	
	private String generateConfiguration(){
		ApacheVirtualHostGenerator generator = new ApacheVirtualHostGenerator();
		return generator.generate(configuration);
	}
	
}
