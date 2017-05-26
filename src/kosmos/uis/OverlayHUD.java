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
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.camera.*;
import kosmos.chunks.*;
import kosmos.world.*;

public class OverlayHUD extends ScreenObject {
	private static final float MAP_SIZE = 0.28f;

	private static int crosshairSelected;
	private GuiObject crossHair;

	private TextureObject hudTexture;
	private TextureObject hudProgress;
	private HudStatus statusHealth;
	private HudStatus statusThirst;
	private HudStatus statusHunger;

	private GuiObject mapBackgroundTexture;
	private GuiObject mapViewTexture;
	private GuiObject mapOverlayTexture;
	private float mapZoomAmount;

	public OverlayHUD(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		OverlayHUD.crosshairSelected = KosmosConfigs.HUD_COSSHAIR_TYPE.setReference(OverlayHUD::getCrosshairSelected).getInteger();
		this.crossHair = new GuiObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.04f, 0.04f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "crosshair.png")).setNumberOfRows(4).create(), crosshairSelected);
		this.crossHair.setInScreenCoords(true);
		this.crossHair.setColourOffset(new Colour(FlounderGuis.get().getGuiMaster().getPrimaryColour()));

		this.hudTexture = TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "hudSprites.png")).setNumberOfRows(3).create();
		this.hudProgress = TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "circularProgress.png")).setNumberOfRows(4).create();
		this.statusHealth = new HudStatus(this, hudTexture, hudProgress, 2, 0.0f, new Colour(1.0f, 0.2f, 0.2f));
		this.statusThirst = new HudStatus(this, hudTexture, hudProgress, 3, 0.1f, new Colour(0.2f, 0.2f, 1.0f));
		this.statusHunger = new HudStatus(this, hudTexture, hudProgress, 4, 0.2f, new Colour(1.0f, 0.4f, 0.0f));

		this.mapBackgroundTexture = new GuiObject(this, new Vector2f(), new Vector2f(MAP_SIZE, MAP_SIZE), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "hudMapBackground.png")).create(), 1);
		this.mapBackgroundTexture.setInScreenCoords(false);

		this.mapViewTexture = new GuiObject(this, new Vector2f(), new Vector2f(MAP_SIZE, MAP_SIZE), null, 1);
		this.mapViewTexture.setScaleDriver(new VarianceDriver(1.0f));
		this.mapViewTexture.setInScreenCoords(false);

		this.mapOverlayTexture = new GuiObject(this, new Vector2f(), new Vector2f(MAP_SIZE, MAP_SIZE), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "hudMap.png")).create(), 1);
		this.mapOverlayTexture.setRotationDriver(new VarianceDriver(0.0f));
		this.mapOverlayTexture.setInScreenCoords(false);

		this.mapZoomAmount = 3.20f;
	}

	@Override
	public void updateObject() {
		this.crossHair.setColourOffset(FlounderGuis.get().getGuiMaster().getPrimaryColour());
		this.crossHair.setSelectedRow(crosshairSelected);
		this.crossHair.setVisible(KosmosCamera.isFirstPerson());

		this.statusHealth.persentage = KosmosWorld.get().getDayFactor();
		this.statusThirst.persentage = KosmosWorld.get().getShadowFactor();
		this.statusHunger.persentage = KosmosWorld.get().getSunriseFactor();

		this.mapViewTexture.setTexture(KosmosChunks.get().getMapGenerator().getMapTexture());
		VarianceDriver.set(mapViewTexture.getScaleDriver(), mapZoomAmount);

		Entity player = KosmosWorld.get().getEntityPlayer();

		if (player != null) {
			float px = player.getPosition().x / Chunk.WORLD_SIZE;
			float pz = player.getPosition().z / Chunk.WORLD_SIZE;

			this.mapBackgroundTexture.getPosition().set(FlounderDisplay.get().getAspectRatio() - (MAP_SIZE / 2.0f), MAP_SIZE / 2.0f);
			this.mapViewTexture.getPosition().set(FlounderDisplay.get().getAspectRatio() - (MAP_SIZE / 2.0f), MAP_SIZE / 2.0f);
			this.mapViewTexture.getPosition().set(
					(((mapViewTexture.isInScreenCoords() ? FlounderDisplay.get().getAspectRatio() : 1.0f) * mapViewTexture.getPosition().x) + (MAP_SIZE * 0.5f)) - (MAP_SIZE * mapZoomAmount * px) - (MAP_SIZE * 0.5f),
					(mapViewTexture.getPosition().y + (MAP_SIZE * 0.5f)) - (MAP_SIZE * mapZoomAmount * pz) - (MAP_SIZE * 0.5f)
			);
			this.mapOverlayTexture.getPosition().set(FlounderDisplay.get().getAspectRatio() - (MAP_SIZE / 2.0f), MAP_SIZE / 2.0f);

			this.mapViewTexture.getScissor().set(
					(1.0f - (MAP_SIZE / FlounderDisplay.get().getAspectRatio())) * FlounderDisplay.get().getWidth(), (1.0f - MAP_SIZE) * FlounderDisplay.get().getHeight(),
					MAP_SIZE * FlounderDisplay.get().getWidth(), MAP_SIZE * FlounderDisplay.get().getHeight()
			);
			this.mapBackgroundTexture.getScissor().set(this.mapViewTexture.getScissor());
			this.mapOverlayTexture.getScissor().set(this.mapViewTexture.getScissor());

			VarianceDriver.set(mapOverlayTexture.getRotationDriver(), -player.getRotation().y + 180.0f);
		}
	}

	public static int getCrosshairSelected() {
		return crosshairSelected;
	}

	public static void setCrosshairSelected(int crosshairSelected) {
		OverlayHUD.crosshairSelected = (int) Maths.clamp(crosshairSelected, 0, 8);
	}

	@Override
	public void deleteObject() {
	}

	private static class HudStatus extends ScreenObject {
		private GuiObject background;
		private GuiObject foreground;
		private GuiObject progress;
		private GuiObject mainIcon;
		private float persentage;

		private HudStatus(ScreenObject parent, TextureObject hudTexture, TextureObject hudProgress, int main, float offset, Colour colour) {
			super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));

			this.background = new GuiObject(this, new Vector2f(0.06f + offset, 0.94f), new Vector2f(0.1f, 0.1f), hudTexture, 0);
			this.background.setInScreenCoords(false);

			this.foreground = new GuiObject(this, new Vector2f(0.06f + offset, 0.94f), new Vector2f(0.08f, 0.08f), hudTexture, 1);
			this.foreground.setInScreenCoords(false);

			this.progress = new GuiObject(this, new Vector2f(0.06f + offset, 0.94f), new Vector2f(0.08f, 0.08f), hudProgress, 1);
			this.progress.setInScreenCoords(false);
			this.progress.setColourOffset(colour);

			this.mainIcon = new GuiObject(this, new Vector2f(0.06f + offset, 0.94f), new Vector2f(0.06f, 0.06f), hudTexture, main);
			this.mainIcon.setInScreenCoords(false);

			this.persentage = 0.0f;
		}

		@Override
		public void updateObject() {
			progress.setSelectedRow((int) Math.floor(persentage * Math.pow(progress.getTexture().getNumberOfRows(), 2)));
		}

		@Override
		public void deleteObject() {
		}
	}
}
