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
	public Entity generateEntity(Chunk chunk, Vector2f worldPos, Vector2f tilePosition, float height) {
		if (KosmosWorld.getNoise().noise2(worldPos.x / 50.0f * (float) Math.sin(worldPos.y), worldPos.y / 50.0f * (float) Math.sin(worldPos.x)) <= 0.1f) {
			return null;
		}

		float rotation = KosmosWorld.getNoise().noise1((worldPos.x - worldPos.y) / 66.6f) * 3600.0f;
		float spawn = KosmosWorld.getNoise().noise1((worldPos.y - worldPos.x) / 11.0f) * 400.0f;

		switch ((int) spawn) {
			case 1:
				return new InstanceBush(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								0.625f + height * 0.5f,
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 2:
				return new InstanceGemGreen(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								-0.3f + height * 0.5f,
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 3:
				return new InstanceGemPurple(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								0.425f + height * 0.5f,
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 4:
				return new InstanceGemRed(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								0.425f + height * 0.5f,
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
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
