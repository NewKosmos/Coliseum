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
import kosmos.world.*;

public class BiomeGrass implements IBiome {
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "grass.png")).clampEdges().create();
	private static final ParticleType particle = new ParticleType("rain", TextureFactory.newBuilder().setFile(new MyFile(KosmosParticles.PARTICLES_FOLDER, "rainParticle.png")).setNumberOfRows(4).create(), 3.5f, 0.15f);

	@Override
	public String getBiomeName() {
		return "grass";
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

		if (tilePosition.y < 0.0f) {
			if (spawn == 1) {
			//	return new InstanceLilipad(FlounderEntities.getEntities(), new Vector3f(tilePosition.x, 0.025f, tilePosition.z), new Vector3f(0.0f, rotation, 0.0f));
			}

			return null;
		}

		switch ((int) spawn) {
			case 1:
				return new InstanceTreeBirchLarge(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.5f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 2:
				return new InstanceTreeBirchMedium(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.625f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 3:
				if (spawn - 3 < 0.123f) {
					return new InstancePod(FlounderEntities.getEntities(),
							new Vector3f(
									tilePosition.x,
									1.05f + tilePosition.y * 0.5f,
									tilePosition.z
							),
							new Vector3f(0.0f, rotation, 0.0f)
					);
				}

				return null;
			case 4:
				return new InstanceBush(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.625f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 5:
				return new InstanceTreeCherryLarge(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.5f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 6:
				return new InstanceTreeCherryMedium(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.4f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 7:
				return new InstanceTreeCherrySmall(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.5f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
			case 8:
				return new InstanceFlowerpatch1(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.375f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f()
				);
			case 9:
				return new InstanceChicken(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.61f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f()
				);
			case 10:
				return new InstanceTallGrass(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.25f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f()
				);
			case 11:
				return new InstanceTreeBirchSmall(FlounderEntities.getEntities(),
						new Vector3f(
								tilePosition.x,
								0.42f + tilePosition.y * 0.5f,
								tilePosition.z
						),
						new Vector3f()
				);
			default:
				return null;
		}
	}

	@Override
	public ParticleType getWeatherParticle() {
		return particle;
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

	@Override
	public float getWindSpeed() {
		return 0.17f;
	}
}
