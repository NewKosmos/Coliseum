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
	protected static final float[][] TILE_DELTAS = new float[][]{{1.0f, 0.0f, -1.0f}, {0.0f, 1.0f, -1.0f}, {-1.0f, 1.0f, 0.0f}, {-1.0f, 0.0f, 1.0f}, {0.0f, -1.0f, 1.0f}, {1.0f, -1.0f, 0.0f}};
	protected static final double[][] CHUNK_DELTAS = new double[][]{{9.5, 7.0}, {-0.5, 13.0}, {-10.0, 6.0}, {-9.5, -7.0}, {0.5, -13.0}, {10.0, -6.0}};

	public static final double HEXAGON_SIDE_LENGTH = 2.0; //  Each tile can be broken into equilateral triangles with sides of length.

	public static final int CHUNK_RADIUS = 7; // The amount of tiles that make up the radius. 7-9 are the optimal chunk radius ranges.

	public static final float CHUNK_WORLD_SIZE = (float) Math.sqrt(3.0) * (CHUNK_RADIUS - 0.5f); // The overall world radius footprint per chunk.

	private ISpatialStructure<Entity> entities;

	private List<Chunk> childrenChunks;
	private IBiome.Biomes biome;
	private ChunkMesh chunkMesh;

	private Sphere sphere;

	private boolean forceRebuild;

	private ParticleSystem particleSystem;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position) {
		super(structure, position, new Vector3f());
		this.entities = new StructureBasic<>();

		float biomeID = Math.abs(KosmosWorld.getNoise().noise1((position.x + position.z) / 300.0f)) * 3.0f * (IBiome.Biomes.values().length + 1);
		biomeID = Maths.clamp((int) biomeID, 0.0f, IBiome.Biomes.values().length - 1);

		this.childrenChunks = new ArrayList<>();
		this.biome = IBiome.Biomes.values()[(int) biomeID];
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

	protected void createChunksAround() {
		if (childrenChunks.size() == 6) {
			childrenChunks.removeIf((Chunk child) -> child == null || !KosmosChunks.getChunks().contains(child));

			if (childrenChunks.size() == 6) {
				return;
			}
		}

		for (int i = 0; i < 6; i++) {
			// These three variables find the positioning for chunks around the parent.
			float x = this.getPosition().x + (float) (CHUNK_DELTAS[i][0] * Math.sqrt(3.0));
			float z = this.getPosition().z + (float) (CHUNK_DELTAS[i][1] * 1.5f);
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
			forceRebuild = !chunkMesh.rebuild(generate(), KosmosChunks.getModelHexagon());
			super.setMoved();
		}

		// Adds this mesh AABB to the bounding render pool.
		FlounderBounding.addShapeRender(getSphere());

		for (Entity entity : entities.getAll()) {
			entity.update();
		}

		super.update();
	}

	private List<Vector3f> generate() {
		List<Vector3f> tiles = new ArrayList<>();

		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			float r = 0.0f;
			float g = -i;
			float b = i;
			generateTile(tiles, tileWorldSpace(r, g, b, HEXAGON_SIDE_LENGTH, null));

			for (int j = 0; j < 6; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + TILE_DELTAS[j][0];
					g = g + TILE_DELTAS[j][1];
					b = b + TILE_DELTAS[j][2];
					generateTile(tiles, tileWorldSpace(r, g, b, HEXAGON_SIDE_LENGTH, null));
				}
			}
		}

		return tiles;
	}

	private void generateTile(List<Vector3f> tiles, Vector2f position) {
		Vector2f worldPos = new Vector2f(position.x + (getPosition().x * 2.0f), position.y + (getPosition().z * 2.0f)); // TODO
		float height = KosmosChunks.getWorldHeight(worldPos.x, worldPos.y);

		if (height >= 0.0f) {
			tiles.add(new Vector3f(position.x, height, position.y));
			biome.getBiome().generateEntity(this, worldPos, position, height);
		}
	}

	public static Vector3f tileHexagonSpace(float x, float z, double length, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		destination.x = (float) (((Math.sqrt(3.0) / 3.0) * x - (z / 3.0f)) / length);
		destination.y = (float) (-((Math.sqrt(3.0) / 3.0) * x + (z / 3.0f)) / length);
		destination.z = (float) ((2.0 / 3.0) * z / length);
		return destination;
	}

	public static Vector2f tileWorldSpace(float r, float g, float b, double length, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		destination.x = (float) (Math.sqrt(3.0) * length * ((b / 2.0) + r));
		destination.y = (float) ((3.0 / 2.0) * length * b);
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
