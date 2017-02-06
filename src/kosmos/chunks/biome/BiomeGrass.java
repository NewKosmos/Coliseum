/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.biome;

import kosmos.chunks.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;

public class BiomeGrass implements IBiome {
	@Override
	public Tile getMainTile() {
		return Tile.TILE_GRASS;
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return KosmosParticles.load("rain");
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
}
