package com.mgs.plugin.apache.wizard.external.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mgs.plugin.apache.wizard.external.StdOutputHandler;

public class SystemAdminWindows {

	private StdOutputHandler stdOutputHandler;
	
	public void setStdOutputHandler(StdOutputHandler stdOutputHandler) {
		this.stdOutputHandler = stdOutputHandler;
	}
	
	protected String readFile(File file) throws IOException{
		
		String contents = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String separator = System.getProperty("line.separator");
		String l;
		while(null != (l = reader.readLine())){
			contents += l + separator;
		}
		return contents;
	}
	
	protected void writeFile(File file, String content) throws IOException{
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.close();
	}

	protected void command(String command) throws IOException{
		command(command, true);
	}
	protected void command(String command, boolean wait) throws IOException{
		
		Process p;
		
		if(stdOutputHandler != null){
			stdOutputHandler.readCommand(command);
		}
		
		// lancement de la commande.
		p = Runtime.getRuntime().exec(command);
		
		// delegate stream reading.
		if(stdOutputHandler != null && wait){
			stdOutputHandler.readStdOutputs(p.getInputStream(), p.getErrorStream());
		}
		
		if(wait){
			int exitCode = 0;
			try {
				exitCode = p.waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
			if(exitCode != 0){
				throw new IOException("process exit with code " + exitCode);
			}
		}
			
	}
}
