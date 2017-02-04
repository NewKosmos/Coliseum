package coliseum;

import coliseum.world.*;
import flounder.devices.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.standard.*;

import static org.lwjgl.glfw.GLFW.*;

public class ColiseumInterface extends IStandard {
	private KeyButton screenshot;
	private KeyButton fullscreen;
	private KeyButton polygons;
	private KeyButton closeWindow;

	public ColiseumInterface() {
		super(FlounderDisplay.class, FlounderKeyboard.class, ColiseumWorld.class);
	}

	@Override
	public void init() {
		this.screenshot = new KeyButton(GLFW_KEY_F2);
		this.fullscreen = new KeyButton(GLFW_KEY_F11);
		this.polygons = new KeyButton(GLFW_KEY_P);
		this.closeWindow = new KeyButton(GLFW_KEY_DELETE);

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return screenshot.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderDisplay.screenshot();
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return fullscreen.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderDisplay.setFullscreen(!FlounderDisplay.isFullscreen());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return polygons.wasDown();
			}

			@Override
			public void onEvent() {
				OpenGlUtils.goWireframe(!OpenGlUtils.isInWireframe());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return closeWindow.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderFramework.requestClose();
			}
		});
	}

	@Override
	public void update() {

	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {
		Coliseum.closeConfigs();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
