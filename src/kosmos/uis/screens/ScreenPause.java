package kosmos.uis.screens;

import flounder.guis.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.network.packets.*;
import kosmos.uis.*;
import kosmos.world.*;

public class ScreenPause extends ScreenObject {
	public ScreenPause(OverlaySlider slider) {
		super(slider, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		float yPosition = 0.30f;
		float ySpacing = 0.07f;

		GuiButtonText saveGame = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Save Game", GuiAlign.CENTRE);
		saveGame.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				KosmosConfigs.saveAllConfigs();
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

		GuiButtonText exitToMenu = new GuiButtonText(this, new Vector2f(0.5f, yPosition += 1.2f * ySpacing), "Exit To Menu", GuiAlign.CENTRE);
		exitToMenu.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
				slider.sliderStartMenu(true);
				if (FlounderNetwork.getSocketClient() != null) {
					new PacketDisconnect(FlounderNetwork.getUsername()).writeData(FlounderNetwork.getSocketClient());
					FlounderNetwork.closeClient();
				}
				KosmosWorld.removeAllPlayers();
				KosmosWorld.deletePlayer();
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
