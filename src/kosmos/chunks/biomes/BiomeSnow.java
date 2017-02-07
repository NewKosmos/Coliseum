/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.biomes;

import kosmos.chunks.tiles.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;

public class BiomeSnow implements IBiome {
	@Override
	public String getBiomeName() {
		return "snow";
	}

	@Override
	public Tile getMainTile() {
		return Tile.TILE_SNOW;
	}

	@Override
	public Tile[] getOreTiles() {
		return new Tile[] { Tile.TILE_ROCK_GEM };
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return KosmosParticles.load("snow");
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
}
