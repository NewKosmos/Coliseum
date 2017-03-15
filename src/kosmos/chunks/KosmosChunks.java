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
import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.chunks.biomes.*;
import kosmos.entities.instances.*;
import kosmos.world.*;

import java.util.*;

public class KosmosChunks extends Module {
	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	public static final MyFile TERRAINS_FOLDER = new MyFile(MyFile.RES_FOLDER, "terrains");

	private ISpatialStructure<Entity> chunks;

	private Entity entityPlayer;
	private Entity entitySun;
	private Entity entityMoon;

	private Sphere chunkRange;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	private ModelObject modelHexagon;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.chunks = new StructureBasic<>();

		this.entityPlayer = new InstancePlayer(FlounderEntities.getEntities(), new Vector3f(), new Vector3f());
		this.entityMoon = new InstanceMoon(FlounderEntities.getEntities(), new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entitySun = new InstanceSun(FlounderEntities.getEntities(), new Vector3f(-200.0f, -200.0f, -200.0f), new Vector3f(0.0f, 0.0f, 0.0f));

		this.chunkRange = new Sphere(40.0f);

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		setCurrent(new Chunk(KosmosChunks.getChunks(), new Vector3f(
				KosmosConfigs.configSave.getFloatWithDefault("chunk_x", 0.0f, () -> KosmosChunks.getCurrent().getPosition().x),
				0.0f,
				KosmosConfigs.configSave.getFloatWithDefault("chunk_z", 0.0f, () -> KosmosChunks.getCurrent().getPosition().z)
		))); // The root chunk.

		this.modelHexagon = ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "hexagon.obj")).create();
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() != null) {
			Vector3f playerPos = FlounderCamera.getPlayer().getPosition();
			Chunk playerChunk = null;

			if (!playerPos.equals(lastPlayerPos)) {
				Sphere.recalculate(chunkRange, playerPos, 1.0f, chunkRange);
			}

			// Goes though all chunks looking for changes.
			for (Entity entity : chunks.getAll()) {
				Chunk chunk = (Chunk) entity;

				if (chunk != null && chunk.isLoaded() && chunk.getBounding() != null) {
					// Checks if it is in the view.
					if (chunk.getBounding().inFrustum(FlounderCamera.getCamera().getViewFrustum())) {
						if (chunk.getBounding().contains(entityPlayer.getPosition())) {
							playerChunk = chunk; // This chunk is now the chunk with the player in it.
						}
					}

					// Updates the chunk.
					chunk.update();
				}
			}

			setCurrent(playerChunk); // This chunk is now the current chunk.
			lastPlayerPos.set(playerPos);
			FlounderBounding.addShapeRender(chunkRange);
		}

		/*if (FlounderCamera.getCamera() != null && FlounderMouse.getMouse(0)) {
			Ray viewRay = FlounderCamera.getCamera().getViewRay();
			Entity entityInRay = null;
			float entityRayDistance = 0.0f;

			for (Entity entity : currentChunk.getEntities().getAll()) {
				IntersectData data = entity.getBounding().intersects(viewRay);

				if (entity.getBounding() != null && data.isIntersection()) {
					if (entityInRay == null || data.getDistance() < entityRayDistance) {
						entityInRay = entity;
						entityRayDistance = data.getDistance();
					}
				}
			}

			if (entityInRay != null) {
				entityInRay.forceRemove(true);
			}
		}*/
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Size", chunks.getSize());
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Current", currentChunk);
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
		float height = (float) Math.sqrt(2.0) * (int) (KosmosWorld.getNoise().noise2(positionX / 64.0f, positionZ / 64.0f) * 10.0f);

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
		float biomeID = Math.abs(KosmosWorld.getNoise().noise1((positionX + positionZ) / 300.0f)) * 3.0f * (IBiome.Biomes.values().length + 1);

		// Limits the search for biomes in the size provided.
		biomeID = Maths.clamp((int) biomeID, 0.0f, IBiome.Biomes.values().length - 1);

		// Returns the biome at the generated ID.
		return IBiome.Biomes.values()[(int) biomeID];
	}

	public static ISpatialStructure<Entity> getChunks() {
		return INSTANCE.chunks;
	}

	public static Entity getEntityPlayer() {
		return INSTANCE.entityPlayer;
	}

	public static Entity getEntitySun() {
		return INSTANCE.entitySun;
	}

	public static Entity getEntityMoon() {
		return INSTANCE.entityMoon;
	}

	public static Chunk getCurrent() {
		return INSTANCE.currentChunk;
	}

	/**
	 * Sets the current chunk that that player is contained in. This will generate surrounding chunks.
	 *
	 * @param currentChunk
	 */
	public static void setCurrent(Chunk currentChunk) {
		if (currentChunk != null && INSTANCE.currentChunk != currentChunk) {
			currentChunk.createChunksAround();
			currentChunk.getChildrenChunks().forEach(Chunk::createChunksAround);

			Iterator it = INSTANCE.chunks.getAll().iterator();

			while (it.hasNext()) {
				Chunk chunk = (Chunk) it.next();

				if (chunk != currentChunk && chunk.isLoaded()) {
					if (!chunk.getBounding().intersects(INSTANCE.chunkRange).isIntersection() && !INSTANCE.chunkRange.contains((Sphere) chunk.getBounding())) {
						chunk.delete();
						it.remove();
					}
				}
			}

			INSTANCE.currentChunk = currentChunk;
		}
	}

	public static void clear() {
		INSTANCE.chunks.getAll().forEach((Entity chunk) -> ((Chunk) chunk).delete());
		INSTANCE.chunks.clear();
		setCurrent(new Chunk(KosmosChunks.getChunks(), getCurrent().getPosition())); // The new root chunk.
	}

	public static ModelObject getModelHexagon() {
		return INSTANCE.modelHexagon;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		clear();
	}
}
