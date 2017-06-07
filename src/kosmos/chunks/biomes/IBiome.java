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
import flounder.textures.*;
import kosmos.chunks.*;

/**
 * A interface used to define biome types.
 */
public abstract class IBiome {
	public enum Biomes {
		OCEAN(new BiomeOcean()),
		BARE(new BiomeBare()),
		GRASSLAND(new BiomeGrassland()),
		SCORCHED(new BiomeScorched()),
		SHRUBLAND(new BiomeShrubland()),
		SNOW(new BiomeSnow()),
		SUBTROPICAL_DESERT(new BiomeSubtropicalDesert()),
		TAIGA(new BiomeTaiga()),
		TEMPERATE_DECIDUOUS_FOREST(new BiomeTemperateDeciduousForest()),
		TEMPERATE_DESERT(new BiomeTemperateDesert()),
		TEMPERATE_RAIN_FOREST(new BiomeTemperateRainForest()),
		TROPICAL_RAIN_FOREST(new BiomeTropicalRainForest()),
		TROPICAL_SEASONAL_FOREST(new BiomeTropicalSeasonalForest()),
		TUNDRA(new BiomeTundra());

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

	public abstract Colour getColour();

	public Entity generateEntity(Chunk chunk, Vector3f tilePosition) {
		if (tilePosition.y < 0.0f) {
			return null;
		}

		if (Math.abs(KosmosChunks.get().getNoise().noise(tilePosition.z * (float) Math.sin(tilePosition.x), tilePosition.x * (float) Math.sin(tilePosition.z))) <= 0.3f) {
			return null;
		}

		float spawn = KosmosChunks.get().getNoise().noise((tilePosition.z - tilePosition.x) * (float) Math.sin(tilePosition.x + tilePosition.z), 1.0f) * 23.0f * getEntitySpawns().length;
		float rotation = KosmosChunks.get().getNoise().noise(tilePosition.x - tilePosition.z, 1.0f) * 3600.0f;

		if (getEntitySpawns().length > 0 && (int) spawn >= 0.0f && (int) spawn < getEntitySpawns().length) {
			EntitySpawn entitySpawn = getEntitySpawns()[(int) spawn];

			if (entitySpawn != null && spawn - (int) spawn <= entitySpawn.spawnChance) {
				Entity entity = entitySpawn.create.create(FlounderEntities.get().getEntities(), new Vector3f(tilePosition.x, entitySpawn.heightOffset + tilePosition.y * 0.5f, tilePosition.z), new Vector3f(0.0f, rotation, 0.0f));

				if (entity != null) {
					new ComponentChild(entity, chunk);
				}

				return entity;
			}
		}

		return null;
	}
}
