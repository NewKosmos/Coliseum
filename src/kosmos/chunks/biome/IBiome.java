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
import kosmos.particles.loading.*;

public interface IBiome {
	Tile getMainTile();

	ParticleTemplate getWeatherParticle();

	float getTempDay();

	float getTempNight();

	float getHumidity();
}
