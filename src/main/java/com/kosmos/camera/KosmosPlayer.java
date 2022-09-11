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
import com.flounder.entities.*;
import com.flounder.entities.components.*;
import com.flounder.events.*;
import com.flounder.guis.*;
import com.flounder.inputs.*;
import com.flounder.logger.*;
import com.flounder.maths.*;
import com.flounder.maths.Timer;
import com.flounder.maths.vectors.*;
import com.flounder.networking.*;
import com.flounder.physics.*;
import com.kosmos.*;
import com.kosmos.entities.components.*;
import com.kosmos.entities.instances.*;
import com.kosmos.network.packets.*;
import com.kosmos.world.*;
import com.kosmos.world.chunks.*;

import java.util.*;

import static com.flounder.platform.Constants.*;

public class KosmosPlayer extends Player {
	public static final float PLAYER_OFFSET_Y = (float) (Math.sqrt(2.0) * 0.25);
	public static final float PLAYER_TAG_Y = 2.25f + PLAYER_OFFSET_Y;

	public static final float RUN_SPEED = 6.0f;
	public static final float STRAFE_SPEED = 4.0f;
	public static final float BOOST_MUL = 2.0f;
	public static final float JUMP_POWER = 8.0f;
	public static final float FLY_SPEED = 8.0f;

	private Vector3f position;
	private Vector3f rotation;

	private MouseButton buttonRemove;
	private List<Entity> entityObjects;

	private boolean noclipEnabled;

	private Timer needSendTimer;
	private boolean needSendData;

	private static String username;

	public KosmosPlayer() {
		super();
	}

	@Override
	public void init() {
		this.position = new Vector3f();
		this.rotation = new Vector3f();

		this.buttonRemove = new MouseButton(GLFW_MOUSE_BUTTON_RIGHT);
		this.entityObjects = new ArrayList<>();

		this.noclipEnabled = false;

		this.needSendTimer = new Timer(1.0 / 10.0); // 10.0 ticks per second.
		this.needSendData = true;

		KosmosPlayer.username = KosmosConfigs.CLIENT_USERNAME.setReference(() -> username).getString();

		FlounderEvents.get().addEvent(new EventStandard() {
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
						Vector3f test = new Vector3f(terrainPosition.x, 0.0f, terrainPosition.z);
						Chunk inChunk = null;

						for (Entity entity : FlounderEntities.get().getEntities().getAll(null)) {
							if (entity != null && entity instanceof Chunk) {
								Chunk chunk = (Chunk) entity;

								if (chunk.getSphere().contains(test)) {
									inChunk = chunk;
								}
							}
						}

						if (inChunk == null) {
							FlounderLogger.get().error("Could not find chunk for terrain position: " + terrainPosition);
							return;
						}

						Vector2f tilePosition = KosmosChunks.convertWorldToTile(inChunk, terrainPosition, null);
						tilePosition.x = Math.round(tilePosition.x);
						tilePosition.y = Math.round(tilePosition.y);
						Vector3f roundedPosition = KosmosChunks.convertTileToWorld(inChunk, tilePosition.x, tilePosition.y, null);
						roundedPosition.y = KosmosChunks.getWorldHeight(roundedPosition.x, roundedPosition.z);

						Entity entity = new InstanceTable(FlounderEntities.get().getEntities(),
								new Vector3f(
										roundedPosition.x,
										0.5f + roundedPosition.y * 0.5f,
										roundedPosition.z
								),
								new Vector3f()
						);
						new ComponentChild(entity, inChunk);
						new ComponentSelect(entity);
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
				float height = KosmosChunks.getWorldHeight(testPoint.getX(), testPoint.getZ());

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

		if (!FlounderGuis.get().getGuiMaster().isGamePaused()) {
			Ray cameraRay = FlounderCamera.get().getCamera().getViewRay();

			if (KosmosWorld.get().getEntityPlayer() != null) {
				for (Entity entity : FlounderEntities.get().getEntities().queryInFrustum(FlounderCamera.get().getCamera().getViewFrustum(), entityObjects)) {
					if (entity.getCollider() != null && entity.getComponent(ComponentPlayer.class) == null && entity.getComponent(ComponentMultiplayer.class) == null && entity.getComponent(ComponentChunk.class) == null) {
						IntersectData data = entity.getCollider().intersects(cameraRay);
						float distance = Vector3f.getDistance(entity.getPosition(), KosmosWorld.get().getEntityPlayer().getPosition());

						if (data.isIntersection() && distance < 3.0f) {
							if (buttonRemove.wasDown()) {
								ComponentChild componentChild = ((ComponentChild) entity.getComponent(ComponentChild.class));

								if (componentChild != null && FlounderNetwork.get().getSocketClient() != null) {
									new PacketEntityRemove(FlounderNetwork.get().getUsername(), componentChild.getParent().getPosition(), entity.getPosition()).writeData(FlounderNetwork.get().getSocketClient());
								}

								entity.forceRemove();
							} else {
								ComponentSelect componentSelect = ((ComponentSelect) entity.getComponent(ComponentSelect.class));

								if (componentSelect != null) {
									componentSelect.setSelected(true);
								}
							}
						}
					}
				}
			}
		}

		// Gets the current position of the player.
		Vector3f newPosition = KosmosWorld.get().getEntityPlayer().getPosition();
		Vector3f newRotation = KosmosWorld.get().getEntityPlayer().getRotation();

		// Try's to send data when needed.
		if (needSendData || needSendTimer.isPassedTime()) {
			// Gets the current position and deltas.
			float dx = newPosition.x - position.x;
			float dy = newPosition.y - position.y;
			float dz = newPosition.z - position.z;
			float ry = newRotation.y - rotation.y;

			// Try to send data to the server if needed.
			if (needSendData || dx != 0.0f || dy != 0.0f || dz != 0.0f || ry != 0.0f) {
				// Sends this players data to the server.
				if (FlounderNetwork.get().getUsername() != null && FlounderNetwork.get().getSocketClient() != null && KosmosChunks.get().getCurrent() != null) {
					new PacketMove(FlounderNetwork.get().getUsername(), position, rotation, KosmosChunks.get().getCurrent().getPosition().x, KosmosChunks.get().getCurrent().getPosition().z).writeData(FlounderNetwork.get().getSocketClient());
				}

				needSendData = false;
				needSendTimer.resetStartTime();
			}
		}

		// Sets the current player position to the current entity.
		this.position.set(newPosition);
		this.rotation.set(newRotation);
	}

	public boolean isNoclipEnabled() {
		return noclipEnabled;
	}

	public void setNoclipEnabled(boolean noclipEnabled) {
		this.noclipEnabled = noclipEnabled;
	}

	public void askSendData() {
		this.needSendData = true;
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		KosmosPlayer.username = username;
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
