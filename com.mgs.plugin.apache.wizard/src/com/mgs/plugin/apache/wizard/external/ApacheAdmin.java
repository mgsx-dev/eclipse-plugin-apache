package com.mgs.plugin.apache.wizard.external;

import org.eclipse.core.runtime.IProgressMonitor;

public interface ApacheAdmin {

	/**
	 * Indique si l'administration apache est supportée par l'implémentation.
	 * @return
	 */
	public boolean isSupported();
	
	public boolean adminPasswordRequired();
	
	public void setAdminPassword(String password);
	
	public void setStdOutputHandler(StdOutputHandler handler);

	public void clear();
	
	public boolean isError();
	
	public String getDefaultInstallDirectory();

	public void createConfiguration(String configurationName, String serverName, String configurationContent, IProgressMonitor monitor);
	
}
