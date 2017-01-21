package coliseum.uis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.Timer;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import org.lwjgl.glfw.*;

import java.util.*;

public class ConsoleUi extends GuiComponent {
	private static final float GRAB_BAR_HEIGHT = 0.03f;
	private static final float INPUT_AREA_HEIGHT = 0.041f;
	private static final float BUTTON_BAR_SIZE = 0.02f;

	private GuiTexture consoleTopBar;
	private GuiTexture consoleBody;
	private GuiTexture consoleInput;
	private GuiTexture consoleClose;
	private GuiTexture consoleLock;
	private Vector2f consolePosition;
	private Vector2f consoleSize;

	private ConsoleUiText consoleUiText;
	private ConsoleUIInput consoleUIInput;

	private MouseButton leftClick;
	private MouseButton rightClick;
	private boolean cursorGrabbed;

	private boolean windowLocked;
	private boolean windowClosed;

	public ConsoleUi() {
		consoleTopBar = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_top.png")).create());
		consoleTopBar.getTexture().setHasTransparency(true);
		consoleBody = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_body.png")).create());
		consoleBody.getTexture().setHasTransparency(true);
		consoleInput = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_input.png")).create());
		consoleInput.getTexture().setHasTransparency(true);
		consoleClose = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_circle.png")).create());
		consoleClose.getTexture().setHasTransparency(true);
		consoleLock = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_circle.png")).create());
		consoleLock.getTexture().setHasTransparency(true);
		consolePosition = new Vector2f(0.2f, 0.2f);
		consoleSize = new Vector2f(0.7f, 0.5f);

		consoleUiText = new ConsoleUiText();
		consoleUiText.addText("Use left mouse within text area to move, right to resize.", new Colour(0.81f, 0.37f, 0.24f));
		consoleUiText.addText("To enter commands: have your cursor in the console and type!", new Colour(0.81f, 0.37f, 0.24f));
		addComponent(consoleUiText, 0.0f, 0.0f, 1.0f, 1.0f);

		consoleUIInput = new ConsoleUIInput(consoleUiText);
		addComponent(consoleUIInput, 0.0f, 0.0f, 1.0f, 1.0f);

		leftClick = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_1);
		rightClick = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_2);

		cursorGrabbed = false;
		windowLocked = false;
		windowClosed = false;

		consoleClose.setColourOffset(new Colour(0.910f, 0.137f, 0.066f));
		consoleLock.setColourOffset(new Colour(0.066f, 0.510f, 0.137f));
	}

	@Override
	protected void updateSelf() {
		if (leftClick.wasDown()) {
			if (consoleClose.isMouseOver()) {
				windowClosed = !windowClosed;

				if (windowClosed) {
					consoleClose.setColourOffset(new Colour(0.137f, 0.066f, 0.910f));
				} else {
					consoleClose.setColourOffset(new Colour(0.910f, 0.137f, 0.066f));
				}

				FlounderLogger.log("Console Window Closed: " + windowClosed);
			} else if (consoleLock.isMouseOver()) {
				windowLocked = !windowLocked;

				if (windowLocked) {
					consoleLock.setColourOffset(new Colour(0.066f, 1.00f, 0.037f));
				} else {
					consoleLock.setColourOffset(new Colour(0.066f, 0.510f, 0.137f));
				}

				FlounderLogger.log("Console Window Locked: " + windowLocked);
			} else if (consoleUiText.isMouseOver() || consoleTopBar.isMouseOver()) {
				cursorGrabbed = true;
			}
		} else if (rightClick.wasDown()) {
			if (consoleUiText.isMouseOver() || consoleTopBar.isMouseOver()) {
				cursorGrabbed = true;
			}
		}

		if (leftClick.isDown() && !windowLocked) {
			if (cursorGrabbed) {
				consolePosition.x -= FlounderMouse.getDeltaX() * FlounderDisplay.getAspectRatio() * (1.0f / FlounderFramework.getDelta());
				consolePosition.y -= FlounderMouse.getDeltaY() * (1.0f / FlounderFramework.getDelta());
				cursorGrabbed = true;
			}
		} else if (rightClick.isDown() && !windowLocked) {
			if (cursorGrabbed) {
				consoleSize.x -= FlounderMouse.getDeltaX() * FlounderDisplay.getAspectRatio() * (1.0f / FlounderFramework.getDelta());
				consoleSize.y -= FlounderMouse.getDeltaY() * (1.0f / FlounderFramework.getDelta());
				consoleSize.x = Maths.clamp(consoleSize.x, 3.0f * BUTTON_BAR_SIZE, Float.POSITIVE_INFINITY);
				consoleSize.y = Maths.clamp(consoleSize.y, GRAB_BAR_HEIGHT, Float.POSITIVE_INFINITY);
				cursorGrabbed = true;
			}
		} else {
			cursorGrabbed = false;
		}

		consoleTopBar.setPosition((consolePosition.x) + (consoleSize.x / 2.0f), consolePosition.y, consoleSize.x, GRAB_BAR_HEIGHT);
		consoleTopBar.update();

		consoleBody.setPosition((consolePosition.x) + (consoleSize.x / 2.0f), consolePosition.y + ((GRAB_BAR_HEIGHT + consoleSize.y) / 2.0f), consoleSize.x, consoleSize.y);
		consoleBody.update();

		consoleInput.setPosition((consolePosition.x) + (consoleSize.x / 2.0f), consolePosition.y + consoleSize.y - (INPUT_AREA_HEIGHT / 5.0f), consoleSize.x, INPUT_AREA_HEIGHT);
		consoleInput.update();

		consoleClose.setPosition((consolePosition.x) + (BUTTON_BAR_SIZE / 2.0f) + ((GRAB_BAR_HEIGHT - BUTTON_BAR_SIZE) / 2.0f), consolePosition.y, BUTTON_BAR_SIZE, BUTTON_BAR_SIZE);
		consoleClose.update();

		consoleLock.setPosition((consolePosition.x) + ((3.0f * BUTTON_BAR_SIZE) / 2.0f) + (GRAB_BAR_HEIGHT - BUTTON_BAR_SIZE), consolePosition.y, BUTTON_BAR_SIZE, BUTTON_BAR_SIZE);
		consoleLock.update();

		consoleUIInput.setRelativeX(consolePosition.x);
		consoleUIInput.setRelativeY(consolePosition.y + consoleSize.y - (INPUT_AREA_HEIGHT / 5.0f));

		consoleUiText.setRelativeX(consolePosition.x);
		consoleUiText.setRelativeY(consolePosition.y + GRAB_BAR_HEIGHT);

		consoleUiText.show(!windowClosed);
		consoleUIInput.show(!windowClosed);

		consoleUIInput.setActiveTyping(consoleUiText.isMouseOver());
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		guiTextures.add(consoleTopBar);
		if (!windowClosed) {
			guiTextures.add(consoleBody);
			guiTextures.add(consoleInput);
		}
		guiTextures.add(consoleClose);
		guiTextures.add(consoleLock);
	}

	public static class ConsoleUIInput extends GuiComponent {
		// TODO: Add blinking addition point + arrow controls, fix shift items.

		private static final String START_STRING = "Command: ";
		private ConsoleUiText consoleUiText;

		private boolean activeTyping;
		private Text currentInput;

		private int lastKey;
		private Timer lastKeyTimer;

		public ConsoleUIInput(ConsoleUiText consoleUiText) {
			this.consoleUiText = consoleUiText;

			this.activeTyping = false;
			this.currentInput = Text.newText(START_STRING).textAlign(GuiAlign.LEFT).setFontSize(0.875f).setFont(FlounderFonts.SEGO_UI).create();
			this.currentInput.setColour(1.0f, 1.0f, 1.0f);
			addText(currentInput, 0.01f, 0.0f, 1.0f);

			this.lastKey = -1;
			this.lastKeyTimer = new Timer(0.1);
		}

		@Override
		protected void updateSelf() {
			if (!activeTyping) {
				return;
			}

			int[] keyboardKeys = FlounderKeyboard.getKeyboardKeys();
			boolean isUppercase = false;
			int currentKey = -1;

			for (int i = 0; i < keyboardKeys.length; i++) {
				if (keyboardKeys[i] != GLFW.GLFW_RELEASE) {
					if (i == GLFW.GLFW_KEY_LEFT_SHIFT || i == GLFW.GLFW_KEY_RIGHT_SHIFT) {
						isUppercase = true;
					} else {
						currentKey = i;
					}
				}
			}

			if ((currentKey != lastKey || lastKeyTimer.isPassedTime()) && currentKey != -1) {
				if (currentKey == GLFW.GLFW_KEY_ENTER) {
					consoleUiText.addText(currentInput.getTextString().replace(START_STRING, "").trim(), new Colour(1.0f, 1.0f, 1.0f));
					currentInput.setText(START_STRING);
				} else if (currentKey == GLFW.GLFW_KEY_BACKSPACE) {
					String currentString = currentInput.getTextString();

					if (currentString.length() - 1 >= START_STRING.length()) {
						currentInput.setText(currentString.substring(0, currentString.length() - 1));
					}
				} else {
					char key = (char) currentKey;

					if (isUppercase) {
						key = java.lang.Character.toUpperCase(key);
					} else {
						key = java.lang.Character.toLowerCase(key);
					}

					currentInput.setText(currentInput.getTextString() + key);
				}

				lastKeyTimer.resetStartTime();
			}

			lastKey = currentKey;
		}

		@Override
		protected void getGuiTextures(List<GuiTexture> guiTextures) {
		}

		public void setActiveTyping(boolean activeTyping) {
			this.activeTyping = activeTyping;
		}
	}

	public static class ConsoleUiText extends GuiComponent {
		private List<Text> texts;

		public ConsoleUiText() {
			this.texts = new ArrayList<>();
		}

		@Override
		protected void updateSelf() {
		}

		@Override
		protected void getGuiTextures(List<GuiTexture> guiTextures) {
		}

		protected void addText(String string, Colour colour) {
			Text text = Text.newText(" > " + string).textAlign(GuiAlign.LEFT).setFontSize(0.8f).setFont(FlounderFonts.SEGO_UI).create();
			text.setColour(colour);
			addText(text, 0.01f, 0.01f + (texts.size() * 0.03f), 1.0f);
			texts.add(text);
		}
	}
}
