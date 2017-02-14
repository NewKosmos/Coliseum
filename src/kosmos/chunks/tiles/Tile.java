/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.tiles;

import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.textures.*;

public class Tile {
	public static final Tile TILE_GRASS = new Tile(
			ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "grass", "grass.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "grass", "grass.png")).clampEdges().create()
	);
	public static final Tile TILE_STONE = new Tile(
			ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.png")).clampEdges().create()
	);
	public static final Tile TILE_SAND = new Tile(
			ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "sand", "sand.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "sand", "sand.png")).clampEdges().create()
	);
	public static final Tile TILE_SNOW = new Tile(
			ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "snow", "snow.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "snow", "snow.png")).clampEdges().create()
	);
	public static final Tile TILE_ROCK_GEM = new Tile(
			ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "rockGem", "rockGem.obj")).create(),
			Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "terrains", "rockGem", "rockGem.png")).clampEdges().create()
	);

	private ModelObject model;
	private Texture texture;

	protected Tile(ModelObject model, Texture texture) {
		this.model = model;
		this.texture = texture;
	}

	public ModelObject getModel() {
		return model;
	}

	public Texture getTexture() {
		return texture;
	}

	public static Vector3f hexagonSpace(Vector2f position, float length, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		destination.x = (((float) Math.sqrt(3.0f) / 3.0f) * position.x - (position.y / 3.0f)) / length;
		destination.y = -(((float) Math.sqrt(3.0f) / 3.0f) * position.x + (position.y / 3.0f)) / length;
		destination.z = (2.0f / 3.0f) * position.y / length;
		return destination;
	}

	public static Vector2f worldSpace2D(Vector3f position, float length, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		destination.x = (float) Math.sqrt(3.0f) * length * ((position.z / 2.0f) + position.x);
		destination.y = (3.0f / 2.0f) * length * position.z;
		return destination;
	}
}
