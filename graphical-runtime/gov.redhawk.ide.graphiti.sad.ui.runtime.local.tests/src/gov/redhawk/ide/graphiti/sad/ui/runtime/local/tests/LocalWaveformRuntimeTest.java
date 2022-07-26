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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.core.graphiti.sad.ui.ext.ComponentShape;
import gov.redhawk.ide.debug.impl.LocalScaWaveformImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import gov.redhawk.ide.swtbot.ErrorLogUtils;
import gov.redhawk.ide.swtbot.diagram.ComponentUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils.ComponentState;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.diagram.RHBotGefEditor;
import gov.redhawk.ide.swtbot.diagram.StartOrderUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public class LocalWaveformRuntimeTest extends AbstractGraphitiLocalWaveformRuntimeTest {

	private static final String EDITOR_NAME = "gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformSandboxEditor";

	public static final String NAMESPACE_LOCAL_WAVEFORM = "namespaceWF"; // Contains namespaced components

	private static final String HARD_LIMIT = "rh.HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";

	/**
	 * Test the most basic functionality / presence of the waveform sandbox editor (on a sandbox local waveform).
	 * IDE-1120 Check the type of editor that opens as well as its input
	 * IDE-1668 Correct diagram titles and tooltips
	 */
	@Test
	public void sandboxWaveformChalkboardTest() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());

		// IDE-1120
		IEditorPart editorPart = editor.getReference().getEditor(false);
		Assert.assertEquals("Waveform sandbox editor class is incorrect", EDITOR_NAME, editorPart.getClass().getName());
		IEditorInput editorInput = editorPart.getEditorInput();
		Assert.assertEquals("Waveform sandbox editor's input object is incorrect", ScaFileStoreEditorInput.class, editorInput.getClass());
		Assert.assertEquals("Waveform sandbox editor's input SCA object is incorrect", LocalScaWaveformImpl.class,
			((ScaFileStoreEditorInput) editorInput).getScaObject().getClass());

		// IDE-1668
		final String WAVEFORM_SDR_PATH = IdeSdrPreferences.getTargetSdrDomPath().append("waveforms").append(LOCAL_WAVEFORM).append(
			LOCAL_WAVEFORM + ".sad.xml").toOSString();
		Assert.assertEquals("Incorrect title", getWaveFormFullName(), editorPart.getTitle());
		Assert.assertEquals("Incorrect tooltip", WAVEFORM_SDR_PATH, editorPart.getTitleToolTip());
	}

	/**
	 * IDE-671
	 * Launch local waveform waveform diagram
	 * Verify existing component exists
	 * Add components to diagram from palette and TargetSDR
	 * Close diagram and re-open, verify newly added component exists
	 */
	@Test
	public void checkLocalWaveformComponents() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

		// verify existing component exists
		Assert.assertNotNull(editor.getEditPart(SIGGEN_1));

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT_1));
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, getWaveformPath(), HARD_LIMIT_1);

		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(HARD_LIMIT_1));

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragComponentFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		assertHardLimit(editor.getEditPart(HARD_LIMIT_1));
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, getWaveformPath(), HARD_LIMIT_1);

		// Open the chalkboard with components already launched
		editor.close();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.rhGefEditor(getWaveFormFullName());
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT_1));
	}

	/**
	 * IDE-1076 - Make sure start, stop, and release toolbar buttons appear/function during runtime
	 */
	@Test
	public void checkLocalWaveformToolbarButtons() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());

		// Add component to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT_1));
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, getWaveformPath(), HARD_LIMIT_1);

		// Confirm that all components are stopped
		DiagramTestUtils.waitForComponentState(bot, editor, SIGGEN, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(bot, editor, HARD_LIMIT, ComponentState.STOPPED);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Start Waveform").click();
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(gefBot, getWaveformPath(), SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStartedInExplorer(gefBot, getWaveformPath(), HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(gefBot, editor, SIGGEN, ComponentState.STARTED);
		DiagramTestUtils.waitForComponentState(gefBot, editor, HARD_LIMIT, ComponentState.STARTED);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Stop Waveform").click();
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(gefBot, getWaveformPath(), SIGGEN_1);
		ScaExplorerTestUtils.waitUntilResourceStoppedInExplorer(gefBot, getWaveformPath(), HARD_LIMIT_1);
		DiagramTestUtils.waitForComponentState(gefBot, editor, SIGGEN, ComponentState.STOPPED);
		DiagramTestUtils.waitForComponentState(gefBot, editor, HARD_LIMIT, ComponentState.STOPPED);

		editor.setFocus();
		bot.toolbarButtonWithTooltip("Release Waveform").click();
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, getWaveFormFullName());
	}

	/**
	 * IDE-1556
	 * Verify that user can open diagram editors for multiple running instances of the same waveform
	 *
	 * IDE-1018
	 * Verify action handler conflicts are not thrown when opening multiple waveforms
	 */
	@Test
	public void multipleWaveforms() {

		// Start with all editors closed
		bot.closeAllEditors();

		// Launch a second waveform in addition to the default launched via the abstract parent class
		// Need to perform some magic, since all of the utility methods assume only one instance of a waveform will be
		// launched at any one time
		SWTBotView scaExplorerView = bot.viewByTitle("REDHAWK Explorer");
		final SWTBotTreeItem sandbox = scaExplorerView.bot().tree().getTreeItem("Sandbox");
		final int origSize = sandbox.getNodes().size();

		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, LOCAL_WAVEFORM);

		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				if (sandbox.getNodes().size() > origSize) {
					return true;
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return "Second waveform never launched";
			}
		}, 10000);

		List<String> waveformFullNames = new ArrayList<String>();
		for (String node : sandbox.getNodes()) {
			if (node.contains(LOCAL_WAVEFORM)) {
				waveformFullNames.add(node);
			}
		}

		Assert.assertTrue("There should be two instances of the test waveform launched", waveformFullNames.size() == 2);
		for (String waveform : waveformFullNames) {
			ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, waveform, DiagramType.GRAPHITI_CHALKBOARD);
		}

		for (final String waveform : waveformFullNames) {
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					try {
						gefBot.gefEditor(waveform);
						return true;
					} catch (WidgetNotFoundException e) {
						return false;
					}
				}

				@Override
				public String getFailureMessage() {
					return "Editor failed to open for " + waveform;
				}
			});
		}

		String errorMsg = ErrorLogUtils.checkErrorLogForMessage(gefBot, "Conflicting handlers");
		Assert.assertNull(errorMsg, errorMsg);

		// Release the second waveform so that the aftertest assertion doesn't fail
		ScaExplorerTestUtils.releaseFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, waveformFullNames.get(1));
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, waveformFullNames.get(1));
	}

	/**
	 * IDE-671
	 * Launch local waveform that contains namespaced components
	 * Verify component exists
	 * Add components to diagram from palette and TargetSDR
	 * Close diagram and re-open, verify newly added component exists
	 */
	@Test
	public void checkLocalWaveformNamespaceComponents() {
		final String comp1 = "comp_1";
		final String comp2 = "comp_2";

		bot.closeAllEditors();

		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, NAMESPACE_LOCAL_WAVEFORM);
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, NAMESPACE_LOCAL_WAVEFORM);
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, NAMESPACE_LOCAL_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		setWaveFormFullName(ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, NAMESPACE_LOCAL_WAVEFORM));

		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

		// verify waveform elements exist
		Assert.assertNotNull(editor.getEditPart(comp1));
		Assert.assertNotNull(editor.getEditPart(comp2));
		SWTBotGefEditPart providesPort = DiagramTestUtils.getDiagramProvidesPort(editor, comp2);
		SWTBotGefEditPart providesAnchor = DiagramTestUtils.getDiagramPortAnchor(providesPort);
		Assert.assertTrue(providesAnchor.targetConnections().size() == 1);
	}

	/**
	 * IDE-928
	 * Check to make sure FindBy elements do not appear in the RHToolBar when in the Graphiti sandbox
	 */
	@Test
	public void checkFindByNotInSandbox() {
		RHBotGefEditor editor = gefBot.rhGefEditor(getWaveFormFullName());
		editor.setFocus();

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
	}

	/**
	 * Private helper method for {@link #checkComponentPictogramElements()} and
	 * {@link #checkComponentPictogramElementsWithAssemblyController()}.
	 * Asserts the given SWTBotGefEditPart is a HardLimit component and assembly controller
	 * @param gefEditPart
	 */
	@SuppressWarnings("restriction")
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
		Assert.assertNull("start order shape/text should be Null", StartOrderUtils.getStartOrderText(componentShape));

		// HardLimit only has the two ports
		List<EObject> usesPortStubs = componentShape.getUsesPortsContainerShape().getLink().getBusinessObjects();
		List<EObject> providesPortStubs = componentShape.getProvidesPortsContainerShape().getLink().getBusinessObjects();
		Assert.assertTrue(usesPortStubs.size() == 1 && providesPortStubs.size() == 1);

		// Both ports are of type dataDouble
		Assert.assertEquals(((UsesPortStub) usesPortStubs.get(0)).getUses().getInterface().getName(), "dataFloat");
		Assert.assertEquals(((ProvidesPortStub) providesPortStubs.get(0)).getProvides().getInterface().getName(), "dataFloat");
	}
}
