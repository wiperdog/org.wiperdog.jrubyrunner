package org.wiperdog.jrubyrunner;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.wiperdog.jrubyrunner.impl.JrubyRunnerImpl;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		Bundle bundle = context.getBundle();
		context.registerService(JrubyRunner.class.getName(), new JrubyRunnerImpl(bundle), null);
		System.out.println("JrubyRunner Service registered !");

	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
