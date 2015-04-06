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

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class ChalkboardConsoleTest extends AbstractGraphitiChalkboardTest {

	private SWTBotGefEditor editor;
	private static final String SIGGEN_1 = SIGGEN + "_1";
	private static final String CONSOLE_VIEW_LABEL = "Console";
	private static final String CHALKBOARD_WAVEFORM = "Chalkboard";

	/**
	 * IDE-1054 Making sure Console title is set correctly for component process
	 */
	@Test
	public void checkConsoleTitle() {
		editor = openChalkboardDiagram(gefBot);

		// Prepare Graphiti diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		
		//wait for SIGGEN_1 to show up in Sca Explorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, CHALKBOARD_PARENT_PATH, CHALKBOARD, SIGGEN_1);

		bot.cTabItem(CONSOLE_VIEW_LABEL).activate();
		SWTBotView consoleView = bot.activeView(); //.viewByTitle(CONSOLE_VIEW_LABEL);
		SWTBotLabel titleText = consoleView.bot().label();
		String title = titleText.getText();
		Assert.assertTrue("Console title does not start with component and waveform name", 
			title.startsWith(SIGGEN_1 + " [" + CHALKBOARD_WAVEFORM + "] "));
	}
	
}
