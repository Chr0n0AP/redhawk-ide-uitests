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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.ComponentUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public class StartOrderTest extends AbstractGraphitiTest {

	private String waveformName;

	/**
	 * Test ability to set and change start order.
	 * Assembly controller should always have a start order of zero.
	 */
	@Test
	public void changeStartOrderTest() {
		waveformName = "Change_Start_Order";
		final String componentOne = "HardLimit";
		final String componentTwo = "SigGen";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add components to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, componentOne, 100, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, componentTwo, 100, 150);

		// Get component objects
		SadComponentInstantiation componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		SadComponentInstantiation componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);

		// Initial assertion
		MenuUtils.save(editor);
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentOne));
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, componentOneObj.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, componentTwoObj.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentOne, "0", true));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentTwo, "1", false));

		// Decrement start order test - Assert new start orders and new assembly controller assignment
		ComponentUtils.decrementStartOrder(editor, componentOne);
		MenuUtils.save(editor);
		componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentOne));
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentTwo));
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, componentOneObj.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, componentTwoObj.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentOne, "1", false));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentTwo, "0", true));

		// Increment start order test - Assert new start orders and new assembly controller assignment
		ComponentUtils.incrementStartOrder(editor, componentOne);
		MenuUtils.save(editor);
		componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentOne));
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentTwo));
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, componentOneObj.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, componentTwoObj.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentOne, "0", true));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentTwo, "1", false));

		// Set a new assembly controller - Assert new start orders and new assembly controller assignment
		ComponentUtils.setAsAssemblyController(editor, componentTwo);
		MenuUtils.save(editor);
		componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentOne));
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentTwo));
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, componentOneObj.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, componentTwoObj.getStartOrder());
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentOne, "1", false));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentTwo, "0", true));
	}

	/**
	 * IDE-721
	 * Start order should be treated as an optional field, and should not cause errors when null
	 * Similar steps changeStartOrderTest(), but includes a component without a defined start order
	 */
	@Test
	public void changeStartOrderWithNullTest() {
		waveformName = "Null_Start_Order";
		final String compNoStartOrder = "SigGen";
		final String componentOne = "HardLimit";
		final String componentTwo = "DataConverter";

		// Create a new waveform with an assembly controller
		// ...when assembly controllers are added from the new project wizard they don't have a start order
		// ...this is kind of a hack
		WaveformUtils.createNewWaveformWithAssemblyController(gefBot, waveformName, compNoStartOrder);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add additional components to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, componentOne, 10,  150);
		DiagramTestUtils.addFromPaletteToDiagram(editor, componentTwo, 200, 10);
		// Get component objects
		SadComponentInstantiation compNoStartOrderObj = DiagramTestUtils.getComponentObject(editor, compNoStartOrder);
		SadComponentInstantiation componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		SadComponentInstantiation componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);

		// Initial assertion
		MenuUtils.save(editor);
		Assert.assertNull("Start Order should be null", compNoStartOrderObj.getStartOrder());
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, compNoStartOrder));
		Assert.assertNotNull(componentOne + " exists on diagram", componentOneObj);
		Assert.assertNotNull(componentTwo + " exists on diagram", componentTwoObj);
		Assert.assertEquals("Start order is incorrect", BigInteger.ONE, componentOneObj.getStartOrder());
		Assert.assertEquals("Start order is incorrect", 2, componentTwoObj.getStartOrder().intValue());

		// Decrement start order of component one - Assert new start orders and new assembly controller assignment
		ComponentUtils.decrementStartOrder(editor, componentOne);
		MenuUtils.save(editor);
		compNoStartOrderObj = DiagramTestUtils.getComponentObject(editor, compNoStartOrder);
		componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, compNoStartOrder));
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentOne));
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentTwo));

		Assert.assertEquals("Model object start order is incorrect", 2, componentOneObj.getStartOrder().intValue());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, componentTwoObj.getStartOrder());

		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, compNoStartOrder, "", true));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentOne, "2", false));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentTwo, "1", false));

		// Increment start order test - Assert new start orders and new assembly controller assignment
		ComponentUtils.incrementStartOrder(editor, componentOne);
		MenuUtils.save(editor);
		compNoStartOrderObj = DiagramTestUtils.getComponentObject(editor, compNoStartOrder);
		componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertNull("Start Order should be null", compNoStartOrderObj.getStartOrder());
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, compNoStartOrder));
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentOne));
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentTwo));

		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, componentOneObj.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", 2, componentTwoObj.getStartOrder().intValue());

		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, compNoStartOrder, "", true));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentOne, "1", false));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentTwo, "2", false));

		// Set a new assembly controller - Assert new start orders and new assembly controller assignment
		ComponentUtils.setAsAssemblyController(editor, componentTwo);
		MenuUtils.save(editor);
		compNoStartOrderObj = DiagramTestUtils.getComponentObject(editor, compNoStartOrder);
		componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertNull("Start Order should be null", compNoStartOrderObj.getStartOrder());
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, compNoStartOrder));
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentOne));
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentTwo));

		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, componentOneObj.getStartOrder());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ZERO, componentTwoObj.getStartOrder());

		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, compNoStartOrder, "", false));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentOne, "1", false));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentTwo, "0", true));

		// Reset the object with (start order == null) to be the assembly controller - Assert new start orders and new
		// assembly controller assignment
		ComponentUtils.setAsAssemblyController(editor, compNoStartOrder);
		MenuUtils.save(editor);
		compNoStartOrderObj = DiagramTestUtils.getComponentObject(editor, compNoStartOrder);
		componentOneObj = DiagramTestUtils.getComponentObject(editor, componentOne);
		componentTwoObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertNull("Start Order should be null", compNoStartOrderObj.getStartOrder());
		Assert.assertTrue("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, compNoStartOrder));
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentOne));
		Assert.assertFalse("Assembly controller was not updated correctly", ComponentUtils.isAssemblyController(gefBot, editor, componentTwo));

		Assert.assertEquals("Model object start order is incorrect", 2, componentOneObj.getStartOrder().intValue());
		Assert.assertEquals("Model object start order is incorrect", BigInteger.ONE, componentTwoObj.getStartOrder());

		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, compNoStartOrder, "", true));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentOne, "2", false));
		Assert.assertTrue("Graphical start order object is incorrect", ComponentUtils.correctStylingAndValue(editor, componentTwo, "1", false));
	}

	/**
	 * IDE-695
	 * When using the Overview Tab to change the assembly controller, the start order icons
	 * for all components should update the next time the diagram is viewed
	 */
	@Test
	public void setAssemblyControllerFromOverview() {
		waveformName = "AC_From_Overview";
		final String[] component = { "DataConverter", "HardLimit", "SigGen" };

		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);

		// Add components to diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, component[0], 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, component[1], 250, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, component[2], 300, 200);

		// Check initial assembly controller
		MenuUtils.save(editor);
		String assemblyController = component[0];
		Assert.assertTrue(ComponentUtils.isAssemblyController(gefBot, editor, assemblyController));

		// Change assembly controller to last component in list via Overview Tab
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		editor.bot().ccomboBoxWithLabel("Controller:").setSelection(component.length - 1);
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// Confirm start order numbers have adjusted appropriately
		for (int i = 0; i < component.length - 1; i++) {
			Assert.assertTrue("Start order is incorrect for " + component[i],  ComponentUtils.correctStylingAndValue(editor, component[i], Integer.toString(i + 1), false));
		}

		// Check new assembly controller
		MenuUtils.save(editor);
		assemblyController = component[component.length - 1];
		Assert.assertTrue(ComponentUtils.correctStylingAndValue(editor, assemblyController, "0", true));
		Assert.assertTrue(ComponentUtils.isAssemblyController(gefBot, editor, assemblyController));
	}

	/**
	 * IDE-695
	 * Checks that the overview tab assembly controller combo updates correctly when a new assembly controller is set in
	 * the Graphiti diagram editor
	 */
	@Test
	public void setAssemblyControllerFromDiagramChangesOverview() {
		waveformName = "AC_From_Diagram";
		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		final String[] component = { "DataConverter", "HardLimit", "SigGen" };

		// Add components to diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, component[0], 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, component[1], 250, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, component[2], 300, 200);

		// Check initial assembly controller
		MenuUtils.save(editor);
		String assemblyController = component[0];
		Assert.assertTrue(ComponentUtils.isAssemblyController(gefBot, editor, assemblyController));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		Assert.assertTrue(editor.bot().ccomboBoxWithLabel("Controller:").getText().contains(assemblyController));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);

		// Change start order via context menu
		assemblyController = component[component.length - 1];
		ComponentUtils.setAsAssemblyController(editor, assemblyController);

		// Confirm start order numbers have adjusted appropriately
		for (int i = 0; i < component.length - 1; i++) {
			Assert.assertTrue(ComponentUtils.correctStylingAndValue(editor, component[i], Integer.toString(i + 1), false));
		}

		// Check new assembly controller
		MenuUtils.save(editor);
		Assert.assertTrue(ComponentUtils.correctStylingAndValue(editor, assemblyController, "0", true));
		Assert.assertTrue(ComponentUtils.isAssemblyController(gefBot, editor, assemblyController));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);
		Assert.assertTrue(editor.bot().ccomboBoxWithLabel("Controller:").getText().contains(assemblyController));
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.DIAGRAM_TAB);
	}

	/**
	 * Checks to confirm that start order matches the order in which components are dragged from the palette to
	 * the diagram and that the assembly controller is the first component to be added.
	 */
	@Test
	public void checkStartOrderSequence() {
		waveformName = "Start_Order_Seq";
		final String[] component = { "DataConverter", "HardLimit", "SigGen" };

		WaveformUtils.createNewWaveform(gefBot, waveformName, null);
		RHBotGefEditor editor = gefBot.rhGefEditor(waveformName);
		// Add and check start order
		int xCoord = 0;
		for (int i = 0; i < component.length; i++) {
			DiagramTestUtils.addFromPaletteToDiagram(editor, component[i], xCoord, 0);
			Assert.assertEquals(i, ComponentUtils.getStartOrder(editor, component[i]));
			xCoord += 250;
		}
		// Check first added is assembly controller
		Assert.assertTrue(ComponentUtils.isAssemblyController(gefBot, editor, component[0]));
	}


}
