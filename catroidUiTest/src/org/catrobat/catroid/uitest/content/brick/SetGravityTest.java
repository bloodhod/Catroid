package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SetGravityBrick;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;

import com.badlogic.gdx.math.Vector2;
import com.jayway.android.robotium.solo.Solo;

public class SetGravityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private SetGravityBrick setGravityBrick;

	public SetGravityTest() {
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
	public void testSetGravityByBrick() {
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
		String textSetGravity = solo.getString(R.string.brick_set_gravity);
		assertNotNull("TextView does not exist.", solo.getText(textSetGravity));

		Vector2 gravity = new Vector2(1.2f, -3.4f);

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, Float.toString(gravity.x));
		solo.clickOnButton(solo.getString(R.string.ok));

		solo.clickOnEditText(1);
		solo.clearEditText(0);
		solo.enterText(0, Float.toString(gravity.y));
		solo.clickOnButton(solo.getString(R.string.ok));

		Vector2 enteredGravity = (Vector2) UiTestUtils.getPrivateField("gravity", setGravityBrick);
		assertEquals("X text not updated", Float.toString(enteredGravity.x), solo.getEditText(0).getText().toString());
		assertEquals("Y text not updated", Float.toString(enteredGravity.y), solo.getEditText(1).getText().toString());
		assertEquals("Values in Brick are not updated", gravity, enteredGravity);
	}

	private void createProject() {
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		setGravityBrick = new SetGravityBrick(sprite, new Vector2(0, 0));
		script.addBrick(setGravityBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}