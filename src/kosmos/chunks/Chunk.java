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
import kosmos.chunks.tiles.*;
import kosmos.entities.components.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;
import kosmos.particles.spawns.*;

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
	private AABB aabb;

	private boolean tilesChanged;

	private ParticleSystem particleSystem;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position) {
		super(structure, position, new Vector3f());
		this.entities = new StructureBasic<>();

		float biomeID = Math.abs(KosmosChunks.getNoise().noise1((position.x + position.z) / 300.0f)) * 3.0f * (IBiome.Biomes.values().length + 1);
		biomeID = Maths.clamp((int) biomeID, 0.0f, IBiome.Biomes.values().length - 1);

		this.childrenChunks = new ArrayList<>();
		this.biome = IBiome.Biomes.values()[(int) biomeID];
		this.chunkMesh = new ChunkMesh(this);

		this.sphere = new Sphere();
		this.aabb = new AABB();

		this.tilesChanged = true;

		new ComponentModel(this, 1.0f, null, biome.getBiome().getMainTile().getTexture(), 0);
		new ComponentSurface(this, 1.0f, 0.0f, false, false);
		new ComponentCollider(this);
		new ComponentCollision(this);

		// generateWeather();
	}

	private void generateWeather() {
		if (biome.getBiome().getWeatherParticle() != null) {
			List<ParticleTemplate> templates = new ArrayList<>();
			templates.add(biome.getBiome().getWeatherParticle());
			particleSystem = new ParticleSystem(templates, new SpawnCircle(40.0f, new Vector3f(0.0f, 1.0f, 0.0f)), 100, 0.5f, 0.5f);
			particleSystem.setSystemCentre(new Vector3f(getPosition().x, 15.0f, getPosition().z));
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
		if (tilesChanged || chunkMesh.getModel() == null) {
			chunkMesh.rebuild(generate());
			tilesChanged = false;
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
			generateTile(tiles, Tile.worldSpace2D(r, g, b, HEXAGON_SIDE_LENGTH, null));

			for (int j = 0; j < 6; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + TILE_DELTAS[j][0];
					g = g + TILE_DELTAS[j][1];
					b = b + TILE_DELTAS[j][2];
					generateTile(tiles, Tile.worldSpace2D(r, g, b, HEXAGON_SIDE_LENGTH, null));
				}
			}
		}

		return tiles;
	}

	private void generateTile(List<Vector3f> tiles, Vector2f position) {
		Vector2f worldPos = new Vector2f(position.x + (getPosition().x * 2.0f), position.y + (getPosition().z * 2.0f));
		int height = (int) (KosmosChunks.getNoise().noise2(worldPos.x / 75.0f, worldPos.y / 75.0f) * 16.0f);
		//FlounderLogger.log(position.x + ", " + height + ", " + position.y);

		if (height >= 0) {
			tiles.add(new Vector3f(position.x, height * (float) Math.sqrt(2.0f), position.y));

			if (KosmosChunks.getNoise().noise2(worldPos.x / 50.0f * (float) Math.sin(worldPos.y), worldPos.y / 50.0f * (float) Math.sin(worldPos.x)) > 0.1f) {
				biome.getBiome().generateEntity(this, worldPos, position, height);
			}
		}

	/*	for (int i = 0; i < height; i++) {
			tiles.add(new Vector3f(position.x, i * (float) Math.sqrt(2.0f), position.y));

			if (i == height - 1 && height > 0) {
				if (!((KosmosChunks.getNoise().noise1((worldPos.x + worldPos.y) / 11.0f) * 20.0f) > 1.0f)) {
					biome.getBiome().generateEntity(this, worldPos, position, i);
				}
			}
		}*/
	}

	public float getHeight(float worldX, float worldZ) {
		float positionX = worldX - super.getPosition().getX();
		float positionZ = worldZ - super.getPosition().getZ();

		Vector2f worldPos = new Vector2f(positionX + (getPosition().x * 2.0f), positionZ + (getPosition().z * 2.0f));
		int height = (int) (KosmosChunks.getNoise().noise2(worldPos.x / 75.0f, worldPos.y / 75.0f) * 16.0f);

		if (height >= 0) {
			return height * 0.5f * (float) Math.sqrt(2.0);
		} else {
			return Float.NEGATIVE_INFINITY;
		}
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

	public AABB getAABB() {
		return aabb;
	}

	public IBiome.Biomes getBiome() {
		return biome;
	}

	public boolean isLoaded() {
		return chunkMesh.getModel() != null && chunkMesh.getModel().isLoaded();
	}

	@Override
	public IBounding getBounding() {
		return getSphere();
	}

	public void delete() {
		for (Entity entity : entities.getAll()) {
			entity.forceRemove(false);
		}

		entities.clear();
		chunkMesh.delete();

		if (particleSystem != null) {
			particleSystem.delete();
			particleSystem = null;
		}
	}
}
