/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks;

import flounder.camera.*;
import flounder.entities.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

public class KosmosChunks extends Module {
	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	public static final MyFile TERRAINS_FOLDER = new MyFile(MyFile.RES_FOLDER, "terrains");

	private Sphere chunkRange;

	private ModelObject modelHexagon;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderEntities.class, FlounderModels.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.chunkRange = new Sphere(40.0f); // 3.0f * Chunk.CHUNK_WORLD_SIZE

		this.modelHexagon = ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "hexagon.obj")).create();

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.currentChunk = null;

		/*FlounderEvents.addEvent(new IEvent() {
			private MouseButton placeTree = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_1);

			@Override
			public boolean eventTriggered() {
				return placeTree.wasDown() && !FlounderGuis.getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				Ray cameraRay = FlounderCamera.getCamera().getViewRay();

				for (Entity entity : FlounderEntities.getEntities().getAll()) {
					if (entity.getCollider() != null && entity.getComponent(ComponentPlayer.ID) == null && entity.getComponent(ComponentMultiplayer.ID) == null) {
						IntersectData data = entity.getCollider().intersects(cameraRay);
						float distance = Vector3f.getDistance(entity.getPosition(), KosmosWorld.getEntityPlayer().getPosition());

						if (data.isIntersection() && distance < 4.20f) {
							entity.forceRemove(false);
							return;
						}
					}
				}
			}
		});*/

		/*FlounderEvents.addEvent(new IEvent() {
			private static final int RECURSION_COUNT = 512;
			private static final float RAY_RANGE = 32.0f;

			private MouseButton button = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_1);

			@Override
			public boolean eventTriggered() {
				return button.wasDown() && !FlounderGuis.getGuiMaster().isGamePaused();
			}

			@Override
			public void onEvent() {
				Ray cameraRay = FlounderCamera.getCamera().getViewRay();

				if (intersectionInRange(cameraRay, 0.0f, RAY_RANGE)) {
					Vector3f terrainPosition = binarySearch(cameraRay, 0, 0, RAY_RANGE);

					if (terrainPosition.getY() >= 0.0f) {
						new InstanceTreeBirchLarge(FlounderEntities.getEntities(),
								new Vector3f(
										terrainPosition.x,
										0.5f + terrainPosition.y * 0.5f,
										terrainPosition.z
								),
								new Vector3f()
						);
					}
				}
			}

			private Vector3f getPointOnRay(Ray cameraRay, float distance) {
				Vector3f camPos = FlounderCamera.getCamera().getPosition();
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

				if (height == Float.NEGATIVE_INFINITY || testPoint.y < height) {
					return true;
				} else {
					return false;
				}
			}
		});*/
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() != null) {
			Vector3f playerPos = new Vector3f(FlounderCamera.getPlayer().getPosition());
			playerPos.y = 0.0f;

			Chunk playerChunk = null;

			if (!playerPos.equals(lastPlayerPos)) {
				chunkRange.update(playerPos, null, 1.0f, chunkRange);
			}

			// Goes though all chunks looking for changes.
			for (Entity entity : FlounderEntities.getEntities().getAll()) {
				if (entity != null && entity instanceof Chunk) {
					Chunk chunk = (Chunk) entity;

					// Checks if the player position is in this chunk.
					if (chunk.isLoaded() && chunk.getCollider() != null && chunk.getCollider().contains(playerPos)) {
						// This chunk is now the chunk with the player in it.
						playerChunk = chunk;
					}

					// Updates the chunk.
					chunk.update();
				}
			}

			// This chunk is now the current chunk.
			setCurrent(playerChunk);

			// Updates the last player position value.
			lastPlayerPos.set(playerPos);
		}

		// Renders the chunks range.
		// FlounderBounding.addShapeRender(chunkRange);
	}

	@Override
	public void profile() {
		//	FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Size", chunks.getSize());
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Current", currentChunk);
	}

	public static Chunk getCurrent() {
		return INSTANCE.currentChunk;
	}

	/**
	 * Sets the current chunk that that player is contained in. This will generate surrounding chunks.
	 *
	 * @param currentChunk The chunk to be set as the current.
	 */
	public static void setCurrent(Chunk currentChunk) {
		if (currentChunk != null && INSTANCE.currentChunk != currentChunk) {
			// Creates the children chunks for the new current chunk.
			currentChunk.createChunksAround();
			currentChunk.getChildrenChunks().forEach(Chunk::createChunksAround);

			// Removes any old chunks that are out of range.
			Iterator<Entity> it = FlounderEntities.getEntities().getAll().iterator();

			while (it.hasNext()) {
				Entity entity = it.next();

				if (entity != null && entity instanceof Chunk) {
					Chunk chunk = (Chunk) entity;

					if (chunk != currentChunk && chunk.isLoaded()) {
						if (!chunk.getCollider().intersects(INSTANCE.chunkRange).isIntersection() && !INSTANCE.chunkRange.contains(chunk.getCollider())) {
							chunk.delete();
							it.remove();
						}
					}
				}
			}

			// The current instance chunk is what was calculated for in this function.
			INSTANCE.currentChunk = currentChunk;
		}
	}

	/**
	 * Clears all chunks, then creates a current at the previous chunks position.
	 *
	 * @param loadCurrent If the current chunk will be replaced.
	 */
	public static void clear(boolean loadCurrent) {
		// Removes any chunks in the entity list.
		Iterator<Entity> it = FlounderEntities.getEntities().getAll().iterator();

		while (it.hasNext()) {
			Entity entity = it.next();

			if (entity != null && entity instanceof Chunk) {
				Chunk chunk = (Chunk) entity;
				chunk.delete();
				it.remove();
			}
		}

		// Sets up the new root chunk.
		if (loadCurrent && getCurrent() != null) {
			setCurrent(new Chunk(FlounderEntities.getEntities(), getCurrent().getPosition()));
		}
	}

	/**
	 * Gets the default hexagon model.
	 *
	 * @return The hexagon model.
	 */
	public static ModelObject getModelHexagon() {
		return INSTANCE.modelHexagon;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		clear(false);
	}
}
