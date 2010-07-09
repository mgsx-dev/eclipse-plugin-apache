package com.mgs.plugin.apache.wizard.data;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class ResourceSerializer {
	
	public static final String QualifiedName = "com.mgs.plugin.apache";

	private IResource resource;

	public ResourceSerializer(IResource resource) {
		this.resource = resource;
	}
	
	public void clear(boolean recursive) throws CoreException{
		for(Object o : resource.getPersistentProperties().keySet()){
			QualifiedName key = (QualifiedName)o;
			if(key.getQualifier().equals(QualifiedName)){
				resource.setPersistentProperty(key, null);
			}
		}
		if(resource instanceof IContainer && recursive){
			for(IResource r : ((IContainer)resource).members()){
				new ResourceSerializer(r).clear(recursive);
			}
		}
	}
	
	public void save(String name, String value) throws CoreException{
		resource.setPersistentProperty(new QualifiedName(QualifiedName, name), value);
	}
	
	public void save(String name, boolean value) throws CoreException{
		save(name, value ? "true" : "false");
	}
	
	public String load(String name, String defaultValue) throws CoreException{
		String s = resource.getPersistentProperty(new QualifiedName(QualifiedName, name));
		return s == null ? defaultValue : s;
	}
	
	public boolean load(String name, boolean defaultValue) throws CoreException{
		String s = load(name, (String)null);
		return s == null ? defaultValue : s.equals("true");
	}
	
}
