/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos;

import flounder.devices.*;
import flounder.events.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.visual.*;
import kosmos.camera.*;
import kosmos.uis.*;

import static flounder.platform.Constants.*;

public class KosmosGuis extends GuiMaster {
	// private static final Colour COLOUR_PRIMARY = new Colour(0.90196078431f, 0.08235294117f, 0.08235294117f); // Charger Red.
	// private static final Colour COLOUR_PRIMARY = new Colour(0.1f, 0.8f, 0.2f); // Neon Green.
	// private static final Colour COLOUR_PRIMARY = new Colour(0.0824f, 0.396f, 0.753f); // Water Blue.
	private static final Colour COLOUR_PRIMARY = new Colour(0.2f, 0.2f, 1.0f); // Some Blue.

	public static final float SLIDE_TIME = 0.5f;

	private OverlayHUD overlayHUD;
	private OverlayInventory overlayInventory;
	private OverlayMap overlayMap;
	private OverlayDebug overlayDebug;
	private OverlayChat overlayChat;
	private OverlaySlider overlaySlider;
	private OverlayStartup overlayStartup;
	private OverlayAlpha overlayAlpha;

	public KosmosGuis() {
		super();
	}

	@Override
	public void init() {
		this.overlayHUD = new OverlayHUD(FlounderGuis.get().getContainer());
		this.overlayInventory = new OverlayInventory(FlounderGuis.get().getContainer());
		this.overlayMap = new OverlayMap(FlounderGuis.get().getContainer());
		this.overlayDebug = new OverlayDebug(FlounderGuis.get().getContainer());
		this.overlayChat = new OverlayChat(FlounderGuis.get().getContainer());
		this.overlaySlider = new OverlaySlider(FlounderGuis.get().getContainer());
		this.overlayStartup = new OverlayStartup(FlounderGuis.get().getContainer());
		this.overlayAlpha = new OverlayAlpha(FlounderGuis.get().getContainer());

		this.overlayHUD.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayInventory.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayMap.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayDebug.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayChat.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlaySlider.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayStartup.setAlphaDriver(new ConstantDriver(1.0f));
		this.overlayAlpha.setAlphaDriver(new ConstantDriver(0.0f));

		FlounderGuis.get().getSelector().initJoysticks(0, 0, 1, 0, 1);

		FlounderEvents.get().addEvent(new IEvent() {
			private CompoundButton escape = new CompoundButton(new KeyButton(GLFW_KEY_ESCAPE), new JoystickButton(0, 7));

			@Override
			public boolean eventTriggered() {
				return escape.wasDown();
			}

			@Override
			public void onEvent() {
				togglePause(false);
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			private KeyButton toggleDebug = new KeyButton(GLFW_KEY_F3);

			@Override
			public boolean eventTriggered() {
				return toggleDebug.wasDown();
			}

			@Override
			public void onEvent() {
				toggleDebug();
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			private KeyButton toggleHUD = new KeyButton(GLFW_KEY_F4);

			@Override
			public boolean eventTriggered() {
				return toggleHUD.wasDown();
			}

			@Override
			public void onEvent() {
				toggleHUD();
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			private KeyButton toggleChat = new KeyButton(GLFW_KEY_ENTER);

			@Override
			public boolean eventTriggered() {
				return toggleChat.wasDown();
			}

			@Override
			public void onEvent() {
				toggleChat();
			}
		});

		FlounderEvents.get().addEvent(new IEvent() {
			private KeyButton toggleMap = new KeyButton(GLFW_KEY_G);

			@Override
			public boolean eventTriggered() {
				return toggleMap.wasDown();
			}

			@Override
			public void onEvent() {
				toggleMap();
			}
		});
	}

	@Override
	public void update() {
		if (overlayStartup.getAlpha() == 0.0f && overlayStartup.isStarting()) {
			// Enable other GUI things.
			this.overlayAlpha.setAlphaDriver(new SlideDriver(overlayAlpha.getAlpha(), 1.0f, SLIDE_TIME));
			this.overlaySlider.setAlphaDriver(new SlideDriver(overlaySlider.getAlpha(), 1.0f, SLIDE_TIME));

			overlayStartup.setAlphaDriver(new ConstantDriver(0.0f));
			overlayStartup.setStarting(false);
		}

		if (!isGamePaused() && overlayMap.getAlpha() == 0.0f && FlounderMouse.get().isDisplaySelected() && FlounderDisplay.get().isFocused()) {
			FlounderMouse.get().setCursorHidden(KosmosCamera.isMouseLocked());
		} else {
			FlounderMouse.get().setCursorHidden(false);
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public boolean isGamePaused() {
		return overlayStartup.isStarting() || overlaySlider.getAlpha() != 0.0f || overlayChat.getAlpha() != 0.0f;
	}

	@Override
	public float getBlurFactor() {
		if (!overlaySlider.inStartMenu()) {
			return overlaySlider.getBlurFactor();
		} else {
			return 0.0f;
		}
	}

	@Override
	public Colour getPrimaryColour() {
		return COLOUR_PRIMARY;
	}

	public void togglePause(boolean force) {
		if (overlaySlider.inStartMenu()) {
			return;
		}

		if (overlayMap.getAlpha() != 0.0f) {
			toggleMap();
			return;
		}

		if (force) {
			overlayHUD.setAlphaDriver(new ConstantDriver(1.0f));
			overlayDebug.setAlphaDriver(new ConstantDriver(0.0f));
			overlayChat.setAlphaDriver(new ConstantDriver(0.0f));
			overlaySlider.setAlphaDriver(new ConstantDriver(0.0f));
		} else {
			if (overlayChat.getAlpha() == 1.0f) {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
			} else if (isGamePaused()) {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
				overlaySlider.setAlphaDriver(new SlideDriver(overlaySlider.getAlpha(), 0.0f, SLIDE_TIME));
			} else {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
				overlayMap.setAlphaDriver(new SlideDriver(overlayMap.getAlpha(), 0.0f, SLIDE_TIME));
				overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 0.0f, SLIDE_TIME));
				overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
				overlaySlider.setAlphaDriver(new SlideDriver(overlaySlider.getAlpha(), 1.0f, SLIDE_TIME));
			}
		}
	}

	public void forceCloseHUDs() {
		this.overlayHUD.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayInventory.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayMap.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayDebug.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayChat.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlaySlider.setAlphaDriver(new ConstantDriver(1.0f));
	}

	public void toggleDebug() {
		if (!isGamePaused()) {
			if (overlayDebug.getAlpha() < 0.5f) {
				overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 1.0f, SLIDE_TIME));
			} else {
				overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 0.0f, SLIDE_TIME));
			}
		}
	}

	public void toggleHUD() {
		if (!isGamePaused()) {
			if (overlayHUD.getAlpha() < 0.5f) {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
			} else {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
			}
		}
	}

	public void toggleMap() {
		if (overlayChat.getAlpha() < 0.5f && !overlayStartup.isStarting() && overlaySlider.getAlpha() == 0.0f) {
			if (overlayMap.getAlpha() != 1.0f) {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
				overlayMap.setAlphaDriver(new SlideDriver(overlayMap.getAlpha(), 1.0f, SLIDE_TIME));
			} else {
				overlayMap.setAlphaDriver(new SlideDriver(overlayMap.getAlpha(), 0.0f, SLIDE_TIME));
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
			}
		}
	}

	public void toggleChat() {
		if (overlayChat.getAlpha() < 0.5f && !isGamePaused()) {
			overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 1.0f, SLIDE_TIME));
			overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
		}
	}

	public OverlayHUD getOverlayHUD() {
		return overlayHUD;
	}

	public OverlayInventory getOverlayInventory() {
		return overlayInventory;
	}

	public OverlayMap getOverlayMap() {
		return overlayMap;
	}

	public OverlayDebug getOverlayDebug() {
		return overlayDebug;
	}

	public OverlayChat getOverlayChat() {
		return overlayChat;
	}

	public OverlaySlider getOverlaySlider() {
		return overlaySlider;
	}


	public OverlayStartup getOverlayStartup() {
		return overlayStartup;
	}

	public OverlayAlpha getOverlayAlpha() {
		return overlayAlpha;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
