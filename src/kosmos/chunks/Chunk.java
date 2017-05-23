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
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.noise.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.space.*;
import kosmos.chunks.biomes.*;
import kosmos.chunks.meshing.*;
import kosmos.entities.components.*;
import kosmos.world.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
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

	private List<Chunk> childrenChunks;
	private IBiome.Biomes biome;
	private ChunkMesh chunkMesh;
	private Sphere sphere;
	private boolean loaded;

	public Chunk(ISpatialStructure<Entity> structure, Vector3f position) {
		super(structure, position, new Vector3f());

		this.childrenChunks = new ArrayList<>();
		this.biome = getWorldBiome(position.x, position.z);
		this.chunkMesh = new ChunkMesh(this);
		this.sphere = new Sphere(1.0f);
		this.sphere.update(position, null, Chunk.CHUNK_WORLD_SIZE, sphere);
		this.loaded = false;

		new ComponentModel(this, 1.0f, chunkMesh.getModel(), biome.getBiome().getTexture(), 0);
		new ComponentSurface(this, 1.0f, 0.0f, false, false, true);
		new ComponentChunk(this);
	}

	/**
	 * Generates the 6 chunks around this one if they do not exist.
	 */
	protected void createChunksAround(Single<Integer> depth) {
		childrenChunks.removeIf((Chunk child) -> child == null || !FlounderEntities.get().getEntities().contains(child));

		if (childrenChunks.size() == 6) {
			if (childrenChunks.size() == 6) {
				return;
			}
		}

		for (int i = 0; i < 6; i++) {
			// These three variables find the positioning for chunks around the parent.
			float x = this.getPosition().x + (float) ((Math.sqrt(3.0) / 2.0) * DELTA_CHUNK[i][0]);
			float z = this.getPosition().z + (float) ((3.0 / 4.0) * DELTA_CHUNK[i][1]);
			Vector3f p = new Vector3f(x, 0.0f, z);
			Chunk duplicate = null;

			for (Entity entity : FlounderEntities.get().getEntities().getAll()) {
				if (entity != null && entity instanceof Chunk) {
					Chunk chunk = (Chunk) entity;

					if (p.equals(chunk.getPosition())) {
						duplicate = chunk;
					}
				}
			}

			if (duplicate == null) {
				Chunk chunk = new Chunk(FlounderEntities.get().getEntities(), p);
				childrenChunks.add(chunk);
				//	FlounderEntities.getEntities().add(chunk);
			} else {
				childrenChunks.add(duplicate);
			}
		}

		depth.setSingle(depth.getSingle() - 1);

		if (depth.getSingle() > 0) {
			childrenChunks.forEach((chunk -> chunk.createChunksAround(depth)));
		}
	}

	@Override
	public void update() {
		// Updates the entity super class.
		super.update();

		// Updates the mesh.
		this.chunkMesh.update();

		// Adds this mesh AABB to the bounding render pool.
		FlounderBounding.get().addShapeRender(sphere);
	}

	public static List<Vector3f> generate(Chunk chunk) {
		List<Vector3f> tiles = new ArrayList<>();

		for (int i = 0; i < CHUNK_RADIUS; i++) {
			int shapesOnEdge = i;
			double x = 0.0;
			double z = i;
			generateTile(chunk, tiles, x, z);

			for (int j = 0; j < 6; j++) {
				if (j == 5) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					x += DELTA_TILES[j][0];
					z += DELTA_TILES[j][1];
					generateTile(chunk, tiles, x, z);
				}
			}
		}

		return tiles;
	}

	public static Vector3f convertTileToChunk(double x, double z) {
		double cz = (3.0 / 2.0) * HEXAGON_SIDE_LENGTH * z;
		double cx = Math.sqrt(3.0) * HEXAGON_SIDE_LENGTH * ((z / 2.0) + x);
		return new Vector3f((float) cx, 0.0f, (float) cz);
	}

	public static Vector3f convertTileToWorld(Chunk chunk, double x, double z) {
		double wz = (3.0 / 4.0) * HEXAGON_SIDE_LENGTH * z;
		double wx = (Math.sqrt(3.0) / 2.0) * HEXAGON_SIDE_LENGTH * ((z / 2.0) + x);
		return new Vector3f((float) wx + chunk.getPosition().x, 0.0f, (float) wz + chunk.getPosition().z);
	}

	public static Vector2f convertWorldToTile(Chunk chunk, Vector3f worldPosition) {
		double tz = (4.0 * (worldPosition.z - chunk.getPosition().z)) / (3.0 * HEXAGON_SIDE_LENGTH);
		double tx = ((2.0 * (worldPosition.x - chunk.getPosition().x)) / (Math.sqrt(3.0) * HEXAGON_SIDE_LENGTH)) - (tz / 2.0);
		return new Vector2f((float) tx, (float) tz);
	}

	private static void generateTile(Chunk chunk, List<Vector3f> tiles, double x, double z) {
		Vector3f chunkPosition = convertTileToChunk(x, z);
		Vector3f worldPosition = convertTileToWorld(chunk, x, z);

		worldPosition.y = getWorldHeight(worldPosition.x, worldPosition.z);
		chunkPosition.y = worldPosition.y;

		if (worldPosition.y >= 0.0f) {
			tiles.add(chunkPosition);
		}

		chunk.biome.getBiome().generateEntity(chunk, worldPosition);
	}

	private static BufferedImage MAP_IMAGE = null;
	private static final float MAP_MAX_HEIGHT = 8.0f;
	private static final float MAP_MAX_PIXEL_COLOUR = 256.0f * 256.0f * 256.0f;

	static {
		try {
			MAP_IMAGE = ImageIO.read(new MyFile(MyFile.RES_FOLDER, "map.png").getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		final int outputWidth = 512;
		final int outputHeight = 512;
		BufferedImage image = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_RGB);

		PerlinNoise simplexNoise = new PerlinNoise(420);

		for (int y = 0; y < outputHeight; y++) {
			for (int x = 0; x < outputWidth; x++) {
				//float outside = (float) Math.sqrt(Math.pow(x - (outputWidth / 2.0f), 2) + Math.pow(y - (outputHeight / 2.0f), 2)) < ((outputWidth + outputHeight) / 4.0f) ? 1.0f : 0.0f;
				//float islands = simplexNoise.tileableNoise(x / 180.0f, y / 180.0f, outputWidth, outputHeight);
				//float surface = simplexNoise.tileableNoise(x / 30.0f, y / 30.0f, outputWidth, outputHeight) + 1.0f;
				//float height = Maths.clamp(outside * islands * surface, 0.0f, 1.0f);
				float height = Maths.clamp((int) (KosmosWorld.get().getNoise().noise(x / 30.0f, y / 30.0f) * 6.0f), 0.0f, 1.0f);

				int rgb = (int) (255.0f * height);
				rgb = (rgb << 8) + ((int) (255.0f * height));
				rgb = (rgb << 8) + ((int) (255.0f * height));
				image.setRGB(x, y, rgb);
			}
		}

		File outputFile = new File(Framework.getRoamingFolder().getPath() + "/save0_map.png");

		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
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
	public static float getWorldHeight(float positionX, float positionZ) {
		// Calculates the final height for the world position using perlin.
		float height = (float) Math.sqrt(2.0) * (int) (KosmosWorld.get().getNoise().noise(positionX / 30.0f, positionZ / 30.0f) * 12.0f);

		if (MAP_IMAGE != null) {
			int imageX = (int) positionX + (MAP_IMAGE.getWidth() / 2);
			int imageY = (int) positionZ + (MAP_IMAGE.getHeight() / 2);

			if (imageX < 0 || imageX >= MAP_IMAGE.getWidth() || imageY < 0 || imageY >= MAP_IMAGE.getHeight()) {
				return Float.NEGATIVE_INFINITY;
			}

			height = MAP_IMAGE.getRGB(imageX, imageY);
			height += MAP_MAX_PIXEL_COLOUR / 2.0f;
			height /= MAP_MAX_PIXEL_COLOUR / 2.0f;
			height *= MAP_MAX_HEIGHT;
			height = (float) Math.sqrt(2.0) * (int) height;
		}

		// Ignore height that would be water/nothing.
		if (height < 0.0f) {
			height = Float.NEGATIVE_INFINITY;
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
		Vector2f tilePosition = convertWorldToTile(chunk, worldPosition);
		tilePosition.x = Math.round(tilePosition.x);
		tilePosition.y = Math.round(tilePosition.y);
		Vector3f roundedPosition = convertTileToWorld(chunk, tilePosition.x, tilePosition.y);
		return getWorldHeight(roundedPosition.x, roundedPosition.z);
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
		float biomeID = Math.abs(KosmosWorld.get().getNoise().noise((positionX + positionZ) / 256.0f, 1.0f)) * 2.56f * (IBiome.Biomes.values().length + 1);

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
		return loaded; // chunkMesh.getModel() != null && chunkMesh.getModel().isLoaded()
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public Collider getCollider() {
		return sphere;
	}

	public void delete() {
		chunkMesh.delete();
		loaded = false;
		forceRemove();
	}
}
