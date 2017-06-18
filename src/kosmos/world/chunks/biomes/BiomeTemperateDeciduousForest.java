/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.world.chunks.biomes;

import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.world.chunks.*;

public class BiomeTemperateDeciduousForest extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "temperateDeciduousForest.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(0.7059f, 0.7882f, 0.6627f);

	public BiomeTemperateDeciduousForest() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "temperateDeciduousForest";
	}

	@Override
	public EntitySpawn[] getEntitySpawns() {
		return SPAWNS;
	}

	@Override
	public TextureObject getTexture() {
		return TEXTURE;
	}

	@Override
	public Colour getColour() {
		return COLOUR;
	}
}
