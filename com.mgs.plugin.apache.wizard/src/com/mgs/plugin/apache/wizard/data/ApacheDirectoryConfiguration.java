package com.mgs.plugin.apache.wizard.data;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;

public class ApacheDirectoryConfiguration {
	
	private IContainer container;
	
	private boolean localOnly;
	
	private boolean indexes;
	
	private boolean symbolicLinks;
	
	public ApacheDirectoryConfiguration() {
	}

	public IContainer getContainer() {
		return container;
	}

	public void setContainer(IContainer container) {
		this.container = container;
	}
	
	public boolean isLocalOnly() {
		return localOnly;
	}

	public void setLocalOnly(boolean localOnly) {
		this.localOnly = localOnly;
	}

	public boolean isIndexes() {
		return indexes;
	}

	public void setIndexes(boolean indexes) {
		this.indexes = indexes;
	}

	public boolean isSymbolicLinks() {
		return symbolicLinks;
	}

	public void setSymbolicLinks(boolean symbolicLinks) {
		this.symbolicLinks = symbolicLinks;
	}

	public void save() throws CoreException{
		ResourceSerializer rs = new ResourceSerializer(container);
		rs.save("localOnly", localOnly);
		rs.save("indexes", indexes);
		rs.save("symbolicLinks", symbolicLinks);
	}
	
	public void load() throws CoreException{
		ResourceSerializer rs = new ResourceSerializer(container);
		localOnly = rs.load("localOnly", true);
		indexes = rs.load("indexes", true);
		symbolicLinks = rs.load("symbolicLinks", true);
	}
}
