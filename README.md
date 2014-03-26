This bundle will be used for running ruby script file.

JrubyRunner is a osgi-based bundle ,provide service for running ruby script or watching ruby script folder  from JVM 

Usage :
   - Build project with maven
   - Installation : 
		Install bundle from Felix environment (wiperdog ) ,this bundle is require bundle org.wiperdog.directorywatcher and jruby-complete.jar for dependencies
   - Run script ruby from JVM :
    
		JrubyRunner jrService ;
		
		//Run script without passing input data or gem paths 
		//Note : gem path is directly path to 'lib' folder of gem . Example : /path/gems/os-9.2.6/lib
		
		jrService.execute(scriptPath)
		
		//Run script with passing input
		jrService.execute(scriptPath,input)
		
		//Run script with passing input & gem paths
		jrService.execute(scriptPath,input,libpath)
		
		//We can get return data from script running
		dataReturn = jrService.execute(scriptPath,input,libpath)
   - Watching ruby script folder:  The watcher will be notify and run the script from watching folder  if new script added or modified
    
		JrubyRunner jrService ;	
		
		//Watching a script folder
		jrService.startWatcher(scriptDirectory)
		
		//Watching with interval
		jrService.startWatcher(scriptDirectory,interval)
		
		//Watching with passing input and gems path
		jrService.startWatcher(scriptDirectory,input,libpath)
		
   
	  
	  
