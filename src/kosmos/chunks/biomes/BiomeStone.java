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
import flounder.particles.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.chunks.*;
import kosmos.entities.instances.*;
import kosmos.materials.*;

public class BiomeStone extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(1.0f, 0.625f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceBush(structure, position, rotation);
				}
			},
			new EntitySpawn(1.0f, -0.31f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceGemGreen(structure, position, rotation);
				}
			},
			new EntitySpawn(1.0f, 0.42f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceGemPurple(structure, position, rotation);
				}
			},
			new EntitySpawn(1.0f, 0.42f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceGemRed(structure, position, rotation);
				}
			}
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "stone.png")).clampEdges().create();
	private static final ParticleType PARTICLE = null;

	public BiomeStone() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "stone";
	}

	@Override
	public EntitySpawn[] getEntitySpawns() {
		return SPAWNS;
	}

	@Override
	public TextureObject getTexture() {
		return TEXTURE;
	}

	@Override
	public ParticleType getWeatherParticle() {
		return PARTICLE;
	}

	@Override
	public float getHeightModifier() {
		return 1.0f;
	}

	@Override
	public IMaterial getMaterial() {
		return IMaterial.Materials.STONE.getMaterial();
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
