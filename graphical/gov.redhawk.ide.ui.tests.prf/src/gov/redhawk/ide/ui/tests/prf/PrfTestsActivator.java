package gov.redhawk.ide.ui.tests.prf;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PrfTestsActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.ui.tests.prf"; //$NON-NLS-1$

	// The shared instance
	private static PrfTestsActivator plugin;
	
	/**
	 * The constructor
	 */
	public PrfTestsActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PrfTestsActivator getDefault() {
		return plugin;
	}

}
