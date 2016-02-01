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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.ComponentUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public class ChalkboardTest extends AbstractGraphitiChalkboardTest {

	private RHBotGefEditor editor;

	/**
	 * IDE-884 Create the chalkboard waveform diagram. Add components to diagram from palette and TargetSDR.
	 * IDE-658 Open chalkboard with components already launched in the Sandbox.
	 * IDE-960 Show Console Feature.
	 * IDE-1187 Add namespaced component to chalkboard
	 */
	@Test
	public void checkChalkboardComponents() {
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, CHALKBOARD_PATH, HARD_LIMIT_1);

		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(HARD_LIMIT));

		// IDE-984 Make sure device cannot be added from Target SDR
		DiagramTestUtils.dragDeviceFromTargetSDRToDiagram(gefBot, editor, "GPP");
		Assert.assertNull("Unexpected device found in diagram", editor.getEditPart("GPP"));

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragComponentFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, HARD_LIMIT_1);

		// Open the chalkboard with components already launched
		editor.close();
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT));

		// Check 'Show Console' context menu option functionality
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		String[] components = { HARD_LIMIT, SIGGEN };

		for (final String component : components) {
			editor.getEditPart(component).select();
			editor.clickContextMenu("Show Console");
			final String[] consoleLabelTexts = { null };
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					SWTBotView consoleView = ViewUtils.getConsoleView(gefBot);
					String text = consoleView.bot().label(0).getText();
					if (text.matches(".*" + component + ".*")) {
						consoleLabelTexts[0] = text;
						return true;
					}
					return false;
				}

				@Override
				public String getFailureMessage() {
					return "Console view label never loaded for " + component;
				}
			});
			String consoleLabelText = consoleLabelTexts[0];
			Assert.assertNotNull("console label text for " + component, consoleLabelText);
			Assert.assertTrue("Console view for " + component + " did not display", consoleLabelText.matches(".*" + component + ".*"));
		}

		// Add namespaced component to the chalkboard
		String nameSpaceComp = "name.space.comp";
		DiagramTestUtils.addFromPaletteToDiagram(editor, nameSpaceComp, 200, 300);
		Assert.assertNotNull(editor.getEditPart(nameSpaceComp));
	}

	/**
	 * IDE-928 Check to make sure FindBy elements do not appear in the RHToolBar when in the Graphiti sandbox
	 * IDE-124 Check to make sure UsesDevice tool does not appear in the Palette when in the Graphiti sandbox
	 */
	@Test
	public void checkNotInSandbox() {

		// Check for Find Bys
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);
		String[] findByList = { FindByUtils.FIND_BY_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, FindByUtils.FIND_BY_EVENT_CHANNEL,
			FindByUtils.FIND_BY_FILE_MANAGER, FindByUtils.FIND_BY_SERVICE };

		for (String findByType : findByList) {
			try {
				DiagramTestUtils.addFromPaletteToDiagram(editor, findByType, 0, 0);
				Assert.fail(); // The only way to get here is if the FindBy type appears in the Palette
			} catch (WidgetNotFoundException e) {
				Assert.assertTrue(e.getMessage(), e.getMessage().matches(".*" + findByType + ".*"));
			}
		}

		// Check for Uses Devices
		String usesDevice = "Use FrontEnd Tuner Device";
		try {
			DiagramTestUtils.addFromPaletteToDiagram(editor, usesDevice, 0, 0);
			Assert.fail(); // The only way to get here is if the FindBy type appears in the Palette
		} catch (WidgetNotFoundException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().matches(".*" + usesDevice + ".*"));
		}
	}

	/**
	 * IDE-953
	 * Verifies that when the user drags a component to the diagram of a particular implementation
	 * that it in fact the correct implementation was added.
	 */
	@Test
	public void checkCorrectImplementationAddedToDiagram() {
		editor = DiagramTestUtils.openChalkboardDiagram(gefBot);

		// Add two components to diagram from palette
		final String sourceComponent = SIGGEN + " (python)";
		final String targetComponent = HARD_LIMIT + " (java)";
		DiagramTestUtils.addFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, targetComponent, 300, 0);

		// verify sigGen is python
		SWTBotGefEditPart sigGenEditPart = editor.getEditPart(SIGGEN_1);
		// get graphiti shape
		ComponentShape sigGenComponentShape = (ComponentShape) sigGenEditPart.part().getModel();
		// Grab the associated business object and confirm it is a SadComponentInstantiation
		SadComponentInstantiation sigGenSadComponentInstantiation = (SadComponentInstantiation) DUtil.getBusinessObject(sigGenComponentShape);
		Assert.assertEquals("SigGen implementation was not python", "python", sigGenSadComponentInstantiation.getImplID());

		// verify hardLimit is java
		SWTBotGefEditPart hardLimitEditPart = editor.getEditPart(HARD_LIMIT_1);
		// get graphiti shape
		ComponentShape hardLimitComponentShape = (ComponentShape) hardLimitEditPart.part().getModel();
		// Grab the associated business object and confirm it is a SadComponentInstantiation
		SadComponentInstantiation hardLimitSadComponentInstantiation = (SadComponentInstantiation) DUtil.getBusinessObject(hardLimitComponentShape);
		Assert.assertEquals("HardLimit implementation was not java", "java", hardLimitSadComponentInstantiation.getImplID());
	}

	/**
	 * Private helper method for {@link #checkComponentPictogramElements()} and
	 * {@link #checkComponentPictogramElementsWithAssemblyController()}.
	 * Asserts the given SWTBotGefEditPart is a HardLimit component and assembly controller
	 * @param gefEditPart
	 */
	private static void assertHardLimit(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti component shape
		ComponentShape componentShape = (ComponentShape) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a SadComponentInstantiation
		Object bo = DUtil.getBusinessObject(componentShape);
		Assert.assertTrue("business object should be of type SadComponentInstantiation", bo instanceof SadComponentInstantiation);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", HARD_LIMIT, ComponentUtils.getOuterText(componentShape).getValue());
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), ComponentUtils.getInnerText(componentShape).getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", ComponentUtils.getLollipop(componentShape));
		Assert.assertNull("start order shape/text should be null", ComponentUtils.getStartOrderText(componentShape));

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);

		// Both ports are of type dataFloat
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataFloat");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataFloat");
	}
}
