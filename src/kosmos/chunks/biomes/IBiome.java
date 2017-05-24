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
import flounder.entities.components.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.particles.*;
import flounder.textures.*;
import kosmos.chunks.*;
import kosmos.materials.*;
import kosmos.world.*;

/**
 * A interface used to define biome types.
 */
public abstract class IBiome {
	public static final int SPAWN_LEVELS = 4;

	public enum Biomes {
		OCEAN(new BiomeOcean()), // 0
		BEACH(new BiomeBeach()), // 1
		GRASS(new BiomeGrass()), EXOTIC(new BiomeExotic()), // 2
		DESERT(new BiomeDesert()), STONE(new BiomeStone()), // 3
		SNOW(new BiomeSnow()); // 4

		private IBiome biome;

		Biomes(IBiome biome) {
			this.biome = biome;
		}

		public IBiome getBiome() {
			return biome;
		}

		public static Biomes get(float level) {
			if (level >= 4.0f) {
				return SNOW;
			} else if (level >= 3.5f) {
				return STONE;
			} else if (level >= 3.0f) {
				return DESERT;
			} else if (level >= 2.5f) {
				return EXOTIC;
			} else if (level >= 2.0f) {
				return GRASS;
			} else if (level >= 1.0f) {
				return BEACH;
			} else {
				return OCEAN;
			}
		}
	}

	/**
	 * The name of the biome to be used in world gen.
	 *
	 * @return The biome name.
	 */
	public abstract String getBiomeName();

	public abstract int spawnLevel();

	public abstract EntitySpawn[] getEntitySpawns();

	/**
	 * Gets the type of texture to use as the surface. (null will not spawn any tile in the biomes area.
	 *
	 * @return The type of texture to use as the surface.
	 */
	public abstract TextureObject getTexture();

	public abstract Colour getColour();

	public Entity generateEntity(Chunk chunk, Vector3f tilePosition) {
		if (tilePosition.y < 0.0f) {
			return null;
		}

		if (Math.abs(KosmosWorld.get().getNoise().noise(tilePosition.z * (float) Math.sin(tilePosition.x), tilePosition.x * (float) Math.sin(tilePosition.z))) <= 0.3f) {
			return null;
		}

		float spawn = KosmosWorld.get().getNoise().noise((tilePosition.z - tilePosition.x) * (float) Math.sin(tilePosition.x + tilePosition.z), 1.0f) * 23.0f * getEntitySpawns().length;
		float rotation = KosmosWorld.get().getNoise().noise(tilePosition.x - tilePosition.z, 1.0f) * 3600.0f;

		if (getEntitySpawns().length > 0 && (int) spawn >= 0.0f && (int) spawn < getEntitySpawns().length) {
			EntitySpawn entitySpawn = getEntitySpawns()[(int) spawn];

			if (entitySpawn != null && spawn - (int) spawn <= entitySpawn.spawnChance) {
				Entity entity = entitySpawn.create(FlounderEntities.get().getEntities(), new Vector3f(tilePosition.x, entitySpawn.heightOffset + tilePosition.y * 0.5f, tilePosition.z), new Vector3f(0.0f, rotation, 0.0f));

				if (entity != null) {
					new ComponentChild(entity, chunk);
				}

				return entity;
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

	public abstract IMaterial getMaterial();

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
