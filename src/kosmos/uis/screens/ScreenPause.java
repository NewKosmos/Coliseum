package kosmos.uis.screens;

import flounder.framework.*;
import flounder.guis.*;
import flounder.maths.vectors.*;
import kosmos.uis.*;

public class ScreenPause extends ScreenObject {
	private ScreenSettings screenSettings;
	private ScreenAbout screenAbout;

	public ScreenPause(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.screenSettings = new ScreenSettings(slider);
		this.screenAbout = new ScreenAbout(slider);

		this.screenSettings.setVisible(false);
		this.screenAbout.setVisible(false);

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

		GuiButtonText settings = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Settings", GuiAlign.CENTRE);
		settings.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(screenSettings, true);
			}
		});

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
