package com.mgs.plugin.apache.wizard;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.StructuredViewer;

public class ExlusiveChecker implements ICheckStateListener,
		ICheckStateProvider {

	private StructuredViewer viewer;
	private Object currentValue;
	
	public ExlusiveChecker(StructuredViewer viewer, Object defaultValue) {
		this.viewer = viewer;
		this.currentValue = defaultValue;
	}
	
	public Object getCurrentValue() {
		return currentValue;
	}
	
	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		if(event.getChecked()){
			currentValue = event.getElement();
		}else{
			currentValue = null;
		}
		viewer.refresh();
	}

	@Override
	public boolean isChecked(Object element) {
		return element.equals(currentValue);
	}

	@Override
	public boolean isGrayed(Object element) {
		return true;
	}

}
