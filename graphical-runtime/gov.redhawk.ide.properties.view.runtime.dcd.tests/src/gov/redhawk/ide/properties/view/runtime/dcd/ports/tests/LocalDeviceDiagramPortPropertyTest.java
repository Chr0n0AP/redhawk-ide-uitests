/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.properties.view.runtime.dcd.ports.tests;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Before;

import gov.redhawk.ide.properties.view.runtime.tests.PortDescription;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;

public class LocalDeviceDiagramPortPropertyTest extends LocalDevicePortPropertyTest {

	private SWTGefBot gefBot;

	@Before
	public void before() throws Exception {
		super.before();
		gefBot = new SWTGefBot();
	}

	@Override
	protected PortDescription prepareProvidesPort() {
		PortDescription portDesc = super.prepareProvidesPort();
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB_1, PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return portDesc;
	}

	@Override
	protected PortDescription prepareUsesPort() {
		PortDescription portDesc = super.prepareUsesPort();
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB_1, USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
		return portDesc;
	}

	@Override
	protected void prepareProvidesPortAdvanced() {
		super.prepareProvidesPortAdvanced();
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramProvidesPort(editor, USRP_UHD_1, USRP_PROVIDES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
	}

	@Override
	protected void prepareUsesPortAdvanced() {
		super.prepareUsesPortAdvanced();
		RHBotGefEditor editor = DiagramTestUtils.openNodeChalkboardDiagram(gefBot);
		SWTBotGefEditPart editPart = DiagramTestUtils.getDiagramUsesPort(editor, USRP_UHD_1, USRP_USES_PORT);
		DiagramTestUtils.getDiagramPortAnchor(editPart).select();
	}
}
