package coliseum.uis;

import coliseum.*;
import flounder.camera.*;
import flounder.devices.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

import java.util.*;
import java.util.Timer;

public class MasterOverlay extends GuiComponent {
	private Text fpsText;
	private Text upsText;
	private Text positionText;
	private boolean updateText;

	private GuiTexture crossHair;

	public MasterOverlay() {
		fpsText = createStatus("FPS: 0", 0.02f);
		upsText = createStatus("UPS: 0", 0.06f);
		positionText = createStatus("POSITION: [0, 0, 0]", 0.11f);
		createStatus("C TO TOGGLE EFFECTS", 0.16f);
		createStatus("ESC TO HIDE HUD", 0.20f);

		crossHair = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "guis", "crosshair.png")).create());
		crossHair.getTexture().setNumberOfRows(4);
		crossHair.setSelectedRow(Coliseum.configMain.getIntWithDefault("crosshair", 1, () -> crossHair.getSelectedRow()));

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateText = true;
			}
		}, 0, 100);

		super.show(true);
	}

	private Text createStatus(String content, float yPos) {
		Text text = Text.newText(content).setFontSize(0.75f).textAlign(GuiAlign.LEFT).create();
		text.setColour(1.0f, 1.0f, 1.0f);
		text.setBorderColour(0.15f, 0.15f, 0.15f);
		text.setBorder(new ConstantDriver(0.04f));
		super.addText(text, 0.01f, 0.01f + yPos, 0.5f);
		return text;
	}

	@Override
	protected void updateSelf() {
		if (updateText) {
			fpsText.setText("FPS: " + Maths.roundToPlace(1.0f / FlounderFramework.getDeltaRender(), 1));
			upsText.setText("UPS: " + Maths.roundToPlace(1.0f / FlounderFramework.getDelta(), 1));
			positionText.setText("POSITION: [" + (FlounderCamera.getPlayer() == null ? "NULL" : Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().x, 1) + ", " + Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().y, 1) + ", " + Maths.roundToPlace(FlounderCamera.getPlayer().getPosition().z, 1) + "]"));
			updateText = false;
		}

		float size = (66.6f / (FlounderDisplay.getWidth() + FlounderDisplay.getHeight()));
		crossHair.setPosition((FlounderDisplay.getAspectRatio() / 2.0f) + super.getPosition().x, 0.5f, size, size);
		crossHair.update();
		crossHair.setColourOffset(GuiTextButton.HOVER_COLOUR);
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		//if (isShown()) {
		//	guiTextures.add(crossHair);
		//}
	}
}
