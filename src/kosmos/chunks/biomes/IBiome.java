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
import kosmos.chunks.tiles.*;
import kosmos.particles.loading.*;

/**
 * A interface used to define biome types.
 */
public interface IBiome {
	public enum Biomes {
		BEACH(new BiomeBeach()), GRASS(new BiomeGrass()), RIVER(new BiomeRiver()), SNOW(new BiomeSnow());

		private IBiome biome;

		Biomes(IBiome biome) {
			this.biome = biome;
		}

		public IBiome getBiome() {
			return biome;
		}

		public static Biomes random() {
			return Biomes.values()[(int) Maths.randomInRange(0.0f, Biomes.values().length)];
		}
	}

	/**
	 * The name of the biome to be used in world gen.
	 *
	 * @return The biome name.
	 */
	String getBiomeName();

	/**
	 * Gets the type of tile to use as the surface. (null will not spawn any tile in the biomes area.
	 *
	 * @return The type of tile to use as the surface.
	 */
	Tile getMainTile();

	/**
	 * Gets the types of ore tiles that may spawn in the biome.
	 *
	 * @return The viable ore tiles that are spawnable.
	 */
	Tile[] getOreTiles();

	/**
	 * Gets the type of weather particle to spawn when weather is active.
	 *
	 * @return The type of weather particle.
	 */
	ParticleTemplate getWeatherParticle();

	/**
	 * Gets the average day temp (celsius).
	 *
	 * @return The average night day.
	 */
	float getTempDay();

	/**
	 * Gets the average night temp (celsius).
	 *
	 * @return The average night temp.
	 */
	float getTempNight();

	/**
	 * Gets the average humidity %.
	 *
	 * @return The average humidity.
	 */
	float getHumidity();
}
