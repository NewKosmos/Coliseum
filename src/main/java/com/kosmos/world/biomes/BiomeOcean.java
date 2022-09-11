/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.world.biomes;

import com.flounder.maths.*;
import com.flounder.resources.*;
import com.flounder.textures.*;
import com.kosmos.entities.instances.*;
import com.kosmos.world.chunks.*;

public class BiomeOcean extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(InstanceCattail::new, 1.0f, 0.375f),
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "ocean.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(0.0824f, 0.3960f, 0.7530f);

	public BiomeOcean() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "ocean";
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
