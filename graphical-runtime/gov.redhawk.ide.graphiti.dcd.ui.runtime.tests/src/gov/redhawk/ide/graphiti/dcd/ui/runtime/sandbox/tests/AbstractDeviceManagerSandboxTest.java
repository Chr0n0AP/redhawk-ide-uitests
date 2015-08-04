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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.sandbox.tests;

import org.junit.After;
import org.junit.Before;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

/**
 * 
 */
public abstract class AbstractDeviceManagerSandboxTest extends UIRuntimeTest {

	static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	static final String DEVICE_MANAGER = "Device Manager";
	static final String[] SANDBOX_DEVMGR_PATH = { "Sandbox", "Device Manager" };
	static final String DIAGRAM_TAB_TITLE = "Device Manager Chalkboard";

	// Common Test Component Names
	static final String GPP = "GPP";
	static final String GPP_1 = "GPP_1";

	static final String DEVICE_STUB = "DeviceStub";
	static final String DEVICE_STUB_1 = "DeviceStub_1";
	static final String DEVICE_STUB_2 = "DeviceStub_2";
	static final String DEVICE_STUB_DOUBLE_IN_PORT = "dataDouble_in";
	static final String DEVICE_STUB_DOUBLE_OUT_PORT = "dataDouble_out";

	protected RHSWTGefBot gefBot;

	@Before
	public void beforeTest() throws Exception {
		gefBot = new RHSWTGefBot();
	}
	
	@After
	public void afterTest() {
		ScaExplorerTestUtils.terminateFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER);
		
		//wait until waveform empty
		ScaExplorerTestUtils.waitUntilSandboxDeviceManagerEmpty(gefBot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER);
		
		//close editors
		gefBot.closeAllEditors();
		gefBot = null;
	}

	/** Helper method to open Sandbox Device Manager Graphiti Chalkboard Diagram */
	static RHBotGefEditor openNodeChalkboardDiagram(RHSWTGefBot gefBot) {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, DEVICE_MANAGER, DiagramType.GRAPHITI_CHALKBOARD);
		RHBotGefEditor editor = gefBot.rhGefEditor(DIAGRAM_TAB_TITLE);
		editor.setFocus();
		return editor;
	}

}
