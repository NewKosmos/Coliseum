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
import flounder.guis.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.chunks.*;
import kosmos.world.*;

public class OverlayMap extends ScreenObject {
	private static final float VIEW_SIZE_X = 0.98f;
	private static final float VIEW_SIZE_Y = 0.98f;
	private static final float VIEW_POSITION_X = 0.5f;
	private static final float VIEW_POSITION_Y = 0.5f;

	private GuiObject backgroundView;
	private GuiObject mapViewTexture;
	private GuiObject playerPosition;
	private float zoomAmount;

	public OverlayMap(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.backgroundView = new GuiObject(this, new Vector2f(VIEW_POSITION_X, VIEW_POSITION_Y), new Vector2f(VIEW_SIZE_X, VIEW_SIZE_Y), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "map.png")).create(), 1);
		this.backgroundView.setAlphaDriver(new ConstantDriver(0.9f));
		this.backgroundView.setInScreenCoords(false);

		this.mapViewTexture = new GuiObject(this, new Vector2f(VIEW_POSITION_X, VIEW_POSITION_Y), new Vector2f(VIEW_SIZE_X, VIEW_SIZE_Y), null, 1);
		this.mapViewTexture.setAlphaDriver(new ConstantDriver(0.8f));
		// this.mapViewTexture.setScaleDriver(new VarianceDriver(1.0f));
		this.mapViewTexture.setInScreenCoords(false);

		this.playerPosition = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.02f, 0.02f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "pointer.png")).create(), 1);
		this.playerPosition.setRotationDriver(new VarianceDriver(0.0f));
		this.playerPosition.setInScreenCoords(false);

		this.zoomAmount = 1.0f;
	}

	@Override
	public void updateObject() {
		this.mapViewTexture.setTexture(KosmosChunks.get().getMapGenerator().getMapTexture());
		// VarianceDriver.set(mapViewTexture.getScaleDriver(), zoomAmount);

		Entity player = KosmosWorld.get().getEntityPlayer();

		if (player != null) {
			float px = player.getPosition().x / Chunk.WORLD_SIZE;
			float pz = player.getPosition().z / Chunk.WORLD_SIZE;

			this.mapViewTexture.getScissor().set(
					(int) (VIEW_POSITION_X - (VIEW_SIZE_X / FlounderDisplay.get().getAspectRatio() / 2.0f)) * FlounderDisplay.get().getWidth(), (VIEW_POSITION_Y - (VIEW_SIZE_X / 2.0f)) * FlounderDisplay.get().getHeight(),
					VIEW_SIZE_X * FlounderDisplay.get().getWidth(), VIEW_SIZE_Y * FlounderDisplay.get().getHeight()
			);
			this.backgroundView.getScissor().set(this.mapViewTexture.getScissor());

			this.playerPosition.getPosition().set(
					(((mapViewTexture.isInScreenCoords() ? FlounderDisplay.get().getAspectRatio() : 1.0f) * VIEW_POSITION_X) - (VIEW_SIZE_X * 0.5f)) + (VIEW_SIZE_X * px) + (VIEW_SIZE_X * 0.5f),
					(VIEW_POSITION_Y - (VIEW_SIZE_Y * 0.5f)) + (VIEW_SIZE_Y * pz) + (VIEW_SIZE_Y * 0.5f)
			);
			VarianceDriver.set(playerPosition.getRotationDriver(), -player.getRotation().y + 180.0f);
		}
	}

	@Override
	public void deleteObject() {
	}
}