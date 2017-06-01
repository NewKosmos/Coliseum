package kosmos.chunks.map;

import flounder.events.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.chunks.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

public class MapGenerator extends Thread {
	// The size of the rendered map image.
	private static final int MAP_SIZE = 1024;

	private IEvent seedChange;
	private TextureObject mapTexture;

	public MapGenerator() {
		super.setName("maps");
		this.mapTexture = null;

		FlounderEvents.get().addEvent(seedChange = new IEvent() {
			private int seed = KosmosChunks.get().getNoise().getSeed();

			@Override
			public boolean eventTriggered() {
				int currentSeed = KosmosChunks.get().getNoise().getSeed();
				boolean changed = seed != currentSeed;
				seed = currentSeed;
				return changed;
			}

			@Override
			public void onEvent() {
				KosmosChunks.get().clear(true);
				generateMap(seed);
			}
		});
	}

	/**
	 * Generates a map for the current seed.
	 */
	private void generateMap(int seed) {
		// Account for the null seed.
		if (seed == -1) {
			if (mapTexture != null && mapTexture.isLoaded()) {
				mapTexture.delete();
			}

			return;
		}

		FlounderLogger.get().log("Generating map for seed: " + seed);

		//BufferedImage imageIsland = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
		//BufferedImage imageHeight = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
		//BufferedImage imageMoisture = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
		BufferedImage imageBiome = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < MAP_SIZE; y++) {
			for (int x = 0; x < MAP_SIZE; x++) {
				float worldX = ((float) x / ((float) MAP_SIZE / (float) Chunk.WORLD_SIZE)) - ((float) Chunk.WORLD_SIZE / 2.0f);
				float worldZ = ((float) y / ((float) MAP_SIZE / (float) Chunk.WORLD_SIZE)) - ((float) Chunk.WORLD_SIZE / 2.0f);

				float factorIsland = Chunk.getIslandMap(worldX, worldZ);
				//imageIsland.setRGB(x, y, (((int) (255.0f * factorIsland) << 8) + ((int) (255.0f * factorIsland)) << 8) + ((int) (255.0f * factorIsland)));

				float factorHeight = Chunk.getHeightMap(worldX, worldZ);
				//imageHeight.setRGB(x, y, (((int) (255.0f * factorHeight) << 8) + ((int) (255.0f * factorHeight)) << 8) + ((int) (255.0f * factorHeight)));

				float factorMoisture = Chunk.getMoistureMap(worldX, worldZ);
				//Colour colourMoisture = Colour.interpolate(new Colour(1.0f, 0.0f, 0.0f), new Colour(0.0f, 0.0f, 1.0f), factorMoisture, null);
				//imageMoisture.setRGB(x, y, (((int) (255.0f * colourMoisture.r) << 8) + ((int) (255.0f * colourMoisture.g)) << 8) + ((int) (255.0f * colourMoisture.b)));

				Colour colourBiome = Chunk.getBiomeMap(worldX, worldZ).getBiome().getColour();
				imageBiome.setRGB(x, y, (((int) (255.0f * colourBiome.r) << 8) + ((int) (255.0f * colourBiome.g)) << 8) + ((int) (255.0f * colourBiome.b)));
			}
		}

		File directorySave = new File(Framework.getRoamingFolder().getPath() + "/saves/");

		if (!directorySave.exists()) {
			System.out.println("Creating directory: " + directorySave);

			try {
				directorySave.mkdir();
			} catch (SecurityException e) {
				System.out.println("Filed to create directory: " + directorySave.getPath() + ".");
				e.printStackTrace();
			}
		}

		//File outputIsland = new File(directorySave.getPath() + "/" + seed + "-island.png");
		//File outputHeight = new File(directorySave.getPath() + "/" + seed + "-height.png");
		//File outputMoisture = new File(directorySave.getPath() + "/" + seed + "-moisture.png");
		File outputBiome = new File(directorySave.getPath() + "/" + seed + "-biome.png");

		try {
			// Save the map texture.
			//ImageIO.write(imageIsland, "png", outputIsland);
			//ImageIO.write(imageHeight, "png", outputHeight);
			//ImageIO.write(imageMoisture, "png", outputMoisture);
			ImageIO.write(imageBiome, "png", outputBiome);

			// Remove old map texture.
			if (mapTexture != null && mapTexture.isLoaded()) {
				mapTexture.delete();
			}

			// Load the map texture after a few seconds.
			new java.util.Timer().schedule(
					new java.util.TimerTask() {
						@Override
						public void run() {
							mapTexture = TextureFactory.newBuilder().setFile(new MyFile(Framework.getRoamingFolder(), "saves", seed + "-biome.png")).create();
						}
					},
					1000
			);
		} catch (IOException e) {
			FlounderLogger.get().error("Could not save map image to file: " + outputBiome);
			FlounderLogger.get().exception(e);
		}
	}

	public TextureObject getMapTexture() {
		return mapTexture;
	}

	public void delete() {
		FlounderEvents.get().removeEvent(seedChange);

		if (mapTexture != null) {
			mapTexture.delete();
		}
	}
}
