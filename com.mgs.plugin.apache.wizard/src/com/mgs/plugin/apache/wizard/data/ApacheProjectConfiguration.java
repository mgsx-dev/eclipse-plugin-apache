package com.mgs.plugin.apache.wizard.data;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class ApacheProjectConfiguration {

	private IProject project;
	
	private String serverName;
	
	private boolean logs;

	private ApacheDirectoryConfiguration rootDocument;
	
	public ApacheProjectConfiguration() {
		rootDocument = new ApacheDirectoryConfiguration();
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
	
	public boolean isLogs() {
		return logs;
	}

	public void setLogs(boolean logs) {
		this.logs = logs;
	}

	public ApacheDirectoryConfiguration getRootDocument() {
		return rootDocument;
	}
	
	public void save() throws CoreException{
		
		// clear previous informations if any.
		new ResourceSerializer(project).clear(true);
		
		// tag container as rootDocument
		new ResourceSerializer(rootDocument.getContainer()).save("rootDocument", "rootDocument");
		
		// save rootDocument data.
		rootDocument.save();
		
		// Save project settings.
		ResourceSerializer rs = new ResourceSerializer(project);
		rs.save("ServerName", serverName);
		rs.save("logs", logs);
		
	}
	
	public void load() throws CoreException{
		ResourceSerializer rs = new ResourceSerializer(project);
		// default serverName is the project name.
		serverName = rs.load("ServerName", project.getName());
		logs = rs.load("logs", true);
		
		// default rootDocument is the project root folder.
		IContainer c = findRootDocumentRecursively(project);
		if(c == null || false){
			rootDocument.setContainer(project);
		}else{
			rootDocument.setContainer(c);
		}
		// load rootDocument data.
		rootDocument.load();
	}
	
	private static IContainer findRootDocumentRecursively(IContainer container) throws CoreException{
		if(new ResourceSerializer(container).load("rootDocument", null) != null){
			return container;
		}
		for(IResource resource : container.members()){
			if(resource instanceof IContainer){
				IContainer containerFound = findRootDocumentRecursively((IContainer) resource);
				if(containerFound != null){
					return containerFound;
				}
			}
		}
		return null;
	}
	
}
