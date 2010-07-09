package com.mgs.plugin.apache.wizard.external.common;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mgs.plugin.apache.wizard.data.ApacheConfiguration;
import com.mgs.plugin.apache.wizard.external.ApacheAdmin;

public abstract class ApacheAdminBase implements ApacheAdmin {

	private boolean status;

	public ApacheAdminBase() {
		clear();
	}
	
	public void clear() {
		status = true;
	}
	
	public boolean isError() {
		return !status;
	}
	
	protected String getApacheInstallDirectory(){
		ApacheConfiguration conf = new ApacheConfiguration();
		conf.load();
		return conf.getInstallDirectory();
	}

	public void createConfiguration(String configurationName, String serverName, String configurationContent, IProgressMonitor monitor) {
		
		try{
			monitor.beginTask("Applying Apache Configuration", 3);
			
			prerequists();
			
			monitor.subTask("Updating Hosts File");
			updateHostsFile(serverName);
			monitor.worked(1);
			
			monitor.subTask("Create Configuration File");
			createConfigurationFile(configurationName, configurationContent);
			monitor.worked(1);
			
			monitor.subTask("Restarting Apache Server");
			restartApache();
			monitor.worked(1);
		}catch(IOException e){
			status = false;
			e.printStackTrace();
		}
	}
	
	abstract protected void prerequists() throws IOException;
	abstract protected void updateHostsFile(String serverName) throws IOException;
	abstract protected void createConfigurationFile(String configurationName, String configurationContent) throws IOException;
	abstract protected void restartApache() throws IOException;
}
