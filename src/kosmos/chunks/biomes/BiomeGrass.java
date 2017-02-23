/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.biomes;

import flounder.maths.vectors.*;
import kosmos.chunks.*;
import kosmos.chunks.tiles.*;
import kosmos.entities.instances.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;

public class BiomeGrass implements IBiome {
	@Override
	public String getBiomeName() {
		return "grass";
	}

	@Override
	public Tile getMainTile() {
		return Tile.TILE_GRASS;
	}

	@Override
	public void generateEntity(Chunk chunk, Vector2f worldPos, Vector2f tilePosition, int height) {
		float rotation = KosmosChunks.getNoise().noise1((worldPos.x - worldPos.y) / 66.6f) * 3600.0f;

		switch ((int) (KosmosChunks.getNoise().noise1((worldPos.y - worldPos.x) / 11.0f) * 400.0f)) {
			case 1:
				new InstanceTree1(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((2.0 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
				break;
			case 2:
				new InstanceTree3(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((2.5 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
				break;
			case 3:
				new InstancePod(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((4.20 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
				break;
			default:
				break;
		}
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return KosmosParticles.load("rain");
	}

	@Override
	public float getTempDay() {
		return 20.9f;
	}

	@Override
	public float getTempNight() {
		return 12.4f;
	}

	@Override
	public float getHumidity() {
		return 61.0f;
	}
}
