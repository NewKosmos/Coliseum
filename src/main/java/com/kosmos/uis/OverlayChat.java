/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis;

import com.flounder.devices.*;
import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.logger.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.networking.*;
import com.flounder.resources.*;
import com.flounder.tasks.*;
import com.flounder.textures.*;
import com.flounder.visual.*;
import com.kosmos.*;
import com.kosmos.network.packets.*;

import java.util.*;

import static com.flounder.platform.Constants.*;

// TODO: Add blinking addition point + arrow controls, fix shift items.

public class OverlayChat extends ScreenObject {
	private static final float VIEW_AREA_HEIGHT = 0.5f;
	private static final float VIEW_WRAP_HEIGHT = 0.94f;
	private static final float INPUT_AREA_HEIGHT = 0.05f;
	private static final String START_STRING = "Message: ";

	private InputDelay inputDelay;
	private int lastKey;

	private GuiObject textureView;
	private GuiObject textureInput;
	private TextObject currentInput;

	private ChatMessages chatMessages;

	public OverlayChat(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.inputDelay = new InputDelay();
		this.lastKey = 0;

		this.textureView = new GuiObject(this, new Vector2f(0.5f, 1.0f - (VIEW_AREA_HEIGHT / 2.0f)), new Vector2f(1.0f, VIEW_AREA_HEIGHT), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "chatView.png")).create(), 1);
		this.textureView.setInScreenCoords(false);

		this.textureInput = new GuiObject(this, new Vector2f(0.5f, 1.0f - (INPUT_AREA_HEIGHT / 2.0f)), new Vector2f(1.0f, INPUT_AREA_HEIGHT), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "chatInput.png")).create(), 1);
		this.textureInput.setInScreenCoords(false);

		this.currentInput = new TextObject(this, new Vector2f(0.01f, 1.0f - (INPUT_AREA_HEIGHT / 2.0f)), START_STRING, 1.1f, FlounderFonts.CANDARA, 1.0f, GuiAlign.LEFT);
		this.currentInput.setInScreenCoords(false);
		this.currentInput.setColour(new Colour(1.0f, 1.0f, 1.0f));

		this.chatMessages = new ChatMessages(parent, this); // Parent allows showing new messages if the chat is not open.
	}

	@Override
	public void updateObject() {
		if (!isVisible() || getAlpha() < 0.1f) {
			return;
		}

		textureView.getDimensions().set(2.0f * FlounderDisplay.get().getAspectRatio(), textureView.getDimensions().y);
		textureInput.getDimensions().set(2.0f * FlounderDisplay.get().getAspectRatio(), textureInput.getDimensions().y);

		int key = FlounderKeyboard.get().getKeyboardChar();

		// TODO: Fix inputs that are not GLFW defined.
		if (key != 0 && FlounderKeyboard.get().getKey(java.lang.Character.toUpperCase(key))) {
			inputDelay.update(true);

			if (lastKey != key || inputDelay.canInput()) {
				currentInput.setText(currentInput.getTextString() + ((char) key));
				lastKey = key;
			}
		} else if (FlounderKeyboard.get().getKey(GLFW_KEY_BACKSPACE)) {
			inputDelay.update(true);

			if (lastKey != 8 || inputDelay.canInput()) {
				String currentString = currentInput.getTextString();

				if (currentString.length() - 1 >= START_STRING.length()) {
					currentInput.setText(currentString.substring(0, currentString.length() - 1));
					lastKey = 8;
				}
			}
		} else if (FlounderKeyboard.get().getKey(GLFW_KEY_ENTER) && lastKey != 13) {
			inputDelay.update(true);
			String s = currentInput.getTextString().replace(START_STRING, "").trim();

			if (!s.isEmpty()) {
				FlounderLogger.get().log("[Chat]: " + s);
				addText(s, new Colour(1.0f, 1.0f, 1.0f));

				if (FlounderNetwork.get().getSocketClient() != null && s.charAt(0) != '/') {
					new PacketChat(FlounderNetwork.get().getUsername(), s).writeData(FlounderNetwork.get().getSocketClient());
				}

				currentInput.setText(START_STRING);
				lastKey = 13;
			}
		} else {
			inputDelay.update(false);
			lastKey = 0;
		}
	}

	public static void addText(String string, Colour colour) {
		FlounderTasks.get().addTask(() -> {
			FlounderLogger.get().log(string);
			((KosmosGuis) FlounderGuis.get().getGuiMaster()).getOverlayChat().chatMessages.generateObject(string, colour);
		});
	}

	@Override
	public void deleteObject() {
	}

	private static class ChatMessages extends ScreenObject {
		private OverlayChat overlayChat;

		private List<TextObject> chatMessages;
		private float chatHeight;

		private ChatMessages(ScreenObject parent, OverlayChat overlayChat) {
			super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
			super.setInScreenCoords(false);

			this.overlayChat = overlayChat;

			this.chatMessages = new ArrayList<>();
			this.chatHeight = VIEW_AREA_HEIGHT;
		}

		@Override
		public void updateObject() {
			if (!chatMessages.isEmpty()) {
				for (TextObject m : chatMessages) {
					if (!m.getParent().equals(overlayChat) && m.getAlpha() == 0.0f) {
						m.setAlphaDriver(new ConstantDriver(1.0f));
						m.setParent(overlayChat);
					}

					if (overlayChat.getAlpha() > 0.1f && !m.getParent().equals(overlayChat)) {
						m.setAlphaDriver(new ConstantDriver(1.0f));
						m.setParent(overlayChat);
					}
				}
			}
		}

		private void generateObject(String string, Colour colour) {
			TextObject text = new TextObject(this, new Vector2f(0.01f, chatHeight += 0.03f), " > " + string, 1.0f, FlounderFonts.CANDARA, 1.5f, GuiAlign.LEFT);
			text.setColour(colour);
			chatMessages.add(text);

			if (((KosmosGuis) FlounderGuis.get().getGuiMaster()).getOverlayChat().getAlpha() < 0.1f) {
				text.setAlphaDriver(new SlideDriver(1.0f, 0.0f, 6.0f));
			} else {
				text.setParent(overlayChat);
			}

			if (text.getPosition().y >= VIEW_WRAP_HEIGHT) {
				for (TextObject m : chatMessages) {
					m.getPosition().y -= 0.03f;

					if (m.getPosition().y < VIEW_AREA_HEIGHT) {
						m.setVisible(false);
					}
				}

				chatHeight = text.getPosition().y;
			}

			if (string.charAt(0) == '/') {
				String[] data = string.substring(1, string.length()).split("\\s+");

				for (ICommand.ConsoleCommands console : ICommand.ConsoleCommands.values()) {
					if (console.getCommand().commandPrefix().equals(data[0])) {
						console.getCommand().runCommand(string);
						return;
					}
				}

				generateObject("Could not find command: " + data[0], new Colour(0.81f, 0.37f, 0.24f));
			}
		}

		@Override
		public void deleteObject() {
		}
	}
}