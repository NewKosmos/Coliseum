package coliseum;

import coliseum.world.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.events.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.standard.*;

import static org.lwjgl.glfw.GLFW.*;

public class ColiseumInterface extends IStandard {
	private KeyButton screenshot;
	private KeyButton fullscreen;
	private KeyButton polygons;

	public ColiseumInterface() {
		super(FlounderDisplay.class, FlounderKeyboard.class, ColiseumWorld.class);
	}

	@Override
	public void init() {
		this.screenshot = new KeyButton(GLFW_KEY_F2);
		this.fullscreen = new KeyButton(GLFW_KEY_F11);
		this.polygons = new KeyButton(GLFW_KEY_P);

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

		FlounderBounding.toggle(Coliseum.configMain.getBooleanWithDefault("boundings_render", false, FlounderBounding::renders));
		FlounderProfiler.toggle(Coliseum.configMain.getBooleanWithDefault("profiler_open", false, FlounderProfiler::isOpen));
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
