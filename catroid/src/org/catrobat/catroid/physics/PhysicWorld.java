/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.physics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicObject.Type;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class PhysicWorld {
	static {
		GdxNativesLoader.load();
	}

	public final static float RATIO = 40.0f;
	public final static int VELOCITY_ITERATIONS = 8;
	public final static int POSITION_ITERATIONS = 3;

	public final static Vector2 DEFAULT_GRAVITY = new Vector2(0.0f, -10.0f);
	public final static boolean IGNORE_SLEEPING_OBJECTS = false;

	public final static int STABILIZING_STEPS = 6;

	private final World world;
	private final Map<Sprite, PhysicObject> physicObjects;
	private Box2DDebugRenderer renderer;
	private int stabilizingStep = 0;

	public PhysicWorld() {
		world = new World(PhysicWorld.DEFAULT_GRAVITY, PhysicWorld.IGNORE_SLEEPING_OBJECTS);
		physicObjects = new HashMap<Sprite, PhysicObject>();

		PhysicBoundaryBox physicBoundaryBox = new PhysicBoundaryBox(world);
		physicBoundaryBox.create();
	}

	public void step(float deltaTime) {
		if (stabilizingStep < STABILIZING_STEPS) {
			stabilizingStep++;
		} else {
			try {
				world.step(deltaTime, PhysicWorld.VELOCITY_ITERATIONS, PhysicWorld.POSITION_ITERATIONS);
			} catch (NullPointerException nullPointerException) {
				// Ignore all null pointers because they are mainly caused by shape change which is NOT thread-safe.
			}
		}
		updateSprites();
	}

	private void updateSprites() {
		PhysicObject physicObject;
		PhysicLook costume;
		for (Entry<Sprite, PhysicObject> entry : physicObjects.entrySet()) {
			physicObject = entry.getValue();
			if (physicObject.getType() != Type.DYNAMIC) {
			}
			physicObject.setIfOnEdgeBounce(false);

			costume = (PhysicLook) entry.getKey().look;
			costume.updatePositionAndRotation();
		}
	}

	public void render(Matrix4 perspectiveMatrix) {
		//				if (PhysicRenderer.instance.renderer == null) {
		//					PhysicRenderer.instance.renderer = new ShapeRenderer();
		//				}
		//				PhysicRenderer.instance.render(perspectiveMatrix);

		//		if (renderer == null) {
		//			renderer = new Box2DDebugRenderer(PhysicDebugSettings.Render.RENDER_BODIES,
		//					PhysicDebugSettings.Render.RENDER_JOINTS, PhysicDebugSettings.Render.RENDER_AABBs,
		//					PhysicDebugSettings.Render.RENDER_INACTIVE_BODIES);
		//		}
		//		renderer.render(world, perspectiveMatrix.scl(PhysicWorld.RATIO));
	}

	public void setGravity(Vector2 gravity) {
		world.setGravity(gravity);
	}

	public PhysicObject getPhysicObject(Sprite sprite) {
		if (sprite == null) {
			throw new NullPointerException();
		}

		if (physicObjects.containsKey(sprite)) {
			return physicObjects.get(sprite);
		}

		PhysicObject physicObject = createPhysicObject();
		physicObjects.put(sprite, physicObject);

		return physicObject;
	}

	private PhysicObject createPhysicObject() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.bullet = true;

		return new PhysicObject(world.createBody(bodyDef));
	}
}
