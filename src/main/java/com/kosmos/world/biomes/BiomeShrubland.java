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

public class BiomeShrubland extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(InstanceBush::new, 1.0f, 0.625f),
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "shrubland.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(0.7686f, 0.8000f, 0.7333f);

	public BiomeShrubland() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "shrubland";
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
