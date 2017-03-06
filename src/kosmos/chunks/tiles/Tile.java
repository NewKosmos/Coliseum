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
			TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "grass", "grass.png")).clampEdges().create()
	);
	public static final Tile TILE_STONE = new Tile(
			ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.obj")).create(),
			TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "stone", "stone.png")).clampEdges().create()
	);
	public static final Tile TILE_SAND = new Tile(
			ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "sand", "sand.obj")).create(),
			TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "sand", "sand.png")).clampEdges().create()
	);
	public static final Tile TILE_SNOW = new Tile(
			ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "snow", "snow.obj")).create(),
			TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "snow", "snow.png")).clampEdges().create()
	);

	private ModelObject model;
	private TextureObject texture;

	protected Tile(ModelObject model, TextureObject texture) {
		this.model = model;
		this.texture = texture;
		this.texture.setHasAlpha(false);
	}

	public ModelObject getModel() {
		return model;
	}

	public TextureObject getTexture() {
		return texture;
	}

	public static Vector3f hexagonSpace(float x, float z, double length, Vector3f destination) {
		if (destination == null) {
			destination = new Vector3f();
		}

		destination.x = (float) (((Math.sqrt(3.0) / 3.0) * x - (z / 3.0f)) / length);
		destination.y = (float) (-((Math.sqrt(3.0) / 3.0) * x + (z / 3.0f)) / length);
		destination.z = (float) ((2.0 / 3.0) * z / length);
		return destination;
	}

	public static Vector2f worldSpace2D(float r, float g, float b, double length, Vector2f destination) {
		if (destination == null) {
			destination = new Vector2f();
		}

		destination.x = (float) (Math.sqrt(3.0) * length * ((b / 2.0) + r));
		destination.y = (float) ((3.0 / 2.0) * length * b);
		return destination;
	}
}
