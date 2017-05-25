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
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.noise.*;
import flounder.physics.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.chunks.map.*;

import java.util.*;

public class KosmosChunks extends Module {
	public static final MyFile TERRAINS_FOLDER = new MyFile(MyFile.RES_FOLDER, "terrains");

	private PerlinNoise noise;
	private MapGenerator mapGenerator;
	private Sphere chunkRange;
	private ModelObject modelHexagon;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	public KosmosChunks() {
		super(FlounderEvents.class, FlounderEntities.class, FlounderModels.class, FlounderTextures.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.noise = new PerlinNoise(-1);
		this.mapGenerator = new MapGenerator();
		this.chunkRange = new Sphere(40.0f); // 3.0f * Chunk.CHUNK_WORLD_SIZE
		this.modelHexagon = ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "hexagon.obj")).create();

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.currentChunk = null;
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		if (FlounderCamera.get().getPlayer() != null) {
			Vector3f playerPos = new Vector3f(FlounderCamera.get().getPlayer().getPosition());
			playerPos.y = 0.0f;

			Chunk playerChunk = null;

			if (!playerPos.equals(lastPlayerPos)) {
				chunkRange.update(playerPos, null, 1.0f, chunkRange);
			}

			// Goes though all chunks looking for changes.
			for (Entity entity : new ArrayList<>(FlounderEntities.get().getEntities().getAll())) {
				if (entity != null && entity instanceof Chunk) {
					Chunk chunk = (Chunk) entity;

					// Checks if the player position is in this chunk.
					if (chunk.isLoaded() && chunk.getSphere() != null && chunk.getSphere().contains(playerPos)) {
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

	@Handler.Function(Handler.FLAG_PROFILE)
	public void profile() {
		//	FlounderProfiler.get().add(getTab(), "Chunks Size", chunks.getSize());
		FlounderProfiler.get().add(getTab(), "Chunks Current", currentChunk);
		FlounderProfiler.get().add(getTab(), "Seed", noise.getSeed());
	}

	public PerlinNoise getNoise() {
		return this.noise;
	}

	public MapGenerator getMapGenerator() {
		return mapGenerator;
	}

	/**
	 * Gets the default hexagon model.
	 *
	 * @return The hexagon model.
	 */
	public ModelObject getModelHexagon() {
		return this.modelHexagon;
	}

	public Chunk getCurrent() {
		return this.currentChunk;
	}

	/**
	 * Sets the current chunk that that player is contained in. This will generate surrounding chunks.
	 *
	 * @param currentChunk The chunk to be set as the current.
	 */
	public void setCurrent(Chunk currentChunk) {
		if (currentChunk != null && this.currentChunk != currentChunk) {
			// Creates the children chunks for the new current chunk.
			currentChunk.createChunksAround(new Single<>(2)); // TODO: Make work?

			// Removes any old chunks that are out of range.
			Iterator<Entity> it = FlounderEntities.get().getEntities().getAll().iterator();

			while (it.hasNext()) {
				Entity entity = it.next();

				if (entity != null && entity instanceof Chunk) {
					Chunk chunk = (Chunk) entity;

					if (chunk != currentChunk && chunk.isLoaded()) {
						if (!chunk.getSphere().intersects(this.chunkRange).isIntersection() && !this.chunkRange.contains(chunk.getSphere())) {
							chunk.delete();
							it.remove();
						}
					}
				}
			}

			// The current instance chunk is what was calculated for in this function.
			this.currentChunk = currentChunk;
		}
	}

	/**
	 * Clears all chunks, then creates a current at the previous chunks position.
	 *
	 * @param loadCurrent If the current chunk will be replaced.
	 */
	public void clear(boolean loadCurrent) {
		// Removes any chunks in the entity list.
		Iterator<Entity> it = FlounderEntities.get().getEntities().getAll().iterator();

		while (it.hasNext()) {
			Entity entity = it.next();

			if (entity != null && entity instanceof Chunk) {
				Chunk chunk = (Chunk) entity;
				chunk.delete();
				it.remove();
			}
		}

		// Sets up the new root chunk.
		if (loadCurrent && currentChunk != null) {
			setCurrent(new Chunk(FlounderEntities.get().getEntities(), currentChunk.getPosition()));
		} else {
			currentChunk = null;
			lastPlayerPos.set(0.0f, 0.0f, 0.0f);
		}
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		clear(false);
		mapGenerator.delete();
	}

	@Module.Instance
	public static KosmosChunks get() {
		return (KosmosChunks) Framework.getInstance(KosmosChunks.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Kosmos Chunks";
	}
}
