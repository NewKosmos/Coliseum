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

import static org.lwjgl.glfw.GLFW.*;

public class KosmosGuis extends GuiMaster {
	// private static final Colour COLOUR_PRIMARY = new Colour(0.90196078431f, 0.08235294117f, 0.08235294117f); // Charger Red.
	// private static final Colour COLOUR_PRIMARY = new Colour(0.1f, 0.8f, 0.2f); // Neon Green.
	private static final Colour COLOUR_PRIMARY = new Colour(0.0824f, 0.396f, 0.753f); // Water Blue.

	public static final float SLIDE_TIME = 0.5f;

	private OverlayAlpha overlayAlpha;
	private OverlayHUD overlayHUD;
	private OverlayUsernames overlayUsernames;
	private OverlayDebug overlayDebug;
	private OverlayChat overlayChat;
	private OverlaySlider overlaySlider;

	public KosmosGuis() {
		super();
	}

	@Override
	public void init() {
		this.overlayAlpha = new OverlayAlpha(FlounderGuis.getContainer());
		this.overlayHUD = new OverlayHUD(FlounderGuis.getContainer());
		this.overlayUsernames = new OverlayUsernames(FlounderGuis.getContainer());
		this.overlayDebug = new OverlayDebug(FlounderGuis.getContainer());
		this.overlayChat = new OverlayChat(FlounderGuis.getContainer());
		this.overlaySlider = new OverlaySlider(FlounderGuis.getContainer());

		this.overlayAlpha.setAlphaDriver(new ConstantDriver(1.0f));
		this.overlayHUD.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayUsernames.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayDebug.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayChat.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlaySlider.setAlphaDriver(new ConstantDriver(1.0f));

		FlounderGuis.getSelector().initJoysticks(0, 0, 1, 0, 1);

		FlounderEvents.addEvent(new IEvent() {
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

		FlounderEvents.addEvent(new IEvent() {
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

		FlounderEvents.addEvent(new IEvent() {
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

		FlounderEvents.addEvent(new IEvent() {
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
	}

	@Override
	public void update() {
		if (!isGamePaused() && FlounderMouse.isDisplaySelected() && FlounderDisplay.isFocused()) {
			FlounderMouse.setCursorHidden(KosmosCamera.isMouseLocked());
		} else {
			FlounderMouse.setCursorHidden(false);
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public boolean isGamePaused() {
		return overlaySlider.getAlpha() > 0.1f || overlayChat.getAlpha() >= 0.1f;
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

		if (force) {
			overlayHUD.setAlphaDriver(new ConstantDriver(1.0f));
			overlayUsernames.setAlphaDriver(new ConstantDriver(1.0f));
			overlayDebug.setAlphaDriver(new ConstantDriver(0.0f));
			overlayChat.setAlphaDriver(new ConstantDriver(0.0f));
			overlaySlider.setAlphaDriver(new ConstantDriver(0.0f));
		} else {
			if (overlayChat.getAlpha() == 1.0f) {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
			} else if (isGamePaused()) {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
				overlaySlider.setAlphaDriver(new SlideDriver(overlaySlider.getAlpha(), 0.0f, SLIDE_TIME));
			} else {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
				overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
				overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 0.0f, SLIDE_TIME));
				overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
				overlaySlider.setAlphaDriver(new SlideDriver(overlaySlider.getAlpha(), 1.0f, SLIDE_TIME));
			}
		}
	}

	public void toggleDebug() {
		if (overlayChat.getAlpha() != 1.0f && !isGamePaused()) {
			if (overlayDebug.getAlpha() < 0.5f) {
				overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 1.0f, SLIDE_TIME));
			} else {
				overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 0.0f, SLIDE_TIME));
			}
		}
	}

	public void toggleHUD() {
		if (overlayChat.getAlpha() != 1.0f && !isGamePaused()) {
			if (overlayHUD.getAlpha() < 0.5f) {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
			} else {
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
				overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
			}
		}
	}

	public void toggleChat() {
		if (overlayChat.getAlpha() < 0.5f && !isGamePaused()) {
			overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 1.0f, SLIDE_TIME));
			overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
			overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
		}
	}

	public OverlayAlpha getOverlayAlpha() {
		return overlayAlpha;
	}

	public OverlayHUD getOverlayHUD() {
		return overlayHUD;
	}

	public OverlayUsernames getOverlayUsernames() {
		return overlayUsernames;
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

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
