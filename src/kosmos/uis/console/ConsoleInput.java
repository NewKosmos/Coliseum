/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis.console;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import org.lwjgl.glfw.*;

import java.util.*;

public class ConsoleInput extends GuiComponent {
	// TODO: Add blinking addition point + arrow controls, fix shift items.

	private static final String START_STRING = "Command: ";
	private ConsoleText consoleText;

	private ConsoleDelay inputDelay;
	private boolean activeTyping;
	private int lastKey;

	private Text currentInput;

	protected ConsoleInput(ConsoleText consoleText) {
		this.consoleText = consoleText;

		this.inputDelay = new ConsoleDelay();
		this.activeTyping = false;
		this.lastKey = 0;

		this.currentInput = Text.newText(START_STRING).textAlign(GuiAlign.LEFT).setFontSize(0.875f).setFont(FlounderFonts.SEGO_UI).create();
		this.currentInput.setColour(0.5f, 0.5f, 0.5f);
		addText(currentInput, 0.01f, -0.001f, 1.0f);
	}

	@Override
	protected void updateSelf() {
		if (!activeTyping) {
			return;
		}

		int key = FlounderKeyboard.getKeyboardChar();

		// TODO: Fix inputs that are not GLFW defined.
		if (key != 0 && FlounderKeyboard.getKey(java.lang.Character.toUpperCase(key))) {
			inputDelay.update(true);

			if (lastKey != key || inputDelay.canInput()) {
				currentInput.setText(currentInput.getTextString() + ((char) key));
				lastKey = key;
			}
		} else if (FlounderKeyboard.getKey(GLFW.GLFW_KEY_BACKSPACE)) {
			inputDelay.update(true);

			if (lastKey != 8 || inputDelay.canInput()) {
				String currentString = currentInput.getTextString();

				if (currentString.length() - 1 >= START_STRING.length()) {
					currentInput.setText(currentString.substring(0, currentString.length() - 1));
					lastKey = 8;
				}
			}
		} else if (FlounderKeyboard.getKey(GLFW.GLFW_KEY_ENTER) && lastKey != 13) {
			inputDelay.update(true);
			String s = currentInput.getTextString().replace(START_STRING, "").trim();

			if (!s.isEmpty()) {
				FlounderLogger.log("[Console]: " + s);
				IConsoleCommand.ConsoleCommands.runCommand(s);
				consoleText.addText(s, new Colour(1.0f, 1.0f, 1.0f));
				currentInput.setText(START_STRING);
				lastKey = 13;
			}
		} else {
			inputDelay.update(false);
			lastKey = 0;
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}

	protected void setActiveTyping(boolean activeTyping) {
		if (activeTyping) {
			this.currentInput.setColour(1.0f, 1.0f, 1.0f);
		} else {
			this.currentInput.setColour(0.5f, 0.5f, 0.5f);
		}

		this.activeTyping = activeTyping;
	}
}
