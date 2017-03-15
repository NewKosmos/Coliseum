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
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.space.*;
import kosmos.chunks.biomes.*;
import kosmos.chunks.meshing.*;
import kosmos.entities.components.*;
import kosmos.entities.instances.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;
import kosmos.particles.spawns.*;
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

	// Each tile can be broken into equilateral triangles with sides of length.
	private static final double HEXAGON_SIDE_LENGTH = 2.0;

	// The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.
	private static final int CHUNK_RADIUS = 7;

	// The overall world radius footprint per chunk.
	public static final float CHUNK_WORLD_SIZE = (float) Math.sqrt(3.0) * (CHUNK_RADIUS - 0.5f);

	private ISpatialStructure<Entity> entities;
	private ParticleSystem particleSystem;

	private List<Chunk> childrenChunks;
	private IBiome.Biomes biome;
	private ChunkMesh chunkMesh;
	private Sphere sphere;

	private boolean forceRebuild;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position) {
		super(structure, position, new Vector3f());
		this.entities = new StructureBasic<>();

		this.childrenChunks = new ArrayList<>();
		this.biome = KosmosChunks.getWorldBiome(position.x, position.z);
		this.chunkMesh = new ChunkMesh(this);
		this.sphere = new Sphere();

		this.forceRebuild = true;

		new ComponentModel(this, 1.0f, chunkMesh.getModel(), biome.getBiome().getTexture(), 0);
		new ComponentSurface(this, 1.0f, 0.0f, false, false);
		new ComponentCollider(this);
		new ComponentCollision(this);

		// generateWeather();
		// generateClouds();
	}

	private void generateWeather() {
		if (biome.getBiome().getWeatherParticle() != null) {
			List<ParticleTemplate> templates = new ArrayList<>();
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

				new InstanceCloud(entities, new Vector3f(
						getPosition().x + (x * 11.0f) + offsetX,
						getPosition().y + 7.0f * height,
						getPosition().z + (y * 11.0f) + offsetZ),
						new Vector3f(0.0f, rotationY, rotationZ),
						Maths.randomInRange(1.0f, 2.25f)
				);
			}
		}
	}

	/**
	 * Generates the 6 chunks around this one if they do not exist.
	 */
	protected void createChunksAround() {
		childrenChunks.removeIf((Chunk child) -> child == null || !KosmosChunks.getChunks().contains(child));

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

			for (Entity entity : KosmosChunks.getChunks().getAll()) {
				Chunk chunk = (Chunk) entity;

				if (p.equals(chunk.getPosition())) {
					duplicate = chunk;
				}
			}

			if (duplicate == null) {
				Chunk chunk = new Chunk(KosmosChunks.getChunks(), p);
				childrenChunks.add(chunk);
				KosmosChunks.getChunks().add(chunk);
			} else {
				childrenChunks.add(duplicate);
			}
		}
	}

	@Override
	public void update() {
		// Builds or rebulds this chunks mesh.
		if (forceRebuild) {
			forceRebuild = !chunkMesh.rebuild(KosmosChunks.getModelHexagon());
		}

		// Adds this mesh AABB to the bounding render pool.
		FlounderBounding.addShapeRender(getSphere());

		for (Entity entity : entities.getAll()) {
			entity.update();
		}

		super.update();
	}

	public static List<Vector3f> generate(Chunk chunk) {
		List<Vector3f> tiles = new ArrayList<>();

		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			double x = 0.0;
			double y = i;
			generateTile(chunk, tiles, tileWorldSpace(x, y, HEXAGON_SIDE_LENGTH, null));

			for (int j = 0; j < 6; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					x += DELTA_TILES[j][0];
					y += DELTA_TILES[j][1];
					generateTile(chunk, tiles, tileWorldSpace(x, y, HEXAGON_SIDE_LENGTH, null));
				}
			}
		}

		return tiles;
	}

	private static void generateTile(Chunk chunk, List<Vector3f> tiles, Vector2f position) {
		Vector2f worldPos = new Vector2f(position.x + (chunk.getPosition().x * 2.0f), position.y + (chunk.getPosition().z * 2.0f)); // TODO
		float height = KosmosChunks.getWorldHeight(worldPos.x, worldPos.y);

		if (height >= 0.0f) {
			tiles.add(new Vector3f(position.x, height, position.y));
			chunk.biome.getBiome().generateEntity(chunk, worldPos, position, height);
		}
	}

	public static Vector3f tileHexagonSpace(double x, double z, double length, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		destination.x = (float) (((Math.sqrt(3.0) / 3.0) * x - (z / 3.0f)) / length);
		destination.y = (float) (-((Math.sqrt(3.0) / 3.0) * x + (z / 3.0f)) / length);
		destination.z = (float) ((2.0 / 3.0) * z / length);
		return destination;
	}

	public static Vector2f tileWorldSpace(double x, double y, double length, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		destination.x = (float) (Math.sqrt(3.0) * length * ((y / 2.0) + x));
		destination.y = (float) ((3.0 / 2.0) * length * y);
		return destination;
	}

	public ISpatialStructure<Entity> getEntities() {
		return entities;
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
	public IBounding getBounding() {
		return sphere;
	}

	public void delete() {
		for (Entity entity : entities.getAll()) {
			entity.forceRemove(false);
		}

		entities.clear();
		chunkMesh.delete();
		forceRebuild = true;

		if (particleSystem != null) {
			particleSystem.delete();
			particleSystem = null;
		}
	}
}
