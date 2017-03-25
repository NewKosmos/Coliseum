package kosmos.uis;

import flounder.camera.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.visual.*;
import kosmos.world.*;

public class OverlayUsernames extends ScreenObject {
	private Vector3f screenPosition;

	private TextObject playerUsername;

	public OverlayUsernames(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.screenPosition = new Vector3f();

		this.playerUsername = new TextObject(this, new Vector2f(0.5f, 0.5f), FlounderNetwork.getUsername(), 1.0f, FlounderFonts.CANDARA, 0.4f, GuiAlign.CENTRE);
		this.playerUsername.setInScreenCoords(false);
		this.playerUsername.setColour(new Colour(1.0f, 1.0f, 1.0f));
		this.playerUsername.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
		this.playerUsername.setBorder(new ConstantDriver(0.175f));
		this.playerUsername.setAlphaDriver(new ConstantDriver(0.75f));
	}

	@Override
	public void updateObject() {
		updateUsername(playerUsername, KosmosWorld.getEntityPlayer());
	}

	private void updateUsername(TextObject object, Entity entity) {
		Maths.worldToScreenSpace(entity.getPosition(), FlounderCamera.getCamera().getViewMatrix(), FlounderCamera.getCamera().getProjectionMatrix(), this.screenPosition);
		// FlounderLogger.log(screenPosition.x + ", " + screenPosition.y);
		object.getPosition().set(screenPosition.x + (FlounderDisplay.getAspectRatio() / 2.0f), -screenPosition.y);
	}

	@Override
	public void deleteObject() {
	}
}
