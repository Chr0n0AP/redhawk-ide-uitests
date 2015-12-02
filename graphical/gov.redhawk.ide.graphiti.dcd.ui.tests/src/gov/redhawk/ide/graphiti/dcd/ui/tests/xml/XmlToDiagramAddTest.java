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
package gov.redhawk.ide.graphiti.dcd.ui.tests.xml;

import java.util.List;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.tests.ComponentDescription;
import gov.redhawk.ide.graphiti.ui.tests.xml.AbstractXmlToDiagramAddTest;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.RHSWTGefBot;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

/**
 * Test class that deals with adding elements to the sad.xml and making sure they appear correctly in the diagram
 */
public class XmlToDiagramAddTest extends AbstractXmlToDiagramAddTest {

	private RHBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String DEVICE_STUB = "DeviceStub";

	private RHSWTGefBot gefBot = new RHSWTGefBot();

	/**
	 * IDE-994
	 * Add a connection to the diagram via the dcd.xml
	 */
	@Test
	public void addConnectionInXmlTest() {
		projectName = "Add_Connection_Xml";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		// Add device to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 200, 0);
		MenuUtils.save(editor);

		// Confirm that no connections currently exist
		SWTBotGefEditPart gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, gppUsesEditPart);
		Assert.assertTrue("No connections should exist", sourceConnections.isEmpty());

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		String newConnection = "</partitioning> <connections> <connectinterface id=\"connection_1\"> "
			+ "<usesport> <usesidentifier>propEvent</usesidentifier> <componentinstantiationref refid=\"GPP_1\"/> "
			+ "</usesport> <providesport> <providesidentifier>dataDouble_in</providesidentifier> "
			+ "<componentinstantiationref refid=\"DeviceStub_1\"/> </providesport> </connectinterface> </connections>";
		editorText = editorText.replace("</partitioning>", newConnection);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		gppUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		SWTBotGefEditPart deviceStubProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB, "dataDouble_in");

		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, gppUsesEditPart);
		Assert.assertFalse("Connection should exist", sourceConnections.isEmpty());

		Connection connection = (Connection) sourceConnections.get(0).part().getModel();
		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) gppUsesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort,
			DUtil.getBusinessObject((ContainerShape) deviceStubProvidesEditPart.part().getModel()));
	}

	@Override
	protected ComponentDescription getComponentADescription() {
		ComponentDescription description = new ComponentDescription("DeviceStub", new String[0], new String[] { "dataFloat_out" });
		description.setKey("path", "devices");
		return description;
	}

	@Override
	protected ComponentDescription getComponentBDescription() {
		ComponentDescription description = new ComponentDescription("PortSupplierService", new String[] { "dataFloat_in" }, new String[0]);
		description.setKey("path", "services");
		return description;
	}

	@Override
	protected RHBotGefEditor createEditor(String name) {
		NodeUtils.createNewNodeProject(gefBot, name, DOMAIN_NAME);
		return gefBot.rhGefEditor(name);
	}

	@Override
	protected EditorType getEditorType() {
		return EditorType.DCD;
	}
}
