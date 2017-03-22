package kosmos.uis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.network.packets.*;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

// TODO: Add blinking addition point + arrow controls, fix shift items.

public class OverlayChat extends ScreenObject {
	private static final float INPUT_AREA_HEIGHT = 0.05f;
	private static final String START_STRING = "Message: ";

	public static final List<String> newMessages = new ArrayList<>();

	private ConsoleDelay inputDelay;
	private int lastKey;

	private GuiObject textureInput;
	private TextObject currentInput;

	private List<TextObject> chatMessages;

	public OverlayChat(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.inputDelay = new ConsoleDelay();
		this.lastKey = 0;

		this.textureInput = new GuiObject(this, new Vector2f(0.5f, 1.0f - (INPUT_AREA_HEIGHT / 2.0f)), new Vector2f(1.0f, INPUT_AREA_HEIGHT), TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "guis", "chatInput.png")).create(), 1);
		this.textureInput.setInScreenCoords(false);

		this.currentInput = new TextObject(this, new Vector2f(0.01f, 1.0f - (INPUT_AREA_HEIGHT / 2.0f)), START_STRING, 1.1f, FlounderFonts.CANDARA, 1.0f, GuiAlign.LEFT);
		this.currentInput.setInScreenCoords(false);
		this.currentInput.setColour(new Colour(1.0f, 1.0f, 1.0f));

		this.chatMessages = new ArrayList<>();
	}

	@Override
	public void updateObject() {
		if (!isVisible()) {
			return;
		}

		// Add new chat messages.
		if (!newMessages.isEmpty()) {
			for (String message : newMessages) {
				addText(message, new Colour(0.9f, 1.0f, 0.9f));
			}

			newMessages.clear();
		}

		textureInput.getDimensions().set(2.0f * FlounderDisplay.getAspectRatio(), textureInput.getDimensions().y);

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

				if (FlounderNetwork.getSocketClient() != null && s.charAt(0) != '/') {
					new PacketChat(FlounderNetwork.getUsername(), s).writeData(FlounderNetwork.getSocketClient());
				}

				currentInput.setText(START_STRING);
				lastKey = 13;
			}
		} else {
			inputDelay.update(false);
			lastKey = 0;
		}
	}

	public void addText(String string, Colour colour) {
		TextObject text = new TextObject(this, new Vector2f(0.01f, 0.02f + (chatMessages.size() * 0.03f)), " > " + string, 1.0f, FlounderFonts.CANDARA, 1.5f, GuiAlign.LEFT);
		text.setColour(colour);
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
	public void deleteObject() {
	}
}