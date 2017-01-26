package coliseum.uis.console;

import flounder.devices.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import org.lwjgl.glfw.*;

import java.util.*;

public class ConsoleUi extends GuiComponent {
	private static final float TOP_BAR_HEIGHT = 0.041f;
	private static final float TOP_BUTTON_SIZE = 0.0234f;
	private static final float INPUT_AREA_HEIGHT = 0.041f;

	private GuiTexture textureTopBar;
	private GuiTexture textureBody;
	private GuiTexture textureInput;
	private GuiTexture textureClose;
	private GuiTexture textureLock;
	private Vector2f consolePosition;
	private Vector2f consoleSize;

	private ConsoleText consoleText;
	private ConsoleInput consoleInput;

	private MouseButton leftClick;
	private MouseButton rightClick;
	private boolean cursorGrabbed;

	private boolean windowLocked;
	private boolean windowClosed;

	public ConsoleUi() {
		textureTopBar = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_top.png")).create());
		textureTopBar.getTexture().setHasTransparency(true);
		textureBody = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_body.png")).create());
		textureBody.getTexture().setHasTransparency(true);
		textureInput = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_input.png")).create());
		textureInput.getTexture().setHasTransparency(true);
		textureClose = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_circle.png")).create());
		textureClose.getTexture().setHasTransparency(true);
		textureLock = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "console_circle.png")).create());
		textureLock.getTexture().setHasTransparency(true);
		consolePosition = new Vector2f(0.2f, 0.2f);
		consoleSize = new Vector2f(0.7f, 0.5f);

		consoleText = new ConsoleText();
		consoleText.addText("Use left mouse within text area to move, right to resize.", new Colour(0.81f, 0.37f, 0.24f));
		consoleText.addText("To enter commands: have your cursor in the console and type!", new Colour(0.81f, 0.37f, 0.24f));
		addComponent(consoleText, 0.0f, 0.0f, 1.0f, 1.0f);

		consoleInput = new ConsoleInput(consoleText);
		addComponent(consoleInput, 0.0f, 0.0f, 1.0f, 1.0f);

		leftClick = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_1);
		rightClick = new MouseButton(GLFW.GLFW_MOUSE_BUTTON_2);

		cursorGrabbed = false;
		windowLocked = false;
		windowClosed = false;

		textureClose.setColourOffset(new Colour(0.910f, 0.137f, 0.066f));
		textureLock.setColourOffset(new Colour(0.066f, 0.510f, 0.137f));
	}

	@Override
	protected void updateSelf() {
		if (leftClick.wasDown()) {
			if (textureClose.isMouseOver()) {
				windowClosed = !windowClosed;

				if (windowClosed) {
					textureClose.setColourOffset(new Colour(0.137f, 0.066f, 0.910f));
				} else {
					textureClose.setColourOffset(new Colour(0.910f, 0.137f, 0.066f));
				}

				FlounderLogger.log("Console Window Closed: " + windowClosed);
			} else if (textureLock.isMouseOver()) {
				windowLocked = !windowLocked;

				if (windowLocked) {
					textureLock.setColourOffset(new Colour(0.066f, 1.00f, 0.037f));
				} else {
					textureLock.setColourOffset(new Colour(0.066f, 0.510f, 0.137f));
				}

				FlounderLogger.log("Console Window Locked: " + windowLocked);
			} else if (consoleText.isMouseOver() || textureTopBar.isMouseOver()) {
				cursorGrabbed = true;
			}
		} else if (rightClick.wasDown()) {
			if (consoleText.isMouseOver() || textureTopBar.isMouseOver()) {
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
				consoleSize.x = Maths.clamp(consoleSize.x, 4.0f * TOP_BUTTON_SIZE, Float.POSITIVE_INFINITY);
				consoleSize.y = Maths.clamp(consoleSize.y, TOP_BAR_HEIGHT, Float.POSITIVE_INFINITY);
				cursorGrabbed = true;
			}
		} else {
			cursorGrabbed = false;
		}

		textureTopBar.setPosition(consolePosition.x + (consoleSize.x / 2.0f), consolePosition.y, consoleSize.x, TOP_BAR_HEIGHT);
		textureTopBar.update();

		textureBody.setPosition(consolePosition.x + (consoleSize.x / 2.0f), consolePosition.y + ((TOP_BAR_HEIGHT + consoleSize.y) / 2.0f), consoleSize.x, consoleSize.y);
		textureBody.update();

		textureInput.setPosition(consolePosition.x + (consoleSize.x / 2.0f), consolePosition.y + consoleSize.y, consoleSize.x, INPUT_AREA_HEIGHT);
		textureInput.update();

		textureClose.setPosition(consolePosition.x + (TOP_BUTTON_SIZE / 2.0f) + ((TOP_BAR_HEIGHT - TOP_BUTTON_SIZE) / 2.0f), consolePosition.y, TOP_BUTTON_SIZE, TOP_BUTTON_SIZE);
		textureClose.update();

		textureLock.setPosition(consolePosition.x + ((3.0f * TOP_BUTTON_SIZE) / 2.0f) + (TOP_BAR_HEIGHT - TOP_BUTTON_SIZE), consolePosition.y, TOP_BUTTON_SIZE, TOP_BUTTON_SIZE);
		textureLock.update();

		consoleInput.setRelativeX(consolePosition.x);
		consoleInput.setRelativeY(consolePosition.y + consoleSize.y);

		consoleText.setRelativeX(consolePosition.x);
		consoleText.setRelativeY(consolePosition.y + (TOP_BAR_HEIGHT / 2.0f));

		consoleText.show(!windowClosed);
		consoleInput.show(!windowClosed);

		consoleInput.setActiveTyping(consoleText.isMouseOver());
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		guiTextures.add(textureTopBar);

		if (!windowClosed) {
			guiTextures.add(textureInput);
			guiTextures.add(textureBody);
		}

		guiTextures.add(textureClose);
		guiTextures.add(textureLock);
	}
}
