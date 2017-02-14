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

public class BiomeGrass implements IBiome {
	@Override
	public String getBiomeName() {
		return "grass";
	}

	@Override
	public Tile getMainTile() {
		return Tile.TILE_GRASS;
	}

	@Override
	public Tile[] getOreTiles() {
		return new Tile[]{Tile.TILE_ROCK_GEM};
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
