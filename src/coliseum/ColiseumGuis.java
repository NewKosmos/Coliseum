package coliseum;

import coliseum.uis.*;
import coliseum.uis.console.*;
import flounder.devices.*;
import flounder.events.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.physics.bounding.*;
import org.lwjgl.glfw.*;

public class ColiseumGuis extends IGuiMaster {
	private MasterOverlay masterOverlay;

	private ConsoleUi consoleUi;

	public ColiseumGuis() {
		super(FlounderKeyboard.class, FlounderGuis.class, FlounderFonts.class, FlounderBounding.class);
	}

	@Override
	public void init() {
		this.masterOverlay = new MasterOverlay();
		FlounderGuis.addComponent(masterOverlay, 0.0f, 0.0f, 1.0f, 1.0f);

		//	this.consoleUi = new ConsoleUi();
		//	FlounderGuis.addComponent(consoleUi, 0.0f, 0.0f, 1.0f, 1.0f);

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW.GLFW_KEY_ESCAPE);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				masterOverlay.show(!masterOverlay.isShown());
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
	public boolean isGamePaused() {
		return false;
	}

	@Override
	public void openMenu() {

	}

	@Override
	public float getBlurFactor() {
		return 0;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
