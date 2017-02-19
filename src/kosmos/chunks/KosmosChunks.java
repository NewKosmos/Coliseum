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
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.textures.*;
import kosmos.chunks.tiles.*;
import kosmos.entities.instances.*;

import java.util.*;

public class KosmosChunks extends Module {
	protected static final float[][] GENERATE_DELTAS = new float[][]{{9.5f, 7.0f}, {-0.5f, 13.0f}, {-10.0f, 6.0f}, {-9.5f, -7.0f}, {0.5f, -13.0f}, {10.0f, -6.0f}};

	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	private List<Chunk> chunks;
	private PerlinNoise noise;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.chunks = new ArrayList<>();
		this.noise = new PerlinNoise(420);
		new InstanceCowboy(FlounderEntities.getEntities(), new Vector3f(0.0f, (float) (Math.sqrt(2.0) * 0.25), 0.0f), new Vector3f());
		generateClouds();

		Chunk parent = new Chunk(FlounderEntities.getEntities(), new Vector3f(), Tile.TILE_GRASS.getTexture());
		chunks.add(parent);
		parent.createChunksAround();

		/*List<Chunk> c = new ArrayList<>(chunks);
		c.remove(parent);
		for (Chunk i : c) {
			i.createChunksAround();
		}*/
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
		for (Chunk chunk : chunks) {
			if (FlounderCamera.getPlayer() != null) {
				chunk.update(FlounderCamera.getPlayer().getPosition());
			} else {
				chunk.update(null);
			}
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Size", chunks.size());
	}

	public static List<Chunk> getChunks() {
		return INSTANCE.chunks;
	}

	public static PerlinNoise getNoise() {
		return INSTANCE.noise;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
