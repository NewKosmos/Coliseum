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
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.noise.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.chunks.tiles.*;
import kosmos.entities.instances.*;
import kosmos.world.*;

import java.util.*;

public class KosmosChunks extends Module {
	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	private PerlinNoise noise;
	private ISpatialStructure<Entity> chunks;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.noise = new PerlinNoise(537);
		this.chunks = new StructureBasic<>();
		this.lastPlayerPos = new Vector3f();
		this.currentChunk = null;
		generateClouds();

		new Chunk(KosmosChunks.getChunks(), new Vector3f(), Tile.TILE_GRASS.getTexture()); // The root chunk.
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

		//List<ParticleTemplate> templates = new ArrayList<>();
		//templates.add(KosmosParticles.load("rain"));
		//ParticleSystem system = new ParticleSystem(templates, new SpawnCircle(40.0f, new Vector3f(0.0f, 1.0f, 0.0f)), 500, 0.5f, 0.75f);
		//system.setSystemCentre(new Vector3f(0.0f, 15.0f, 0.0f));
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() != null) {
			Vector3f playerPos = FlounderCamera.getPlayer().getPosition();
			Chunk playerChunk = null;

			for (Entity entity : chunks.getAll(new ArrayList<>())) {
				Chunk chunk = (Chunk) entity;

				if (chunk.isLoaded() && chunk.getBounding().inFrustum(FlounderCamera.getCamera().getViewFrustum())) {
					if (chunk.getBounding().contains(KosmosWorld.getEntityPlayer().getBounding())) {
						playerChunk = chunk;
					}
				}

				chunk.update(playerPos);
			}

			if (playerChunk != currentChunk) {
				/*if (currentChunk != null && !currentChunk.getChildrenChunks().isEmpty()) {
					for (Chunk children : currentChunk.getChildrenChunks()) {
						if (children != playerChunk) {
						//	children.delete();
						}
					}

					if (currentChunk.getChildrenChunks().contains(playerChunk)) {
						currentChunk.getChildrenChunks().clear();
						currentChunk.getChildrenChunks().add(playerChunk);
					} else {
						currentChunk.getChildrenChunks().clear();
					}
				}*/

				if (playerChunk != null && playerChunk.getChildrenChunks().isEmpty()) {
					playerChunk.createChunksAround();
				}

				FlounderLogger.log(playerChunk);
			}

			currentChunk = playerChunk;

			lastPlayerPos.set(playerPos);
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

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
