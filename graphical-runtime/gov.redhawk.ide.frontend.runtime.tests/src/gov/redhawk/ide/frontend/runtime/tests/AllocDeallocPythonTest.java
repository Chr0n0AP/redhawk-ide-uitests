/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.frontend.runtime.tests;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

/**
 * Launches the analogTuner device (an analog tuner) in the sandbox for allocation/deallocation tests.
 * IDE-1356 Deallocation failed for Python devices
 * IDE-797 Analog FEI devices were missing the tuner container
 */
public class AllocDeallocPythonTest extends AbstractTunerTest {

	private static final String FEI_DEVICE = "analogTuner";
	private static final String FEI_DEVICE_1 = FEI_DEVICE + "_1";
	private static final String FEI_DEVICE_IMPL = "python";

	@Before
	public void before() throws Exception {
		super.before();
		ScaExplorerTestUtils.launchDeviceFromTargetSDR(bot, FEI_DEVICE, FEI_DEVICE_IMPL);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { SANDBOX, DEV_MGR, FEI_DEVICE_1}, FEI_CONTAINER);
	}

	@After
	public void after() throws CoreException {
		ScaExplorerTestUtils.terminate(bot, new String[] { SANDBOX }, DEV_MGR);
		bot.waitUntil(new WaitForLaunchTermination(false));
		ConsoleUtils.removeTerminatedLaunches(bot);
		super.after();
	}

	protected String getDeviceName() {
		return FEI_DEVICE;
	}

	protected String getDeviceImpl() {
		return FEI_DEVICE_IMPL;
	}
}
