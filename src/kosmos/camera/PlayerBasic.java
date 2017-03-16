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
import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import kosmos.*;
import kosmos.chunks.*;
import kosmos.network.packets.*;
import kosmos.water.*;
import kosmos.world.*;
import org.lwjgl.glfw.*;

public class PlayerBasic extends Player {
	private static final float RUN_SPEED = 5.0f;
	private static final float BOOST_SPEED = 10.0f;
	private static final float JUMP_POWER = 5.5f;
	private static final float TURN_SPEED = 300.0f;

	public static final float PLAYER_OFFSET_Y = (float) (Math.sqrt(2.0) * 0.25);

	private Vector3f position;
	private Vector3f rotation;

	private float currentSpeed;
	private float currentUpwardSpeed;
	private float currentTurnSpeed;
	private IAxis inputForward;
	private IAxis inputTurn;
	private IButton inputBoost;
	private IButton inputJump;

	private Timer timer;
	private boolean needSendData;

	public PlayerBasic() {
		super();
	}

	@Override
	public void init() {
		this.position = new Vector3f(
				KosmosConfigs.configSave.getFloatWithDefault("player_x", 0.0f, () -> KosmosChunks.getEntityPlayer().getPosition().x),
				0.0f,
				KosmosConfigs.configSave.getFloatWithDefault("player_z", 0.0f, () -> KosmosChunks.getEntityPlayer().getPosition().z)
		);
		this.rotation = new Vector3f();

		IButton leftKeyButtons = new KeyButton(GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT);
		IButton rightKeyButtons = new KeyButton(GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_RIGHT);
		IButton upKeyButtons = new KeyButton(GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_UP);
		IButton downKeyButtons = new KeyButton(GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_DOWN);
		IButton boostButtons = new KeyButton(GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT);
		IButton jumpButtons = new KeyButton(GLFW.GLFW_KEY_SPACE);

		this.currentSpeed = 0.0f;
		this.currentUpwardSpeed = 0.0f;
		this.currentTurnSpeed = 0.0f;
		this.inputForward = new CompoundAxis(new ButtonAxis(upKeyButtons, downKeyButtons), new JoystickAxis(0, 1));
		this.inputTurn = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons), new JoystickAxis(0, 0));
		this.inputBoost = new CompoundButton(boostButtons);
		this.inputJump = new CompoundButton(jumpButtons);

		this.timer = new Timer(1.0 / 32.0); // 32 ticks per second.
		this.needSendData = true;
	}

	@Override
	public void update() {
		// Gets movement and rotation data from player inputs.
		if (FlounderGuis.getGuiMaster() != null && !FlounderGuis.getGuiMaster().isGamePaused()) {
			currentSpeed = -(inputBoost.isDown() ? BOOST_SPEED : RUN_SPEED) * Maths.deadband(0.05f, inputForward.getAmount());
			currentUpwardSpeed = (inputJump.wasDown() && Maths.deadband(0.05f, currentUpwardSpeed) == 0.0f) ? JUMP_POWER : currentUpwardSpeed;
			currentTurnSpeed = -TURN_SPEED * Maths.deadband(0.05f, inputTurn.getAmount());
		} else {
			currentSpeed = 0.0f;
			currentTurnSpeed = 0.0f;
		}

		currentUpwardSpeed += KosmosWorld.GRAVITY * Framework.getDelta();

		float distance = currentSpeed * Framework.getDelta();
		float dx = (float) (distance * Math.sin(Math.toRadians(rotation.y)));
		float dy = currentUpwardSpeed * Framework.getDelta();
		float dz = (float) (distance * Math.cos(Math.toRadians(rotation.y)));
		float ry = currentTurnSpeed * Framework.getDelta();

		// Finds the water level at the next player xz pos.
		float waterLevel = (KosmosWater.getWater() != null) ? KosmosWater.getWater().getPosition().y : 0.0f;

		// Finds the chunk height at the next player xz pos.
		float chunkHeight = KosmosChunks.getWorldHeight(position.x + dx, position.z + dz) * 0.5f;

		// Does collision with the highest world object.
		float worldHeight = Math.max(waterLevel - (float) Math.sqrt(2.0), chunkHeight);

		if (position.y + dy < worldHeight) {
			dy = worldHeight - position.getY();
		//	dy *= 30.0f * Framework.getDelta();
			currentUpwardSpeed = 0.0f;
		}

		// Moves and rotates the player.
		position.x += dx;
		position.y += dy;
		position.z += dz;
		rotation.y += ry;

		// Fixes player rotation exceeding +/-360 degrease.
		rotation.y = Maths.normalizeAngle(rotation.y);

		// Try to send data to the server if needed.
		if (dx != 0.0f || dy != 0.0f || dz != 0.0f || ry != 0.0f) {
			if (!needSendData) {
				needSendData = true;
				timer.resetStartTime();
			}
		}

		// Stop spam requests by using a tick rate.
		if (needSendData && timer.isPassedTime()) {
			sendData();
		}
	}

	/**
	 * Sends this players data to the server.
	 */
	public void sendData() {
		if (FlounderNetwork.getUsername() != null && FlounderNetwork.getSocketClient() != null && KosmosChunks.getCurrent() != null) {
			new PacketMove(FlounderNetwork.getUsername(), position, rotation, KosmosChunks.getCurrent().getPosition().x, KosmosChunks.getCurrent().getPosition().z).writeData(FlounderNetwork.getSocketClient());
			needSendData = false;
			timer.resetStartTime();
		}
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
