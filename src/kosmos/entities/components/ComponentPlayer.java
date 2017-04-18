/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.camera.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.shaders.*;
import kosmos.camera.*;
import kosmos.chunks.*;
import kosmos.water.*;
import kosmos.world.*;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.*;

public class ComponentPlayer extends IComponentEntity implements IComponentRender, IComponentEditor {
	private float currentSpeed;
	private float currentUpwardSpeed;
	private float currentTurnSpeed;
	private IAxis inputForward;
	private IAxis inputTurn;
	private IAxis inputFlyHeight;
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
		this.currentUpwardSpeed = 0.0f;
		this.currentTurnSpeed = 0.0f;
		this.inputForward = new CompoundAxis(new ButtonAxis(downKeyButtons, upKeyButtons), new JoystickAxis(0, 1));
		this.inputTurn = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons), new JoystickAxis(0, 0));
		this.inputBoost = new CompoundButton(boostButtons, new JoystickButton(0, 1));
		this.inputJump = new CompoundButton(jumpButtons, new JoystickButton(0, 0));
		this.inputFlyHeight = new CompoundAxis(new ButtonAxis(crouchButtons, jumpButtons), new JoystickAxis(0, 0));

		this.moveAmount = new Vector3f();
		this.rotateAmount = new Vector3f();

	//	PlayRequest request = PlayRequest.new3dSoundPlayRequest(IMaterial.Materials.GRASS.getMaterial().getSoundWalk(), 1.0f, 1.0f, getEntity().getPosition(), 0.0f, 10.0f);
	//	request.setLooping(true);
	//	FlounderSound.playSystemSound(IMaterial.Materials.GRASS.getMaterial().getSoundWalk());
	}

	@Override
	public void update() {
		// Gets if noclip is enabled.
		boolean noclip = ((KosmosPlayer) FlounderCamera.getPlayer()).isNoclipEnabled();

		// Gets movement and rotation data from player inputs.
		if (!FlounderGuis.getGuiMaster().isGamePaused()) {
			currentSpeed = (inputBoost.isDown() ? KosmosPlayer.BOOST_SPEED : KosmosPlayer.RUN_SPEED) * Maths.deadband(0.05f, inputForward.getAmount());
			currentUpwardSpeed = (inputJump.wasDown() && Maths.deadband(0.05f, currentUpwardSpeed) == 0.0f) ? KosmosPlayer.JUMP_POWER : currentUpwardSpeed;
			currentTurnSpeed = -KosmosPlayer.TURN_SPEED * Maths.deadband(0.05f, inputTurn.getAmount());
		} else {
			currentSpeed = 0.0f;
			currentTurnSpeed = 0.0f;
		}

		// Applies gravity over time.
		if (!noclip) {
			currentUpwardSpeed += KosmosWorld.GRAVITY * Framework.getDelta();
		} else if (!FlounderGuis.getGuiMaster().isGamePaused()) {
			currentSpeed *= 0.5f * KosmosPlayer.FLY_SPEED;
			currentUpwardSpeed = 2.0f * inputFlyHeight.getAmount() * KosmosPlayer.FLY_SPEED;
			currentTurnSpeed *= 0.5f * KosmosPlayer.FLY_SPEED;
		}

		// Calculates the deltas to the moved distance, and rotations.
		float distance = currentSpeed * Framework.getDelta();
		float dx = (float) (distance * Math.sin(Math.toRadians(getEntity().getRotation().y)));
		float dy = currentUpwardSpeed * Framework.getDelta();
		float dz = (float) (distance * Math.cos(Math.toRadians(getEntity().getRotation().y)));
		float ry = currentTurnSpeed * Framework.getDelta();

		// Finds the water level at the next player xz pos.
		float waterLevel = (KosmosWater.getWater() != null) ? KosmosWater.getWater().getPosition().y : 0.0f;

		// Finds the chunk height at the next player xz pos.
		float chunkHeight = Chunk.roundedHeight(KosmosChunks.getCurrent(), getEntity().getPosition()) * 0.5f;

		// Does collision with the highest world object.
		float worldHeight = Math.max(waterLevel - (float) Math.sqrt(2.0), chunkHeight) + KosmosPlayer.PLAYER_OFFSET_Y;

		// If the player is below the world height then force the player back on the ground.
		if (!noclip && getEntity().getPosition().y + dy < worldHeight) {
			dy = worldHeight - getEntity().getPosition().getY();
			currentUpwardSpeed = 0.0f;
		}

		// First person rotation.
		if (KosmosCamera.isFirstPerson()) {
			float x = currentSpeed * Framework.getDelta();
			float y = ry * (KosmosPlayer.RUN_SPEED / KosmosPlayer.TURN_SPEED);
			double theta = Math.toRadians(FlounderCamera.getCamera().getRotation().y);

			dz = (float) -(x * Math.cos(theta) - y * Math.sin(theta));
			dx = (float) -(x * Math.sin(theta) + y * Math.cos(theta));
			ry = (FlounderCamera.getCamera().getRotation().y + 180.0f) - getEntity().getRotation().y;
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
