package kosmos.uis;

import flounder.camera.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.visual.*;
import kosmos.world.*;

import java.util.Timer;
import java.util.*;

public class OverlayDebug extends ScreenObject {
	private TextObject fpsText;
	private TextObject upsText;
	private TextObject positionText;
	private TextObject timeText;
	private TextObject seedText;
	private boolean updateText;

	public OverlayDebug(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		fpsText = createStatus("FPS: 0", 0.01f);
		upsText = createStatus("UPS: 0", 0.04f);
		positionText = createStatus("POSITION: [0, 0, 0]", 0.07f);
		timeText = createStatus("TIME: 0", 0.10f);
		seedText = createStatus("SEED: 0", 0.13f);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateText = true;
			}
		}, 0, 100);
	}

	private TextObject createStatus(String content, float yPos) {
		TextObject text = new TextObject(this, new Vector2f(0.01f, 0.01f + yPos), content, 0.75f, FlounderFonts.CANDARA, 0.5f, GuiAlign.LEFT);
		text.setInScreenCoords(false);
		text.setColour(new Colour(1.0f, 1.0f, 1.0f));
		text.setBorderColour(new Colour(0.15f, 0.15f, 0.15f));
		text.setBorder(new ConstantDriver(0.04f));
		return text;
	}

	@Override
	public void updateObject() {
		if (updateText) {
			fpsText.setText("FPS: " + Maths.roundToPlace(1.0f / Framework.getDeltaRender(), 1));
			upsText.setText("UPS: " + Maths.roundToPlace(1.0f / Framework.getDelta(), 1));
			positionText.setText("POSITION: [" + (FlounderCamera.getPlayer() == null ? "NULL" : Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().x, 1) + ", " + Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().y, 1) + ", " + Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().z, 1) + "]"));
			timeText.setText("TIME: " + Maths.roundToPlace(KosmosWorld.getDayFactor(), 3));
			seedText.setText("SEED: " + KosmosWorld.getNoise().getSeed());
			updateText = false;
		}
	}

	@Override
	public void deleteObject() {
	}
}