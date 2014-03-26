package org.wiperdog.jrubyrunner.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jruby.embed.ScriptingContainer;
import org.wiperdog.directorywatcher.Listener;

/**
 * An implement of org.wiperdog.directorywatcher.Listernet Any
 * change to script of watching folder will be raised an action by this listerner
 */
class RubyScripListener implements Listener {
	public String directory;
	public long interval = 1000 ;
	public ScriptingContainer container;
	public Map<String,Object> inputData;
	private static final Logger logger = Logger.getLogger(RubyScripListener.class);
	
	public RubyScripListener(ScriptingContainer container, String directory,Map<String,Object> inputData,long interval) {
		this.container = container;
		this.directory = directory;
		this.interval = interval;
		this.inputData = inputData;
	}
	public RubyScripListener(ScriptingContainer container, String directory,Map<String,Object> inputData) {
		this.container = container;
		this.directory = directory;
		this.inputData = inputData;
	}
	public RubyScripListener(ScriptingContainer container, String directory,long interval) {
		this.container = container;
		this.directory = directory;
		this.interval = interval;
	}
	public RubyScripListener(ScriptingContainer container, String directory) {
		this.container = container;
		this.directory = directory;
	}
	public String getDirectory() {

		return this.directory;
	}

	public long getInterval() {
		return interval;
	}

	public boolean filterFile(File file) {
		if (file.isFile()) {
			String fname = file.getName();
			if (fname.endsWith(".rb")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean notifyModified(File target) throws IOException {
		logger.debug("File modified: " + target.getAbsolutePath());
		this.runScript(target);
		return true;
	}

	public boolean notifyAdded(File target) throws IOException {
		logger.debug("File added: " + target.getAbsolutePath());
		this.runScript(target);
		return true;
	}

	public boolean notifyDeleted(File target) throws IOException {
		logger.debug("File deleted: " + target.getAbsolutePath());
		return true;
	}

	public void runScript(File file) {
		try {
			Reader reader = new FileReader(file);
			if(this.inputData != null ){
				this.setInputData(container,this.inputData);
			}
			container.runScriptlet(reader, file.getName());
		} catch (FileNotFoundException e) {
			logger.debug(" File " + file.getAbsolutePath() + " not found !");
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}
	public void setInputData(ScriptingContainer container,Map<String, Object> input) {
		if (input != null) {
			for (Entry<String, Object> entry : input.entrySet()) {
				container.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
