package com.mgs.plugin.apache.wizard.external.windows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mgs.plugin.apache.wizard.external.ApacheAdmin;
import com.mgs.plugin.apache.wizard.external.StdOutputHandler;
import com.mgs.plugin.apache.wizard.external.common.ApacheAdminBase;
import com.mgs.plugin.apache.wizard.external.common.HostsFileEditor;

// TODO 1 tell user to run apache as service (easy php option or apache.exe -k install)
// TODO 1 tell user to enable named virtual hosts ("NameVirtualHost *:80" directive)

public class ApacheAdminWindows extends ApacheAdminBase implements ApacheAdmin {

	private SystemAdminWindows system;
	
	public ApacheAdminWindows() {
		super();
		system = new SystemAdminWindows();
	}
	
	@Override
	public String getDefaultInstallDirectory() {
		return "C:\\apache2";
	}
	
	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public boolean adminPasswordRequired() {
		return false;
	}

	@Override
	public void setAdminPassword(String password) {
	}

	@Override
	public void setStdOutputHandler(StdOutputHandler handler) {
		system.setStdOutputHandler(handler);
	}

	@Override
	protected void createConfigurationFile(String configurationName,
			String configurationContent) throws IOException {
		
		// assert folder eclipse.
		File folder = new File(getApacheInstallDirectory() + "\\conf\\eclipse");
		if(!folder.exists()){
			folder.mkdir();
		}
		
		// create the configuration file.
		File confFile = new File(getApacheInstallDirectory() + "\\conf\\eclipse\\" + configurationName);
		system.writeFile(confFile, configurationContent);
		
		// assert the eclipse folder inclusion in the main configuration file.
		File mainConfFile = new File(getApacheInstallDirectory() + "\\conf\\httpd.conf");
		String contents = system.readFile(mainConfFile);
		String directive = "Include conf/eclipse/";
		if(!contents.contains(directive)){
			contents += "\n" + directive;
			system.writeFile(mainConfFile, contents);
		}
	}

	@Override
	protected void prerequists() throws IOException {
		// no prerequists.
	}

	@Override
	protected void restartApache() throws IOException {
		// find the Apache executable (apache.exe or httpd.exe).
		String exe = getApacheInstallDirectory() + "\\bin\\apache.exe";
		if(!new File(exe).exists()){
			exe = getApacheInstallDirectory() + "\\bin\\httpd.exe";
			if(!new File(exe).exists()){
				throw new IOException("Apache program not found");
			}
		}
		
		String cmdCheck = "\"" + exe + "\" -t";
		system.command(cmdCheck);
		String cmd = "\"" + exe + "\" -k restart";
		// TODO 1 voir pourquoi apache bloque s'il n'y a pas d'erreur (avec easyphp).
		system.command(cmd);
	}

	@Override
	protected void updateHostsFile(String serverName) throws IOException {
		File hostsFile = new File("C:\\WINDOWS\\system32\\drivers\\etc\\hosts");
		
		String content = system.readFile(hostsFile);
		
		HostsFileEditor editor = new HostsFileEditor();
		editor.setContents(content);
		editor.addMapping("127.0.0.1", serverName);
		content = editor.getContents();
		
		if(editor.isModified()){
			try{
				system.writeFile(hostsFile, content);
			}catch(FileNotFoundException e){
				// restrictions.
				// TODO send a user message : you have to modify the hosts file manually.
				throw e;
			}
		}
		
	}

}
