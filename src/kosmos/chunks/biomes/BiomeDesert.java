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

public class BiomeDesert extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(1.0f, 0.4375f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceCactus1(structure, position, rotation);
				}
			},
			new EntitySpawn(1.0f, 0.4375f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceCactus2(structure, position, rotation);
				}
			},
			new EntitySpawn(1.0f, 0.4375f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceTreePalm(structure, position, rotation);
				}
			},
			new EntitySpawn(1.0f, 0.375f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceCattail(structure, position, rotation);
				}
			}
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "sand.png")).clampEdges().create();
	private static final ParticleType PARTICLE = null;

	public BiomeDesert() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "desert";
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
	public IMaterial getMaterial() {
		return IMaterial.Materials.SAND.getMaterial();
	}

	@Override
	public float getTempDay() {
		return 23.0f;
	}

	@Override
	public float getTempNight() {
		return 14.0f;
	}

	@Override
	public float getHumidity() {
		return 89.0f;
	}

	@Override
	public float getWindSpeed() {
		return 0.3f;
	}
}
