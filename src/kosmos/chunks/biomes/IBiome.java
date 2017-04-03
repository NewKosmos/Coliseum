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
import flounder.maths.vectors.*;
import flounder.textures.*;
import kosmos.chunks.*;
import kosmos.particles.*;
import kosmos.world.*;

/**
 * A interface used to define biome types.
 */
public abstract class IBiome {
	public enum Biomes {
		GRASS(new BiomeGrass()), SNOW(new BiomeSnow()), STONE(new BiomeStone()), DESERT(new BiomeDesert()); // RIVER(new BiomeRiver()),

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
	public abstract String getBiomeName();

	public abstract EntitySpawn[] getEntitySpawns();

	/**
	 * Gets the type of texture to use as the surface. (null will not spawn any tile in the biomes area.
	 *
	 * @return The type of texture to use as the surface.
	 */
	public abstract TextureObject getTexture();

	public Entity generateEntity(Chunk chunk, Vector3f tilePosition) {
		if (Math.abs(KosmosWorld.getNoise().noise2(tilePosition.z * (float) Math.sin(tilePosition.x), tilePosition.x * (float) Math.sin(tilePosition.z))) <= 0.3f) {
			return null;
		}

		float spawn = KosmosWorld.getNoise().noise1((tilePosition.z - tilePosition.x) * (float) Math.sin(tilePosition.x + tilePosition.z)) * 23.0f * getEntitySpawns().length;
		float rotation = KosmosWorld.getNoise().noise1(tilePosition.x - tilePosition.z) * 3600.0f;

		if (getEntitySpawns().length > 0 && (int) spawn >= 0.0f && (int) spawn < getEntitySpawns().length) {
			EntitySpawn entitySpawn = getEntitySpawns()[(int) spawn];

			if (entitySpawn != null && spawn - (int) spawn <= entitySpawn.spawnChance) {
				return entitySpawn.create(FlounderEntities.getEntities(), new Vector3f(tilePosition.x, entitySpawn.heightOffset + tilePosition.y * 0.5f, tilePosition.z), new Vector3f(0.0f, rotation, 0.0f));
			}
		}

		return null;
	}

	/**
	 * Gets the type of weather particle to spawn when weather is active.
	 *
	 * @return The type of weather particle.
	 */
	public abstract ParticleType getWeatherParticle();

	/**
	 * Gets the average day temp (celsius).
	 *
	 * @return The average night day.
	 */
	public abstract float getTempDay();

	/**
	 * Gets the average night temp (celsius).
	 *
	 * @return The average night temp.
	 */
	public abstract float getTempNight();

	/**
	 * Gets the average humidity %.
	 *
	 * @return The average humidity.
	 */
	public abstract float getHumidity();

	/**
	 * Gets the wind speed % (0-1).
	 *
	 * @return The average wind speed.
	 */
	public abstract float getWindSpeed();
}
