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
import flounder.space.*;
import kosmos.chunks.biomes.*;
import kosmos.chunks.meshing.*;
import kosmos.entities.components.*;
import flounder.particles.*;
import flounder.particles.spawns.*;
import kosmos.world.*;

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
	private static final double[][] DELTA_CHUNK = new double[][]{{9.5, 7.0}, {-0.5, 13.0}, {-10.0, 6.0}, {-9.5, -7.0}, {0.5, -13.0}, {10.0, -6.0}};

	// The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.
	private static final int CHUNK_RADIUS = 7;

	// Each tile can be broken into equilateral triangles with sides of length.
	private static final double HEXAGON_SIDE_LENGTH = 2.0;

	// The overall world radius footprint per chunk.
	public static final float CHUNK_WORLD_SIZE = (float) Math.sqrt(3.0) * (CHUNK_RADIUS - 0.5f);

	private ParticleSystem particleSystem;

	private List<Chunk> childrenChunks;
	private IBiome.Biomes biome;
	private ChunkMesh chunkMesh;
	private Sphere sphere;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position) {
		super(structure, position, new Vector3f());

		this.childrenChunks = new ArrayList<>();
		this.biome = getWorldBiome(position.x, position.z);
		this.chunkMesh = new ChunkMesh(this);
		this.sphere = new Sphere();

		new ComponentModel(this, 1.0f, chunkMesh.getModel(), biome.getBiome().getTexture(), 0);
		new ComponentSurface(this, 1.0f, 0.0f, false, false);
		new ComponentChunk(this);

		// generateWeather();
		// generateClouds();
	}

	private void generateWeather() {
		if (biome.getBiome().getWeatherParticle() != null) {
			List<ParticleType> templates = new ArrayList<>();
			templates.add(biome.getBiome().getWeatherParticle());
			particleSystem = new ParticleSystem(templates, new SpawnCircle(40.0f, new Vector3f(0.0f, 1.0f, 0.0f)), 100, 0.5f, 0.5f);
			particleSystem.setSystemCentre(new Vector3f(getPosition().x, 15.0f, getPosition().z));
		}
	}

	private void generateClouds() {
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				float offsetX = KosmosWorld.getNoise().noise2(x / 4.0f, y / 4.0f) * 20.0f;
				float offsetZ = KosmosWorld.getNoise().noise2(x / 9.0f, y / 9.0f) * 20.0f;
				float height = Math.abs(KosmosWorld.getNoise().noise2(x / 2.0f, y / 2.0f) * 2.0f) + 2.0f;
				float rotationY = KosmosWorld.getNoise().noise1((x - y) / 60.0f) * 3600.0f;
				float rotationZ = KosmosWorld.getNoise().noise1((x - y) / 20.0f) * 3600.0f;

				/*Entity entity = new InstanceCloud(FlounderEntities.getEntities(), new Vector3f(
						getPosition().x + (x * 11.0f) + offsetX,
						getPosition().y + 7.0f * height,
						getPosition().z + (y * 11.0f) + offsetZ),
						new Vector3f(0.0f, rotationY, rotationZ),
						Maths.randomInRange(1.0f, 2.25f)
				);
				new ComponentChild(entity, this);*/
			}
		}
	}

	/**
	 * Generates the 6 chunks around this one if they do not exist.
	 */
	protected void createChunksAround() {
		childrenChunks.removeIf((Chunk child) -> child == null || !FlounderEntities.getEntities().contains(child));

		if (childrenChunks.size() == 6) {
			if (childrenChunks.size() == 6) {
				return;
			}
		}

		for (int i = 0; i < 6; i++) {
			// These three variables find the positioning for chunks around the parent.
			float x = this.getPosition().x + (float) (DELTA_CHUNK[i][0] * Math.sqrt(3.0));
			float z = this.getPosition().z + (float) (DELTA_CHUNK[i][1] * 1.5f);
			Vector3f p = new Vector3f(x, 0.0f, z);
			Chunk duplicate = null;

			for (Entity entity : FlounderEntities.getEntities().getAll()) {
				if (entity != null && entity instanceof Chunk) {
					Chunk chunk = (Chunk) entity;

					if (p.equals(chunk.getPosition())) {
						duplicate = chunk;
					}
				}
			}

			if (duplicate == null) {
				Chunk chunk = new Chunk(FlounderEntities.getEntities(), p);
				childrenChunks.add(chunk);
			//	FlounderEntities.getEntities().add(chunk);
			} else {
				childrenChunks.add(duplicate);
			}
		}
	}

	@Override
	public void update() {
		// Updates the entity super class.
		super.update();

		// Updates the mesh.
		this.chunkMesh.update();

		// Adds this mesh AABB to the bounding render pool.
		// FlounderBounding.addShapeRender(getSphere());
	}

	public static List<Vector3f> generate(Chunk chunk) {
		List<Vector3f> tiles = new ArrayList<>();

		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			double x = 0.0;
			double y = i;
			generateTile(chunk, tiles, x, y);

			for (int j = 0; j < 6; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					x += DELTA_TILES[j][0];
					y += DELTA_TILES[j][1];
					generateTile(chunk, tiles, x, y);
				}
			}
		}

		return tiles;
	}

	private static void generateTile(Chunk chunk, List<Vector3f> tiles, double x, double y) {
		float positionX = (float) (Math.sqrt(3.0) * HEXAGON_SIDE_LENGTH * ((y / 2.0) + x));
		float positionZ = (float) ((3.0 / 2.0) * HEXAGON_SIDE_LENGTH * y);

		Vector2f worldPos = new Vector2f(0.5f * positionX + chunk.getPosition().x, 0.5f * positionZ + chunk.getPosition().z);
		float height = getWorldHeight(worldPos.x, worldPos.y, chunk.biome.getBiome().getHeightModifier());

		if (height >= 0.0f) {
			tiles.add(new Vector3f(positionX, height, positionZ));
		}

		chunk.biome.getBiome().generateEntity(chunk, new Vector3f(worldPos.x, height, worldPos.y));
	}

	/**
	 * Gets the terrain height for a position in the world.
	 *
	 * @param positionX The worlds X position.
	 * @param positionZ The worlds Z position.
	 * @param yModifier How much the world height is scaled.
	 *
	 * @return The found height at that world position.
	 */
	public static float getWorldHeight(float positionX, float positionZ, float yModifier) {
		// Calculates the final height for the world position using perlin.
		float height = (float) Math.sqrt(2.0) * (int) (KosmosWorld.getNoise().noise2(positionX / 30.0f, positionZ / 30.0f) * 12.0f * yModifier);

		// Ignore height that would be water/nothing.
		if (height < 0.0f) {
			height = Float.NEGATIVE_INFINITY;
		}

		// Returns the final height,
		return height;
	}

	/**
	 * Gets the type of biome for the position in the world.
	 *
	 * @param positionX The worlds X position.
	 * @param positionZ The worlds Z position.
	 *
	 * @return The found biome at that world position.
	 */
	public static IBiome.Biomes getWorldBiome(float positionX, float positionZ) {
		// Calculates the biome id based off of the world position using perlin.
		float biomeID = Math.abs(KosmosWorld.getNoise().noise1((positionX + positionZ) / 256.0f)) * 2.56f * (IBiome.Biomes.values().length + 1);

		// Limits the search for biomes in the size provided.
		biomeID = Maths.clamp((int) biomeID, 0.0f, IBiome.Biomes.values().length - 1);

		// Returns the biome at the generated ID.
		return IBiome.Biomes.values()[(int) biomeID];
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
		return chunkMesh.getModel() != null && chunkMesh.getModel().isLoaded();
	}

	@Override
	public Collider getCollider() {
		return sphere;
	}

	public void delete() {
		chunkMesh.delete();

		if (particleSystem != null) {
			particleSystem.delete();
			particleSystem = null;
		}

		forceRemove(false);
	}
}
