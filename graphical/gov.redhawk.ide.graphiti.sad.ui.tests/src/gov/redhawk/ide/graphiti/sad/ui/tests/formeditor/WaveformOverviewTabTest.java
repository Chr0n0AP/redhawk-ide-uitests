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
package gov.redhawk.ide.graphiti.sad.ui.tests.formeditor;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.EditorUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.finder.RHBot;
import gov.redhawk.ide.swtbot.finder.widgets.RHBotFormText;
import gov.redhawk.ide.swtbot.finder.widgets.RHBotSection;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.util.DceUuidUtil;

public class WaveformOverviewTabTest extends AbstractWaveformTabTest {

	private static final String PROJECT_NAME = "Waveform01";

	@Override
	protected String getProjectName() {
		return PROJECT_NAME;
	}

	@Override
	protected String getTabName() {
		return DiagramTestUtils.OVERVIEW_TAB;
	}

	@Test
	public void id() {
		// Test generating a new ID
		String oldID = editorBot.textWithLabel("ID:").getText();
		editorBot.button("Generate").click();
		String newID = bot.textWithLabel("ID:").getText();
		Assert.assertNotEquals(oldID, newID);
		Assert.assertTrue("Not valid DCE UUID", DceUuidUtil.isValid(newID));
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals(newID, sad.getId());

		// Test an invalid ID
		editorBot.textWithLabel("ID:").setText("DCE");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);

		// Back to a valid ID
		editorBot.textWithLabel("ID:").setText("");
		editorBot.textWithLabel("ID:").typeText("DCE:8745512e-cdaf-41ad-93e4-a404d5e8e6db");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("DCE:8745512e-cdaf-41ad-93e4-a404d5e8e6db", sad.getId());

		// Test empty ID
		editorBot.textWithLabel("ID:").setText("");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
	}

	@Test
	public void name() {
		editorBot.textWithLabel("Name:").setText("");
		editorBot.textWithLabel("Name:").typeText("foo");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("foo", sad.getName());
	}

	@Test
	public void version() {
		editorBot.textWithLabel("Version:").typeText("1.2.3");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("1.2.3", sad.getVersion());

		editorBot.textWithLabel("Version:").setText("");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals(null, sad.getVersion());
	}

	@Test
	public void controller() {
		editorBot.ccomboBoxWithLabel("Controller:").setSelection("DataConverter_1");
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("DataConverter_1", sad.getAssemblyController().getComponentInstantiationRef().getRefid());

		ScaModelCommand.execute(sad, new ScaModelCommand() {
			@Override
			public void execute() {
				sad.setAssemblyController(null);
			}
		});
		EditorUtils.assertEditorTabInvalid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);

		editorBot.ccomboBoxWithLabel("Controller:").setSelection("SigGen_1");
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("SigGen_1", sad.getAssemblyController().getComponentInstantiationRef().getRefid());
	}

	/**
	 * IDE-1240
	 */
	@Test
	public void description() {
		editorBot.textWithLabel("Description:").typeText("A test description");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("A test description", sad.getDescription());

		editorBot.textWithLabel("Description:").setText("");
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertNull(sad.getDescription());
	}

	@Test
	public void waveformOptions() {
		RHBot rhEditorBot = new RHBot(editor.bot());
		RHBotSection section = rhEditorBot.section("Waveform Options");
		section.expand();

		// Test adding a new option
		editorBot.button("Add", 0).click();
		SWTBotTable table = editorBot.table(0);
		Assert.assertEquals(1, table.rowCount());
		Assert.assertEquals("Option Name", table.getTableItem(0).getText(0));
		Assert.assertEquals("Option Value", table.getTableItem(0).getText(1));
		Assert.assertNotNull(sad.getOptions());
		Assert.assertEquals(1, sad.getOptions().getOption().size());
		Assert.assertEquals("Option Name", sad.getOptions().getOption().get(0).getName());
		Assert.assertEquals("Option Value", sad.getOptions().getOption().get(0).getValue());

		// Test editing an option
		table.select(0);
		StandardTestActions.selectComboListFromCell(rhEditorBot, table, 0, 0, "STOP_TIMEOUT", false);
		editorBot.sleep(600);
		Assert.assertEquals("STOP_TIMEOUT", sad.getOptions().getOption().get(0).getName());

		table.select(0);
		StandardTestActions.writeToCell(editorBot, table, 0, 1, "True", false);
		editorBot.sleep(600);
		Assert.assertEquals("True", sad.getOptions().getOption().get(0).getValue());

		// Test removing an option
		editorBot.button("Remove", 0).click();
		Assert.assertEquals(0, table.rowCount());
		Assert.assertNull(sad.getOptions());
	}

	@Test
	public void externalPorts() {
		editorBot.button("Add", 1).click();
		SWTBotShell shell = bot.shell("Add external Port");

		shell.bot().table(0).select("SigGen_1");
		shell.bot().waitUntil(Conditions.tableHasRows(shell.bot().table(1), 2));
		Assert.assertFalse(shell.bot().button("Finish").isEnabled());

		shell.bot().table(0).select("DataConverter_1");
		shell.bot().waitUntil(Conditions.tableHasRows(shell.bot().table(1), 12));
		Assert.assertFalse(shell.bot().button("Finish").isEnabled());

		shell.bot().table(1).select("dataDouble_in");
		shell.bot().waitUntil(Conditions.widgetIsEnabled(shell.bot().button("Finish")));

		shell.bot().textWithLabel("Description (Optional):").typeText("my desc");

		shell.bot().button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		SWTBotTable table = editorBot.table(1);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals(1, table.rowCount());
		Assert.assertEquals("DataConverter_1", table.getTableItem(0).getText(0));
		Assert.assertEquals("dataDouble_in", table.getTableItem(0).getText(1));
		Assert.assertEquals("", table.getTableItem(0).getText(2));
		Assert.assertNotNull(sad.getExternalPorts());
		Assert.assertEquals(1, sad.getExternalPorts().getPort().size());
		Assert.assertEquals("DataConverter_1", sad.getExternalPorts().getPort().get(0).getComponentInstantiationRef().getInstantiation().getUsageName());
		Assert.assertEquals("dataDouble_in", sad.getExternalPorts().getPort().get(0).getProvidesIdentifier());
		Assert.assertEquals(null, sad.getExternalPorts().getPort().get(0).getExternalName());
		Assert.assertEquals("my desc", sad.getExternalPorts().getPort().get(0).getDescription());

		table.select(0);
		StandardTestActions.writeToCell(editorBot, table, 0, 2, "extern", false);
		editorBot.sleep(600);
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals("extern", sad.getExternalPorts().getPort().get(0).getExternalName());

		editorBot.button("Remove", 1).click();
		EditorUtils.assertEditorTabValid(editor, EditorUtils.SAD_EDITOR_OVERVIEW_TAB_ID);
		Assert.assertEquals(0, table.rowCount());
	}

	/**
	 * Tests using the header link
	 */
	@Test
	public void headerLink() {
		// Expand the section
		RHBot rhBot = new RHBot(editorBot);
		RHBotSection section = rhBot.section("Project Documentation");
		section.expand();

		// Press enter with focus on the hyper link
		rhBot = new RHBot(section.widget);
		RHBotFormText formText = rhBot.formText();
		formText.setFocus();
		Assert.assertEquals("Header", formText.widget.getSelectedLinkText());
		KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.CR);

		// Assert editor is open, file in project
		SWTBotEditor secondEditor = bot.editorByTitle("HEADER");
		secondEditor.close();
		SWTBot projectViewBot = ViewUtils.getProjectView(bot).bot();
		StandardTestActions.waitForTreeItemToAppear(projectViewBot, projectViewBot.tree(), Arrays.asList(PROJECT_NAME, "HEADER"));
	}
}
