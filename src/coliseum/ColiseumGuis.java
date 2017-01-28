package coliseum;

import coliseum.uis.*;
import coliseum.uis.console.*;
import flounder.devices.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.physics.bounding.*;

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
