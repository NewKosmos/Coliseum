/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.world.biomes;

import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.entities.instances.*;
import kosmos.world.chunks.*;

public class BiomeGrassland extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(InstanceTallGrass::new, 1.0f, 0.25f),
			new EntitySpawn(InstanceFlowerpatch1::new, 1.0f, 0.375f)
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "grassland.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(0.7686f, 0.8314f, 0.6666f);

	public BiomeGrassland() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "grassland";
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
