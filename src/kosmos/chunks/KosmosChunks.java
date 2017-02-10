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
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.textures.*;
import kosmos.chunks.tiles.*;
import kosmos.entities.instances.*;

import java.util.*;

public class KosmosChunks extends IModule {
	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	private List<Chunk> chunks;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.chunks = new ArrayList<>();

		// new InstanceDerpWalk(FlounderEntities.getEntities(), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f());
		new InstanceCowboy(FlounderEntities.getEntities(), new Vector3f(0.0f, 1.5f, 0.0f), new Vector3f());
		// new InstanceRobit(FlounderEntities.getEntities(), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f());

		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y++) {
				new InstanceCloud(FlounderEntities.getEntities(), new Vector3f(
						(x * 55.0f) + Maths.randomInRange(-20.0f, 20.0f),
						24.0f + Maths.randomInRange(-1.25f, 6.05f),
						(y * 55.0f) + Maths.randomInRange(-20.0f, 20.0f)
				), new Vector3f(
						0.0f,
						Maths.randomInRange(0.0f, 360.0f),
						Maths.randomInRange(0.0f, 180.0f)
				), Maths.randomInRange(2.0f, 4.5f));
			}
		}

		// List<ParticleTemplate> templates = new ArrayList<>();
		// templates.add(KosmosParticles.load("rain"));
		// ParticleSystem system = new ParticleSystem(templates, new SpawnCircle(75.0f, new Vector3f(0.0f, 1.0f, 0.0f)), 150, 0.5f, 0.75f);
		// system.setSystemCentre(new Vector3f(0.0f, 30.0f, 0.0f));

		Chunk parent = new Chunk(FlounderEntities.getEntities(), new Vector2f(0.0f, 0.0f), Tile.TILE_GRASS.getTexture());
		chunks.add(parent);

		for (int i = 0; i < 6; i++) {
			double theta = ((360.0 / 6.0) * (i + 1));
			FlounderLogger.log("[" + i + "]: " + theta);
			Vector2f position = new Vector2f();
			position.x = (ChunkGenerator.CHUNK_WORLD_SIZE) * (float) Math.cos(Math.toRadians(theta));
			position.y = (ChunkGenerator.CHUNK_WORLD_SIZE) * (float) Math.sin(Math.toRadians(theta));
			Vector2f.add(position, parent.getPosition().toVector2f(), position);
			chunks.add(new Chunk(FlounderEntities.getEntities(), position, Tile.TILE_SNOW.getTexture()));
		}

		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector2f(0.0f, 0.0f), Tile.TILE_GRASS.getTexture()));
		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector3f(10.392304f, 0.0f, 18.0f), Tile.TILE_SNOW.getTexture())));
		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector2f(20.784609f, 0.0f), Tile.TILE_SNOW.getTexture())));
		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector2f(10.392304f, -18.0f), Tile.TILE_SNOW.getTexture())));
		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector2f(-10.392304f, -18.0f), Tile.TILE_SNOW.getTexture())));
		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector2f(-20.784609f, 0.0f), Tile.TILE_SNOW.getTexture())));
		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector2f(-10.392304f, 18.0f), Tile.TILE_SNOW.getTexture())));
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
	public IModule getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
