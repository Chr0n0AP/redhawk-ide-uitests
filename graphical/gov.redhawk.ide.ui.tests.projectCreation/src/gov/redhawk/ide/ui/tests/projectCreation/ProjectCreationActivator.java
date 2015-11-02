/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.ui.tests.projectCreation;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class ProjectCreationActivator extends Plugin {
	
	public static final String ID = "gov.redhawk.ide.ui.tests.projectCreation";
	private static ProjectCreationActivator instance;

	public void start(BundleContext context) throws Exception {
		instance = this;
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		instance = null;
	}
	
	public static ProjectCreationActivator getInstance() {
		return instance;
	}

}
