/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.space.*;
import kosmos.chunks.biomes.*;
import kosmos.chunks.meshing.*;
import kosmos.entities.components.*;

import java.util.*;

/**
 * A hexagonal chunk.
 * http://www.redblobgames.com/grids/hexagons/#range
 * http://stackoverflow.com/questions/2459402/hexagonal-grid-coordinates-to-pixel-coordinates
 */
public class Chunk extends Entity {
	// Deltas used to position tiles in a chunk of any size.
	private static final double[][] DELTA_TILES = new double[][]{{1.0, -1.0}, {0.0, -1.0}, {-1.0, 0.0}, {-1.0, 1.0}, {0.0, 1.0}, {1.0, 0.0}};

	// Deltas used to position chunks around a centre chunk when the radius is 7 for each chunk.
	private static final double[][] DELTA_CHUNK = new double[][]{{19.0, 14.0}, {-1.0, 26.0}, {-20.0, 12.0}, {-19.0, -14.0}, {1.0, -26.0}, {20.0, -12.0}};

	// The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.
	private static final int CHUNK_RADIUS = 7;

	// Each tile can be broken into equilateral triangles with sides of length.
	private static final double HEXAGON_SIDE_LENGTH = 2.0;

	// The overall world radius footprint per chunk.
	public static final float CHUNK_WORLD_SIZE = (float) Math.sqrt(3.0) * (CHUNK_RADIUS - 0.5f);

	// Island world generations.
	public static final int WORLD_SIZE = 1536; // The width and height of the world, in tile size.
	public static final float WORLD_NOISE_HEIGHT = 43.0f; // The height multiplier, max world height.
	public static final float WORLD_ISLAND_INSIDE = 0.80f; // The inside radius of the island shape.
	public static final float WORLD_ISLAND_OUTSIDE = 1.0f; // The outside radius of the island shape.
	public static final float WORLD_ISLAND_PARAMETER = 0.4f; // The shape parameter (0=circular, 1=rectangular).

	private List<Chunk> childrenChunks;
	private IBiome.Biomes biome;
	private ChunkMesh chunkMesh;
	private Sphere sphere;
	private boolean loaded;

	private int createDepth;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position) {
		super(structure, position, new Vector3f());

		this.childrenChunks = new ArrayList<>();
		this.biome = getBiomeMap(position.x, position.z);
		this.chunkMesh = new ChunkMesh(this);
		this.sphere = new Sphere(1.0f);
		this.sphere.update(position, null, Chunk.CHUNK_WORLD_SIZE, sphere);
		this.loaded = false;

		this.createDepth = 0;

		new ComponentModel(this, 1.0f, chunkMesh.getModel(), biome.getBiome().getTexture(), 0);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		new ComponentChunk(this);
	}

	@Override
	public void update() {
		// Updates the entity super class.
		super.update();

		// Creates the children for this chunk if signaled to.
		if (createDepth != 0) {
			// Creates children if it can.
			if (childrenChunks.size() != 6) {
				for (int i = 0; i < 6; i++) {
					// These three variables find the positioning for chunks around the parent.
					float x = this.getPosition().x + (float) ((Math.sqrt(3.0) / 2.0) * DELTA_CHUNK[i][0]);
					float z = this.getPosition().z + (float) ((3.0 / 4.0) * DELTA_CHUNK[i][1]);
					Vector3f p = new Vector3f(x, 0.0f, z);
					Chunk duplicate = null;

					for (Entity entity : FlounderEntities.get().getEntities().getAll(null)) {
						if (entity != null && entity instanceof Chunk) {
							Chunk chunk = (Chunk) entity;

							if (p.equals(chunk.getPosition())) {
								duplicate = chunk;
							}
						}
					}

					if (duplicate == null) {
						childrenChunks.add(new Chunk(FlounderEntities.get().getEntities(), p));
					} else {
						childrenChunks.add(duplicate);
					}
				}
			}

			// Tells the children this last create depth - 1.
			childrenChunks.forEach((chunk -> chunk.createChunksAround(createDepth - 1)));

			// Clears this depth.
			createDepth = 0;
		}

		// Updates the mesh.
		this.chunkMesh.update();

		// Adds this mesh to the bounding render pool.
		if (chunkMesh.getModel() != null && chunkMesh.getModel().isLoaded()) {
			FlounderBounding.get().addShapeRender(sphere);
		}
	}

	/**
	 * Generates the 6 chunks around this one if they do not exist.
	 *
	 * @param depth The radius of chunks to create from this chunk, large radii can cause lag.
	 */
	protected void createChunksAround(int depth) {
		// If beyond the depth, don't create children.
		if (depth <= 0) {
			return;
		}

		// Removes children if they do not exist any more!
		childrenChunks.removeIf((Chunk child) -> child == null || !FlounderEntities.get().getEntities().contains(child));
		// Sets the create depth to the provided depth.
		createDepth = depth;
	}

	/**
	 * Generates a array of positions for tiles.
	 *
	 * @return The new array of tiles.
	 */
	public Map<Vector3f, Boolean[]> generate() {
		Map<Vector3f, Boolean[]> tiles = new HashMap<>();

		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			double x = 0.0;
			double z = i;
			generateTile(this, tiles, x, z, false, 0.0f, true);

			for (int j = 0; j < 6; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					x += DELTA_TILES[j][0];
					z += DELTA_TILES[j][1];
					generateTile(this, tiles, x, z, false, 0.0f, true);
				}
			}
		}

		return tiles;
	}

	private static void generateTile(Chunk chunk, Map<Vector3f, Boolean[]> tiles, double x, double z, boolean floating, float yOffset, boolean spawnEntity) {
		// Calculate the position and height.
		Vector3f worldPosition = convertTileToWorld(chunk, x, z, null);
		worldPosition.y = getWorldHeight(worldPosition.x, worldPosition.z) + yOffset;
		Vector3f tilePosition = convertTileToChunk(x, z, null);
		tilePosition.y = worldPosition.y;

		// Ignore tile if below world.
		if (tilePosition.y < 0.0f) {
			return;
		}

		// Samples the 6 tiles around this tile.
		Vector3f samplePosition = new Vector3f();
		convertTileToWorld(chunk, x + DELTA_TILES[0][0], z + DELTA_TILES[0][1], samplePosition);
		float height0 = getWorldHeight(samplePosition.x, samplePosition.z);
		convertTileToWorld(chunk, x + DELTA_TILES[1][0], z + DELTA_TILES[1][1], samplePosition);
		float height1 = getWorldHeight(samplePosition.x, samplePosition.z);
		convertTileToWorld(chunk, x + DELTA_TILES[2][0], z + DELTA_TILES[2][1], samplePosition);
		float height2 = getWorldHeight(samplePosition.x, samplePosition.z);
		convertTileToWorld(chunk, x + DELTA_TILES[3][0], z + DELTA_TILES[3][1], samplePosition);
		float height3 = getWorldHeight(samplePosition.x, samplePosition.z);
		convertTileToWorld(chunk, x + DELTA_TILES[4][0], z + DELTA_TILES[4][1], samplePosition);
		float height4 = getWorldHeight(samplePosition.x, samplePosition.z);
		convertTileToWorld(chunk, x + DELTA_TILES[5][0], z + DELTA_TILES[5][1], samplePosition);
		float height5 = getWorldHeight(samplePosition.x, samplePosition.z);

		// The smallest height from the samples.
		float heightMin = Maths.minValue(height0, height1, height2, height3, height4, height5);

		// Sets and stores the model object states and tile position.
		Boolean[] objects = new Boolean[KosmosChunks.get().getHexagons().length];
		objects[0] = yOffset == 0.0f;
		objects[1] = floating;
		objects[2] = height0 < tilePosition.y;
		objects[3] = height1 < tilePosition.y;
		objects[4] = height2 < tilePosition.y;
		objects[5] = height3 < tilePosition.y;
		objects[6] = height4 < tilePosition.y;
		objects[7] = height5 < tilePosition.y;
		tiles.put(tilePosition, objects);

		// Generates tiles below if there is a terrain drop, for cliff faces. This could be more efficient but is not.
		if (tilePosition.y - heightMin > Math.sqrt(2.0f) && tilePosition.y - (float) Math.sqrt(2.0f) > heightMin) {
			generateTile(chunk, tiles, x, z, false, yOffset - (float) Math.sqrt(2.0f), false);
		}

		// Spawns entities if this is the top tile.
		if (spawnEntity) {
			chunk.biome.getBiome().generateEntity(chunk, worldPosition);
		}
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

	public List<Chunk> getChildrenChunks() {
		return childrenChunks;
	}

	public ChunkMesh getChunkMesh() {
		return chunkMesh;
	}

	public Sphere getSphere() {
		return sphere;
	}

	public IBiome.Biomes getBiome() {
		return biome;
	}

	public boolean isLoaded() {
		return loaded; // chunkMesh.getModel() != null && chunkMesh.getModel().isLoaded()
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	//@Override // Call getSphere instead, this is not used any more so chunks can be culled.
	//public Collider getCollider() {
	//	return sphere;
	//}

	public void delete() {
		chunkMesh.delete();
		loaded = false;
		forceRemove();
	}
}
