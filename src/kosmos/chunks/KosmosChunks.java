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
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.world.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class KosmosChunks extends Module {
	public static final MyFile TERRAINS_FOLDER = new MyFile(MyFile.RES_FOLDER, "terrains");

	// The size of the rendered map image.
	private static final int MAP_SIZE = 512;

	private Sphere chunkRange;

	private ModelObject modelHexagon;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	private TextureObject mapTexture;

	public KosmosChunks() {
		super(FlounderEvents.class, FlounderEntities.class, FlounderModels.class, FlounderTextures.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.chunkRange = new Sphere(40.0f); // 3.0f * Chunk.CHUNK_WORLD_SIZE

		this.modelHexagon = ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "hexagon.obj")).create();

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.currentChunk = null;

		this.mapTexture = null;

		FlounderEvents.get().addEvent(new IEvent() {
			private int seed = KosmosWorld.get().getNoise().getSeed();

			@Override
			public boolean eventTriggered() {
				int currentSeed = KosmosWorld.get().getNoise().getSeed();
				boolean changed = seed != currentSeed && currentSeed != -1;
				seed = currentSeed;
				return changed;
			}

			@Override
			public void onEvent() {
				KosmosChunks.get().clear(true);
				KosmosChunks.get().generateMap();
			}
		});
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
	}

	/**
	 * Generates a map for the current seed.
	 */
	private void generateMap() {
		int seed = KosmosWorld.get().getNoise().getSeed();
		BufferedImage imageOutput = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);

		FlounderLogger.get().log("Generating map for seed: " + seed);

		for (int y = 0; y < MAP_SIZE; y++) {
			for (int x = 0; x < MAP_SIZE; x++) {
				Colour mapColour = Chunk.getWorldBiome((x / ((float) MAP_SIZE / (float) Chunk.WORLD_SIZE)) - ((float) Chunk.WORLD_SIZE / 2.0f), (y / ((float) MAP_SIZE / (float) Chunk.WORLD_SIZE)) - ((float) Chunk.WORLD_SIZE / 2.0f)).getBiome().getColour();
				// float typeHeight = 0.1f + Chunk.getWorldHeight(x - (Chunk.WORLD_SIZE / 2.0f), y - (Chunk.WORLD_SIZE / 2.0f)) / 10.0f;

				int rgb = (int) (255.0f * mapColour.r);
				rgb = (rgb << 8) + ((int) (255.0f * mapColour.g));
				rgb = (rgb << 8) + ((int) (255.0f * mapColour.b));
				imageOutput.setRGB(x, y, rgb);
			}
		}

		File fileOutput = new File(Framework.getRoamingFolder().getPath() + "/saves/map-" + seed + ".png");
		MyFile fileSeed = new MyFile(Framework.getRoamingFolder(), "saves", "map-" + seed + ".png");

		try {
			// Save the map texture..
			ImageIO.write(imageOutput, "png", fileOutput);

			// Remove old map texture.
			if (mapTexture != null && mapTexture.isLoaded()) {
				mapTexture.delete();
			}

			// Load the map texture.
			mapTexture = TextureFactory.newBuilder().setFile(fileSeed).create();
		} catch (IOException e) {
			FlounderLogger.get().error("Could not save map image to file: " + fileOutput);
			FlounderLogger.get().exception(e);
		}
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
		}
	}

	public TextureObject getMapTexture() {
		return mapTexture;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		clear(false);
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
