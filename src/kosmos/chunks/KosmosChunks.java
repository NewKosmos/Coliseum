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
import flounder.noise.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.entities.instances.*;

import java.util.*;

public class KosmosChunks extends Module {
	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	private PerlinNoise noise;
	private ISpatialStructure<Entity> chunks;

	private Entity entityPlayer;

	private Sphere chunkRange;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.noise = new PerlinNoise(537); // new PerlinNoise(KosmosConfigs.configSave.getIntWithDefault("seed", (int) Maths.randomInRange(1.0, 10000.0), () -> KosmosChunks.getNoise().getSeed()));
		this.chunks = new StructureBasic<>();

		this.entityPlayer = new InstancePlayer(FlounderEntities.getEntities(), new Vector3f(0.0f, (float) (Math.sqrt(2.0) * 0.25), 0.0f), new Vector3f()); // InstanceMuliplayer

		this.chunkRange = new Sphere(40.0f); // new AABB();

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		this.currentChunk = null;
		//	generateClouds();

		new Chunk(KosmosChunks.getChunks(), new Vector3f(KosmosConfigs.configSave.getFloatWithDefault("chunk_x", 0.0f, () -> KosmosChunks.getCurrent().getPosition().x), 0.0f, KosmosConfigs.configSave.getFloatWithDefault("chunk_z", 0.0f, () -> KosmosChunks.getCurrent().getPosition().z))); // The root chunk.
	}

	private void generateClouds() {
		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y++) {
				float offsetX = noise.noise2(x / 4.0f, y / 4.0f) * 20.0f;
				float offsetZ = noise.noise2(x / 9.0f, y / 9.0f) * 20.0f;
				float height = Math.abs(noise.noise2(x / 2.0f, y / 2.0f) * 5.0f) + 0.9f;
				float rotationY = noise.noise1((x - y) / 60.0f) * 3600.0f;
				float rotationZ = noise.noise1((x - y) / 20.0f) * 3600.0f;
				new InstanceCloud(FlounderEntities.getEntities(), new Vector3f((x * 11.0f) + offsetX, 7.0f * height, (y * 11.0f) + offsetZ), new Vector3f(0.0f, rotationY, rotationZ), Maths.randomInRange(1.0f, 2.25f));
			}
		}
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() != null) {
			Vector3f playerPos = FlounderCamera.getPlayer().getPosition();
			Chunk playerChunk = null;

			if (!playerPos.equals(lastPlayerPos)) {
				Sphere.recalculate(chunkRange, playerPos, 1.0f, chunkRange);
			}

			for (Entity entity : chunks.getAll()) {
				Chunk chunk = (Chunk) entity;

				if (chunk.isLoaded() && chunk.getBounding().inFrustum(FlounderCamera.getCamera().getViewFrustum())) {
					if (chunk.getBounding().contains(entityPlayer.getPosition())) {
						playerChunk = chunk;
					}
				}

				chunk.update();
			}

			if (playerChunk != currentChunk) {
				if (playerChunk != null) {
					playerChunk.createChunksAround();
					playerChunk.getChildrenChunks().forEach(Chunk::createChunksAround);

					Iterator it = chunks.getAll().iterator();

					while (it.hasNext()) {
						Chunk chunk = (Chunk) it.next();

						if (chunk != currentChunk && chunk.isLoaded()) {
							if (!chunk.getBounding().intersects(chunkRange).isIntersection() && !chunkRange.contains((Sphere) chunk.getBounding())) {
								chunk.delete();
								it.remove();
							}
						}
					}

					currentChunk = playerChunk;
				}
			}

			lastPlayerPos.set(playerPos);
			FlounderBounding.addShapeRender(chunkRange);
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Size", chunks.getSize());
	}

	public static PerlinNoise getNoise() {
		return INSTANCE.noise;
	}

	public static ISpatialStructure<Entity> getChunks() {
		return INSTANCE.chunks;
	}

	public static Entity getEntityPlayer() {
		return INSTANCE.entityPlayer;
	}

	public static Chunk getCurrent() {
		return INSTANCE.currentChunk;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
