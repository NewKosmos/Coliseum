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
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.events.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.physics.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;
import kosmos.network.packets.*;
import kosmos.world.*;

import static flounder.platform.Constants.*;

public class KosmosPlayer extends Player {
	public static final float PLAYER_OFFSET_Y = (float) (Math.sqrt(2.0) * 0.25);
	public static final float PLAYER_TAG_Y = 1.8f + PLAYER_OFFSET_Y;

	public static final float RUN_SPEED = 6.0f;
	public static final float STRAFE_SPEED = 4.0f;
	public static final float BOOST_MUL = 2.0f;
	public static final float JUMP_POWER = 8.0f;
	public static final float FLY_SPEED = 8.0f;

	private Vector3f position;
	private Vector3f rotation;

	private boolean noclipEnabled;

	private Timer timer;
	private static boolean needSendData;

	public KosmosPlayer() {
		super();
	}

	@Override
	public void init() {
		this.position = new Vector3f();
		this.rotation = new Vector3f();

		this.noclipEnabled = false;

		this.timer = new Timer(1.0 / 14.3); // 14.3 ticks per second.
		KosmosPlayer.needSendData = true;

		FlounderEvents.get().addEvent(new IEvent() {
			private MouseButton buttonRemove = new MouseButton(GLFW_MOUSE_BUTTON_RIGHT);

			@Override
			public boolean eventTriggered() {
				return buttonRemove.wasDown() && !FlounderGuis.get().getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				Ray cameraRay = FlounderCamera.get().getCamera().getViewRay();

				for (Entity entity : FlounderEntities.get().getEntities().getAll()) {
					if (entity.getCollider() != null && entity.getComponent(ComponentPlayer.class) == null && entity.getComponent(ComponentMultiplayer.class) == null && entity.getComponent(ComponentChunk.class) == null) {
						IntersectData data = entity.getCollider().intersects(cameraRay);
						float distance = Vector3f.getDistance(entity.getPosition(), KosmosWorld.get().getEntityPlayer().getPosition());

						if (data.isIntersection() && distance < 2.0f) {
							entity.forceRemove();
							return;
						}
					}
				}
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			private final int RECURSION_COUNT = 256;
			private final float RAY_RANGE = 70.0f;

			private MouseButton buttonPlace = new MouseButton(GLFW_MOUSE_BUTTON_LEFT);

			@Override
			public boolean eventTriggered() {
				return buttonPlace.wasDown() && !FlounderGuis.get().getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				Ray cameraRay = FlounderCamera.get().getCamera().getViewRay();

				if (intersectionInRange(cameraRay, 0.0f, RAY_RANGE)) {
					Vector3f terrainPosition = binarySearch(cameraRay, 0, 0, RAY_RANGE);

					if (terrainPosition.getY() >= 0.0f) {
						Chunk inChunk = null;

						for (Entity entity : FlounderEntities.get().getEntities().getAll()) {
							if (entity != null && entity instanceof Chunk) {
								Chunk chunk = (Chunk) entity;

								if (chunk.getSphere().contains(new Vector3f(terrainPosition.x, 0.0f, terrainPosition.z))) {
									inChunk = chunk;
								}
							}
						}

						if (inChunk == null) {
							FlounderLogger.get().error("Could not find chunk for terrain position: " + terrainPosition);
							return;
						}

						Vector2f tilePosition = Chunk.convertWorldToTile(inChunk, terrainPosition, null);
						tilePosition.x = Math.round(tilePosition.x);
						tilePosition.y = Math.round(tilePosition.y);
						Vector3f roundedPosition = Chunk.convertTileToWorld(inChunk, tilePosition.x, tilePosition.y, null);
						roundedPosition.y = Chunk.getWorldHeight(roundedPosition.x, roundedPosition.z);

						Entity entity = new InstanceBush(FlounderEntities.get().getEntities(),
								new Vector3f(
										roundedPosition.x,
										0.5f + roundedPosition.y * 0.5f,
										roundedPosition.z
								),
								new Vector3f()
						);
						new ComponentChild(entity, inChunk);
					}
				}
			}

			private Vector3f getPointOnRay(Ray cameraRay, float distance) {
				Vector3f camPos = FlounderCamera.get().getCamera().getPosition();
				Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
				Vector3f scaledRay = new Vector3f(cameraRay.getCurrentRay().x * distance, cameraRay.getCurrentRay().y * distance, cameraRay.getCurrentRay().z * distance);
				return Vector3f.add(start, scaledRay, null);
			}

			private Vector3f binarySearch(Ray cameraRay, int count, float start, float finish) {
				float half = start + ((finish - start) / 2.0f);

				if (count >= RECURSION_COUNT) {
					return getPointOnRay(cameraRay, half);
				}

				if (intersectionInRange(cameraRay, start, half)) {
					return binarySearch(cameraRay, count + 1, start, half);
				} else {
					return binarySearch(cameraRay, count + 1, half, finish);
				}
			}

			private boolean intersectionInRange(Ray cameraRay, float start, float finish) {
				Vector3f startPoint = getPointOnRay(cameraRay, start);
				Vector3f endPoint = getPointOnRay(cameraRay, finish);

				if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
					return true;
				} else {
					return false;
				}
			}

			private boolean isUnderGround(Vector3f testPoint) {
				float height = Chunk.getWorldHeight(testPoint.getX(), testPoint.getZ());

				if (height < 0.0f || testPoint.y < height) {
					return true;
				} else {
					return false;
				}
			}
		});
	}

	@Override
	public void update() {
		if (KosmosWorld.get().getEntityPlayer() == null) {
			return;
		}

		// Gets the current position and deltas.
		Vector3f newPosition = KosmosWorld.get().getEntityPlayer().getPosition();
		Vector3f newRotation = KosmosWorld.get().getEntityPlayer().getRotation();
		float dx = newPosition.x - position.x;
		float dy = newPosition.y - position.y;
		float dz = newPosition.z - position.z;
		float ry = newRotation.y - rotation.y;

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

		// Sets the current player position to the current entity.
		this.position.set(newPosition);
		this.rotation.set(newRotation);
	}

	/**
	 * Sends this players data to the server.
	 */
	private void sendData() {
		if (FlounderNetwork.get().getUsername() != null && FlounderNetwork.get().getSocketClient() != null && KosmosChunks.get().getCurrent() != null) {
			new PacketMove(FlounderNetwork.get().getUsername(), position, rotation, KosmosChunks.get().getCurrent().getPosition().x, KosmosChunks.get().getCurrent().getPosition().z).writeData(FlounderNetwork.get().getSocketClient());
			needSendData = false;
			timer.resetStartTime();
		}
	}

	public boolean isNoclipEnabled() {
		return noclipEnabled;
	}

	public void setNoclipEnabled(boolean noclipEnabled) {
		this.noclipEnabled = noclipEnabled;
	}

	public static void askSendData() {
		KosmosPlayer.needSendData = true;
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
