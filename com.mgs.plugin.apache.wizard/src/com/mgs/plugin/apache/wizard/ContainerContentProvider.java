package com.mgs.plugin.apache.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ContainerContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	
	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
	
	@Override
	public Object getParent(Object element) {
		if(element instanceof IProject){
			return null;
		}
		if(element instanceof IFolder){
			return ((IFolder) element).getParent();
		}
		return null;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IContainer){
			try {
				List<IResource> folders = new ArrayList<IResource>();
				IResource [] res = ((IContainer) parentElement).members();
				for(IResource r : res){
					if(r.getType() == IContainer.FOLDER){
						folders.add(r);
					}
				}
				return folders.toArray();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return new Object[]{};
	}

}
