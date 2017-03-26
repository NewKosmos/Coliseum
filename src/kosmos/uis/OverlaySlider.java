package kosmos.uis;

import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.uis.screens.*;
import org.lwjgl.glfw.*;

public class OverlaySlider extends ScreenObject {
	public static KeyButton BACK_KEY = new KeyButton(GLFW.GLFW_KEY_BACKSPACE);

	private ScreenPause screenPause;
	private ScreenStart screenStart;

	private ScreenObject menuActive;
	private ScreenObject secondaryScreen;
	private ScreenObject newSecondaryScreen;

	public OverlaySlider(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.screenPause = new ScreenPause(this);
		this.screenStart = new ScreenStart(this);

		sliderStartMenu(true); // Framework.isRunningFromJar()

		this.secondaryScreen = null;
		this.newSecondaryScreen = null;
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

	public void sliderStartMenu(boolean useStartMenu) {
		if (useStartMenu) {
			this.menuActive = screenStart;
			this.screenPause.setAlphaDriver(new ConstantDriver(0.0f));
		} else {
			this.menuActive = screenPause;
			this.screenStart.setAlphaDriver(new ConstantDriver(0.0f));
		}

		this.menuActive.setAlphaDriver(new ConstantDriver(1.0f));
	}

	public boolean inStartMenu() {
		return menuActive == screenStart;
	}

	public float getBlurFactor() {
		return getAlpha();
	}

	@Override
	public void deleteObject() {
	}
}
