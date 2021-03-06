package com.mgs.plugin.apache.wizard.external;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.mgs.plugin.apache.wizard.data.ApacheDirectoryConfiguration;
import com.mgs.plugin.apache.wizard.data.ApacheProjectConfiguration;

public class ApacheVirtualHostGenerator {
	
	public String generate(ApacheProjectConfiguration configuration){
		String projectPath = configuration.getProject().getLocation().toPortableString();
		
		String documentRoot = configuration.getRootDocument().getContainer().getLocation().toPortableString();
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(stream);
		
		out.println("# Generated by eclipse");
		out.println("#");
		out.println("<VirtualHost *:80>");
		out.println();
		out.println("    ServerName " + configuration.getServerName());
		out.println();
		out.println("    DocumentRoot " + apacheUri(documentRoot));
		out.println();
		generate(out, configuration.getRootDocument());
		out.println();
		out.println("    LogLevel warn");
		out.println();
		if(configuration.isLogs()){
			out.println("    CustomLog " + apacheUri(projectPath + "/access.log") + " combined");
			out.println();
			out.println("    ErrorLog " + apacheUri(projectPath + "/error.log"));
			out.println();
		}else{
			out.println("    CustomLog /dev/null combined");
			out.println();
			out.println("    ErrorLog /dev/null");
			out.println();
		}
		out.println("</VirtualHost>");
		
		out.flush();
		return stream.toString();
	}
	
	private void generate(PrintStream out, ApacheDirectoryConfiguration configuration){
		
		// begin
		out.println("    <Directory " + apacheUri(configuration.getContainer().getLocation().toPortableString()) + ">");
		
		// Options.
		out.print("        Options MultiViews");
		if(configuration.isIndexes()){
			out.print(" Indexes");
		}
		if(configuration.isSymbolicLinks()){
			out.print(" FollowSymLinks");
		}
		out.println();
		
		// override.
		out.println("        AllowOverride None");
		
		// restrictions.
		out.println("        Order deny,allow");
		if(configuration.isLocalOnly()){
			out.println("        Deny from all");
			out.println("        Allow from 127.0.0.0/255.0.0.0"); // Allow from 127.0.0.0/255.0.0.0 ::1/128
		}else{
			out.println("        Allow from all");
		}
		
		// end.
		out.println("    </Directory>");
	}
	
	private String apacheUri(String uri){
		if(uri.indexOf(" ") >= 0){
			return "\"" + uri + "\"";
		}
		return uri;
	}

}
