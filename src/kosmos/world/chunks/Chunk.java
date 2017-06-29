/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.world.chunks;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.space.*;
import kosmos.entities.components.*;
import kosmos.world.*;
import kosmos.world.biomes.*;
import kosmos.world.chunks.meshing.*;

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

	private List<Chunk> childrenChunks;
	private IBiome.Biomes biome;
	private ChunkMesh chunkMesh;
	private Sphere sphere;
	private boolean loaded;

	private int createDepth;

	private List<Vector3f> entitiesRemoved;
	private List<Entity> entitiesAdded;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position) {
		super(structure, position, new Vector3f());

		this.childrenChunks = new ArrayList<>();
		this.biome = KosmosChunks.getBiomeMap(position.x, position.z);
		this.chunkMesh = new ChunkMesh(this);
		this.sphere = new Sphere(1.0f);
		this.sphere.update(position, null, KosmosChunks.CHUNK_WORLD_SIZE, sphere);
		this.loaded = false;

		this.createDepth = 0;

		this.entitiesRemoved = KosmosWorld.get().getWorld().getChunkRemoved(position);
		this.entitiesAdded = KosmosWorld.get().getWorld().getChunkAdded(position);

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
					float x = this.getPosition().x + (float) ((Math.sqrt(3.0) / 2.0) * KosmosChunks.HEXAGON_SIDE_LENGTH * DELTA_CHUNK[i][0]);
					float z = this.getPosition().z + (float) ((3.0 / 4.0) * KosmosChunks.HEXAGON_SIDE_LENGTH * DELTA_CHUNK[i][1]);
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
			childrenChunks.forEach(((chunk) -> chunk.createChunksAround(createDepth - 1)));

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

		for (int i = 0; i < KosmosChunks.CHUNK_RADIUS; i++) {
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
		Vector3f worldPosition = KosmosChunks.convertTileToWorld(chunk, x, z, null);
		worldPosition.y = KosmosChunks.getWorldHeight(worldPosition.x, worldPosition.z) + yOffset;
		Vector3f tilePosition = KosmosChunks.convertTileToChunk(x, z, null);
		tilePosition.y = worldPosition.y;

		// Ignore tile if below world.
		if (tilePosition.y < 0.0f) {
			return;
		}

		// Samples the 6 tiles around this tile.
		Vector3f samplePosition = new Vector3f();
		float height0 = getTileHeight(chunk, x, z, DELTA_TILES[0], samplePosition);
		float height1 = getTileHeight(chunk, x, z, DELTA_TILES[1], samplePosition);
		float height2 = getTileHeight(chunk, x, z, DELTA_TILES[2], samplePosition);
		float height3 = getTileHeight(chunk, x, z, DELTA_TILES[3], samplePosition);
		float height4 = getTileHeight(chunk, x, z, DELTA_TILES[4], samplePosition);
		float height5 = getTileHeight(chunk, x, z, DELTA_TILES[5], samplePosition);
		float heightMin = Maths.minAbsValue(height0, height1, height2, height3, height4, height5);

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

		// Spawns entities if this is the top tile, and if it was not removed.
		if (spawnEntity) {
			Entity entity = chunk.biome.getBiome().generateEntity(chunk, worldPosition);

			if (entity != null && chunk.entitiesRemoved.contains(entity.getPosition())) {
				FlounderEntities.get().getEntities().remove(entity);
			}
		}
	}

	private static float getTileHeight(Chunk chunk, double x, double z, double[] delta, Vector3f sample) {
		if (sample == null) {
			sample = new Vector3f();
		}

		KosmosChunks.convertTileToWorld(chunk, x + delta[0], z + delta[1], sample);
		return KosmosChunks.getWorldHeight(sample.x, sample.z);
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

	public List<Vector3f> getEntitiesRemoved() {
		return entitiesRemoved;
	}

	public List<Entity> getEntitiesAdded() {
		return entitiesAdded;
	}

	public void entityAdd(Entity entity) {
		entitiesAdded.add(entity);
	}

	public void entityRemove(Entity entity) {
		if (isRemoved()) {
			return;
		}

		FlounderLogger.get().log("Removing entity: " + entity);

		if (entitiesAdded.contains(entity)) {
			entitiesAdded.remove(entity);
		} else {
			entitiesRemoved.add(entity.getPosition());
		}
	}

	public void entityRemove(Vector3f entity) {
		if (isRemoved()) {
			return;
		}

		FlounderLogger.get().log("Removing entity: " + entity);
		entitiesRemoved.add(entity);
		Entity entityWorld = null;

		for (Entity e : FlounderEntities.get().getEntities().getAll(null)) {
			if (e.getPosition().equals(entity) && e.getComponent(ComponentPlayer.class) == null && e.getComponent(ComponentMultiplayer.class) == null && e.getComponent(ComponentChunk.class) == null) {
				entityWorld = e;
			}
		}

		if (entityWorld != null) {
			entityWorld.remove();
		}
	}

	public void prepareSave() {
		if (KosmosWorld.get().getWorld() == null) {
			return;
		}

		String chunkKey = WorldDefinition.vectorToString(getPosition());

		if (!KosmosWorld.get().getWorld().getChunkData().containsKey(chunkKey)) {
			KosmosWorld.get().getWorld().getChunkData().put(chunkKey, new Pair<>(new ArrayList<>(), new ArrayList<>()));
		}

		Pair<List<Vector3f>, List<Entity>> data = KosmosWorld.get().getWorld().getChunkData().get(chunkKey);
		data.getFirst().clear();
		data.getFirst().addAll(entitiesRemoved);
		data.getSecond().clear();
		data.getSecond().addAll(entitiesAdded);
	}

	//@Override // Call getSphere instead, this is not used any more so chunks can be culled.
	//public Collider getCollider() {
	//	return sphere;
	//}

	public void delete() {
		chunkMesh.delete();
		loaded = false;
		forceRemove();
		prepareSave();
	}
}
