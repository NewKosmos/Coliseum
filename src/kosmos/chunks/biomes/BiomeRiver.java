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
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.chunks.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;

public class BiomeRiver implements IBiome {
	private static final TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(KosmosChunks.TERRAINS_FOLDER, "sand.png")).clampEdges().create();
	private static final ParticleTemplate particle = new ParticleTemplate("rain", TextureFactory.newBuilder().setFile(new MyFile(KosmosParticles.PARTICLES_FOLDER, "rainParticle.png")).setNumberOfRows(4).create(), 3.5f, 0.15f);

	@Override
	public String getBiomeName() {
		return "river";
	}

	@Override
	public TextureObject getTexture() {
		return texture;
	}

	@Override
	public Entity generateEntity(Chunk chunk, Vector2f worldPos, Vector2f tilePosition, float height) {
		return null;
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return particle;
	}

	@Override
	public float getTempDay() {
		return 21.0f;
	}

	@Override
	public float getTempNight() {
		return 19.0f;
	}

	@Override
	public float getHumidity() {
		return 94.0f;
	}

	@Override
	public float getWindSpeed() {
		return 0.2f;
	}
}
