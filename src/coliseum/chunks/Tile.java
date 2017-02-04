package coliseum.chunks;

import flounder.models.*;
import flounder.resources.*;
import flounder.textures.*;

public class Tile {
	public static final Tile TILE_GRASS = new Tile(
			Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "grass", "grass.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "grass", "grass.png")).clampEdges().create()
	);
	public static final Tile TILE_STONE = new Tile(
			Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.png")).clampEdges().create()
	);
	public static final Tile TILE_SAND = new Tile(
			Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "sand", "sand.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "sand", "sand.png")).clampEdges().create()
	);
	public static final Tile TILE_SNOW = new Tile(
			Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "snow", "snow.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "snow", "snow.png")).clampEdges().create()
	);
	public static final Tile TILE_ROCK_GEM = new Tile(
			Model.newModel(new MyFile(MyFile.RES_FOLDER, "terrains", "rockGem", "rockGem.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "rockGem", "rockGem.png")).clampEdges().create()
	);

	public static final Texture TESTING = TILE_SNOW.texture;//Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "testing.png")).clampEdges().create();


	private Model model;
	private Texture texture;

	protected Tile(Model model, Texture texture) {
		this.model = model;
		this.texture = texture;
	}

	public Model getModel() {
		return model;
	}

	public Texture getTexture() {
		return texture;
	}
}
