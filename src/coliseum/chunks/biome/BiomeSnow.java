package coliseum.chunks.biome;

import coliseum.chunks.*;
import coliseum.particles.*;
import coliseum.particles.loading.*;

public class BiomeSnow implements IBiome {
	@Override
	public Tile getMainTile() {
		return Tile.TILE_SNOW;
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return ColiseumParticles.load("snow");
	}

	@Override
	public float getTempDay() {
		return -0.5f;
	}

	@Override
	public float getTempNight() {
		return -3.0f;
	}

	@Override
	public float getHumidity() {
		return 23.0f;
	}
}
