package com.mgs.plugin.apache.wizard.external;

import com.mgs.plugin.apache.wizard.external.linux.ApacheAdminLinux;
import com.mgs.plugin.apache.wizard.external.other.ManualApacheAdmin;
import com.mgs.plugin.apache.wizard.external.windows.ApacheAdminWindows;



abstract public class AdminFactory {
	
	private AdminFactory() {
	}
	
	public static ApacheAdmin createApacheAdmin(){
		String osName = System.getProperty("os.name");
		if(osName.equals("Linux")){
			return new ApacheAdminLinux();
		}else if(osName.startsWith("Windows")){
			return new ApacheAdminWindows();
		}else{
			return new ManualApacheAdmin();
		}
	}
}
