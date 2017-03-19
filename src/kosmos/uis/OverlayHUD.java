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
import flounder.framework.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.world.*;

import java.util.*;

public class OverlayHUD extends GuiComponent {
	private TextureObject hudTexture;
	private HudStatus statusHealth;
	private HudStatus statusThirst;
	private HudStatus statusHunger;

	private GuiTexture crossHair;

	public OverlayHUD() {
		this.hudTexture = TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "guis", "hudSprites.png")).setNumberOfRows(3).create();
		this.statusHealth = new HudStatus(hudTexture, 2, 0.0f, new Colour(1.0f, 0.2f, 0.2f));
		this.statusThirst = new HudStatus(hudTexture, 3, 0.1f, new Colour(0.2f, 0.2f, 1.0f));
		this.statusHunger = new HudStatus(hudTexture, 4, 0.2f, new Colour(1.0f, 0.4f, 0.0f));

		this.crossHair = new GuiTexture(TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "guis", "crosshair.png")).setNumberOfRows(4).create());
		this.crossHair.setSelectedRow(KosmosConfigs.HUD_COSSHAIR_TYPE.setReference(() -> crossHair.getSelectedRow()).getInteger());
		this.crossHair.setColourOffset(new Colour(0.6f, 0.2f, 0.5f));

		super.show(true);
	}

	@Override
	protected void updateSelf() {
		statusHealth.update();
		statusThirst.update();
		statusHunger.update();

		float size = (66.6f / (FlounderDisplay.getWidth() + FlounderDisplay.getHeight()));
		crossHair.setPosition((FlounderDisplay.getAspectRatio() / 2.0f) + super.getPosition().x, 0.5f, size, size);
		crossHair.update();
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		if (isShown()) {
			statusHealth.getGuiTextures(guiTextures);
			statusThirst.getGuiTextures(guiTextures);
			statusHunger.getGuiTextures(guiTextures);

			guiTextures.add(crossHair);
		}
	}

	private static class HudStatus {
		private GuiTexture background;
		private GuiTexture foreground;
		private GuiTexture circularProgress;
		private GuiTexture mainIcon;
		private float offset;

		private float progress;

		private HudStatus(TextureObject hudTexture, int main, float offset, Colour colour) {
			this.background = new GuiTexture(hudTexture);
			this.background.setSelectedRow(0);

			this.foreground = new GuiTexture(hudTexture);
			this.foreground.setSelectedRow(1);

			this.circularProgress = new GuiTexture(TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "guis", "circularProgress.png")).setNumberOfRows(4).create());
			this.circularProgress.setColourOffset(colour);

			this.progress = (float) Math.random();

			this.mainIcon = new GuiTexture(hudTexture);
			this.mainIcon.setSelectedRow(main);

			this.offset = offset;

		}

		protected void update() {
			background.setPosition(0.06f + offset, 0.94f, 0.1f, 0.1f);
			background.update();

			foreground.setPosition(0.06f + offset, 0.94f, 0.08f, 0.08f);
			foreground.update();

			progress = KosmosWorld.getDayFactor();
			circularProgress.setPosition(0.06f + offset, 0.94f, 0.08f, 0.08f);
			circularProgress.setSelectedRow((int) Math.floor(progress * Math.pow(circularProgress.getTexture().getNumberOfRows(), 2)));
			circularProgress.update();

			mainIcon.setPosition(0.06f + offset, 0.94f, 0.06f, 0.06f);
			mainIcon.update();
		}

		protected void getGuiTextures(List<GuiTexture> guiTextures) {
			guiTextures.add(background);
			guiTextures.add(foreground);
			guiTextures.add(circularProgress);
			guiTextures.add(mainIcon);
		}
	}
}
