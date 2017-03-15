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
import kosmos.entities.instances.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;
import kosmos.world.*;

public class BiomeSnow implements IBiome {
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "snow.png")).clampEdges().create();
	private static final ParticleTemplate particle = new ParticleTemplate("snow", TextureFactory.newBuilder().setFile(new MyFile(KosmosParticles.PARTICLES_FOLDER, "snowParticle.png")).setNumberOfRows(4).create(), 3.5f, 0.20f);

	@Override
	public String getBiomeName() {
		return "snow";
	}

	@Override
	public TextureObject getTexture() {
		return texture;
	}

	@Override
	public Entity generateEntity(Chunk chunk, Vector2f worldPos, Vector2f tilePosition, float height) {
		if (KosmosWorld.getNoise().noise2(worldPos.x / 50.0f * (float) Math.sin(worldPos.y), worldPos.y / 50.0f * (float) Math.sin(worldPos.x)) <= 0.1f) {
			return null;
		}

		float rotation = KosmosWorld.getNoise().noise1((worldPos.x - worldPos.y) / 66.6f) * 3600.0f;
		float spawn = Math.abs(KosmosWorld.getNoise().noise1((worldPos.y - worldPos.x) / 10.0f) * 250.0f);

		switch ((int) spawn) {
			case 1:
				return new InstanceTreePine(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((1.5 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 2:
				if (spawn - 2 < 0.2f) {
					return new InstanceTreeSnow(chunk.getEntities(),
							new Vector3f(
									chunk.getPosition().x + (float) (tilePosition.x * 0.5),
									(float) ((1.5 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
									chunk.getPosition().z + (float) (tilePosition.y * 0.5)
							),
							new Vector3f(0.0f, rotation, 0.0f)
					);
				}

					return null;
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
