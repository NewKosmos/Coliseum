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
import kosmos.world.*;

public class OverlayMap extends ScreenObject {
	private static final float VIEW_SIZE_X = 0.9f;
	private static final float VIEW_SIZE_Y = 0.9f;
	private static final float VIEW_POSITION_X = 0.5f;
	private static final float VIEW_POSITION_Y = 0.5f;

	private GuiObject textureView;
	private GuiObject playerPosition;

	public OverlayMap(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.textureView = new GuiObject(this, new Vector2f(VIEW_POSITION_X, VIEW_POSITION_Y), new Vector2f(VIEW_SIZE_X, VIEW_SIZE_Y), TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "map.png")).create(), 1);
		this.textureView.setInScreenCoords(true);

		this.playerPosition = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.04f, 0.04f), TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "pointer.png")).create(), 1);
		this.playerPosition.setInScreenCoords(false);
	}

	@Override
	public void updateObject() {
		Entity player = KosmosWorld.get().getEntityPlayer();

		if (player != null) {
			float px = player.getPosition().x / 512.0f;
			float pz = player.getPosition().z / 512.0f;
			playerPosition.getPosition().set(((FlounderDisplay.get().getAspectRatio() * VIEW_POSITION_X) - (VIEW_SIZE_X * 0.5f)) + (VIEW_SIZE_X * px) + (VIEW_SIZE_X * 0.5f), (VIEW_POSITION_Y - (VIEW_SIZE_Y * 0.5f)) + (VIEW_SIZE_Y * pz) + (VIEW_SIZE_Y * 0.5f));
			playerPosition.setRotationDriver(new ConstantDriver(-player.getRotation().y + 180.0f));
		}
	}

	@Override
	public void deleteObject() {
	}
}