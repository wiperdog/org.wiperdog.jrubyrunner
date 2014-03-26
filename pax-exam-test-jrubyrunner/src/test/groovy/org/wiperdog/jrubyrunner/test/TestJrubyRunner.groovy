package org.wiperdog.jrubyrunner.test;
import java.util.Map;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import org.apache.felix.framework.Felix;
import org.apache.maven.wagon.InputData;
import org.jruby.RubyProcess.Sys;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.junit.runner.JUnitCore;
import org.osgi.service.cm.ManagedService;
import org.wiperdog.jrubyrunner.JrubyRunner;
import org.wiperdog.jrubyrunner.test.common.TestUTCommon;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TestJrubyRunner{

	JrubyRunner jrService;
	def libPaths;
	def inputData;
	//script folder for test execute function
	def rubyScriptDir;
	//script folder for test watching function
	def rubyScriptDir2;
	TestUTCommon testCommon ;
	def dataTestDir;
	long interval ;
	@Inject
	private org.osgi.framework.BundleContext context;

	@Configuration
	public Option[] config() {
		String wd = System.getProperty("user.dir");
		return options(
		cleanCaches(true),
		frameworkStartLevel(6),
		// felix log level
		systemProperty("felix.log.level").value("4"), // 4 = DEBUG
		// setup properties for fileinstall bundle.
		systemProperty("felix.home").value(wd),
		//
		// Pax-exam make this test code into OSGi bundle at runtime, so
		// we need "groovy-all" bundle to use this groovy test code.
		mavenBundle("org.codehaus.groovy", "groovy-all", "2.2.1").startLevel(2),
		mavenBundle("org.jruby", "jruby-complete", "1.7.10").startLevel(2),
		mavenBundle("org.wiperdog", "org.wiperdog.directorywatcher", "0.1.1-SNAPSHOT").startLevel(2),
		mavenBundle("org.wiperdog", "org.wiperdog.jrubyrunner", "1.0").startLevel(3),

		junitBundles()
		);
	}

	@Before
	public void prepare() {
		dataTestDir = "src/test/resources/data_test"
		jrService = context.getService(context.getServiceReference(JrubyRunner.class.getName()));
		libPaths = [
			dataTestDir + "/gems/os-0.9.6/lib"
		]
		inputData = [a : 15 , b : 21 ,c :["Hello", "JrubyRunner"]]
		//rubyScriptDir = "src/test/resources/rubScript"
		rubyScriptDir = dataTestDir + "/rubyScript"
		rubyScriptDir2 = dataTestDir + "/rubyScript2"
		testCommon = new TestUTCommon();
		interval = 2000 ;
	}

	@After
	public void finish() {
	}

	public String checkOS(){
		if(System.properties["os.name"].toLowerCase().contains("linux")){
			return "linux"
		}else{
			if(System.properties["os.name"].toLowerCase().contains("windows")){
				return  "windows"
			}else{
				return "other"
			}
		}
	}
	//--------------------------Test execute script function ------------------------
	/**
	 * Test execute script function with params : rubyFile , inputData ,libPaths
	 * Ruby script with be get data input from @prepare ,println some information
	 * using 'os' gem and return result from a simple operation : a + b  (a,b is data from input) 
	 */
	@Test
	public void TestCase1(){
		def rubyFile = rubyScriptDir + "/test1.rb"
		def dataReturn = jrService.execute(rubyFile , inputData,libPaths)
		assertEquals(36, dataReturn);
	}


	/**
	 * Test execute script function with params : rubyFile , inputData
	 * Ruby script with be get data input from @prepare ,println some information
	 * using 'os' gem and return result is string from 'c' variable
	 */
	@Test
	public void TestCase2(){
		def rubyFile = rubyScriptDir + "/test2.rb"
		def dataReturn = jrService.execute(rubyFile , inputData)
		assertEquals("Hello JrubyRunner", dataReturn);
	}

	/**
	 * Test execute script function with params : rubyFile , libpaths
	 * Ruby script with be get os type and return to test result
	 * 
	 */
	@Test
	public void TestCase3(){
		def rubyFile = rubyScriptDir + "/test3.rb"
		def os = this.checkOS();
		//get os type running and comparision with the return result of ruby script

		def dataReturn = jrService.execute(rubyFile , libPaths)
		assertEquals(os, dataReturn);
	}

	/**
	 * Test execute script function with params : rubyFile 
	 * Ruby script with return true
	 *
	 */
	@Test
	public void TestCase4(){
		def rubyFile = rubyScriptDir + "/test4.rb"
		def dataReturn = jrService.execute(rubyFile , libPaths)
		assertTrue(dataReturn);
		
	}

	/**
	 * Test execute script function with params : rubyFile 
	 * And the ruby script not available 
	 * Ruby script with return null , an error message will be logged to console : 
	 * " File src/test/resources/rubyScript/test5.rb not found !" 
	 */
	@Test
	public void TestCase5(){
		def rubyFile = rubyScriptDir + "/test5.rb"
		def dataReturn = jrService.execute(rubyFile , libPaths)
		assertNull(dataReturn);
		
	}

	/**
	 * Test execute script function with params : rubyFile,inputData,libPaths
	 * And the input data for script is not available
	 * Ruby script with return null , an error message will be logged to console :
	 * " NameError: undefined local variable or method `e' for main:Object"
	 */
	@Test
	public void TestCase6(){
		def rubyFile = rubyScriptDir + "/test6.rb"
		inputData = null;
		//Bundle bundle = context.getBundles();
		JrubyRunner jrService2 = context.getService(context.getServiceReference(JrubyRunner.class.getName()));
		def dataReturn = jrService2.execute(rubyFile ,inputData,libPaths)
		assertNull(dataReturn);
	}

	/**
	 * Test execute script function with params : rubyFile,inputData,libPaths
	 * And the gem paths is not available
	 * Ruby script with return null , an error message will be logged to console :
	 * " no such file to load -- mysql"
	 */
	@Test
	public void TestCase7(){
		def rubyFile = rubyScriptDir + "/test7.rb"
		libPaths = null;
		def dataReturn = jrService.execute(rubyFile ,inputData,libPaths)
		assertNull(dataReturn);
	}
	//------------------Test directory watcher------------------------

	/**
	 * Test for watching  script folder function with params : rubydirectory,inputData,libPaths
	 * If ruby file is modifired ,script will be run and write some text to file 
	 * Expected : Data expected will be the same as output from ruby script 
	 */
	@Test
	public void TestCase8(){
		
			inputData = [list_str : [
					"Welcome",
					"to",
					"the",
					"JrubyRunner"
				]]
			def watchingDir = rubyScriptDir2 + "/testcase8";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase8/output/test8.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test8_origin")
			def rubyFile = new File(watchingDir + "/test8.rb")
			rubyFile.setText(orginFile.getText());
			def dataReturn = jrService.startWatcher(watchingDir ,inputData,libPaths);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase8/output/test8.output",dataTestDir + "/testcase8/expected/test8.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase8/output/test8_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test8_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase8/output/test8_2.output",dataTestDir + "/testcase8/expected/test8_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase8/output/test8_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test8_add").renameTo(new File(watchingDir + "/test8_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase8/output/test8_3.output",dataTestDir + "/testcase8/expected/test8_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test8_2.rb").renameTo(new File(watchingDir + "/test8_add"))

		
	}

	/**
	 * Test for watching  script folder function with params : rubydirectory,inputData
	 * If ruby file is modifired ,script will be run and write some text to file
	 * Expected : Data expected will be the same as output from ruby script
	 */
	@Test
	public void TestCase9(){

			inputData = [list_str : [
					"Welcome",
					"to",
					"the",
					"JrubyRunner"
				]]
			def watchingDir = rubyScriptDir2 + "/testcase9";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase9/output/test9.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test9_origin")
			def rubyFile = new File(watchingDir + "/test9.rb")
			rubyFile.setText(orginFile.getText());
			def dataReturn = jrService.startWatcher(watchingDir ,inputData);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase9/output/test9.output",dataTestDir + "/testcase9/expected/test9.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase9/output/test9_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test9_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase9/output/test9_2.output",dataTestDir + "/testcase9/expected/test9_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase9/output/test9_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test9_add").renameTo(new File(watchingDir + "/test9_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase9/output/test9_3.output",dataTestDir + "/testcase9/expected/test9_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test9_2.rb").renameTo(new File(watchingDir + "/test9_add"))

	}

	/**
	 * Test for watching  script folder function with params : rubydirectory,libpaths
	 * If ruby file is modifired ,script will be run and write some text to file
	 * Expected : Data expected will be the same as output from ruby script
	 */
	@Test
	public void TestCase10(){

			def watchingDir = rubyScriptDir2 + "/testcase10";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase10/output/test10.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test10_origin")
			def rubyFile = new File(watchingDir + "/test10.rb")
			rubyFile.setText(orginFile.getText());
			def os = this.checkOS();
			//create expected data in runtime because OS is may difference .
			//Ruby script will running and using gem from libpath to get os type
			testCommon.cleanData(dataTestDir + "/testcase10/expected/test10.txt");
			def expected = new File(dataTestDir + "/testcase10/expected/test10.txt")
			expected.createNewFile();
			expected.setText("OS is " + os);

			def dataReturn = jrService.startWatcher(watchingDir ,libPaths);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase10/output/test10.output",dataTestDir + "/testcase10/expected/test10.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase10/output/test10_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test10_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase10/output/test10_2.output",dataTestDir + "/testcase10/expected/test10_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase10/output/test10_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test10_add").renameTo(new File(watchingDir + "/test10_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase10/output/test10_3.output",dataTestDir + "/testcase10/expected/test10_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test10_2.rb").renameTo(new File(watchingDir + "/test10_add"))

	}

	/**
	 * Test for watching  script folder function with params : rubydirectory
	 * If ruby file is modifired ,script will be run and write some text to file
	 * Expected : Data expected will be the same as output from ruby script
	 */
	@Test
	public void TestCase11(){
			def watchingDir = rubyScriptDir2 + "/testcase11";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase11/output/test11.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test11_origin")
			def rubyFile = new File(watchingDir + "/test11.rb")
			rubyFile.setText(orginFile.getText());
			
			def dataReturn = jrService.startWatcher(watchingDir ,libPaths);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase11/output/test11.output",dataTestDir + "/testcase11/expected/test11.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase11/output/test11_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test11_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase11/output/test11_2.output",dataTestDir + "/testcase11/expected/test11_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase11/output/test11_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test11_add").renameTo(new File(watchingDir + "/test11_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase11/output/test11_3.output",dataTestDir + "/testcase11/expected/test11_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test11_2.rb").renameTo(new File(watchingDir + "/test11_add"))
	}
	
	/**
	 * Test for watching  script folder function with params : rubydirectory,inputData,libPaths,interval
	 * If ruby file is modifired ,script will be run and write some text to file
	 * Expected : Data expected will be the same as output from ruby script
	 */
	@Test
	public void TestCase12(){
		
			inputData = [list_str : [
					"Welcome",
					"to",
					"the",
					"JrubyRunner"
				]]
			def watchingDir = rubyScriptDir2 + "/testcase12";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase12/output/test12.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test12_origin")
			def rubyFile = new File(watchingDir + "/test12.rb")
			rubyFile.setText(orginFile.getText());
			def dataReturn = jrService.startWatcher(watchingDir ,inputData,libPaths,interval);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase12/output/test12.output",dataTestDir + "/testcase12/expected/test12.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase12/output/test12_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test12_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase12/output/test12_2.output",dataTestDir + "/testcase12/expected/test12_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase12/output/test12_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test12_add").renameTo(new File(watchingDir + "/test12_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase12/output/test12_3.output",dataTestDir + "/testcase12/expected/test12_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test12_2.rb").renameTo(new File(watchingDir + "/test12_add"))

	
	}
	/**
	 * Test for watching  script folder function with params : rubydirectory,inputData,interval
	 * If ruby file is modifired ,script will be run and write some text to file
	 * Expected : Data expected will be the same as output from ruby script
	 */
	@Test
	public void TestCase13(){
			inputData = [list_str : [
					"Welcome",
					"to",
					"the",
					"JrubyRunner"
				]]
			def watchingDir = rubyScriptDir2 + "/testcase13";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase13/output/test13.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test13_origin")
			def rubyFile = new File(watchingDir + "/test13.rb")
			rubyFile.setText(orginFile.getText());
			def dataReturn = jrService.startWatcher(watchingDir ,inputData,interval);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase13/output/test13.output",dataTestDir + "/testcase13/expected/test13.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase13/output/test13_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test13_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase13/output/test13_2.output",dataTestDir + "/testcase13/expected/test13_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase13/output/test13_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test13_add").renameTo(new File(watchingDir + "/test13_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase13/output/test13_3.output",dataTestDir + "/testcase13/expected/test13_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test13_2.rb").renameTo(new File(watchingDir + "/test13_add"))

	}
	/**
	 * Test for watching  script folder function with params : rubydirectory,libpaths,interval
	 * If ruby file is modifired ,script will be run and write some text to file
	 * Expected : Data expected will be the same as output from ruby script
	 */
	@Test
	public void TestCase14(){

			def watchingDir = rubyScriptDir2 + "/testcase14";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase14/output/test14.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test14_origin")
			def rubyFile = new File(watchingDir + "/test14.rb")
			rubyFile.setText(orginFile.getText());
			def os = this.checkOS();
			//create expected data in runtime because OS is may difference .
			//Ruby script will running and using gem from libpath to get os type
			testCommon.cleanData(dataTestDir + "/testcase14/expected/test14.txt");
			def expected = new File(dataTestDir + "/testcase14/expected/test14.txt")
			expected.createNewFile();
			expected.setText("OS is " + os);

			def dataReturn = jrService.startWatcher(watchingDir ,libPaths,interval);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase14/output/test14.output",dataTestDir + "/testcase14/expected/test14.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase14/output/test14_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test14_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase14/output/test14_2.output",dataTestDir + "/testcase14/expected/test14_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase14/output/test14_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test14_add").renameTo(new File(watchingDir + "/test14_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase14/output/test14_3.output",dataTestDir + "/testcase14/expected/test14_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test14_2.rb").renameTo(new File(watchingDir + "/test14_add"))

	}
	
	/**
	 * Test for watching  script folder function with params : rubydirectory
	 * If ruby file is modifired ,script will be run and write some text to file
	 * Expected : Data expected will be the same as output from ruby script
	 */
	@Test
	public void TestCase15(){
			def watchingDir = rubyScriptDir2 + "/testcase15";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase15/output/test15.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test15_origin")
			def rubyFile = new File(watchingDir + "/test15.rb")
			rubyFile.setText(orginFile.getText());
			
			def dataReturn = jrService.startWatcher(watchingDir);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase15/output/test15.output",dataTestDir + "/testcase15/expected/test15.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase15/output/test15_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test15_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase15/output/test15_2.output",dataTestDir + "/testcase15/expected/test15_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase15/output/test15_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test15_add").renameTo(new File(watchingDir + "/test15_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase15/output/test15_3.output",dataTestDir + "/testcase15/expected/test15_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test15_2.rb").renameTo(new File(watchingDir + "/test15_add"))
	}
	
	/**
	 * Test for watching  script folder function with params : rubydirectory ,interval
	 * If ruby file is modifired ,script will be run and write some text to file
	 * Expected : Data expected will be the same as output from ruby script
	 */
	@Test
	public void TestCase16(){
			def watchingDir = rubyScriptDir2 + "/testcase16";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase16/output/test16.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test16_origin")
			def rubyFile = new File(watchingDir + "/test16.rb")
			rubyFile.setText(orginFile.getText());
			
			def dataReturn = jrService.startWatcher(watchingDir,interval);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase16/output/test16.output",dataTestDir + "/testcase16/expected/test16.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase16/output/test16_2.output");
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test16_tmp")
			rubyFile.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase16/output/test16_2.output",dataTestDir + "/testcase16/expected/test16_2.txt" ))

			testCommon.cleanData(dataTestDir + "/testcase16/output/test16_3.output");
			//Add new ruby file to watching folder
			new File(watchingDir + "/test16_add").renameTo(new File(watchingDir + "/test16_2.rb"))
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData(dataTestDir + "/testcase16/output/test16_3.output",dataTestDir + "/testcase16/expected/test16_3.txt" ))
			//Delete a ruby file ,do nothing
			new File(watchingDir + "/test16_2.rb").renameTo(new File(watchingDir + "/test16_add"))
	}
	
	/**
	 * Test for stop watching  script folder function with params : rubydirectory
	 * Expected : No script run after the specifice watcher is stopped
	 * A message print out to console : "DirectoryWatcher for [rubyScriptDir] is stopping"
	 */
	@Test
	public void TestCase17(){
			def watchingDir = rubyScriptDir2 + "/testcase17";
			def watchingDir2 = rubyScriptDir2 + "/testcase17_2";
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase17/output/test17.output");
			//clear output data before runscript
			testCommon.cleanData(dataTestDir + "/testcase17_2/output/test17_2.output");
			//Set orginal source script
			def orginFile = new File(watchingDir + "/test17_origin")
			def rubyFile = new File(watchingDir + "/test17.rb")
						
			def orginFile2 = new File(watchingDir2 + "/test17_2_origin")
			def rubyFile2= new File(watchingDir2 + "/test17_2.rb")
			
			rubyFile.setText(orginFile.getText());
			rubyFile2.setText(orginFile2.getText());
			
			jrService.startWatcher(watchingDir);
			jrService.startWatcher(watchingDir2);
			//sleep to wait for script run from watcher
			Thread.currentThread().sleep(10000);
			assertTrue(testCommon.compareData( dataTestDir + "/testcase17/output/test17.output",dataTestDir + "/testcase17/expected/test17.txt" ))
			assertTrue(testCommon.compareData( dataTestDir + "/testcase17_2/output/test17_2.output",dataTestDir + "/testcase17_2/expected/test17_2.txt" ))
			//clear output data before next change
			testCommon.cleanData(dataTestDir + "/testcase17/output/test17_2.output");
			testCommon.cleanData(dataTestDir + "/testcase17_2/output/test17_2_2.output");
			//Stopping one watcher  , another is still running
			jrService.stopWatcher(watchingDir)
			//sleep a while to change the ruby file in watching folder
			def codeChange = new File(watchingDir+ "/test17_tmp")
			rubyFile.setText(codeChange.getText());
			codeChange = new File(watchingDir2+ "/test17_2_tmp")
			rubyFile2.setText(codeChange.getText());
			Thread.currentThread().sleep(10000);
			//Change file in 2 watcher directories but only 1 script from one running watcher is applied to run
			assertFalse(new File(dataTestDir + "/testcase17/output/test17_2.output").exists())
			assertTrue(testCommon.compareData( dataTestDir + "/testcase17_2/output/test17_2_2.output",dataTestDir + "/testcase17_2/expected/test17_2_2.txt" ))
			
			

	}
}