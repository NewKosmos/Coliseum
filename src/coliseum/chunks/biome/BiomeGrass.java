package coliseum.chunks.biome;

import coliseum.chunks.*;
import coliseum.particles.*;
import coliseum.particles.loading.*;

public class BiomeGrass implements IBiome {
	@Override
	public Tile getMainTile() {
		return Tile.TILE_GRASS;
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return ColiseumParticles.load("rain");
	}

	@Override
	public float getTempDay() {
		return 20.9f;
	}

	@Override
	public float getTempNight() {
		return 12.4f;
	}

	@Override
	public float getHumidity() {
		return 61.0f;
	}
}
