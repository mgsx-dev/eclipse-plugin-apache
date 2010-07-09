package com.mgs.plugin.apache.wizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenApacheConfigurationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IProject project = null;
		
		ISelection selection = HandlerUtil.getActivePart(event).getSite().getSelectionProvider().getSelection();
		if(selection instanceof IStructuredSelection){
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if(element instanceof IResource){
				project = ((IResource) element).getProject();
			}else if(element instanceof IAdaptable){
				IAdaptable adaptable = (IAdaptable)element;
			    Object adapter = adaptable.getAdapter(IResource.class);
			    IResource r = (IResource)adapter;
			    if(r != null){
			    	project = r.getProject();
			    }
			}
		}
		
		Shell window = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		if(project == null){
			new MessageDialog(window, "Apache Configuration", null, "No project selected", MessageDialog.WARNING, new String[]{"Ok"}, 0).open();
		}else{
			ApacheProjectConfigurationWindow confWindow = new ApacheProjectConfigurationWindow(window, project);
			
			confWindow.setVisible(true);
		}
		
		return null;
	}

}
