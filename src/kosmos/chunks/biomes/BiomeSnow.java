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
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.particles.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.chunks.*;
import kosmos.entities.instances.*;
import kosmos.materials.*;

public class BiomeSnow extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(1.0f, 0.375f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceTreePine(structure, position, rotation);
				}
			},
			new EntitySpawn(1.0f, 0.53f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceTreeMaple(structure, position, rotation);
				}
			},
			new EntitySpawn(1.0f, 0.5f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceTreeYellow(structure, position, rotation);
				}
			},
			new EntitySpawn(0.4f, 0.375f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceTreeDeadSnow(structure, position, rotation);
				}
			},
			new EntitySpawn(0.3f, -0.35f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceBottleMaple(structure, position, rotation);
				}
			}
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "snow.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(226.0f, 226.0f, 226.0f, true);
	private static final ParticleType PARTICLE = new ParticleType("snow", TextureFactory.newBuilder().setFile(new MyFile(FlounderParticles.PARTICLES_FOLDER, "snowParticle.png")).setNumberOfRows(4).create(), 4.75f, 0.20f);

	public BiomeSnow() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "snow";
	}

	@Override
	public int spawnLevel() {
		return 4;
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
	public Colour getColour() {
		return COLOUR;
	}

	@Override
	public ParticleType getWeatherParticle() {
		return PARTICLE;
	}

	@Override
	public IMaterial getMaterial() {
		return IMaterial.Materials.SNOW.getMaterial();
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
