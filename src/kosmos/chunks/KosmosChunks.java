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
	private static final int MAP_SIZE = 1024;

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
		BufferedImage imageIsland = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
		BufferedImage imageHeight = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
		BufferedImage imageMoisture = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
		BufferedImage imageBiome = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);

		FlounderLogger.get().log("Generating map for seed: " + seed);

		for (int y = 0; y < MAP_SIZE; y++) {
			for (int x = 0; x < MAP_SIZE; x++) {
				float worldX = ((float) x / ((float) MAP_SIZE / (float) Chunk.WORLD_SIZE)) - ((float) Chunk.WORLD_SIZE / 2.0f);
				float worldZ = ((float) y / ((float) MAP_SIZE / (float) Chunk.WORLD_SIZE)) - ((float) Chunk.WORLD_SIZE / 2.0f);

				float factorIsland = Chunk.getIslandMap(worldX, worldZ);
				imageIsland.setRGB(x, y, (((int) (255.0f * factorIsland) << 8) + ((int) (255.0f * factorIsland)) << 8) + ((int) (255.0f * factorIsland)));

				float factorHeight = Chunk.getHeightMap(worldX, worldZ);
				imageHeight.setRGB(x, y, (((int) (255.0f * factorHeight) << 8) + ((int) (255.0f * factorHeight)) << 8) + ((int) (255.0f * factorHeight)));

				float factorMoisture = Chunk.getMoistureMap(worldX, worldZ);
				Colour colourMoisture = Colour.interpolate(new Colour(1.0f, 0.0f, 0.0f), new Colour(0.0f, 0.0f, 1.0f), factorMoisture, null);
				imageMoisture.setRGB(x, y, (((int) (255.0f * colourMoisture.r) << 8) + ((int) (255.0f * colourMoisture.g)) << 8) + ((int) (255.0f * colourMoisture.b)));

				Colour colourBiome = new Colour(0.0f, 0.0f, 0.0f);
				if (factorHeight <= 0.125f) {
					colourBiome.set(0.0824f, 0.396f, 0.753f); // Ocean.
				} /* else {
					colourBiome.r = 161.200853168913f + (30.1885001433901f * factorHeight) + (8.20691138514417f * factorMoisture);
					colourBiome.g = 201.866038141669f - (0.0741611700597815f * factorHeight) + (11.6816030972177f * factorMoisture);
					colourBiome.b = 161.200853168913f + (30.1885001433901f * factorHeight) + (8.20691138514417f * factorMoisture);
				}*/ else if (factorHeight <= 0.25f) {
					if (factorMoisture <= 0.16f) {
						colourBiome.set(233, 221, 199, true); // Subtropical Desert.
					} else if (factorMoisture <= 0.33f) {
						colourBiome.set(196, 212, 170, true); // Grassland.
					} else if (factorMoisture <= 0.66f) {
						colourBiome.set(169, 204, 164, true); // Tropical Seasonal Forest.
					} else if (factorMoisture <= 1.0f) {
						colourBiome.set(164, 196, 168, true); // Temperate Rain Forest.
					}
				} else if (factorHeight <= 0.5f) {
					if (factorMoisture <= 0.16f) {
						colourBiome.set(228, 232, 202, true); // Temperate Desert.
					} else if (factorMoisture <= 0.5f) {
						colourBiome.set(196, 212, 170, true); // Grassland.
					} else if (factorMoisture <= 0.83f) {
						colourBiome.set(180, 201, 169, true); // Temperate Deciduous Forest.
					} else if (factorMoisture <= 1.0f) {
						colourBiome.set(164, 196, 168, true); // Temperate Rain Forest.
					}
				} else if (factorHeight <= 0.75f) {
					if (factorMoisture <= 0.33f) {
						colourBiome.set(228, 232, 202, true); // Temperate Desert.
					} else if (factorMoisture <= 0.66f) {
						colourBiome.set(196, 204, 187, true); // Shrubland.
					} else if (factorMoisture <= 1.0f) {
						colourBiome.set(204, 212, 187, true); // Tiga.
					}
				} else if (factorHeight <= 1.0f) {
					if (factorMoisture <= 0.16f) {
						colourBiome.set(153, 153, 153, true); // Scorched.
					} else if (factorMoisture <= 0.33f) {
						colourBiome.set(187, 187, 187, true); // Bare.
					} else if (factorMoisture <= 0.5f) {
						colourBiome.set(221, 221, 187, true); // Tundra.
					} else if (factorMoisture <= 1.0f) {
						colourBiome.set(255, 255, 255, true); // Snow.
					}
				}
				imageBiome.setRGB(x, y, (((int) (255.0f * colourBiome.r) << 8) + ((int) (255.0f * colourBiome.g)) << 8) + ((int) (255.0f * colourBiome.b)));
			}
		}

		File outputIsland = new File(Framework.getRoamingFolder().getPath() + "/saves/" + seed + "-island.png");
		File outputHeight = new File(Framework.getRoamingFolder().getPath() + "/saves/" + seed + "-height.png");
		File outputMoisture = new File(Framework.getRoamingFolder().getPath() + "/saves/" + seed + "-moisture.png");
		File outputBiome = new File(Framework.getRoamingFolder().getPath() + "/saves/" + seed + "-biome.png");

		try {
			// Save the map texture.
			ImageIO.write(imageIsland, "png", outputIsland);
			ImageIO.write(imageHeight, "png", outputHeight);
			ImageIO.write(imageMoisture, "png", outputMoisture);
			ImageIO.write(imageBiome, "png", outputBiome);

			// Remove old map texture.
			if (mapTexture != null && mapTexture.isLoaded()) {
				mapTexture.delete();
			}

			// Load the map texture.
			mapTexture = TextureFactory.newBuilder().setFile(new MyFile(Framework.getRoamingFolder(), "saves", seed + "-biome.png")).create();
		} catch (IOException e) {
			FlounderLogger.get().error("Could not save map image to file: " + outputBiome);
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
