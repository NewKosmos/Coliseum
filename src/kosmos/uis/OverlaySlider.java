package kosmos.uis;

import flounder.framework.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.uis.screens.*;
import org.lwjgl.glfw.*;

public class OverlaySlider extends ScreenObject {
	public static KeyButton BACK_KEY = new KeyButton(GLFW.GLFW_KEY_BACKSPACE);

	private ScreenObject menuActive;
	private ScreenObject secondaryScreen;
	private ScreenObject newSecondaryScreen;

	public OverlaySlider(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		ScreenPause screenPause = new ScreenPause(this);

		if (Framework.isRunningFromJar()) {
		//	this.menuActive = screenStart;
		} else {
			this.menuActive = screenPause;
		}

		this.secondaryScreen = null;
	}

	@Override
	public void updateObject() {
		if (BACK_KEY.wasDown()) {
			closeSecondaryScreen();
		}

		if (newSecondaryScreen != null && secondaryScreen.getAlpha() == 0.0f) {
			secondaryScreen = newSecondaryScreen;
			newSecondaryScreen = null;
		}

		if (menuActive.getAlpha() == 1.0f && secondaryScreen != null && secondaryScreen.getAlpha() == 0.0f && newSecondaryScreen == null) {
			secondaryScreen = null;
		}
	}

	public void setNewSecondaryScreen(ScreenObject secondScreen, boolean slideForwards) {
		if (secondaryScreen == secondScreen || newSecondaryScreen == secondScreen) {
			return;
		}

		newSecondaryScreen = secondScreen;
		newSecondaryScreen.setAlphaDriver(new SlideDriver(0.0f, 1.0f, KosmosGuis.SLIDE_TIME));

		if (secondaryScreen != null) {
			secondaryScreen.setAlphaDriver(new SlideDriver(1.0f, 0.0f, KosmosGuis.SLIDE_TIME));
		} else {
			secondaryScreen = newSecondaryScreen;
			newSecondaryScreen = null;
		}

		if (menuActive.getAlpha() == 1.0f) {
			menuActive.setAlphaDriver(new SlideDriver(1.0f, 0.0f, KosmosGuis.SLIDE_TIME));
		}
	}

	public void closeSecondaryScreen() {
		if (newSecondaryScreen != null) {
			return;
		}

		if (secondaryScreen != null) {
			secondaryScreen.setAlphaDriver(new SlideDriver(1.0f, 0.0f, KosmosGuis.SLIDE_TIME));
		}

		menuActive.setAlphaDriver(new SlideDriver(0.0f, 1.0f, KosmosGuis.SLIDE_TIME));
	}

	public float getBlurFactor() {
		return getAlpha();
	}

	@Override
	public void deleteObject() {
	}
}
