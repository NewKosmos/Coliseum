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

public class BiomeRiver extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(1.0f, 0.375f) {
				@Override
				public Entity create(ISpatialStructure<Entity> structure, Vector3f position, Vector3f rotation) {
					return new InstanceCattail(structure, position, rotation);
				}
			}
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "sand.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(0.0f, 0.0f, 1.0f);
	private static final ParticleType PARTICLE = new ParticleType("rain", TextureFactory.newBuilder().setFile(new MyFile(FlounderParticles.PARTICLES_FOLDER, "rainParticle.png")).setNumberOfRows(4).create(), 4.75f, 0.15f);

	public BiomeRiver() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "river";
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
		return IMaterial.Materials.WATER.getMaterial();
	}

	@Override
	public float getTempDay() {
		return 21.0f;
	}

	@Override
	public float getTempNight() {
		return 19.0f;
	}

	@Override
	public float getHumidity() {
		return 94.0f;
	}

	@Override
	public float getWindSpeed() {
		return 0.2f;
	}
}
