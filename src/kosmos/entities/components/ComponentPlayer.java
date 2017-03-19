/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import kosmos.camera.*;
import kosmos.chunks.*;
import kosmos.water.*;
import kosmos.world.*;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.*;

public class ComponentPlayer extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private float currentSpeed;
	private float currentUpwardSpeed;
	private float currentTurnSpeed;
	private IAxis inputForward;
	private IAxis inputTurn;
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
		super(entity, ID);

		IButton leftKeyButtons = new KeyButton(GLFW_KEY_A, GLFW_KEY_LEFT);
		IButton rightKeyButtons = new KeyButton(GLFW_KEY_D, GLFW_KEY_RIGHT);
		IButton upKeyButtons = new KeyButton(GLFW_KEY_W, GLFW_KEY_UP);
		IButton downKeyButtons = new KeyButton(GLFW_KEY_S, GLFW_KEY_DOWN);
		IButton boostButtons = new KeyButton(GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT);
		IButton jumpButtons = new KeyButton(GLFW_KEY_SPACE);

		this.currentSpeed = 0.0f;
		this.currentUpwardSpeed = 0.0f;
		this.currentTurnSpeed = 0.0f;
		this.inputForward = new CompoundAxis(new ButtonAxis(upKeyButtons, downKeyButtons), new JoystickAxis(0, 1));
		this.inputTurn = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons), new JoystickAxis(0, 0));
		this.inputBoost = new CompoundButton(boostButtons);
		this.inputJump = new CompoundButton(jumpButtons);

		this.moveAmount = new Vector3f();
		this.rotateAmount = new Vector3f();
	}

	@Override
	public void update() {
		// Gets movement and rotation data from player inputs.
		if (FlounderGuis.getGuiMaster() != null && !FlounderGuis.getGuiMaster().isGamePaused()) {
			currentSpeed = -(inputBoost.isDown() ? PlayerBasic.BOOST_SPEED : PlayerBasic.RUN_SPEED) * Maths.deadband(0.05f, inputForward.getAmount());
			currentUpwardSpeed = (inputJump.wasDown() && Maths.deadband(0.05f, currentUpwardSpeed) == 0.0f) ? PlayerBasic.JUMP_POWER : currentUpwardSpeed;
			currentTurnSpeed = -PlayerBasic.TURN_SPEED * Maths.deadband(0.05f, inputTurn.getAmount());
		} else {
			currentSpeed = 0.0f;
			currentTurnSpeed = 0.0f;
		}

		// Applies gravity over time.
		currentUpwardSpeed += KosmosWorld.GRAVITY * Framework.getDelta();

		// Calculates the deltas to the moved distance, and rotations.
		float distance = currentSpeed * Framework.getDelta();
		float dx = (float) (distance * Math.sin(Math.toRadians(getEntity().getRotation().y)));
		float dy = currentUpwardSpeed * Framework.getDelta();
		float dz = (float) (distance * Math.cos(Math.toRadians(getEntity().getRotation().y)));
		float ry = currentTurnSpeed * Framework.getDelta();

		// Finds the water level at the next player xz pos.
		float waterLevel = (KosmosWater.getWater() != null) ? KosmosWater.getWater().getPosition().y : 0.0f;

		// Finds the chunk height at the next player xz pos.
		float chunkHeight = Chunk.getWorldHeight(getEntity().getPosition().x + dx, getEntity().getPosition().z + dz) * 0.5f;

		// Does collision with the highest world object.
		float worldHeight = Math.max(waterLevel - (float) Math.sqrt(2.0), chunkHeight) + PlayerBasic.PLAYER_OFFSET_Y;

		// If the player is below the world height then force the player back on the ground.
		if (getEntity().getPosition().y + dy < worldHeight) {
			dy = worldHeight - getEntity().getPosition().getY();
			currentUpwardSpeed = 0.0f;
		}

		// Limits ry rotation. TODO
		//float cry = FlounderCamera.getCamera().getRotation().y;
		//float rot = Maths.clamp(getEntity().getRotation().y, Maths.normalizeAngle(cry - 90.0f), Maths.normalizeAngle(cry + 90.0f));
		//ry = rot - getEntity().getRotation().y;

		// Moves and rotates the player.
		float lastY = getEntity().getPosition().y;
		getEntity().move(moveAmount.set(dx, dy, dz), rotateAmount.set(0.0f, ry, 0.0f));

		// If there has been no change then the player has probably landed.
		if (getEntity().getPosition().y - lastY == 0.0f) {
			currentUpwardSpeed = 0.0f;
		}
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
