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
import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.chunks.biomes.*;
import kosmos.entities.instances.*;
import kosmos.world.*;

import java.util.*;

public class KosmosChunks extends Module {
	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	public static final MyFile TERRAINS_FOLDER = new MyFile(MyFile.RES_FOLDER, "terrains");

	private ISpatialStructure<Entity> chunks;

	private Entity entityPlayer;
	private Entity entitySun;
	private Entity entityMoon;

	private Sphere chunkRange;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	private ModelObject modelHexagon;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderEntities.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.chunks = new StructureBasic<>();

		this.entityPlayer = new InstancePlayer(FlounderEntities.getEntities(), new Vector3f(
				KosmosConfigs.configSave.getFloatWithDefault("player_x", 0.0f, () -> KosmosChunks.getEntityPlayer().getPosition().x),
				0.0f,
				KosmosConfigs.configSave.getFloatWithDefault("player_z", 0.0f, () -> KosmosChunks.getEntityPlayer().getPosition().z)
		), new Vector3f());
		this.entityMoon = new InstanceMoon(FlounderEntities.getEntities(), new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entitySun = new InstanceSun(FlounderEntities.getEntities(), new Vector3f(-200.0f, -200.0f, -200.0f), new Vector3f(0.0f, 0.0f, 0.0f));

		this.chunkRange = new Sphere(40.0f);

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		setCurrent(new Chunk(KosmosChunks.getChunks(), new Vector3f(
				KosmosConfigs.configSave.getFloatWithDefault("chunk_x", 0.0f, () -> KosmosChunks.getCurrent().getPosition().x),
				0.0f,
				KosmosConfigs.configSave.getFloatWithDefault("chunk_z", 0.0f, () -> KosmosChunks.getCurrent().getPosition().z)
		))); // The root chunk.

		this.modelHexagon = ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "hexagon.obj")).create();
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

	public static Entity getEntityPlayer() {
		return INSTANCE.entityPlayer;
	}

	public static Entity getEntitySun() {
		return INSTANCE.entitySun;
	}

	public static Entity getEntityMoon() {
		return INSTANCE.entityMoon;
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
