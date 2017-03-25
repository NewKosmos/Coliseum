package kosmos.uis.screens;

import flounder.guis.*;
import flounder.maths.vectors.*;

public class ScreenSettings extends ScreenObject {
	public ScreenSettings(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		float yPosition = 0.30f;
		float ySpacing = 0.07f;

		GuiButtonText back = new GuiButtonText(this, new Vector2f(0.5f, yPosition += ySpacing), "Back", GuiAlign.CENTRE);
		back.addLeftListener(new GuiButtonText.ListenerBasic() {
			@Override
			public void eventOccurred() {
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
