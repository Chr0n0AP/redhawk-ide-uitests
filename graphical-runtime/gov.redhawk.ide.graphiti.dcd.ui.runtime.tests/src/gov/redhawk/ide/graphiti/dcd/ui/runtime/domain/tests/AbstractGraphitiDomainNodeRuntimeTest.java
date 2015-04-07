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
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.junit.After;
import org.junit.Before;

/**
 * 
 */
public abstract class AbstractGraphitiDomainNodeRuntimeTest extends UIRuntimeTest {

	static final String DOMAIN = "SWTBOT_TEST_" + (int) (100.0 * Math.random());
	static final String[] DOMAIN_NODE_PARENT_PATH = { DOMAIN + " CONNECTED", "Device Managers"};
	static final String DOMAIN_MANAGER_PROCESS = "Domain Manager";
	static final String DEVICE_MANAGER_PROCESS = "Device Manager";
	static final String DEVICE_MANAGER = "DevMgr";
	static final String DEVICE_MANAGER_W_BULKIO = "DevMgr_with_bulkio"; // For tests that need a device with a Bulkio port
	static final String NAMESPACE_DEVICE_MANAGER = "namespaceNode";
	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE VisibilityModifier
	private String nodeFullName; //full name of device node that is launched

	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		super.before();
		
		//Launch Domain
		ScaExplorerTestUtils.launchDomain(gefBot, DOMAIN, DEVICE_MANAGER);
		
		//wait until Domain launched and connected
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(gefBot, DOMAIN);

		// Open Node Explorer Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER, DiagramType.GRAPHITI_NODE_EXPLORER);
		nodeFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, DOMAIN_NODE_PARENT_PATH, DEVICE_MANAGER);
	}
	
	@After
	public void afterTest() {
		
		//delete domain instance from sca explorer
		ScaExplorerTestUtils.deleteDomainInstance(bot, DOMAIN);
		
		//Stop all processes that may have been started
		ConsoleUtils.terminateAllProcesses(gefBot);
	}
	
	public String getNodeFullName() {
		return nodeFullName;
	}

	public void setNodeFullName(String waveFormFullName) {
		this.nodeFullName = waveFormFullName;
	}

}
