package testing;

import flounder.devices.*;
import flounder.entities.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.bounding.*;
import flounder.standards.*;
import flounder.textures.*;
import testing.entities.*;

import static org.lwjgl.glfw.GLFW.*;

public class TestingInterface extends Standard {
	private KeyButton screenshot;
	private KeyButton fullscreen;
	private KeyButton polygons;
	private KeyButton aabbs;
	private KeyButton closeWindow;

	public TestingInterface() {
		super(FlounderEvents.class, FlounderEntities.class, FlounderTextures.class, FlounderModels.class);
	}

	@Override
	public void init() {
		this.screenshot = new KeyButton(GLFW_KEY_F2);
		this.fullscreen = new KeyButton(GLFW_KEY_F11);
		this.polygons = new KeyButton(GLFW_KEY_P);
		this.aabbs = new KeyButton(GLFW_KEY_O);
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
				return aabbs.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderBounding.toggle(!FlounderBounding.renders());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return closeWindow.wasDown();
			}

			@Override
			public void onEvent() {
				Framework.requestClose();
			}
		});

		new InstanceTree3(FlounderEntities.getEntities(), new Vector3f(), new Vector3f());
	}

	@Override
	public void update() {

	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
