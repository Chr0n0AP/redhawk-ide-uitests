package gov.redhawk.ide.ui.tests.projectCreation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;

public class IdlWizardTest extends UITest {

	private static final String PROJ_NAME = "IdlTestProject";

	@Before
	@Override
	public void before() throws Exception {
		super.before();

		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		SWTBot wizardBot = wizardShell.bot();
		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", "REDHAWK IDL Project"));
		treeItem.select();
		wizardBot.button("Next >").click();

		wizardShell.bot().textWithLabel("Project name:").typeText(PROJ_NAME);
		wizardShell.bot().button("Finish").click();
		ProjectExplorerUtils.waitUntilNodeAppears(bot, PROJ_NAME);
	}

	/**
	 * Test that the expected default includes paths are being added at project generation
	 */
	@Test
	public void includePathTest() {
		final SWTBotTreeItem projectNode = ProjectExplorerUtils.selectNode(bot, PROJ_NAME);
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				checkFileContents((IProject) projectNode.widget.getData());

			}
		});
		projectNode.expand();

	}

	private void checkFileContents(IProject project) {
		final String INCLUDE_PATHS = ".INCLUDE_PATHS=";
		final String[] includePaths = { "${OssieHome}/share/idl", "/usr/share/idl/omniORB", "/usr/share/idl/omniORB/COS" };
		final List<String> defaultIncludePaths = Arrays.asList(includePaths);

		BufferedReader bufferedReader = null;
		try {
			File file = project.getFile(".ecpproperties").getLocation().toFile();
			bufferedReader = new BufferedReader(new FileReader(file));

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.matches(".*" + INCLUDE_PATHS + ".*")) {
					String testString = line.substring(INCLUDE_PATHS.length());
					String[] foundIncludePaths = testString.split(";");
					Assert.assertEquals("Incorrected number of default include paths found", defaultIncludePaths.size(), foundIncludePaths.length);
					for (String path : foundIncludePaths) {
						Assert.assertTrue("Unexpected default include path found: " + path, defaultIncludePaths.contains(path));
					}
				}
			}
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}
}
