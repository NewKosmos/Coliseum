package kosmos.uis.screens;

import flounder.guis.*;
import flounder.maths.vectors.*;
import kosmos.uis.*;

public class ScreenSettings extends ScreenObject {
	public ScreenSettings(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		float yPosition = 0.30f;
		float ySpacing = 0.07f;

		ScreenObject paneLeft = new ScreenObject(this, new Vector2f(0.25f, 0.5f), new Vector2f(0.5f, 1.0f)) {
			@Override public void updateObject() {}
			@Override public void deleteObject() {}
		};
		paneLeft.setInScreenCoords(true);

		ScreenObject paneRight = new ScreenObject(this, new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 1.0f)) {
			@Override public void updateObject() {}
			@Override public void deleteObject() {}
		};
		paneRight.setInScreenCoords(true);

		GuiButtonText testing1 = new GuiButtonText(paneLeft, new Vector2f(0.25f, 0.2f), "Testing Left", GuiAlign.CENTRE);
		GuiButtonText testing2 = new GuiButtonText(paneRight, new Vector2f(0.75f, 0.2f), "Testing Right", GuiAlign.CENTRE);

		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.closeSecondaryScreen();
			}
		});
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}
}
