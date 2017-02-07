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

		/*for (int i = 0; i < 2; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			chunks.add(new Chunk(FlounderEntities.getEntities(), Chunk.worldSpace2D(new Vector3f(r, g, b), ChunkGenerator.HEXAGON_SIDE_LENGTH * ChunkGenerator.CHUNK_RADIUS, null)));

			for (int j = 0; j < ChunkGenerator.HEXAGON_SIDE_COUNT; j++) {
				if (j == ChunkGenerator.HEXAGON_SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + ChunkGenerator.GENERATE_DELTAS[j][0];
					g = g + ChunkGenerator.GENERATE_DELTAS[j][1];
					b = b + ChunkGenerator.GENERATE_DELTAS[j][2];
					chunks.add(new Chunk(FlounderEntities.getEntities(), Chunk.worldSpace2D(new Vector3f(r, g, b), ChunkGenerator.HEXAGON_SIDE_LENGTH * ChunkGenerator.CHUNK_RADIUS, null)));
				}
			}
		}*/

		// http://www.java-gaming.org/topics/getting-the-chunk-of-a-hex/37645/view.html
		for (int x = -3; x < 3; x++) {
			for (int y = -3; y < 3; y++) {
				// float chunkX = (x * ChunkGenerator.CHUNK_WORLD_SIZE) - ((y % 2 == 0) ? ChunkGenerator.CHUNK_WORLD_SIZE * (2.0f / 3.0f) : 0.0f);
				// float chunkY = (y * (float) Math.sqrt(3.0) * ChunkGenerator.CHUNK_WORLD_SIZE);

				// TODO: Make this math work.
				float HEX_X_DX = 70.0f;

				float HEX_Y_DX = 35.0f;
				float HEX_Y_DY = 56.25f;

				int CHUNK_LENGTH = 5;
				int CHUNK_SIZE = 2;

				float size_x = HEX_X_DX * (CHUNK_LENGTH - 1);
				float size_y = HEX_Y_DY * CHUNK_LENGTH;

				float x_dx = HEX_X_DX;
				float x_dy = HEX_Y_DX / CHUNK_LENGTH;

				float y_dx = HEX_Y_DY * CHUNK_SIZE / (CHUNK_LENGTH - 1);
				float y_dy = HEX_Y_DY;

				float chunkX = ((float) x * x_dx + (float) y * x_dy) / size_x;
				float chunkY = ((float) x * y_dx + (float) y * y_dy) / size_y;

				Texture t = (x == 0 && y == 0) ? Tile.TILE_STONE.getTexture() : Tile.TILE_SNOW.getTexture();
				chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector2f(chunkX, chunkY), t));

			}
		}

		//chunks.add(new Chunk(new Vector2f(0.0f, 0.0f)));
		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector3f(10.392304f, 0.0f, 18.0f)));
		//chunks.add(new Chunk(new Vector2f(20.784609f, 0.0f)));
		//chunks.add(new Chunk(new Vector2f(10.392304f, -18.0f)));
		//chunks.add(new Chunk(new Vector2f(-10.392304f, -18.0f)));
		//chunks.add(new Chunk(new Vector2f(-20.784609f, 0.0f)));
		//chunks.add(new Chunk(new Vector2f(-10.392304f, 18.0f)));
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
