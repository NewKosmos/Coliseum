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
import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.physics.bounding.*;
import flounder.visual.*;
import kosmos.uis.*;

import static org.lwjgl.glfw.GLFW.*;

public class KosmosGuis extends GuiMaster {
	protected static final float SLIDE_TIME = 0.7f;

	private OverlayHUD overlayHUD;
	private OverlayDebug overlayDebug;
	private OverlayChat overlayChat;

	private ValueDriver slideDriver;
	private float backgroundAlpha;

	public KosmosGuis() {
		super(FlounderKeyboard.class, FlounderGuis.class, FlounderFonts.class, FlounderBounding.class);
	}

	@Override
	public void init() {
		this.overlayHUD = new OverlayHUD();
		FlounderGuis.addComponent(overlayHUD, 0.0f, 0.0f, 1.0f, 1.0f);

		this.overlayDebug = new OverlayDebug();
		FlounderGuis.addComponent(overlayDebug, 0.0f, 0.0f, 1.0f, 1.0f);

		this.overlayChat = new OverlayChat();
		FlounderGuis.addComponent(overlayChat, 0.0f, 0.0f, 1.0f, 1.0f);
		overlayChat.addText("Type in plain text to create a message, hit enter to send, escape for discarding or editing.", new Colour(0.81f, 0.37f, 0.24f));
		overlayChat.addText("To find command type '/h', to enter commands enter '/command params'.", new Colour(0.81f, 0.37f, 0.24f));

		this.slideDriver = new ConstantDriver(0.0f);
		this.backgroundAlpha = 0.0f;

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_ENTER);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				overlayDebug.show(false);
				overlayHUD.show(false);
				overlayChat.show(true);
				slideDriver = new SlideDriver(backgroundAlpha, 1.0f, SLIDE_TIME);
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_ESCAPE);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				if (overlayChat.isShown()) {
					overlayDebug.show(false);
					overlayHUD.show(true);
					overlayChat.show(false);
				} else {
					// TODO: Toggle pause!
				}

				if (isGamePaused()) {
					slideDriver = new SlideDriver(backgroundAlpha, 1.0f, SLIDE_TIME);
				} else {
					slideDriver = new SlideDriver(backgroundAlpha, 0.0f, SLIDE_TIME);
				}
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_F3);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				if (!isGamePaused()) {
					overlayDebug.show(!overlayDebug.isShown());
				}
			}
		});
	}

	@Override
	public void update() {
		backgroundAlpha = slideDriver.update(Framework.getDelta());

		if (!isGamePaused()) {
			FlounderMouse.setCursorHidden(KosmosConfigs.CAMERA_MOUSE_LOCKED.getBoolean());
		} else {
			FlounderMouse.setCursorHidden(false);
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public boolean isGamePaused() {
		return overlayChat.isShown();
	}

	@Override
	public void openMenu() {
	}

	@Override
	public float getBlurFactor() {
		return backgroundAlpha;
	}

	public OverlayHUD getOverlayHUD() {
		return overlayHUD;
	}

	public OverlayDebug getOverlayDebug() {
		return overlayDebug;
	}

	public OverlayChat getOverlayChat() {
		return overlayChat;
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
