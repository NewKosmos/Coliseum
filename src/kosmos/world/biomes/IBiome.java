/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.world.biomes;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.textures.*;
import kosmos.world.*;
import kosmos.world.chunks.*;

/**
 * A interface used to define biome types.
 */
public abstract class IBiome {
	/**
	 * The name of the biome to be used in world gen.
	 *
	 * @return The biome name.
	 */
	public abstract String getBiomeName();

	/**
	 * Gets the type of texture to use as the surface. (null will not spawn any tile in the biomes area.
	 *
	 * @return The type of texture to use as the surface.
	 */
	public abstract TextureObject getTexture();

	public abstract Colour getColour();

	public Entity generateEntity(Chunk chunk, Vector3f worldPosition) {
		if (worldPosition.y < 0.0f) {
			return null;
		}

		if (Math.abs(KosmosWorld.get().getWorld().getNoise().noise(worldPosition.z * (float) Math.sin(worldPosition.x), worldPosition.x * (float) Math.sin(worldPosition.z))) <= 0.3f) {
			return null;
		}

		float spawn = KosmosWorld.get().getWorld().getNoise().noise((worldPosition.z - worldPosition.x) * (float) Math.sin(worldPosition.x + worldPosition.z), 1.0f) * 23.0f * getEntitySpawns().length;
		float rotation = KosmosWorld.get().getWorld().getNoise().noise(worldPosition.x - worldPosition.z, 1.0f) * 3600.0f;

		if (getEntitySpawns().length > 0 && (int) spawn >= 0.0f && (int) spawn < getEntitySpawns().length) {
			EntitySpawn entitySpawn = getEntitySpawns()[(int) spawn];

			if (entitySpawn != null && spawn - (int) spawn <= entitySpawn.spawnChance) {
				Entity entity = entitySpawn.create.create(FlounderEntities.get().getEntities(), new Vector3f(worldPosition.x, entitySpawn.heightOffset + worldPosition.y * 0.5f, worldPosition.z), new Vector3f(0.0f, rotation, 0.0f));

				if (entity != null) {
					new ComponentChild(entity, chunk, () -> chunk.entityRemove(entity));
				}

				return entity;
			}
		}

		return null;
	}

	public abstract EntitySpawn[] getEntitySpawns();

	public enum Biomes {
		OCEAN(new BiomeOcean(), 0),
		BARE(new BiomeBare(), 1),
		GRASSLAND(new BiomeGrassland(), 2),
		SCORCHED(new BiomeScorched(), 3),
		SHRUBLAND(new BiomeShrubland(), 4),
		SNOW(new BiomeSnow(), 5),
		SUBTROPICAL_DESERT(new BiomeSubtropicalDesert(), 6),
		TAIGA(new BiomeTaiga(), 7),
		TEMPERATE_DECIDUOUS_FOREST(new BiomeTemperateDeciduousForest(), 8),
		TEMPERATE_DESERT(new BiomeTemperateDesert(), 9),
		TEMPERATE_RAIN_FOREST(new BiomeTemperateRainForest(), 10),
		TROPICAL_RAIN_FOREST(new BiomeTropicalRainForest(), 11),
		TROPICAL_SEASONAL_FOREST(new BiomeTropicalSeasonalForest(), 12),
		TUNDRA(new BiomeTundra(), 13);

		private IBiome biome;
		private int id;

		Biomes(IBiome biome, int id) {
			this.biome = biome;
			this.id = id;
		}

		public IBiome getBiome() {
			return biome;
		}

		public int getId() {
			return id;
		}
	}
}
