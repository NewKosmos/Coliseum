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
import kosmos.materials.*;

public class BiomeExotic extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{

	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "exotic.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(117.0f, 7.0f, 121.0f, true);
	private static final ParticleType PARTICLE = null;

	public BiomeExotic() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "exotic";
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
		return IMaterial.Materials.GRASS.getMaterial();
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
