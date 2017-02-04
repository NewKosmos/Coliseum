package coliseum.chunks.biome;

import coliseum.chunks.*;
import coliseum.particles.*;
import coliseum.particles.loading.*;

public class BiomeRiver implements IBiome {
	@Override
	public Tile getMainTile() {
		return null;
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return ColiseumParticles.load("rain");
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
}
