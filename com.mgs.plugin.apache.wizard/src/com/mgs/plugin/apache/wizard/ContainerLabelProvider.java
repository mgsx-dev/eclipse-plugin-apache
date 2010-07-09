package com.mgs.plugin.apache.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ContainerLabelProvider implements ILabelProvider {

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
	
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {
	}
	
	@Override
	public String getText(Object element) {
		if(element instanceof IResource){
			return ((IResource) element).getName();
		}
		return null;
	}
	
	@Override
	public Image getImage(Object element) {
		return null;
	}

}
