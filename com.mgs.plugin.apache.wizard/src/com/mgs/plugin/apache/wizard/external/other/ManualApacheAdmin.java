package com.mgs.plugin.apache.wizard.external.other;

import java.io.ByteArrayInputStream;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mgs.plugin.apache.wizard.external.ApacheAdmin;
import com.mgs.plugin.apache.wizard.external.StdOutputHandler;


public class ManualApacheAdmin implements ApacheAdmin {

	private StdOutputHandler handler;
	
	@Override
	public String getDefaultInstallDirectory() {
		return "";
	}
	
	@Override
	public void createConfiguration(String configurationName,
			String serverName, String configurationContent, IProgressMonitor monitor) {
		if(handler != null){
			StringBuffer sb = new StringBuffer();
			sb.append("Apache auto configuration is not yet implemented for your OS.\n");
			sb.append("You need to update files manually :\n");
			sb.append("- Edit your hosts file.\n");
			sb.append("- Edit your apache virtual hosts file.\n");
			sb.append("- Restart your apache server.");
			ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes());
			handler.readStdOutputs(null, stream);
		}
	}

	@Override
	public boolean adminPasswordRequired() {
		return false;
	}

	@Override
	public boolean isSupported() {
		return false;
	}

	@Override
	public void setAdminPassword(String password) {
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean isError() {
		return true;
	}

	@Override
	public void setStdOutputHandler(StdOutputHandler handler) {
		this.handler = handler;
	}

}
