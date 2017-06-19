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

public class BiomeTundra extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(InstanceTreePine::new, 1.0f, 0.375f),
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "tundra.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(0.8666f, 0.8666f, 0.7333f);

	public BiomeTundra() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "tundra";
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
