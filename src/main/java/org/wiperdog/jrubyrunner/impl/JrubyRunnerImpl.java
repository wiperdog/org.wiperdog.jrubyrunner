/**
 * Implement for JrubyRunner service
 */
package org.wiperdog.jrubyrunner.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.DocFlavor.INPUT_STREAM;

import org.apache.log4j.Logger;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.osgi.OSGiScriptingContainer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.wiperdog.directorywatcher.Listener;
import org.wiperdog.jrubyrunner.JrubyRunner;

public class JrubyRunnerImpl implements JrubyRunner {
	Bundle bundle;
	BundleContext context;
	ScriptingContainer container;
	private static final Logger logger = Logger.getLogger(JrubyRunnerImpl.class);
	List<ServiceRegistration> listWatcher = new ArrayList<ServiceRegistration>();

	public JrubyRunnerImpl(Bundle bundle) {
		this.bundle = bundle;
		context = this.bundle.getBundleContext();
		container = new OSGiScriptingContainer(this.bundle, LocalContextScope.CONCURRENT,
				LocalVariableBehavior.PERSISTENT);
		container.setHomeDirectory("classpath:/META-INF/jruby.home");
	}

	/**
	 * Excute a ruby script input data and gems path
	 * 
	 * @param rubyPath
	 *            Ruby script path
	 * @param inputData
	 *            input data for script running
	 * @param libPaths
	 *            gems path for script running
	 * @return dataReturn : data return from script running
	 */
	public Object execute(String scriptPath, Map<String, Object> inputData, List<String> libPaths) {

		try {
			this.setLibPaths(container, libPaths);			
			return this.execute(scriptPath,inputData);
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}

	/**
	 * Excute ruby script with input data for script
	 * 
	 * @param scriptPath
	 *            : Ruby script path
	 * @param input
	 *            : input data for script running
	 * @return data for script running
	 */
	public Object execute(String scriptPath, Map<String, Object> inputData) {

		try {
			this.setInputData(container,inputData);
			return this.execute(scriptPath);
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}

	/**
	 * Excute ruby script with gems paths
	 * 
	 * @param scriptPath
	 * @param libPaths
	 * @return
	 */
	public Object execute(String scriptPath, List<String> libPaths) {
		this.setLibPaths(container, libPaths);
		try {
			return this.execute(scriptPath);
		} catch (Exception e) {
			logger.debug(e);
		}
		return null;
	}

	/**
	 * Excute ruby script
	 * 
	 * @param scriptPath
	 * @return
	 */
	public Object execute(String scriptPath) {
		File rubyFile = new File(scriptPath);
		Reader r;
		Object dataReturn = null;
		try {
			r = new FileReader(rubyFile);
			dataReturn = container.runScriptlet(r, rubyFile.getName());
		} catch (FileNotFoundException e) {
			logger.debug(" File " + rubyFile.getAbsolutePath() + " not found !");
		} catch (Exception e) {
			logger.debug(e);
		}
		return dataReturn;
	}

	/**
	 * Start to watching a ruby script folder with input data and library paths
	 * for script running
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param inputData
	 *            : input data for script running
	 * @param libPaths
	 *            : gems path for script running
	 */
	public void startWatcher(String rubyDirPath, Map<String, Object> inputData,List<String> libPaths) {
		try {
			this.setLibPaths(container, libPaths);
			this.startWatcher(rubyDirPath, inputData);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

	}

	/**
	 * Start to watching a ruby script folder with input data for script running
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param inputData
	 *            : input data for script running
	 */
	public void startWatcher(String rubyDirPath, Map<String, Object> inputData) {
		try {
				RubyScripListener rbl = new RubyScripListener(container, rubyDirPath,inputData);
				ServiceRegistration sr = context.registerService(Listener.class.getName(), rbl, null);
				listWatcher.add(sr);
			} catch (Exception e) {
			logger.debug(e.getMessage());
		}

	}

	/**
	 * Start to watching a ruby script folder with given library paths
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param libPaths
	 *            : gems path for script running
	 */
	public void startWatcher(String rubyDirPath, List<String> libPaths) {

		try {
			this.setLibPaths(container, libPaths);
			this.startWatcher(rubyDirPath);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Start to watching a ruby script folder
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 */
	public void startWatcher(String rubyDirPath) {
		try {
			RubyScripListener rbl = new RubyScripListener(container, rubyDirPath);
			ServiceRegistration sr = context.registerService(Listener.class.getName(), rbl, null);
			listWatcher.add(sr);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Start to watching a ruby script folder with input data and library paths
	 * for script running with interval setting
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param inputData
	 *            : input data for script running
	 * @param libPaths
	 *            : gems path for script running
	 * @param interval
	 *           : Time interval for directory watcher
	 *            
	 */
	public void startWatcher(String rubyDirPath, Map<String, Object> inputData,List<String> libPaths, long interval) {
		try {
			System.out.println("input " + inputData.size());
			this.setLibPaths(container, libPaths);
			this.startWatcher(rubyDirPath, inputData,interval);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

	}

	/**
	 * Start to watching a ruby script folder with input data for script running with interval setting
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param inputData
	 *            : input data for script running
	 * @param interval
	 *           : Time interval for directory watcher
	 */
	public void startWatcher(String rubyDirPath, Map<String, Object> inputData, long interval) {
		try {
			//this.startWatcher(rubyDirPath, interval);
			RubyScripListener rbl = new RubyScripListener(container, rubyDirPath,inputData, interval);
			ServiceRegistration sr = context.registerService(Listener.class.getName(), rbl, null);
			listWatcher.add(sr);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

	}

	/**
	 * Start to watching a ruby script folder with given library paths with interval setting
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param libPaths
	 *            : gems path for script running
	 * @param interval
	 *           : Time interval for directory watcher
	 */
	public void startWatcher(String rubyDirPath, List<String> libPaths, long interval) {
		try {
			this.setLibPaths(container, libPaths);
			this.startWatcher(rubyDirPath, interval);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Start to watching a ruby script folder with interval setting
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param interval
	 *            : Time interval for directory watcher            
	 */
	public void startWatcher(String rubyDirPath, long interval) {

		try {
			RubyScripListener rbl = new RubyScripListener(container, rubyDirPath, interval);
			ServiceRegistration sr = context.registerService(Listener.class.getName(), rbl, null);
			listWatcher.add(sr);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Stop watching a ruby script folder
	 * 
	 * @param rubyDirPath
	 */
	public void stopWatcher(String rubyDirPath) {
		Iterator<ServiceRegistration> iter = listWatcher.iterator();
		while (iter.hasNext()) {
			ServiceRegistration sr = iter.next();
			RubyScripListener objListener = (RubyScripListener) context.getService(sr
					.getReference());
			if (objListener != null) {
				if (objListener.getDirectory().equals(rubyDirPath)) {
					sr.unregister();
					iter.remove();
				}
			}
		}
	}

	/**
	 * Set gems paths for script running
	 * 
	 * @param libPaths
	 *            : List of gems path
	 * @param container
	 *            : a container to excute script
	 */
	public void setLibPaths(ScriptingContainer container, List<String> libPaths) {
		if (libPaths != null) {
			for (String i : libPaths) {
				container.put("path", i);
				container.runScriptlet("$LOAD_PATH << path ");
			}
		}
	}

	/**
	 * Set input data for script running
	 * 
	 * @param input
	 *            : input data fro script
	 * @param container
	 *            : a container to excute script
	 */
	public void setInputData(ScriptingContainer container ,Map<String, Object> input) {
		if (input != null) {
			for (Entry<String, Object> entry : input.entrySet()) {
				container.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
