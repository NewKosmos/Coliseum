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
import flounder.noise.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.tasks.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.chunks.map.*;

import java.util.*;

public class KosmosChunks extends Module {
	public static final MyFile TERRAINS_FOLDER = new MyFile(MyFile.RES_FOLDER, "terrains");

	private PerlinNoise noise;
	private MapGenerator mapGenerator;
	private Sphere chunkRange;
	private ModelObject[] hexagons;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	private int chunkDistance;

	public KosmosChunks() {
		super(FlounderEvents.class, FlounderTasks.class, FlounderEntities.class, FlounderModels.class, FlounderTextures.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.noise = new PerlinNoise(-1);
		this.mapGenerator = new MapGenerator();
		this.chunkRange = new Sphere(40.0f);
		this.hexagons = new ModelObject[]{
				ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "models", "hexagon_u.obj")).create(), // 0
				ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "models", "hexagon_l.obj")).create(), // 1
				ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "models", "hexagon_0.obj")).create(), // 2
				ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "models", "hexagon_1.obj")).create(), // 3
				ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "models", "hexagon_2.obj")).create(), // 4
				ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "models", "hexagon_3.obj")).create(), // 5
				ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "models", "hexagon_4.obj")).create(), // 6
				ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "models", "hexagon_5.obj")).create(), // 7
		};

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.currentChunk = null;

		this.chunkDistance = KosmosConfigs.CHUNK_DISTANCE.getInteger();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		if (FlounderCamera.get().getPlayer() != null) {
			Vector3f playerPos = new Vector3f(FlounderCamera.get().getPlayer().getPosition());
			playerPos.y = 0.0f;

			Chunk playerChunk = null;

			if (!playerPos.equals(lastPlayerPos)) {
				chunkRange.setRadius(10.0f + ((1 + chunkDistance) * Chunk.CHUNK_WORLD_SIZE));
				chunkRange.update(playerPos, null, 1.0f, chunkRange);
			}

			// Goes though all chunks looking for changes.
			for (Entity entity : FlounderEntities.get().getEntities().getAll(null)) {
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
		FlounderBounding.get().addShapeRender(chunkRange);
	}

	public PerlinNoise getNoise() {
		return this.noise;
	}

	public MapGenerator getMapGenerator() {
		return mapGenerator;
	}

	/**
	 * Gets the hexagon models.
	 *
	 * @return The hexagon models.
	 */
	public ModelObject[] getHexagons() {
		return this.hexagons;
	}

	public boolean getHexagonsLoaded() {
		for (ModelObject model : hexagons) {
			if (model == null || !model.isLoaded()) {
				return false;
			}
		}

		return true;
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
			// Removes any old chunks that are out of range.
			Iterator<Entity> it = FlounderEntities.get().getEntities().iterator();

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

			// Creates chunks around the new current chunk for a range, does not include the current chunk.
			currentChunk.createChunksAround(chunkDistance);
		}
	}

	/**
	 * Clears all chunks, then creates a current at the previous chunks position.
	 *
	 * @param loadCurrent If the current chunk will be replaced.
	 */
	public void clear(boolean loadCurrent) {
		// Removes any chunks in the entity list.
		Iterator<Entity> it = FlounderEntities.get().getEntities().iterator();

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

	public int getChunkDistance() {
		return chunkDistance;
	}

	public void setChunkDistance(int chunkDistance) {
		this.chunkDistance = chunkDistance;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		clear(false);
		mapGenerator.delete();
	}

	@Module.Instance
	public static KosmosChunks get() {
		return (KosmosChunks) Framework.get().getInstance(KosmosChunks.class);
	}

	@Module.TabName
	public static String getTab() {
		return "Kosmos Chunks";
	}
}
