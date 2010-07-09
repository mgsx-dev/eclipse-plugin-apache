package com.mgs.plugin.apache.wizard.external.common;

public class HostsFileEditor {

	private String contents;
	
	private boolean modified;

	public HostsFileEditor() {
		setContents("");
	}
	
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
		modified = false;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	/**
	 * Add a mapping in the contents
	 * @param ip
	 * @param hostname
	 */
	public void addMapping(String ip, String hostname){
		
		String lineSeparator = System.getProperty("line.separator");
		
		String [] lines = contents.split(lineSeparator);
		
		boolean done = false;
		for(String line : lines){
			// skip comments.
			if(!line.trim().startsWith("#")){
				if(line.startsWith(ip)){
					String rightPart = line.substring(ip.length()).trim();
					if(rightPart.contains("#")){
						rightPart = rightPart.substring(0, rightPart.indexOf("#")).trim();
					}
					String [] lstHost = rightPart.split(" ");
					for(String host : lstHost){
						if(host.trim().equals(hostname)){
							done = true;
						}
					}
					if(done){
						break;
					}
				}
			}
		}
		if(!done){
			// need to add mapping.
			modified = true;
			contents += lineSeparator + ip + "\t" + hostname;
		}
		
	}
}
