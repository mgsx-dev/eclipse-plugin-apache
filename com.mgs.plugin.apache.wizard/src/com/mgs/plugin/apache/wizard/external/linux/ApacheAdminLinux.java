package com.mgs.plugin.apache.wizard.external.linux;

import java.io.File;
import java.io.IOException;

import com.mgs.plugin.apache.wizard.external.ApacheAdmin;
import com.mgs.plugin.apache.wizard.external.StdOutputHandler;
import com.mgs.plugin.apache.wizard.external.common.ApacheAdminBase;
import com.mgs.plugin.apache.wizard.external.common.HostsFileEditor;


public class ApacheAdminLinux extends ApacheAdminBase implements ApacheAdmin {
	
	private SystemAdminLinux system;
	
	public ApacheAdminLinux() {
		super();
		system = new SystemAdminLinux();
	}

	@Override
	public String getDefaultInstallDirectory() {
		return "/etc/apache2";
	}
	
	protected void updateHostsFile(String serverName) throws IOException{
		
		File hostsFile = new File("/etc/hosts");
		
		String content = system.readFile(hostsFile);
		
		HostsFileEditor editor = new HostsFileEditor();
		editor.setContents(content);
		editor.addMapping("127.0.0.1", serverName);
		
		if(editor.isModified()){
			system.writeFile(hostsFile, content, true);
		}
	}
	
	protected void createConfigurationFile(String configurationName, String configurationContent) throws IOException{
		
		system.writeFile(new File(getApacheInstallDirectory() + "/sites-available/" + configurationName), configurationContent, true);
		
		File link = new File(getApacheInstallDirectory() + "/sites-enabled/" + configurationName);
		
		if(link.exists()){
			system.removeSymbolicLink(link, true);
		}
		system.createSymbolicLink(link, "../sites-available/" + configurationName, true);
		
	}
	
	protected void restartApache() throws IOException{
		
		String apacheService = "/etc/init.d/apache2";
		if(!new File(apacheService).exists()){
			apacheService = "/etc/init.d/httpd";
			if(!new File(apacheService).exists()){
				throw new IOException("apache service not found");
			}
		}
		
		system.command(apacheService + " restart", true);
		
	}

	public void setStdOutputHandler(StdOutputHandler handler) {
		system.setStdOutputHandler(handler);
	}
	
	@Override
	public boolean adminPasswordRequired() {
		return system.getRootPassword() == null;
	}

	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public void setAdminPassword(String password) {
		system.setRootPassword(password);
	}

	@Override
	protected void prerequists() throws IOException {
		try {
			system.sudoTest();
		} catch (IOException e1) {
			system.setRootPassword(null);
			throw e1;
		}
	}

}
