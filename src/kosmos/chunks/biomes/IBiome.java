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
import kosmos.particles.loading.*;

/**
 * A interface used to define biome types.
 */
public interface IBiome {
	public enum Biomes {
		GRASS(new BiomeGrass()), SNOW(new BiomeSnow()), RIVER(new BiomeRiver()), STONE(new BiomeStone()), DESERT(new BiomeDesert());

		private IBiome biome;

		Biomes(IBiome biome) {
			this.biome = biome;
		}

		public IBiome getBiome() {
			return biome;
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

	void generateEntity(Chunk chunk, Vector2f worldPos, Vector2f tilePosition, int height);

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
