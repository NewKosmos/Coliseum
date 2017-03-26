package kosmos.uis.screens.settings;

import flounder.devices.*;
import flounder.guis.*;
import flounder.maths.vectors.*;
import kosmos.*;
import kosmos.uis.*;
import kosmos.uis.screens.*;

public class ScreenSettingAudio extends ScreenObject {
	public ScreenSettingAudio(OverlaySlider slider, ScreenSettings settings) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		GuiButtonText toggleMusic = new GuiButtonText(this, new Vector2f(0.5f, 0.20f), "Music Enabled: " + KosmosConfigs.MUSIC_ENABLED.getBoolean(), GuiAlign.CENTRE);
		toggleMusic.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				if (!FlounderSound.getMusicPlayer().isPaused()) {
					FlounderSound.getMusicPlayer().pauseTrack();
				} else {
					FlounderSound.getMusicPlayer().unpauseTrack();
				}

				toggleMusic.setText("Music Enabled: " + !FlounderSound.getMusicPlayer().isPaused());
			}
		});

		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, 0.9f), "Back", GuiAlign.CENTRE);
		back.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.setNewSecondaryScreen(settings, false);
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
