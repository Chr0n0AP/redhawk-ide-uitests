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
package gov.redhawk.ide.ui.tests.prf;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.SimpleSequence;

public class SimpleSequencePropertyTest extends AbstractPropertyTest<SimpleSequence> {

	@Override
	protected void createType() {
		editorBot.button("Add Sequence").click();
	}
	
	@Override
	protected SimpleSequence getModelObject() throws IOException {
		return getModelFromXml().getSimpleSequence().get(0);
	}

	@Test
	public void testID() throws CoreException {
		// ID should already be set
		assertFormValid();
		Assert.assertEquals("ID", editorBot.textWithLabel(ID_FIELD).getText());

		editorBot.textWithLabel(ID_FIELD).setText("");
		assertFormInvalid();
		editorBot.textWithLabel(ID_FIELD).setText("hello");
		assertFormValid();
	}

	@Test
	public void testName() {
		editorBot.textWithLabel(NAME_FIELD).setText("Name1");
		assertFormValid();
		Assert.assertEquals("Name1", editorBot.textWithLabel(ID_FIELD).getText());
	}

	@Test
	public void testValuesChar() {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesChar(valuesViewer);
	}

	@Test
	public void testValuesDouble() {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesDouble(valuesViewer);
	}

	@Test
	public void testValuesFloat() {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesFloat(valuesViewer);
	}

	@Test
	public void testValuesLong() {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesLong(valuesViewer);
	}

	@Test
	public void testValuesLongLong() {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesLongLong(valuesViewer);
	}

	@Test
	public void testValuesShort() {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesShort(valuesViewer);
	}

	@Test
	public void testValuesObjRef() {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesObjRef(valuesViewer);
	}

	@Test
	public void testValuesString() throws IOException {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesString(valuesViewer);
	}

	@Test
	public void testValuesUTCTime() {
		SWTBotTable valuesViewer = editorBot.tableWithLabel("Values:");
		testValuesUTCTime(valuesViewer);
	}

	private void testValuesChar(SWTBotTable valuesViewer) {
		setType("char", "");
		addValue(editorBot, "1");
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "abc", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1", false, true);
		assertFormValid();
		addValue(editorBot, "1");
		assertFormValid();
		clearValues();
	}

	private void testValuesDouble(SWTBotTable valuesViewer) {
		setType("double (64-bit)", "");
		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "al", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1", false, true);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("-1.1");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

	}

	private void testValuesFloat(SWTBotTable valuesViewer) {
		setType("float (32-bit)", "complex");
		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1+j10.1");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1.1", false, true);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1.1+jjak", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1.1", false, true);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1.1+j10.1", false, true);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-1.1+j10.1");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();
	}

	private void testValuesLong(SWTBotTable valuesViewer) {
		setType("long (32-bit)", "");
		addValue(editorBot, "-11");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1.1", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-11", false, true);
		assertFormValid();
		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("-11");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("ulong (32-bit)", "");
		addValue(editorBot, "11");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "11", false, true);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("11");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();
	}

	private void testValuesLongLong(SWTBotTable valuesViewer) {
		setType("longlong (64-bit)", "");
		addValue(editorBot, "-11");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1.1", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-11", false, true);
		assertFormValid();
		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("-11");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();

		setType("ulonglong (64-bit)", "");
		addValue(editorBot, "11");
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-1", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "11", false, true);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("11");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();
	}

	private void testValuesShort(SWTBotTable valuesViewer) {
		setType("short (16-bit)", "complex");
		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("-11-j2");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1", false, true);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1+100iada", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "-11-j2", false, true);
		assertFormValid();
		addValue(editorBot, "-11-j2");
		assertFormValid();
		clearValues();

		setType("ushort (16-bit)", "complex");
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("11+j2");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1", false, true);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "1+j1ada", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "11", false, true);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "11+j2", false, true);
		assertFormValid();
		editorBot.button("Add...").click();
		shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("11+j2");
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();
	}

	private void testValuesObjRef(SWTBotTable valuesViewer) {
		setType("objref", "");
		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("1");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().button("Cancel").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		assertFormValid();
		clearValues();
	}

	private void testValuesString(SWTBotTable valuesViewer) throws IOException {
		// Start with type selected as string
		addValue(editorBot, "\"\"");
		Assert.assertEquals("Double-quotes did not convert to empty string", "", getModelObject().getValues().getValue().get(0));
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "a", false, true);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "true", false, true);
		assertFormValid();
		addValue(editorBot, "true");
		assertFormValid();
		clearValues();

		setType("string", "");
		addValue(editorBot, "abcd");
		assertFormValid();
		addValue(editorBot, "efg");
		assertFormValid();
		clearValues();
	}

	private void testValuesUTCTime(SWTBotTable valuesViewer) {
		setType("utctime", "real");

		editorBot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText("2017:01:02::03:04:05");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("123");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("now");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().textWithLabel("Value:").setText("2017:01:02::03:04:05.123456");
		Assert.assertTrue("OK should be enabled", shell.bot().button("OK").isEnabled());
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));

		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "2017:01:02::03:04:05", false, true);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "123", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "now", false, true);
		assertFormValid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "abc", false, true);
		assertFormInvalid();
		StandardTestActions.writeToCell(editorBot, valuesViewer, 0, 0, "2017:01:02::03:04:05.123456", false, true);
		assertFormValid();

		addValue(editorBot, "2000:12:31::23:59:59.999999");
		addValue(editorBot, "now");
		assertFormValid();

		clearValues();
	}

	private void setType(String type, String complex) {
		editorBot.comboBoxWithLabel("Type*:").setSelection(type);
		editorBot.comboBoxWithLabel("Type*:", 1).setSelection(complex);
	}

	private void addValue(SWTBot bot, String text) {
		final int startingRows = bot.table().rowCount();
		bot.button("Add...").click();
		SWTBotShell shell = bot.shell("New Value");
		shell.bot().textWithLabel("Value:").setText(text);
		shell.bot().button("OK").click();
		bot.waitUntil(Conditions.shellCloses(shell));
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				int endingRows = bot.table().rowCount();
				return (startingRows + 1) == endingRows;
			}

			@Override
			public String getFailureMessage() {
				return "Value was not added to table";
			}
		});
	}

	private void clearValues() {
		SWTBotTable valuesTable = editorBot.tableWithLabel("Values:");
		while (valuesTable.rowCount() > 0) {
			valuesTable.click(0, 0);
			// Press escape to avoid issues going from the cell's editor to the button
			Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
			keyboard.pressShortcut(Keystrokes.ESC);
			editorBot.button("Remove", 1).click();
		}
	}

	@Override
	public void testEnum() throws CoreException {
		// Override to do nothing since Simple Sequences do not have enums
	}

	@Test
	public void testKind() throws IOException {
		assertFormValid();

		testKind(false, false);
	}

	/**
	 * IDE-152
	 * Check that adding values to a SimpleSequence via the "Properties" tab automatically updates the prf.xml
	 * @throws IOException
	 */
	@Test
	public void testValueAutoUpdate() throws IOException {
		String[] values = { "string1", "string2", "string3" };
		for (String value : values) {
			addValue(editorBot, value);
		}
		assertFormValid();

		// Check xml
		Properties properties = getModelFromXml();
		SimpleSequence simpleSeq = getSimpleSequence(properties);
		for (String value : values) {
			Assert.assertTrue("Values block did not contain '" + value + "'", simpleSeq.getValues().getValue().contains(value));
		}

		clearValues();
	}

	protected SimpleSequence getSimpleSequence(Properties properties) {
		return properties.getSimpleSequence().get(0);
	}
}
