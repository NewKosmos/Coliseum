/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.camera;

import com.flounder.camera.*;
import com.flounder.devices.*;
import com.flounder.framework.*;
import com.flounder.guis.*;
import com.flounder.inputs.*;
import com.flounder.logger.*;
import com.flounder.maths.*;
import com.flounder.maths.matrices.*;
import com.flounder.maths.vectors.*;
import com.flounder.physics.*;
import com.kosmos.*;

public class KosmosCamera extends Camera {
	// Defines basic view frustum sizes.
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 500.0f;

	// Defines how snappy these camera functions will be.
	private static final float ZOOM_AGILITY = 30.0f;
	private static final float ROTATE_AGILITY = 20.0f;
	private static final float PITCH_AGILITY = 20.0f;

	// Defines the strength of motion from the joysick.
	private static final float INFLUENCE_OF_JOYSTICK_DY = 4.5f;
	private static final float INFLUENCE_OF_JOYSTICK_DX = 4.5f;
	private static final float INFLUENCE_OF_JOYSTICK_ZOOM = 0.3f;

	// Defines the strength of motion from the mouse.
	private static final float INFLUENCE_OF_MOUSE_DY = 10000.0f;
	private static final float INFLUENCE_OF_MOUSE_DX = 10000.0f;
	private static final float INFLUENCE_OF_MOUSE_WHEEL = 0.05f;

	private static final float MAX_HORIZONTAL_CHANGE = 30.0f;
	private static final float MAX_VERTICAL_CHANGE = 30.0f;
	private static final float MAX_ZOOM_CHANGE = 0.5f;

	private static final float CAMERA_HEIGHT_OFFSET = 2.0f;
	private static final float CAMERA_SIDE_OFFSET = -1.55f;

	private static final float MAX_ANGLE_OF_ELEVATION_FPS = 90.0f;
	private static final float MIN_ANGLE_OF_ELEVATION_FPS = -45.0f;
	private static final float MAX_ANGLE_OF_ELEVATION = 90.0f;
	private static final float MIN_ANGLE_OF_ELEVATION = -11.25f;

	private static final float MINIMUM_ZOOM = 0.0f;
	private static final float MAXIMUM_ZOOM = 7.0f;
	private static final float NORMAL_ZOOM = 3.50f;

	private Vector3f position;
	private Vector3f rotation;

	private Frustum viewFrustum;
	private Ray viewRay;
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;

	private float angleOfElevation;
	private float angleAroundPlayer;

	private Vector3f targetPosition;
	private Vector3f targetRotation;
	private float targetZoom;
	private float targetElevation;
	private float targetRotationAngle;

	private float actualDistanceFromPoint;
	private float horizontalDistanceFromFocus;
	private float verticalDistanceFromFocus;

	private static float fieldOfView;
	private static float sensitivity;
	private static boolean mouseLocked;
	private static boolean firstPerson;
	private static int angleButton;
	private JoystickAxis joystickVertical;
	private JoystickAxis joystickHorizontal;
	private JoystickButton joystickZoom;

	public KosmosCamera() {
		super(FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class);
	}

	@Override
	public void init() {
		this.position = new Vector3f();
		this.rotation = new Vector3f(0.0f, 20.0f, 0.0f);

		this.viewFrustum = new Frustum();
		this.viewRay = new Ray(false, new Vector2f(0.0f, 0.0f));
		this.viewMatrix = new Matrix4f();
		this.projectionMatrix = new Matrix4f();

		this.angleOfElevation = 25.0f;
		this.angleAroundPlayer = 0.0f;

		this.targetPosition = new Vector3f();
		this.targetRotation = new Vector3f();
		this.targetZoom = NORMAL_ZOOM;
		this.targetElevation = angleOfElevation;
		this.targetRotationAngle = angleAroundPlayer;

		this.actualDistanceFromPoint = targetZoom;
		this.horizontalDistanceFromFocus = 0.0f;
		this.verticalDistanceFromFocus = 0.0f;

		KosmosCamera.fieldOfView = KosmosConfigs.CAMERA_FOV.setReference(() -> fieldOfView).getFloat();
		KosmosCamera.sensitivity = KosmosConfigs.CAMERA_SENSITIVITY.setReference(() -> sensitivity).getFloat();
		KosmosCamera.mouseLocked = KosmosConfigs.CAMERA_MOUSE_LOCKED.setReference(() -> mouseLocked).getBoolean();
		KosmosCamera.angleButton = KosmosConfigs.CAMERA_ANGLE.setReference(() -> angleButton).getInteger();
		this.joystickVertical = new JoystickAxis(0, 3);
		this.joystickHorizontal = new JoystickAxis(0, 2);
		this.joystickZoom = new JoystickButton(0, 9);

		calculateDistances();
	}

	@Override
	public float getNearPlane() {
		return NEAR_PLANE;
	}

	@Override
	public float getFarPlane() {
		return FAR_PLANE;
	}

	@Override
	public float getFOV() {
		return fieldOfView;
	}

	@Override
	public void update(Player player) {
		float delta = Math.min(1.0f / 60.0f, Framework.get().getDelta());

		calculateHorizontalAngle();
		calculateVerticalAngle();
		calculateZoom();

		if (player != null) {
			Vector3f v = new Vector3f(CAMERA_SIDE_OFFSET, 0.0f, 0.0f);
			v.scale(actualDistanceFromPoint / MAXIMUM_ZOOM);
			Vector3f.rotate(v, player.getRotation(), v);
			this.targetPosition.set(player.getPosition());
			Vector3f.add(targetPosition, v, targetPosition);
		}

		updateActualZoom(delta);
		updateHorizontalAngle(delta);
		updatePitchAngle(delta);
		calculateDistances();
		calculatePosition();

		updateViewMatrix();
		viewFrustum.recalculateFrustum(getProjectionMatrix(), viewMatrix);
		viewRay.recalculateRay(position);
		updateProjectionMatrix();

		firstPerson = Maths.deadband(0.1f, targetZoom) == 0.0f;
	}

	private void calculateHorizontalAngle() {
		float angleChange = 0.0f;

		if (FlounderGuis.get().getGuiMaster() != null && !FlounderGuis.get().getGuiMaster().isGamePaused()) {
			if (Maths.deadband(0.05f, joystickHorizontal.getAmount()) != 0.0f && !joystickZoom.isDown()) {
				angleChange = joystickHorizontal.getAmount() * INFLUENCE_OF_JOYSTICK_DX * sensitivity;
			} else {
				if (FlounderMouse.get().isCursorDisabled() || FlounderMouse.get().getMouse(angleButton)) {
					angleChange = -FlounderMouse.get().getDeltaX() * INFLUENCE_OF_MOUSE_DX * sensitivity;
				}
			}
		}

		if (angleChange > MAX_HORIZONTAL_CHANGE) {
			angleChange = MAX_HORIZONTAL_CHANGE;
		} else if (angleChange < -MAX_HORIZONTAL_CHANGE) {
			angleChange = -MAX_HORIZONTAL_CHANGE;
		}

		targetRotationAngle -= angleChange;

		if (targetRotationAngle >= Maths.DEGREES_IN_HALF_CIRCLE) {
			targetRotationAngle -= Maths.DEGREES_IN_CIRCLE;
		} else if (targetRotationAngle <= -Maths.DEGREES_IN_HALF_CIRCLE) {
			targetRotationAngle += Maths.DEGREES_IN_CIRCLE;
		}
	}

	private void calculateVerticalAngle() {
		float angleChange = 0.0f;

		if (FlounderGuis.get().getGuiMaster() != null && !FlounderGuis.get().getGuiMaster().isGamePaused()) {
			if (Maths.deadband(0.05f, joystickVertical.getAmount()) != 0.0f && !joystickZoom.isDown()) {
				angleChange = joystickVertical.getAmount() * INFLUENCE_OF_JOYSTICK_DY * sensitivity;
			} else {
				if (FlounderMouse.get().isCursorDisabled() || FlounderMouse.get().getMouse(angleButton)) {
					angleChange = FlounderMouse.get().getDeltaY() * INFLUENCE_OF_MOUSE_DY * sensitivity;
				}
			}
		}

		if (angleChange > MAX_VERTICAL_CHANGE) {
			angleChange = MAX_VERTICAL_CHANGE;
		} else if (angleChange < -MAX_VERTICAL_CHANGE) {
			angleChange = -MAX_VERTICAL_CHANGE;
		}

		targetElevation -= angleChange;

		if (!firstPerson) {
			if (targetElevation >= MAX_ANGLE_OF_ELEVATION) {
				targetElevation = MAX_ANGLE_OF_ELEVATION;
			} else if (targetElevation <= MIN_ANGLE_OF_ELEVATION) {
				targetElevation = MIN_ANGLE_OF_ELEVATION;
			}
		} else {
			if (targetElevation >= MAX_ANGLE_OF_ELEVATION_FPS) {
				targetElevation = MAX_ANGLE_OF_ELEVATION_FPS;
			} else if (targetElevation <= MIN_ANGLE_OF_ELEVATION_FPS) {
				targetElevation = MIN_ANGLE_OF_ELEVATION_FPS;
			}
		}
	}

	private void calculateZoom() {
		float zoomChange = 0.0f;

		if (FlounderGuis.get().getGuiMaster() != null && !FlounderGuis.get().getGuiMaster().isGamePaused()) {
			if (joystickZoom.isDown()) {
				zoomChange = joystickVertical.getAmount() * INFLUENCE_OF_JOYSTICK_ZOOM * sensitivity;
			} else if (Math.abs(FlounderMouse.get().getDeltaWheel()) > 0.1f) {
				zoomChange = FlounderMouse.get().getDeltaWheel() * INFLUENCE_OF_MOUSE_WHEEL * sensitivity;
			}
		}

		if (zoomChange > MAX_VERTICAL_CHANGE) {
			zoomChange = MAX_VERTICAL_CHANGE;
		} else if (zoomChange < -MAX_VERTICAL_CHANGE) {
			zoomChange = -MAX_ZOOM_CHANGE;
		}

		targetZoom -= zoomChange;

		if (targetZoom < MINIMUM_ZOOM) {
			targetZoom = MINIMUM_ZOOM;
		} else if (targetZoom > MAXIMUM_ZOOM) {
			targetZoom = MAXIMUM_ZOOM;
		}
	}

	private void updateActualZoom(float delta) {
		float offset = targetZoom - actualDistanceFromPoint;
		float change = offset * delta * ZOOM_AGILITY;
		actualDistanceFromPoint += change;
	}

	private void updateHorizontalAngle(float delta) {
		float offset = targetRotationAngle - angleAroundPlayer;

		if (Math.abs(offset) > Maths.DEGREES_IN_HALF_CIRCLE) {
			if (offset < 0) {
				offset = targetRotationAngle + Maths.DEGREES_IN_CIRCLE - angleAroundPlayer;
			} else {
				offset = targetRotationAngle - Maths.DEGREES_IN_CIRCLE - angleAroundPlayer;
			}
		}

		angleAroundPlayer += offset * delta * ROTATE_AGILITY;
		angleAroundPlayer = Maths.normalizeAngle(angleAroundPlayer);
	}

	private void updatePitchAngle(float delta) {
		float offset = targetElevation - angleOfElevation;

		if (Math.abs(offset) > Maths.DEGREES_IN_HALF_CIRCLE) {
			if (offset < 0) {
				offset = targetElevation + Maths.DEGREES_IN_CIRCLE - angleOfElevation;
			} else {
				offset = targetElevation - Maths.DEGREES_IN_CIRCLE - angleOfElevation;
			}
		}

		angleOfElevation += offset * delta * PITCH_AGILITY;
		angleOfElevation = Maths.normalizeAngle(angleOfElevation);
	}

	private void calculateDistances() {
		horizontalDistanceFromFocus = (float) (actualDistanceFromPoint * Math.cos(Math.toRadians(angleOfElevation)));
		verticalDistanceFromFocus = (float) (actualDistanceFromPoint * Math.sin(Math.toRadians(angleOfElevation)));
	}

	private void calculatePosition() {
		double theta = Math.toRadians(targetRotation.y + angleAroundPlayer);
		position.x = targetPosition.x - (float) (horizontalDistanceFromFocus * Math.sin(theta));
		position.y = targetPosition.y + verticalDistanceFromFocus + CAMERA_HEIGHT_OFFSET;
		position.z = targetPosition.z - (float) (horizontalDistanceFromFocus * Math.cos(theta));

		rotation.x = angleOfElevation;
		rotation.y = angleAroundPlayer + targetRotation.y + Maths.DEGREES_IN_HALF_CIRCLE;
		rotation.z = 0.0f;
	}

	private void updateViewMatrix() {
		viewMatrix.setIdentity();
		position.negate();
		Matrix4f.rotate(viewMatrix, Matrix4f.REUSABLE_VECTOR.set(1.0f, 0.0f, 0.0f), (float) Math.toRadians(rotation.x), viewMatrix);
		Matrix4f.rotate(viewMatrix, Matrix4f.REUSABLE_VECTOR.set(0.0f, 1.0f, 0.0f), (float) Math.toRadians(-rotation.y), viewMatrix);
		Matrix4f.rotate(viewMatrix, Matrix4f.REUSABLE_VECTOR.set(0.0f, 0.0f, 1.0f), (float) Math.toRadians(rotation.z), viewMatrix);
		Matrix4f.translate(viewMatrix, position, viewMatrix);
		position.negate();
	}

	private void updateProjectionMatrix() {
		Matrix4f.perspectiveMatrix(getFOV(), FlounderDisplay.get().getAspectRatio(), getNearPlane(), getFarPlane(), projectionMatrix);
	}

	@Override
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public Frustum getViewFrustum() {
		return viewFrustum;
	}

	@Override
	public Ray getViewRay() {
		return viewRay;
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public void reflect(float waterHeight) {
		position.y -= 2.0f * (position.y - waterHeight);
		rotation.x = -rotation.x;
		updateViewMatrix();
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
	public void setRotation(Vector3f rotation) {
		this.rotation.set(rotation);
	}

	public static boolean isFirstPerson() {
		return firstPerson;
	}

	public static float getFieldOfView() {
		return fieldOfView;
	}

	public static void setFieldOfView(float fieldOfView) {
		KosmosCamera.fieldOfView = fieldOfView;
	}

	public static float getSensitivity() {
		return sensitivity;
	}

	public static void setSensitivity(float sensitivity) {
		KosmosCamera.sensitivity = sensitivity;
	}

	public static boolean isMouseLocked() {
		return mouseLocked;
	}

	public static void setMouseLocked(boolean mouseLocked) {
		KosmosCamera.mouseLocked = mouseLocked;
	}

	public static int getAngleButton() {
		return angleButton;
	}

	public static void setAngleButton(int angleButton) {
		KosmosCamera.angleButton = angleButton;
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
