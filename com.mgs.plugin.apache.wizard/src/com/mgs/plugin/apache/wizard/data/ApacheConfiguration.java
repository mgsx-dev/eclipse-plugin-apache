package com.mgs.plugin.apache.wizard.data;

import com.mgs.plugin.apache.wizard.Activator;
import com.mgs.plugin.apache.wizard.external.AdminFactory;

public class ApacheConfiguration {

	private static final String KEY_APACHE_DIRECTORY = "apache-directory";
	
	private String installDirectory;
	
	public String getInstallDirectory() {
		return installDirectory;
	}
	
	public void setInstallDirectory(String installDirectory) {
		this.installDirectory = installDirectory;
	}
	
	public void load(){
		Activator.getDefault().getPreferenceStore().setDefault(KEY_APACHE_DIRECTORY, AdminFactory.createApacheAdmin().getDefaultInstallDirectory());
		installDirectory = Activator.getDefault().getPreferenceStore().getString(KEY_APACHE_DIRECTORY);
	}
	
	public void save(){
		Activator.getDefault().getPreferenceStore().setValue(KEY_APACHE_DIRECTORY, installDirectory);
	}
}
