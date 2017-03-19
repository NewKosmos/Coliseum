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
import flounder.inputs.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.entities.instances.*;
import org.lwjgl.glfw.*;

import java.util.*;

public class KosmosChunks extends Module {
	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	public static final MyFile TERRAINS_FOLDER = new MyFile(MyFile.RES_FOLDER, "terrains");

	private ISpatialStructure<Entity> chunks;

	private Sphere chunkRange;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	private ModelObject modelHexagon;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderEvents.class, FlounderBounding.class, FlounderEntities.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.chunks = new StructureBasic<>();

		this.chunkRange = new Sphere(40.0f); // 3.0f * Chunk.CHUNK_WORLD_SIZE

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		setCurrent(new Chunk(KosmosChunks.getChunks(), new Vector3f(KosmosConfigs.SAVE_CHUNK_X.getFloat(), 0.0f, KosmosConfigs.SAVE_CHUNK_Z.getFloat()))); // The root chunk.

		this.modelHexagon = ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "hexagon.obj")).create();

		FlounderEvents.addEvent(new IEvent() {
			private static final int RECURSION_COUNT = 200;
			private static final float RAY_RANGE = 100;

			private MouseButton button = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_1);

			@Override
			public boolean eventTriggered() {
				return button.wasDown();
			}

			@Override
			public void onEvent() {
				Ray cameraRay = FlounderCamera.getCamera().getViewRay();

				if (intersectionInRange(cameraRay, 0.0f, RAY_RANGE)) {
					Vector3f terrainPosition = binarySearch(cameraRay, 0, 0, RAY_RANGE);

					if (terrainPosition.getY() >= 0.0f) {
						new InstanceTree1(FlounderEntities.getEntities(),
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
		});
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() != null) {
			Vector3f playerPos = FlounderCamera.getPlayer().getPosition();
			Chunk playerChunk = null;

			if (!playerPos.equals(lastPlayerPos)) {
				Sphere.recalculate(chunkRange, playerPos, 1.0f, chunkRange);
			}

			// Goes though all chunks looking for changes.
			for (Entity entity : chunks.getAll()) {
				Chunk chunk = (Chunk) entity;

				if (chunk != null) {
					// Checks if the player position is in this chunk.
					if (chunk.isLoaded() && chunk.getBounding() != null && chunk.getBounding().contains(playerPos)) {
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
		FlounderBounding.addShapeRender(chunkRange);
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Size", chunks.getSize());
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Current", currentChunk);
	}

	public static ISpatialStructure<Entity> getChunks() {
		return INSTANCE.chunks;
	}

	public static Chunk getCurrent() {
		return INSTANCE.currentChunk;
	}

	/**
	 * Sets the current chunk that that player is contained in. This will generate surrounding chunks.
	 *
	 * @param currentChunk
	 */
	public static void setCurrent(Chunk currentChunk) {
		if (currentChunk != null && INSTANCE.currentChunk != currentChunk) {
			// Creates the children chunks for the new current chunk.
			currentChunk.createChunksAround();
			currentChunk.getChildrenChunks().forEach(Chunk::createChunksAround);

			// Removes any old chunks that are out of range.
			Iterator it = INSTANCE.chunks.getAll().iterator();

			while (it.hasNext()) {
				Chunk chunk = (Chunk) it.next();

				if (chunk != currentChunk && chunk.isLoaded()) {
					if (!chunk.getBounding().intersects(INSTANCE.chunkRange).isIntersection() && !INSTANCE.chunkRange.contains((Sphere) chunk.getBounding())) {
						chunk.delete();
						it.remove();
					}
				}
			}

			// The current instance chunk is what was calculated for in this function.
			INSTANCE.currentChunk = currentChunk;
		}
	}

	/**
	 * Clears all chunks, then creates a current at the previous chunks position.
	 */
	public static void clear() {
		INSTANCE.chunks.getAll().forEach((Entity chunk) -> ((Chunk) chunk).delete());
		INSTANCE.chunks.clear();

		// Sets up the new root chunk.
		setCurrent(new Chunk(KosmosChunks.getChunks(), getCurrent().getPosition()));
	}

	public static ModelObject getModelHexagon() {
		return INSTANCE.modelHexagon;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		clear();
	}
}
