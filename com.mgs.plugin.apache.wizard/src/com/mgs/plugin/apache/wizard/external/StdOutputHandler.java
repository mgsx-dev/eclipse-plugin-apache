package com.mgs.plugin.apache.wizard.external;

import java.io.InputStream;

public interface StdOutputHandler {
	
	/**
	 * Std outputs may be read.
	 * @param out
	 * @param err
	 */
	public void readStdOutputs(InputStream out, InputStream err);
	
	public void readCommand(String command);
}
