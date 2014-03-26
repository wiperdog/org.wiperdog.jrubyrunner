
/**
 * Jruby Runner service provide function to excute ruby script 
 * or watching for the change of a ruby script folder
 */
package org.wiperdog.jrubyrunner;

import java.util.List;
import java.util.Map;

public interface JrubyRunner {
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
	public Object execute(String scriptPath,Map<String,Object> input,List<String> libPaths);

	/**
	 * Excute ruby script with input data for script
	 * 
	 * @param scriptPath
	 *            : Ruby script path
	 * @param input
	 *            : input data for script running
	 * @return data for script running
	 */
	public Object execute(String scriptPath,Map<String,Object> input);
	
	/**
	 * Excute ruby script with gems paths
	 * 
	 * @param scriptPath
	 * @param libPaths
	 * @return
	 */
	public Object execute(String scriptPath,List<String> libPaths);
	
	/**
	 * Excute ruby script
	 * 
	 * @param scriptPath
	 * @return
	 */
	public Object execute(String scriptPath);
	

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
	public void startWatcher(String rubyDirPath,Map<String,Object> input,List<String> libPaths,long interval);
	
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
	public void startWatcher(String rubyDirPath,Map<String,Object> input,long interval);
	
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
	public void startWatcher(String rubyDirPath,List<String> libPaths,long interval);
	
	/**
	 * Start to watching a ruby script folder with interval setting
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param interval
	 *            : Time interval for directory watcher            
	 */
	public void startWatcher(String rubyDirPath,long interval);
	
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
	public void startWatcher(String rubyDirPath,Map<String,Object> input,List<String> libPaths);
	
	/**
	 * Start to watching a ruby script folder with input data for script running
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param inputData
	 *            : input data for script running
	 */
	public void startWatcher(String rubyDirPath,Map<String,Object> input);
	
	/**
	 * Start to watching a ruby script folder with given library paths
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 * @param libPaths
	 *            : gems path for script running
	 */
	public void startWatcher(String rubyDirPath,List<String> libPaths);
	
	/**
	 * Start to watching a ruby script folder
	 * 
	 * @param rubyDirPath
	 *            : Ruby script directory will be watching
	 */
	public void startWatcher(String rubyDirPath);
	
	/**
	 * Stop watching a ruby script folder
	 * 
	 * @param rubyDirPath
	 */
	public void stopWatcher(String rubyDirPath);
}
