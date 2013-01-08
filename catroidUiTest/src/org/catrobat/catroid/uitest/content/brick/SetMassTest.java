package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.PhysicObject;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;

import com.jayway.android.robotium.solo.Solo;

public class SetMassTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private SetMassBrick setMassBrick;

	public SetMassTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		super.tearDown();
	}

	@Smoke
	public void testSetup() {
		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2 + 1, solo.getCurrentListViews().get(0).getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		String textSetMass = solo.getString(R.string.brick_set_mass);
		assertNotNull("TextView does not exist.", solo.getText(textSetMass));
	}

	@Smoke
	public void testSetMassByBrick() {
		float mass = 1.234f;

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, Float.toString(mass));
		solo.clickOnButton(solo.getString(R.string.ok));

		float enteredMass = (Float) UiTestUtils.getPrivateField("mass", setMassBrick);
		assertEquals("Text not updated", Float.toString(mass), solo.getEditText(0).getText().toString());
		assertEquals("Value in Brick is not updated", mass, enteredMass);
	}

	@Smoke
	public void testSetInvalidMassValues() {
		float masses[] = { -1.0f, 0.0f, PhysicObject.MIN_MASS / 10.0f };

		for (float mass : masses) {
			solo.clickOnEditText(0);
			solo.clearEditText(0);
			solo.enterText(0, Float.toString(mass));
			solo.clickOnButton(solo.getString(R.string.ok));

			float enteredMass = (Float) UiTestUtils.getPrivateField("mass", setMassBrick);
			assertEquals("Text not updated", Float.toString(0.0f), solo.getEditText(0).getText().toString());
			assertEquals("Value in Brick is not updated", 0.0f, enteredMass);
		}
	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setMassBrick = new SetMassBrick(sprite, 0.0f);
		script.addBrick(setMassBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}