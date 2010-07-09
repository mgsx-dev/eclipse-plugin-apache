package com.mgs.plugin.apache.wizard.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.mgs.plugin.apache.wizard.external.StdOutputHandler;

public class BridgeProcessToConsole implements StdOutputHandler {

	private MessageConsole console;
	
	public BridgeProcessToConsole(MessageConsole console) {
		this.console = console;
	}
	
	public MessageConsole getConsole() {
		return console;
	}
	
	@Override
	public void readStdOutputs(InputStream out, InputStream err) {
		
		String strOut = "";
		String strErr = "";
		
		BufferedReader reader;
		String line;

		if(out != null){
			try{
				reader= new BufferedReader(new InputStreamReader(out));
				while(null != (line = reader.readLine())){
					strOut += line + "\n";
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		if(err != null){
			try{
				reader= new BufferedReader(new InputStreamReader(err));
				while(null != (line = reader.readLine())){
					strErr += line + "\n";
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		final String fstrOut = strOut;
		final String fstrErr = strErr;

		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				MessageConsoleStream stream;
				
				console.activate();
				
				if(fstrOut.length() > 0){
					stream = console.newMessageStream();
					stream.setColor(new Color(null, new RGB(0, 0, 0)));
					stream.print(fstrOut);
				}
				
				if(fstrErr.length() > 0){
					stream = console.newMessageStream();
					stream.setColor(new Color(null, new RGB(255, 0, 0)));
					stream.print(fstrErr);
				}
			}
		});
		
	}

	@Override
	public void readCommand(String command) {
		
		final String fcommand = command;
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				console.activate();
				
				MessageConsoleStream stream = console.newMessageStream();
				stream.setColor(new Color(null, new RGB(0, 0, 255)));
				stream.println(fcommand);
				
			}
		});
		
	}

}
