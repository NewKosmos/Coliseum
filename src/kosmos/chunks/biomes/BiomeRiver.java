/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.biomes;

import flounder.maths.vectors.*;
import kosmos.chunks.*;
import kosmos.chunks.tiles.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;

public class BiomeRiver implements IBiome {
	@Override
	public String getBiomeName() {
		return "river";
	}

	@Override
	public Tile getMainTile() {
		return Tile.TILE_SAND;
	}

	@Override
	public void generateEntity(Chunk chunk, Vector2f worldPos, Vector2f tilePosition, int height) {

	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return KosmosParticles.load("rain");
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
}
