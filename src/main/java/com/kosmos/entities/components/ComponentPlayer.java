/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.entities.components;

import com.flounder.camera.*;
import com.flounder.devices.*;
import com.flounder.entities.*;
import com.flounder.framework.*;
import com.flounder.guis.*;
import com.flounder.helpers.*;
import com.flounder.inputs.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.shaders.*;
import com.kosmos.camera.*;
import com.kosmos.world.*;
import com.kosmos.world.chunks.*;
import com.kosmos.world.water.*;

import javax.swing.*;

import static com.flounder.platform.Constants.*;

public class ComponentPlayer extends IComponentEntity implements IComponentRender, IComponentEditor {
	private float currentSpeed;
	private float currentStrafeSpeed;
	private float currentUpwardSpeed;
	private IAxis inputForward;
	private IAxis inputStrafe;
	private IAxis inputNoclip;
	private IButton inputBoost;
	private IButton inputJump;

	private Vector3f moveAmount;
	private Vector3f rotateAmount;

	/**
	 * Creates a new ComponentPlayer.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentPlayer(Entity entity) {
		super(entity);

		IButton leftKeyButtons = new KeyButton(GLFW_KEY_A, GLFW_KEY_LEFT);
		IButton rightKeyButtons = new KeyButton(GLFW_KEY_D, GLFW_KEY_RIGHT);
		IButton upKeyButtons = new KeyButton(GLFW_KEY_W, GLFW_KEY_UP);
		IButton downKeyButtons = new KeyButton(GLFW_KEY_S, GLFW_KEY_DOWN);
		IButton boostButtons = new KeyButton(GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT);
		IButton crouchButtons = new KeyButton(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_RIGHT_CONTROL);
		IButton jumpButtons = new KeyButton(GLFW_KEY_SPACE);

		this.currentSpeed = 0.0f;
		this.currentStrafeSpeed = 0.0f;
		this.currentUpwardSpeed = 0.0f;
		this.inputForward = new CompoundAxis(new ButtonAxis(downKeyButtons, upKeyButtons), new JoystickAxis(0, 1));
		this.inputStrafe = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons), new JoystickAxis(0, 0));
		this.inputBoost = new CompoundButton(boostButtons, new JoystickButton(0, 1));
		this.inputJump = new CompoundButton(jumpButtons, new JoystickButton(0, 0));
		this.inputNoclip = new CompoundAxis(new ButtonAxis(crouchButtons, jumpButtons), new JoystickAxis(0, 0));

		this.moveAmount = new Vector3f();
		this.rotateAmount = new Vector3f();
	}

	@Override
	public void update() {
		if (FlounderCamera.get().getPlayer() == null || !KosmosPlayer.class.isInstance(FlounderCamera.get().getPlayer()) || KosmosWorld.get().getWorld() == null) {
			return;
		}

		// Gets the delta and limits the lowest UPS to 20 (any less and the game is unplayable).
		float delta = Framework.get().getDelta();
		delta = Math.min(delta, 1.0f / 20.0f);

		// Gets if noclip is enabled.
		boolean noclip = ((KosmosPlayer) FlounderCamera.get().getPlayer()).isNoclipEnabled();

		// Gets movement and rotation data from player inputs.
		if (!FlounderGuis.get().getGuiMaster().isGamePaused()) {
			currentSpeed = KosmosPlayer.RUN_SPEED * Maths.deadband(0.05f, inputForward.getAmount());
			currentStrafeSpeed = -KosmosPlayer.STRAFE_SPEED * Maths.deadband(0.05f, inputStrafe.getAmount());

			if (inputBoost.isDown()) {
				currentSpeed *= KosmosPlayer.BOOST_MUL;
				//	currentStrafeSpeed *= KosmosPlayer.BOOST_MUL;
			}

			if (inputJump.wasDown() && Maths.deadband(0.1f, currentUpwardSpeed) == 0.0f) {
				currentUpwardSpeed = KosmosPlayer.JUMP_POWER;
			}
		} else {
			currentSpeed = 0.0f;
			currentStrafeSpeed = 0.0f;
		}

		// Applies gravity over time.
		if (!noclip) {
			currentUpwardSpeed += KosmosWorld.GRAVITY * delta;
		} else if (!FlounderGuis.get().getGuiMaster().isGamePaused()) {
			currentSpeed *= 0.5f * KosmosPlayer.FLY_SPEED;
			currentStrafeSpeed *= 0.5f * KosmosPlayer.FLY_SPEED;
			currentUpwardSpeed = 2.0f * inputNoclip.getAmount() * KosmosPlayer.FLY_SPEED;
		}

		// Calculates the deltas to the moved distance, and rotations.
		double theta = Math.toRadians(getEntity().getRotation().y - 180.0f);
		float dx = (float) -(currentSpeed * Math.sin(theta) + currentStrafeSpeed * Math.cos(theta)) * delta;
		float dy = currentUpwardSpeed * delta;
		float dz = (float) -(currentSpeed * Math.cos(theta) - currentStrafeSpeed * Math.sin(theta)) * delta;
		float ry = FlounderCamera.get().getCamera().getRotation().y - getEntity().getRotation().y + 180.0f;

		if (FlounderKeyboard.get().getKey(GLFW_KEY_LEFT_ALT)) {
			ry = 0.0f;
		}

		// Finds the water level at the next player xz pos.
		float waterLevel = (KosmosWater.get().getWater() != null) ? KosmosWater.get().getWater().getPosition().y : 0.0f;

		// Finds the chunk height at the next player xz pos.
		float chunkHeight = KosmosChunks.roundedHeight(KosmosChunks.get().getCurrent(), getEntity().getPosition()) * 0.5f;

		// Does collision with the highest world object.
		float worldHeight = Math.max(waterLevel - (float) Math.sqrt(2.0), chunkHeight) + KosmosPlayer.PLAYER_OFFSET_Y;

		// If the player is below the world height then force the player back on the ground.
		float depth = (getEntity().getPosition().y + dy) - worldHeight;

		if (!noclip && depth < 0.0f) {
			dy = Math.min(-depth, (float) Math.sqrt(2.0)) * 10.0f * delta;
			//	dy = Maths.deadband(0.05f, dy);
			currentUpwardSpeed = 0.0f;
		}

		// Moves and rotates the player.
		float lastY = getEntity().getPosition().y;

		if (!noclip) {
			getEntity().move(moveAmount.set(dx, dy, dz), rotateAmount.set(0.0f, ry, 0.0f));
		} else {
			Vector3f.add(getEntity().getPosition(), moveAmount.set(dx, dy, dz), getEntity().getPosition());
			Vector3f.add(getEntity().getRotation(), rotateAmount.set(0.0f, ry, 0.0f), getEntity().getRotation());

			if (moveAmount.lengthSquared() != 0.0f || rotateAmount.lengthSquared() != 0.0f) {
				getEntity().setMoved();
			}
		}

		// If there has been no change then the player has probably landed.
		if (getEntity().getPosition().y - lastY == 0.0f) {
			currentUpwardSpeed = 0.0f;
		}
	}

	@Override
	public void render(ShaderObject shader, Single<Integer> vaoLength) {
		if (KosmosCamera.isFirstPerson()) {
			vaoLength.setSingle(-1);
		}
	}

	@Override
	public void renderClear(ShaderObject shader) {

	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
