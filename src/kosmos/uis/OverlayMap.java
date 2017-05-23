/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis;

import flounder.devices.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.chunks.*;
import kosmos.world.*;

import java.io.*;

public class OverlayMap extends ScreenObject {
	private static final float VIEW_SIZE_X = 0.98f;
	private static final float VIEW_SIZE_Y = 0.98f;
	private static final float VIEW_POSITION_X = 0.5f;
	private static final float VIEW_POSITION_Y = 0.5f;

	private int seed;
	private GuiObject backgroundView;
	private GuiObject textureView;
	private GuiObject playerPosition;

	public OverlayMap(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.seed = -1;

		this.backgroundView = new GuiObject(this, new Vector2f(VIEW_POSITION_X, VIEW_POSITION_Y), new Vector2f(VIEW_SIZE_X, VIEW_SIZE_Y), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "map.png")).create(), 1);
		this.backgroundView.setAlphaDriver(new ConstantDriver(0.8f));
		this.backgroundView.setInScreenCoords(true);

		this.textureView = new GuiObject(this, new Vector2f(VIEW_POSITION_X, VIEW_POSITION_Y), new Vector2f(VIEW_SIZE_X, VIEW_SIZE_Y), null, 1);
		this.textureView.setAlphaDriver(new ConstantDriver(0.7f));
		this.textureView.setInScreenCoords(true);

		this.playerPosition = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.02f, 0.02f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "pointer.png")).create(), 1);
		this.playerPosition.setInScreenCoords(false);
	}

	@Override
	public void updateObject() {
		int newSeed = KosmosWorld.get().getNoise().getSeed();

		if (newSeed != seed || textureView.getTexture() == null) {
			MyFile seedFile = new MyFile(Framework.getRoamingFolder(), "saves", "/map_" + newSeed + ".png");
			File outputFile = new File(Framework.getRoamingFolder().getPath() + "/saves/map_" + newSeed + ".png");

			if (outputFile.exists()) {
				textureView.setTexture(TextureFactory.newBuilder().setFile(seedFile).create());
				seed = newSeed;
			} else {
			//	FlounderLogger.get().warning("Could not find saved map for seed: " + newSeed);
				textureView.setTexture(null);
				seed = -1;
			}
		}

		Entity player = KosmosWorld.get().getEntityPlayer();

		if (player != null) {
			float px = player.getPosition().x / Chunk.MAP_SIZE;
			float pz = player.getPosition().z / Chunk.MAP_SIZE;
			playerPosition.getPosition().set(
					(((textureView.isInScreenCoords() ? FlounderDisplay.get().getAspectRatio() : 1.0f) * VIEW_POSITION_X) - (VIEW_SIZE_X * 0.5f)) + (VIEW_SIZE_X * px) + (VIEW_SIZE_X * 0.5f),
					(VIEW_POSITION_Y - (VIEW_SIZE_Y * 0.5f)) + (VIEW_SIZE_Y * pz) + (VIEW_SIZE_Y * 0.5f)
			);
			playerPosition.setRotationDriver(new ConstantDriver(-player.getRotation().y + 180.0f));
		}
	}

	@Override
	public void deleteObject() {
	}
}