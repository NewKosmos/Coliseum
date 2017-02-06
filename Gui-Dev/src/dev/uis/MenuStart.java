/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package dev.uis;

import flounder.devices.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

public class MenuStart extends GuiComponent {
	private MasterMenu superMenu;
	private MasterSlider masterSlider;

	private GuiTexture t1 = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "1.png")).create());
	private GuiTexture t2 = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "2.png")).create());
	private GuiTexture t3 = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "3.png")).create());
	private GuiTexture t4 = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "4.png")).create());

	public MenuStart(MasterMenu superMenu, MasterSlider masterSlider) {
		this.superMenu = superMenu;
		this.masterSlider = masterSlider;

		/*float currentY = 1.0f + MasterSlider.BUTTONS_Y_SEPARATION;
		createQuitButton(currentY -= MasterSlider.BUTTONS_Y_SEPARATION);
		currentY -= MasterSlider.BUTTONS_Y_SEPARATION * MasterSlider.BUTTONS_Y_SEPARATION;

		createPlayButton(currentY -= MasterSlider.BUTTONS_Y_SEPARATION);*/

		createPlayButton(0.1f);
		createQuitButton(0.2f);
	}

	private void createQuitButton(float yPos) {
		GuiTextButton button = MasterSlider.createButton("Quit", yPos, this);
		button.addLeftListener(FlounderFramework::requestClose);
		button.addRightListener(null);
	}

	private void createPlayButton(float yPos) {
		//	GuiCheckbox checkbox = MasterSlider.createCheckbox("Testing", GuiAlign.LEFT, yPos - MasterSlider.BUTTONS_Y_SEPARATION, false, this);
		GuiTextButton button = MasterSlider.createButton("Play", yPos, this);
		//	button.addLeftListener(() -> masterSlider.setNewSecondaryScreen(screenPlay, true));
		//	button.addRightListener(null);
	}

	@Override
	protected void updateSelf() {
		float extraWidth = 0.15f;
		float extraHeight = 0.15f;

		float moveX = FlounderMouse.getPositionX() - 1.0f;
		float moveY = FlounderMouse.getPositionY() - 1.0f;

		t1.setPosition(FlounderDisplay.getAspectRatio() / 2.0f, 0.5f, FlounderDisplay.getAspectRatio(), 1.0f);
		t1.update();

		t2.setPosition(
				(FlounderDisplay.getAspectRatio() - (extraWidth * -moveX)) / 2.0f,
				(1.0f - (extraHeight * moveY)) / 2.0f,
				FlounderDisplay.getAspectRatio() + extraWidth,
				1.0f + extraHeight
		);
		t2.update();

		t3.setPosition(
				(FlounderDisplay.getAspectRatio() - (extraWidth * moveX)) / 2.0f,
				(1.0f - (extraHeight * -moveY)) / 2.0f,
				FlounderDisplay.getAspectRatio() + extraWidth,
				1.0f + extraHeight
		);
		t3.update();

		t4.setPosition(
				(FlounderDisplay.getAspectRatio() - (extraWidth * moveX)) / 2.0f,
				(1.0f - (extraHeight * moveY)) / 2.0f,
				FlounderDisplay.getAspectRatio() + extraWidth,
				1.0f + extraHeight
		);
		t4.update();
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		//if (isShown()) {
			guiTextures.add(t1);
			guiTextures.add(t2);
			guiTextures.add(t3);
			guiTextures.add(t4);
		//}
	}
}
