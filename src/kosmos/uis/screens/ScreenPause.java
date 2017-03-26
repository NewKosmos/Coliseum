package kosmos.uis.screens;

import flounder.framework.*;
import flounder.guis.*;
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.uis.*;

public class ScreenPause extends ScreenObject {
	public ScreenPause(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		float yPosition = 0.30f;
		float ySpacing = 0.07f;

		GuiButtonText loadSave = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Load Save", GuiAlign.CENTRE);
		loadSave.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
			}
		});

		GuiButtonText multiplayer = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Multiplayer", GuiAlign.CENTRE);
		multiplayer.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
			}
		});

		ScreenSettings screenSettings = new ScreenSettings(slider);
		screenSettings.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText settings = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Settings", GuiAlign.CENTRE);
		settings.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenSettings, true);
			}
		});

		ScreenAbout screenAbout = new ScreenAbout(slider);
		screenAbout.setAlphaDriver(new ConstantDriver(0.0f));
		GuiButtonText about = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "About", GuiAlign.CENTRE);
		about.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenAbout, true);
			}
		});

		GuiButtonText quitGame = new GuiButtonText(this, new Vector2f(0.5f, yPosition += 1.2f * ySpacing), "Quit Game", GuiAlign.CENTRE);
		quitGame.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				Framework.requestClose();
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
