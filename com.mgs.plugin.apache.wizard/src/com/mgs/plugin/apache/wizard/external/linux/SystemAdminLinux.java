package com.mgs.plugin.apache.wizard.external.linux;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.eclipse.ui.console.MessageConsole;

import com.mgs.plugin.apache.wizard.external.StdOutputHandler;

public class SystemAdminLinux {

	protected MessageConsole console;
	
	protected String rootPassword;
	
	private StdOutputHandler stdOutputHandler;
	
	private StringBuffer out;
	private StringBuffer err;
	
	public SystemAdminLinux() {
		clearBuffers();
	}
	
	public void clearBuffers() {
		out = new StringBuffer();
		err = new StringBuffer();
	}
	
	public void setStdOutputHandler(StdOutputHandler stdOutputHandler) {
		this.stdOutputHandler = stdOutputHandler;
	}
	
	public void setRootPassword(String rootPassword) {
		this.rootPassword = rootPassword;
	}
	
	public String getRootPassword() {
		return rootPassword;
	}
	
	public StringBuffer getOut() {
		return out;
	}
	
	public StringBuffer getErr() {
		return err;
	}

	protected void sudoTest() throws IOException{
		
		// command("pwd", true);
		
	}
	
	protected String readFile(File file) throws IOException{
		
		try{
			String contents = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String l;
			while(null != (l = reader.readLine())){
				contents += l + "\n";
			}
			return contents;
		}catch(IOException e){
			if(stdOutputHandler != null){
				// TODO print the exception message to the console.
			}
			throw e;
		}
	}
	
	protected void writeFile(File file, String content, boolean sudo) throws IOException{
		
		if(sudo){
			File tmpFile = new File("/tmp/mgstmpfile");
			
			FileWriter writer = new FileWriter(tmpFile);
			writer.write(content);
			writer.close();
			
			copyFile(tmpFile, file, sudo);
		}else{
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		}
	}
	
	protected void createSymbolicLink(File link, String target, boolean sudo) throws IOException{
		command("ln -s " + target + " " + link.getAbsolutePath(), sudo);
	}
	
	protected void copyFile(File src, File dst, boolean sudo) throws IOException{
		command("cp " + src.getAbsolutePath() + " " + dst.getAbsolutePath(), sudo);
	}
	
	protected void removeSymbolicLink(File link, boolean sudo) throws IOException{
		command("rm " + link.getAbsolutePath(), sudo);
	}
	
	protected void command(String command, boolean sudo) throws IOException{
		
		Process p;
		String commandline;
		if(sudo){
			commandline = "sudo -S " + command;
		}else{
			commandline = command;
		}
		
		if(stdOutputHandler != null){
			stdOutputHandler.readCommand(commandline);
		}
		
		if(sudo){
			// lancement de la commande.
			p = Runtime.getRuntime().exec(commandline);
			
			// passage du mot de passe root.
			PrintWriter writer = new PrintWriter(p.getOutputStream());
			writer.write(rootPassword + "\n");
			writer.close();
		}else{
			// lancement de la commande.
			p = Runtime.getRuntime().exec(commandline);
		}
		
		// delegate stream reading.
		if(stdOutputHandler != null){
			stdOutputHandler.readStdOutputs(p.getInputStream(), p.getErrorStream());
		}
		
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
