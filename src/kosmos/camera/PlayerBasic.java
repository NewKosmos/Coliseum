/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.camera;

import flounder.camera.*;
import flounder.framework.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import kosmos.*;
import kosmos.chunks.*;
import org.lwjgl.glfw.*;

public class PlayerBasic extends Player {
	private static final float RUN_SPEED = 4.5f;
	private static final float BOOST_SPEED = 15.0f;
	private static final float TURN_SPEED = 250.0f;

	private Vector3f position;
	private Vector3f rotation;

	private float currentSpeed;
	private float currentTurnSpeed;
	private IAxis inputForward;
	private IAxis inputTurn;
	private IButton inputBoost;
	private IButton inputRunStraight;
	private boolean runStraight;

	public PlayerBasic() {
		super();
	}

	@Override
	public void init() {
		this.position = new Vector3f(KosmosConfigs.configSave.getFloatWithDefault("player_x", 0.0f, () -> KosmosChunks.getEntityPlayer().getPosition().x), 0.0f, KosmosConfigs.configSave.getFloatWithDefault("player_z", 0.0f, () -> KosmosChunks.getEntityPlayer().getPosition().z));
		this.rotation = new Vector3f();

		IButton leftKeyButtons = new KeyButton(GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT);
		IButton rightKeyButtons = new KeyButton(GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_RIGHT);
		IButton upKeyButtons = new KeyButton(GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_UP);
		IButton downKeyButtons = new KeyButton(GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_DOWN);
		IButton boostButtons = new KeyButton(GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT);
		IButton straightButtons = new KeyButton(GLFW.GLFW_KEY_H);

		this.currentSpeed = 0.0f;
		this.currentTurnSpeed = 0.0f;
		this.inputForward = new CompoundAxis(new ButtonAxis(upKeyButtons, downKeyButtons), new JoystickAxis(0, 1));
		this.inputTurn = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons), new JoystickAxis(0, 0));
		this.inputBoost = new CompoundButton(boostButtons);
		this.inputRunStraight = new CompoundButton(straightButtons);
		this.runStraight = false;
	}

	@Override
	public void update() {
		if (inputRunStraight.wasDown()) {
			runStraight = !runStraight;
		}
		if (runStraight) {
			currentSpeed = -(inputBoost.isDown() ? BOOST_SPEED : RUN_SPEED) * -4.20f;
		} else {
			currentSpeed = -(inputBoost.isDown() ? BOOST_SPEED : RUN_SPEED) * Maths.deadband(0.05f, inputForward.getAmount());
		}
		currentTurnSpeed = -TURN_SPEED * Maths.deadband(0.05f, inputTurn.getAmount());
		float distance = currentSpeed * Framework.getDelta();
		float dx = (float) (distance * Math.sin(Math.toRadians(rotation.y)));
		float dz = (float) (distance * Math.cos(Math.toRadians(rotation.y)));
		float ry = currentTurnSpeed * Framework.getDelta();

		position.x += dx;
		position.z += dz;
		rotation.y += ry;
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
