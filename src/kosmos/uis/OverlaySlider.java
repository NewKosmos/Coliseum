package kosmos.uis;

import flounder.framework.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.vectors.*;
import kosmos.uis.screens.*;
import org.lwjgl.glfw.*;

public class OverlaySlider extends ScreenObject {
	public static KeyButton BACK_KEY = new KeyButton(GLFW.GLFW_KEY_BACKSPACE);
	public static final int SLIDE_SCALAR = 5;

	private ScreenPause screenPause;

	private ScreenObject menuActive;
	private ScreenObject secondaryScreen;

	public OverlaySlider(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.screenPause = new ScreenPause(this);

		if (Framework.isRunningFromJar()) {
		//	this.menuActive = screenStart;
		} else {
			this.menuActive = screenPause;
		}

		this.secondaryScreen = null;
	}

	@Override
	public void updateObject() {
	}

	public void setNewSecondaryScreen(ScreenObject secondScreen, boolean slideForwards) {
		this.secondaryScreen = secondScreen;
		this.secondaryScreen.setVisible(true);
		this.menuActive.setVisible(false);
	}

	public void closeSecondaryScreen() {
		if (secondaryScreen != null) {
			this.secondaryScreen.setVisible(false);
			this.menuActive.setVisible(true);
		}
	}

	public float getBlurFactor() {
		return getAlpha();
	}

	@Override
	public void deleteObject() {
	}
}
