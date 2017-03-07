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
import flounder.physics.bounding.*;
import flounder.visual.*;
import kosmos.uis.*;
import kosmos.uis.console.*;

import static org.lwjgl.glfw.GLFW.*;

public class KosmosGuis extends GuiMaster {
	protected static final float SLIDE_TIME = 0.7f;

	private MasterOverlay masterOverlay;

	private ConsoleUi consoleUi;

	private ValueDriver slideDriver;
	private float backgroundAlpha;

	public KosmosGuis() {
		super(FlounderKeyboard.class, FlounderGuis.class, FlounderFonts.class, FlounderBounding.class);
	}

	@Override
	public void init() {
		this.masterOverlay = new MasterOverlay();
		masterOverlay.show(false);
		FlounderGuis.addComponent(masterOverlay, 0.0f, 0.0f, 1.0f, 1.0f);

		this.consoleUi = new ConsoleUi();
		consoleUi.show(false);
		FlounderGuis.addComponent(consoleUi, 0.0f, 0.0f, 1.0f, 1.0f);

		this.slideDriver = new ConstantDriver(0.0f);
		this.backgroundAlpha = 0.0f;

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_ESCAPE);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				consoleUi.show(!consoleUi.isShown());
				masterOverlay.show(false);

				if (consoleUi.isShown()) {
					slideDriver = new SlideDriver(backgroundAlpha, 1.0f, SLIDE_TIME);
				} else {
					slideDriver = new SlideDriver(backgroundAlpha, 0.0f, SLIDE_TIME);
				}
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_F1);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				if (!isGamePaused()) {
					masterOverlay.show(!masterOverlay.isShown());
				}
			}
		});
	}

	@Override
	public void update() {
		backgroundAlpha = slideDriver.update(Framework.getDelta());
	}

	@Override
	public void profile() {

	}

	public ConsoleUi getConsoleUi() {
		return consoleUi;
	}

	@Override
	public boolean isGamePaused() {
		return consoleUi.isShown();
	}

	@Override
	public void openMenu() {

	}

	@Override
	public float getBlurFactor() {
		return backgroundAlpha;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
