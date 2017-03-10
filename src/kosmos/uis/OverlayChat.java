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
import flounder.fonts.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.networking.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

// TODO: Add blinking addition point + arrow controls, fix shift items.

public class OverlayChat extends GuiComponent {
	private static final float INPUT_AREA_HEIGHT = 0.05f;
	private static final String START_STRING = "Message: ";

	private ConsoleDelay inputDelay;
	private int lastKey;

	private GuiTexture textureInput;
	private Text currentInput;

	private List<Text> chatMessages;

	public OverlayChat() {
		this.inputDelay = new ConsoleDelay();
		this.lastKey = 0;

		this.textureInput = new GuiTexture(TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "guis", "console_input.png")).create());
		this.currentInput = Text.newText(START_STRING).textAlign(GuiAlign.LEFT).setFontSize(0.875f).setFont(FlounderFonts.SEGO_UI).create();
		this.currentInput.setColour(1.0f, 1.0f, 1.0f);
		addText(currentInput, 0.01f, 0.972f, 1.0f);

		this.chatMessages = new ArrayList<>();

		super.show(false);
	}

	@Override
	protected void updateSelf() {
		if (!isShown()) {
			return;
		}

		textureInput.setPosition(0.5f, 1.0f - (INPUT_AREA_HEIGHT / 2.0f), FlounderDisplay.getWidth(), INPUT_AREA_HEIGHT);
		textureInput.update();

		int key = FlounderKeyboard.getKeyboardChar();

		// TODO: Fix inputs that are not GLFW defined.
		if (key != 0 && FlounderKeyboard.getKey(java.lang.Character.toUpperCase(key))) {
			inputDelay.update(true);

			if (lastKey != key || inputDelay.canInput()) {
				currentInput.setText(currentInput.getTextString() + ((char) key));
				lastKey = key;
			}
		} else if (FlounderKeyboard.getKey(GLFW_KEY_BACKSPACE)) {
			inputDelay.update(true);

			if (lastKey != 8 || inputDelay.canInput()) {
				String currentString = currentInput.getTextString();

				if (currentString.length() - 1 >= START_STRING.length()) {
					currentInput.setText(currentString.substring(0, currentString.length() - 1));
					lastKey = 8;
				}
			}
		} else if (FlounderKeyboard.getKey(GLFW_KEY_ENTER) && lastKey != 13) {
			inputDelay.update(true);
			String s = currentInput.getTextString().replace(START_STRING, "").trim();

			if (!s.isEmpty()) {
				FlounderLogger.log("[Chat]: " + s);
				this.addText(s, new Colour(1.0f, 1.0f, 1.0f));
				currentInput.setText(START_STRING);
				lastKey = 13;
			}
		} else {
			inputDelay.update(false);
			lastKey = 0;
		}
	}

	public void addText(String string, Colour colour) {
		Text text = Text.newText(" > " + string).textAlign(GuiAlign.LEFT).setFontSize(0.8f).setFont(FlounderFonts.SEGO_UI).create();
		text.setColour(colour);
		addText(text, 0.01f, 0.02f + (chatMessages.size() * 0.03f), 1.0f);
		chatMessages.add(text);

		if (string.charAt(0) == '/') {
			String[] data = string.substring(1, string.length()).split("\\s+");

			for (ICommand.ConsoleCommands console : ICommand.ConsoleCommands.values()) {
				if (console.getCommand().commandPrefix().equals(data[0])) {
					console.getCommand().runCommand(string);
					return;
				}
			}

			addText("Could not find command: " + data[0], new Colour(0.81f, 0.37f, 0.24f));
		} else {

		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		if (isShown()) {
			guiTextures.add(textureInput);
		}
	}
}
