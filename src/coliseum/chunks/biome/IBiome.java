package coliseum.chunks.biome;

import coliseum.chunks.*;
import coliseum.particles.loading.*;

public interface IBiome {
	Tile getMainTile();

	ParticleTemplate getWeatherParticle();

	float getTempDay();

	float getTempNight();

	float getHumidity();
}
