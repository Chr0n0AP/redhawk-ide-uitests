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
package gov.redhawk.ide.ui.tests.runtime.multiout;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class MultiOutConnectionTest extends AbstractMultiOutPortTest {

	@Override
	public void after() throws CoreException {
		// Cleanup the DataConverter we launched
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, new String[] { "Sandbox" }, "Chalkboard");
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(bot, new String[] { "Sandbox" }, "Chalkboard");

		super.after();
	}

	/**
	 * For this test, we will be content to confirm that the connection ID matches the tuner allocation ID
	 */
	@Override
	@Test
	public void multiOutPortSingleTunerTest() {
		SWTBotTreeItem providesPort = launchComponent();

		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("allocation1", "101.5");
		waitForTunerAllocation(0);

		// Select the device uses port and the components provides port, and then click connect in the context menu
		SWTBot explorerViewBot = ScaExplorerTestUtils.showScaExplorerView(bot);
		explorerViewBot.tree().select(getUsesPort(), providesPort).contextMenu(getContextMenu()).click();

		// Verify that the expected behavior occurred
		testActionResults(0);
	}

	@Test
	public void multiOutPortMultiTunerTest() {
		SWTBotTreeItem providesPort = launchComponent();

		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("firstAllocation", "101.5");
		waitForTunerAllocation(0);

		// Allocate the second tuner, important that it is alphabetically later than the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("secondAllocation", "88.5");
		waitForTunerAllocation(1);

		// Select the device uses port and the components provides port, and then click connect in the context menu
		SWTBot explorerViewBot = ScaExplorerTestUtils.showScaExplorerView(bot);
		explorerViewBot.tree().select(getUsesPort(), providesPort).contextMenu(getContextMenu()).click();

		// Complete the multi-out connection dialog
		SWTBotShell multiOutShell = bot.shell(WIZARD_TITLE);
		multiOutShell.bot().list().select(1);
		multiOutShell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(multiOutShell));

		// Verify that the expected behavior occurred
		testActionResults(1);
	}

	/**
	 * IDE-2044 - Test the multi-out connection dialog fields & validation
	 */
	@Test
	public void multiOutConnectDialogTest() {
		SWTBotTreeItem providesPort = launchComponent();

		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("firstAllocation", "101.5");
		waitForTunerAllocation(0);

		// Make a connection using the first allocation ID
		SWTBot explorerViewBot = ScaExplorerTestUtils.showScaExplorerView(bot);
		explorerViewBot.tree().select(getUsesPort(), providesPort).contextMenu(getContextMenu()).click();
		waitForConnection(0);

		// Allocate the second tuner, important that it is alphabetically later than the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("secondAllocation", "88.5");
		waitForTunerAllocation(1);

		// Initiate another connection to the component
		explorerViewBot.tree().select(getUsesPort(), providesPort).contextMenu(getContextMenu()).click();

		// Wait for the connection dialog to open
		SWTBotShell multiOutShell = bot.shell(WIZARD_TITLE);
		SWTBot dialogBot = multiOutShell.bot();

		// Test widget enabled state
		Assert.assertTrue("'Select ID' radio should be selected by default", dialogBot.radio("Select an existing connection ID").isSelected());
		Assert.assertTrue("Select ID tree should be enabled by default", dialogBot.list().isEnabled());
		Assert.assertFalse("Custom ID text field should be disabled by default", dialogBot.text().isEnabled());

		// Make sure the dialog can't finish when selecting an 'IN USE' ID
		dialogBot.list().select(getAllocationId(0) + " (IN USE)");
		Assert.assertFalse("Dialog should not be allowed to finish if an 'IN USE' connection ID is selected", dialogBot.button("OK").isEnabled());
		dialogBot.list().select(getAllocationId(1));
		Assert.assertTrue("OK button did not re-enable", dialogBot.button("OK").isEnabled());

		// Activate the custom ID input section
		dialogBot.radio("Specify connection ID").click();
		Assert.assertFalse("Select ID tree should be disabled", dialogBot.list().isEnabled());
		Assert.assertTrue("Custom ID text field should be enabled", dialogBot.text().isEnabled());

		// Clear the text field and test that OK button disables
		dialogBot.text().setText("");
		Assert.assertFalse("Dialog should not be able to finish if the custom ID field is enabled and blank", dialogBot.button("OK").isEnabled());

		// Enter a custom ID and make sure the connection gets created
		String connectionId = "connection1";
		dialogBot.text().setText(connectionId);
		Assert.assertTrue("OK button did not re-enable", dialogBot.button("OK").isEnabled());
		dialogBot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(multiOutShell));

		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				String expectedConnectionId = connectionId;
				for (SWTBotTreeItem connectionItem : getUsesPort().getItems()) {
					if (expectedConnectionId.equals(connectionItem.getText())) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return "Connection node never appeared, or had the wrong ID";
			}
		}, 12000);
	}

	/**
	 * IDE-2041 - Test using the connect wizard with a multi-out port
	 */
	@Test
	public void connectWizardTest() {
		launchComponent();

		// Allocate the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("firstAllocation", "101.5");
		waitForTunerAllocation(0);

		testWithSingleTuner();

		// Allocate the second tuner, important that it is alphabetically later than the first tuner
		ScaExplorerTestUtils.allocate(bot, DEVICE_PARENT_PATH, RX_DIGITIZER_SIM_1);
		completeAllocateWizard("secondAllocation", "88.5");
		waitForTunerAllocation(1);

		testWithSecondTuner();
	}

	private void testWithSingleTuner() {
		SWTBot shellBot = initConnectWizard();

		// Gather widgets
		SWTBotRadio selectRadio = shellBot.radio("Select an existing connection ID");
		SWTBotCombo idCombo = shellBot.comboBox();
		SWTBotText idText = shellBot.text();
		SWTBotButton finishButton = shellBot.button("Finish");

		// Confirm default state is as expected
		Assert.assertTrue("'Select an ID radio button' is not selected by default", selectRadio.isSelected());
		Assert.assertTrue("ID combo should be enabled by default", idCombo.isEnabled());
		Assert.assertFalse("ID text field should be disabled by default", idText.isEnabled());
		Assert.assertTrue("Finish should be enabled by default", finishButton.isEnabled());

		// Connection should default to the first unused connection ID alphabetically
		Assert.assertEquals("Combo input in incorrect", getAllocationId(0), idCombo.getText());

		// Finish and make sure the connection node appears
		finishButton.click();
		waitForConnection(0);
	}

	private void testWithSecondTuner() {
		SWTBot shellBot = initConnectWizard();

		// Gather widgets
		SWTBotCombo idCombo = shellBot.comboBox();
		SWTBotText idText = shellBot.text();
		SWTBotRadio inputRadio = shellBot.radio("Specify connection ID");
		SWTBotButton finishButton = shellBot.button("Finish");

		// Check that the combo defaults to the second ID, as the first is now in use
		Assert.assertEquals("Combo input in incorrect", getAllocationId(1), idCombo.getText());

		/* Select the first connection id, make sure:
		 *  - it is marked "IN USE",
		 *  - the finish button is disabled,
		 *  - the correct error message is displayed
		 */
		idCombo.setSelection(getAllocationId(0) + " (IN USE)");
		Assert.assertFalse("Finish button did not disable", finishButton.isEnabled());
		Assert.assertEquals("Error message did not appear", " Selected connection ID is already in use", shellBot.text(1).getText());

		/*
		 * Select the second (available) connection id, make sure:
		 * - the finish is enabled
		 * - the error message clears
		 */
		idCombo.setSelection(getAllocationId(1));
		Assert.assertTrue("Finish button did not enable", finishButton.isEnabled());
		Assert.assertTrue("Error message did not clear as expected", shellBot.text(1).getText().startsWith("Select the source"));

		/*
		 * Select the 'input' radio, make sure:
		 * - the combo field disables
		 * - the text field enables
		 * - there is a default value in the text field
		 * - the finish button remains enabled
		 */
		inputRadio.click();
		Assert.assertFalse("ID Combo did not disable", idCombo.isEnabled());
		Assert.assertTrue("ID Text field did not enable", idText.isEnabled());
		Assert.assertFalse("ID Text field should not be empty by default", idText.getText().isEmpty() || idText.getText() == null);
		Assert.assertTrue("Finish button did not enable", finishButton.isEnabled());

		/* Clear the text box, make sure:
		 * - the finish button disables
		 * - the correct error message is displayed
		 */
		idText.setText("");
		Assert.assertFalse("Finish button did not disable", finishButton.isEnabled());
		Assert.assertEquals("Error message did not appear", " Must enter a valid connection ID", shellBot.text(1).getText());

		/*
		 * Enter a value in the text box, make sure:
		 * - the finish button enables
		 * - the error message clears
		 */
		idText.setText("connection1");
		Assert.assertTrue("Finish button did not enable", finishButton.isEnabled());
		Assert.assertTrue("Error message did not clear as expected", shellBot.text(1).getText().startsWith("Select the source"));

		// click finish and make sure the connection node appears
		finishButton.click();
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				String expectedConnectionId = "connection1";
				for (SWTBotTreeItem connectionItem : getUsesPort().getItems()) {
					if (expectedConnectionId.equals(connectionItem.getText())) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return "Connection node never appeared, or had the wrong ID";
			}
		}, 12000);
	}

	private SWTBot initConnectWizard() {
		// Right click on uses port and click connect to bring up the ConnectWizard
		getUsesPort().contextMenu("Connect").click();
		// get the shell
		SWTBotShell connectShell = bot.shell("Connect");
		SWTBot shellBot = connectShell.bot();
		// select the correct node in both tables
		shellBot.tree(0).getTreeItem("dataShort_out").select();
		shellBot.tree(1).expandNode("Sandbox", "Chalkboard", "DataConverter_1", "dataShort_in").select();
		return shellBot;
	}

	/**
	 * Launch a component for the device to connect to
	 * @return {@link SWTBotTreeItem} for the components targeted provides port
	 */
	private SWTBotTreeItem launchComponent() {
		final String componentName = "rh.DataConverter";
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, componentName, "cpp");
		return ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, new String[] { "Sandbox", "Chalkboard", "DataConverter_1" }, "dataShort_in");
	}

	@Override
	protected String getContextMenu() {
		return "Connect";
	}

	@Override
	protected void testActionResults(int allocationIndex) {
		waitForConnection(allocationIndex);
	}

	@Override
	protected void closeView() {
		// PASS - Nothing to do
	}
}
