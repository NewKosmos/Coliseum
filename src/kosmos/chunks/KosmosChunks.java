/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.chunks;

import flounder.camera.*;
import flounder.entities.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.noise.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.tasks.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.chunks.biomes.*;
import kosmos.chunks.map.*;

import java.util.*;

public class KosmosChunks extends Module {
	public static final MyFile TERRAINS_FOLDER = new MyFile(MyFile.RES_FOLDER, "terrains");

	// The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.
	public static final int CHUNK_RADIUS = 7;

	// Each tile can be broken into equilateral triangles with sides of length.
	public static final double HEXAGON_SIDE_LENGTH = 2.0;

	// The overall world radius footprint per chunk.
	public static final float CHUNK_WORLD_SIZE = (float) Math.sqrt(3.0) * (CHUNK_RADIUS - 0.5f);

	// Island world generations.
	public static final int WORLD_SIZE = 1536; // The width and height of the world, in tile size.
	public static final float WORLD_NOISE_HEIGHT = 43.0f; // The height multiplier, max world height.
	public static final float WORLD_ISLAND_INSIDE = 0.80f; // The inside radius of the island shape.
	public static final float WORLD_ISLAND_OUTSIDE = 1.0f; // The outside radius of the island shape.
	public static final float WORLD_ISLAND_PARAMETER = 0.4f; // The shape parameter (0=circular, 1=rectangular).

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
				chunkRange.setRadius(10.0f + ((1 + chunkDistance) * CHUNK_WORLD_SIZE));
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

	public static Vector3f convertTileToChunk(double x, double z, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		double cz = (3.0 / 2.0) * HEXAGON_SIDE_LENGTH * z;
		double cx = Math.sqrt(3.0) * HEXAGON_SIDE_LENGTH * ((z / 2.0) + x);
		return destination.set((float) cx, 0.0f, (float) cz);
	}

	public static Vector3f convertTileToWorld(Chunk chunk, double x, double z, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		double wz = (3.0 / 4.0) * HEXAGON_SIDE_LENGTH * z;
		double wx = (Math.sqrt(3.0) / 2.0) * HEXAGON_SIDE_LENGTH * ((z / 2.0) + x);
		return destination.set((float) wx + chunk.getPosition().x, 0.0f, (float) wz + chunk.getPosition().z);
	}

	public static Vector2f convertWorldToTile(Chunk chunk, Vector3f worldPosition, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		double tz = (4.0 * (worldPosition.z - chunk.getPosition().z)) / (3.0 * HEXAGON_SIDE_LENGTH);
		double tx = ((2.0 * (worldPosition.x - chunk.getPosition().x)) / (Math.sqrt(3.0) * HEXAGON_SIDE_LENGTH)) - (tz / 2.0);
		return destination.set((float) tx, (float) tz);
	}

	/**
	 * Gets the island factor for a position in the world.
	 *
	 * @param positionX The worlds X position.
	 * @param positionZ The worlds Z position.
	 *
	 * @return The island factor at that world position.
	 */
	public static float getIslandMap(float positionX, float positionZ) {
		if (KosmosChunks.get().getNoise().getSeed() == -1) {
			return 0.0f;
		}

		float circular = (float) Math.sqrt(Math.pow(positionX, 2) + Math.pow(positionZ, 2)); // The current radius (circular map).
		float rectangular = Math.max(Math.abs(positionX), Math.abs(positionZ)); // The current radius (rectangular map).
		float reading = ((1.0f - WORLD_ISLAND_PARAMETER) * circular) + (WORLD_ISLAND_PARAMETER * rectangular);

		float radius1 = WORLD_ISLAND_INSIDE * (WORLD_SIZE / 2.0f); // The inside radius to the blur.
		float radius2 = WORLD_ISLAND_OUTSIDE * (WORLD_SIZE / 2.0f); // The outside radius to the blur.

		if (positionX == 0.0f && positionZ == 0.0f) { // The special case where the reading is undefined.
			return 1.0f;
		} else if (reading > radius2) { // If outside the upper bound there is no factor!
			return 0.0f;
		} else if (reading >= radius1) { // Something between upper and lower, uses cos interpolation.
			float blend = Maths.clamp((reading - radius1) / (radius2 - radius1), 0.0f, 1.0f);
			return Maths.clamp(Maths.cosInterpolate(1.0f, 0.0f, blend), 0.0f, 1.0f);
		} else { // Fully inside of the lower radius, so full factor.
			return 1.0f;
		}
	}

	/**
	 * Gets the terrain height for a position in the world.
	 *
	 * @param positionX The worlds X position.
	 * @param positionZ The worlds Z position.
	 *
	 * @return The found height at that world position.
	 */
	public static float getHeightMap(float positionX, float positionZ) {
		// Gets the height from a perlin noise map and from the island factor.
		float island = getIslandMap(positionX, positionZ);
		float height = island * 1.70f * KosmosChunks.get().getNoise().turbulence((positionX + WORLD_SIZE) / 400.0f, (positionZ + WORLD_SIZE) / 400.0f, 40.0f);
		height = Maths.clamp(height, 0.0f, 1.0f);

		// Ignore height that would be water/nothing.
		if (height <= 0.1f) {
			return Float.NEGATIVE_INFINITY;
		}

		// Returns the final height,
		return height;
	}

	/**
	 * Gets the world terrain height for a position in the world.
	 *
	 * @param positionX The worlds X position.
	 * @param positionZ The worlds Z position.
	 *
	 * @return The found height at that world position.
	 */
	public static float getWorldHeight(float positionX, float positionZ) {
		float height = getHeightMap(positionX, positionZ) * WORLD_NOISE_HEIGHT;
		height = (float) Math.sqrt(2.0) * (int) height;
		height -= 5.6f;

		if (height < 0.0f) {
			return Float.NEGATIVE_INFINITY;
		}

		// Returns the final height,
		return height;
	}

	/**
	 * Gets the terrain height for a position in the world.
	 * The world position is rounded into tile space and then back into world space, creating positions rounded the the centres of the tiles.
	 *
	 * @param chunk The chunk to get the position from.
	 * @param worldPosition The world position to sample from.
	 *
	 * @return The found height at that world position.
	 */
	public static float roundedHeight(Chunk chunk, Vector3f worldPosition) {
		Vector2f tilePosition = convertWorldToTile(chunk, worldPosition, null);
		tilePosition.x = Math.round(tilePosition.x);
		tilePosition.y = Math.round(tilePosition.y);
		Vector3f roundedPosition = convertTileToWorld(chunk, tilePosition.x, tilePosition.y, null);
		return getWorldHeight(roundedPosition.x, roundedPosition.z);
	}

	/**
	 * Gets the moisture for a position in the world.
	 *
	 * @param positionX The worlds X position.
	 * @param positionZ The worlds Z position.
	 *
	 * @return The moisture at that world position.
	 */
	public static float getMoistureMap(float positionX, float positionZ) {
		float height = getHeightMap(positionX, positionZ);

		// Calculate the moisture as a inverse of height with added noise.
		float moisture = height;

		// Set to 100% moisture in the ocean/lakes/rivers.
		if (height <= 0.0f) {
			moisture = 1.0f;
		} else {
			moisture += KosmosChunks.get().getNoise().turbulence(positionX / 150.0f, positionZ / 150.0f, 16.0f);
		}

		moisture = Maths.clamp(moisture, 0.0f, 1.0f);

		return Maths.clamp(moisture, 0.0f, 1.0f);
	}

	/**
	 * Gets the biome for a position in the world.
	 *
	 * @param positionX The worlds X position.
	 * @param positionZ The worlds Z position.
	 *
	 * @return The biome at that world position.
	 */
	public static IBiome.Biomes getBiomeMap(float positionX, float positionZ) {
		float height = getHeightMap(positionX, positionZ);
		float moisture = getMoistureMap(positionX, positionZ);

		if (height <= 0.125f) {
			// Ocean.
			return IBiome.Biomes.OCEAN;
		} else if (height <= 0.25f) {
			if (moisture <= 0.16f) {
				// Subtropical Desert.
				return IBiome.Biomes.SUBTROPICAL_DESERT;
			} else if (moisture <= 0.33f) {
				// Grassland.
				return IBiome.Biomes.GRASSLAND;
			} else if (moisture <= 0.66f) {
				// Tropical Seasonal Forest.
				return IBiome.Biomes.TROPICAL_SEASONAL_FOREST;
			} else if (moisture <= 1.0f) {
				// Tropical Rain Forest.
				return IBiome.Biomes.TROPICAL_RAIN_FOREST;
			}
		} else if (height <= 0.5f) {
			if (moisture <= 0.16f) {
				// Temperate Desert.
				return IBiome.Biomes.TEMPERATE_DESERT;
			} else if (moisture <= 0.5f) {
				// Grassland.
				return IBiome.Biomes.GRASSLAND;
			} else if (moisture <= 0.83f) {
				// Temperate Deciduous Forest.
				return IBiome.Biomes.TEMPERATE_DECIDUOUS_FOREST;
			} else if (moisture <= 1.0f) {
				// Temperate Rain Forest.
				return IBiome.Biomes.TEMPERATE_RAIN_FOREST;
			}
		} else if (height <= 0.75f) {
			if (moisture <= 0.33f) {
				// Temperate Desert.
				return IBiome.Biomes.TEMPERATE_DESERT;
			} else if (moisture <= 0.66f) {
				// Shrubland.
				return IBiome.Biomes.SHRUBLAND;
			} else if (moisture <= 1.0f) {
				// Taiga.
				return IBiome.Biomes.TAIGA;
			}
		} else if (height <= 1.0f) {
			if (moisture <= 0.16f) {
				// Scorched.
				return IBiome.Biomes.SCORCHED;
			} else if (moisture <= 0.33f) {
				// Bare.
				return IBiome.Biomes.BARE;
			} else if (moisture <= 0.5f) {
				// Tundra.
				return IBiome.Biomes.TUNDRA;
			} else if (moisture <= 1.0f) {
				// Snow.
				return IBiome.Biomes.SNOW;
			}
		}

		return IBiome.Biomes.OCEAN;
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
}
