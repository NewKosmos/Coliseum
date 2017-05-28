/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.biomes;

import flounder.maths.*;
import flounder.particles.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.chunks.*;
import kosmos.entities.instances.*;
import kosmos.materials.*;

public class BiomeSnow extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(InstanceTreePine::new, 1.0f, 0.375f),
			new EntitySpawn(InstanceTreeMaple::new, 1.0f, 0.53f),
			new EntitySpawn(InstanceTreeYellow::new, 1.0f, 0.5f),
			new EntitySpawn(InstanceTreeDeadSnow::new, 0.4f, 0.375f),
			new EntitySpawn(InstanceBottleMaple::new, 0.3f, -0.35f),
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "snow.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(1.0000f, 1.0000f, 1.0000f);
	private static final ParticleType PARTICLE = new ParticleType("snow", TextureFactory.newBuilder().setFile(new MyFile(FlounderParticles.PARTICLES_FOLDER, "snowParticle.png")).setNumberOfRows(4).create(), 4.75f, 0.20f);

	public BiomeSnow() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "snow";
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
}
