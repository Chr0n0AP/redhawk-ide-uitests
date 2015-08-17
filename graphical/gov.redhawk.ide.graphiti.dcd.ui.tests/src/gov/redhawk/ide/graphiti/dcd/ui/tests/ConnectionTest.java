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
package gov.redhawk.ide.graphiti.dcd.ui.tests;

import java.util.List;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class ConnectionTest extends AbstractGraphitiTest {

	private RHBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String DEVICE_STUB = "DeviceStub";
	private static final String SERVICE_STUB = "ServiceStub";

	/**
	 * IDE-985
	 * Users should be able to create connections between devices/services in the Graphiti diagram
	 */
	@Test
	public void connectFeatureTest() {
		projectName = "Connection-Test";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.rhGefEditor(projectName);
		editor.setFocus();

		// Add to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 200);

		// Get component edit parts and container shapes
		SWTBotGefEditPart gppEditPart = editor.getEditPart(GPP);
		ContainerShape gppShape = (ContainerShape) gppEditPart.part().getModel();
		SWTBotGefEditPart deviceStubEditPart = editor.getEditPart(DEVICE_STUB);
		ContainerShape deviceStubShape = (ContainerShape) deviceStubEditPart.part().getModel();

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB, "dataDouble_in");

		// Confirm that no connections currently exist
		Diagram diagram = DUtil.findDiagram(gppShape);
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertFalse(editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// Attempt to make an illegal connection and confirm that it was not actually made
		SWTBotGefEditPart illegalTarget = DiagramTestUtils.getDiagramUsesPort(editor, DEVICE_STUB);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, illegalTarget);
		Assert.assertTrue("Illegal connection should not have been drawn", diagram.getConnections().isEmpty());

		// Draw the connection and save
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		MenuUtils.save(editor);

		// Test to make sure connection was made correctly
		Assert.assertFalse("Connection should exist", diagram.getConnections().isEmpty());

		Connection connection = DUtil.getIncomingConnectionsContainedInContainerShape(deviceStubShape).get(0);

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) providesEditPart.part().getModel()));

		// Check dcd.xml new for connection
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The dcd.xml should include a new connection", editorText.matches("(?s).*<connectinterface id=\"connection_1\">.*"));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// Delete connection (IDE-687 - Users need to be able to delete connections)
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertFalse("Source connections should not be empty for this test", sourceConnections.isEmpty());
		for (SWTBotGefConnectionEditPart con : sourceConnections) {
			DiagramTestUtils.deleteFromDiagram(editor, con);
		}
		MenuUtils.save(editor);
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Source connections should be empty, all connections were deleted", sourceConnections.isEmpty());
		Assert.assertTrue("All connections should have been deleted", diagram.getConnections().isEmpty());
	}
	
	/**
	 * IDE-1132
	 * Test that connection decorators are drawn for incompatible connections
	 */
	@Test
	public void incompatibleConnectionTest() {
		projectName = "incompat_connect_nodes";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);

		// Add devices and a service to diagram from palette
		editor = gefBot.rhGefEditor(projectName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, DEVICE_STUB, 300, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SERVICE_STUB, 300, 300);

		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, GPP);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, DEVICE_STUB, "dataDouble_in");

		// Draw incompatible connection and confirm error decorator exists
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, providesEditPart);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not added", connections.size() == 1);

		Connection connection = (Connection) connections.get(0).part().getModel();
		Assert.assertTrue("Error decorator should have been added", connection.getConnectionDecorators().size() == 2);
		connections.get(0).select();
		editor.clickContextMenu("Delete");
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not removed", connections.size() == 0);
		
		// Test incompatible connection to component supported interface (DEVICE)
		SWTBotGefEditPart lollipopEditPart = DiagramTestUtils.getComponentSupportedInterface(editor, DEVICE_STUB);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, lollipopEditPart);
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not added", connections.size() == 1);
		connection = (Connection) connections.get(0).part().getModel();
		Assert.assertTrue("Error decorator should have been added", connection.getConnectionDecorators().size() == 2);
		
		connections.get(0).select();
		editor.clickContextMenu("Delete");
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not removed", connections.size() == 0);
		
		// Test incompatible connection to component supported interface (SERVICE)
		lollipopEditPart = DiagramTestUtils.getComponentSupportedInterface(editor, SERVICE_STUB);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, usesEditPart, lollipopEditPart);
		connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);
		Assert.assertTrue("Connection was not added", connections.size() == 1);
		connection = (Connection) connections.get(0).part().getModel();
		Assert.assertTrue("Error decorator should have been added", connection.getConnectionDecorators().size() == 2);
	}

}
