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
	protected static final double[][] GENERATE_DELTAS = new double[][]{{-0.5, Math.sqrt(3.0) / 2.0}, {0.5, Math.sqrt(3.0) / 2.0}, {1.0, 0.0}, {0.5, -Math.sqrt(3.0) / 2.0}, {-0.5, -Math.sqrt(3.0) / 2.0}, {-1.0, 0.0}};

	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	private List<Chunk> chunks;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.chunks = new ArrayList<>();
		new InstanceCowboy(FlounderEntities.getEntities(), new Vector3f(0.0f, (float) (Math.sqrt(2.0) * 0.25), 0.0f), new Vector3f());
		generateClouds();

		Chunk parent = new Chunk(FlounderEntities.getEntities(), new Vector3f(), Tile.TILE_GRASS.getTexture());
		chunks.add(parent);

		for (int i = 0; i < 6; i++) {
			float csx = Chunk.CHUNK_RADIUS * 2.5f; // The side length of the master hexagon (x).
			float csy = Chunk.CHUNK_RADIUS * 1.8f; // The side length of the master hexagon (y).
			Vector2f o = new Vector2f((float) GENERATE_DELTAS[i][0] * csx, (float) GENERATE_DELTAS[i][1] * csy);
			double theta = Math.atan(((2.0 * Chunk.CHUNK_RADIUS) - 1.0) / -0.5);
			Vector2f.rotate(o, (float) Math.toDegrees(theta), o);

			Vector3f p = new Vector3f(o.x, 0.0f, o.y);
			Vector3f.add(parent.getPosition(), p, p);

			chunks.add(new Chunk(FlounderEntities.getEntities(), p, Tile.TILE_STONE.getTexture()));
		}
	}

	private void generateClouds() {
		PerlinNoise noise = new PerlinNoise(420);

		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y++) {
				float offsetX = noise.noise2(x / 4.0f, y / 4.0f) * 17.0f;
				float offsetZ = noise.noise2(x / 9.0f, y / 9.0f) * 17.0f;
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

	public List<Chunk> getChunks() {
		return chunks;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
