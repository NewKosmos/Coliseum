/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.biomes;

import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.chunks.*;
import kosmos.entities.instances.plants.*;
import kosmos.entities.instances.rocks.*;
import kosmos.particles.loading.*;
import kosmos.world.*;

public class BiomeStone implements IBiome {
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "stone.png")).clampEdges().create();
	private static final ParticleTemplate particle = null;

	@Override
	public String getBiomeName() {
		return "stone";
	}

	@Override
	public TextureObject getTexture() {
		return texture;
	}

	@Override
	public Entity generateEntity(Chunk chunk, Vector3f tilePosition) {
		if (Math.abs(KosmosWorld.getNoise().noise2(tilePosition.z * (float) Math.sin(tilePosition.x), tilePosition.x * (float) Math.sin(tilePosition.z))) <= 0.3f) {
			return null;
		}

		float spawn = KosmosWorld.getNoise().noise1((tilePosition.z - tilePosition.x) * (float) Math.sin(tilePosition.x + tilePosition.z)) * 100.0f;
		float rotation = KosmosWorld.getNoise().noise1(tilePosition.x - tilePosition.z) * 3600.0f;

		switch ((int) spawn) {
			case 1:
				return new InstanceBush(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.625f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 2:
				return new InstanceGemGreen(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								-0.3f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 3:
				return new InstanceGemPurple(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.425f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 4:
				return new InstanceGemRed(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.425f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			default:
				return null;
		}
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return particle;
	}

	@Override
	public float getTempDay() {
		return 8.2f;
	}

	@Override
	public float getTempNight() {
		return -13.0f;
	}

	@Override
	public float getHumidity() {
		return 10.0f;
	}

	@Override
	public float getWindSpeed() {
		return 0.1f;
	}
}
