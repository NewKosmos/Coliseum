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
import kosmos.world.*;

public class BiomeSnow implements IBiome {
	@Override
	public String getBiomeName() {
		return "snow";
	}

	@Override
	public Tile getMainTile() {
		return Tile.TILE_SNOW;
	}

	@Override
	public void generateEntity(Chunk chunk, Vector2f worldPos, Vector2f tilePosition, int height) {
		float rotation = KosmosWorld.getNoise().noise1((worldPos.x - worldPos.y) / 66.6f) * 3600.0f;
		float spawn = Math.abs(KosmosWorld.getNoise().noise1((worldPos.y - worldPos.x) / 10.0f) * 250.0f);

		switch ((int) spawn) {
			case 1:
				new InstanceTreePine(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((1.5 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
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
		return KosmosParticles.load("snow");
	}

	@Override
	public float getTempDay() {
		return -0.5f;
	}

	@Override
	public float getTempNight() {
		return -3.0f;
	}

	@Override
	public float getHumidity() {
		return 23.0f;
	}

	@Override
	public float getWindSpeed() {
		return 0.37f;
	}
}
