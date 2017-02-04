package coliseum.chunks.biome;

import coliseum.chunks.*;
import coliseum.particles.*;
import coliseum.particles.loading.*;

public class BiomeBeach implements IBiome {
	@Override
	public Tile getMainTile() {
		return Tile.TILE_SAND;
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return ColiseumParticles.load("rain");
	}

	@Override
	public float getTempDay() {
		return 23.0f;
	}

	@Override
	public float getTempNight() {
		return 14.0f;
	}

	@Override
	public float getHumidity() {
		return 89.0f;
	}
}
