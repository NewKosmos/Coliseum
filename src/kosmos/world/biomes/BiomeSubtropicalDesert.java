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

public class BiomeSubtropicalDesert extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(InstanceCactus1::new, 1.0f, 0.4375f),
			new EntitySpawn(InstanceCactus2::new, 1.0f, 0.4375f),
			new EntitySpawn(InstanceTreePalm::new, 1.0f, 0.4375f),
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "subtropicalDesert.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(0.914f, 0.8275f, 0.7804f);

	public BiomeSubtropicalDesert() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "subtropicalDesert";
	}

	@Override
	public TextureObject getTexture() {
		return TEXTURE;
	}

	@Override
	public Colour getColour() {
		return COLOUR;
	}

	@Override
	public EntitySpawn[] getEntitySpawns() {
		return SPAWNS;
	}
}
