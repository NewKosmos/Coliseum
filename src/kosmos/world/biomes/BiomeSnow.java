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

public class BiomeSnow extends IBiome {
	private static final EntitySpawn[] SPAWNS = new EntitySpawn[]{
			new EntitySpawn(InstanceTreePine::new, 1.0f, 0.375f),
			new EntitySpawn(InstanceTreeMaple::new, 1.0f, 0.53f),
			new EntitySpawn(InstanceTreeYellow::new, 1.0f, 0.5f),
			new EntitySpawn(InstanceTreeDeadSnow::new, 0.4f, 0.7f),
	};
	private static final TextureObject TEXTURE = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "snow.png")).clampEdges().create();
	private static final Colour COLOUR = new Colour(1.0000f, 1.0000f, 1.0000f);

	public BiomeSnow() {
		super();
	}

	@Override
	public String getBiomeName() {
		return "snow";
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
