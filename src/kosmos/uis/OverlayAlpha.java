package kosmos.uis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

public class OverlayAlpha extends ScreenObject {
	private static float SIZE = 0.042f;

	private GuiObject cornerAlpha;
	private TextObject cornerVersion;

	public OverlayAlpha(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.cornerAlpha = new GuiObject(this, new Vector2f(FlounderDisplay.getAspectRatio() - SIZE, SIZE), new Vector2f(0.30f, 0.06f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "cornerAlpha.png")).create(), 1);
		this.cornerAlpha.setInScreenCoords(false);
		this.cornerAlpha.setColourOffset(new Colour());
		this.cornerAlpha.setRotationDriver(new ConstantDriver(45.0f));

		this.cornerVersion = new TextObject(this, new Vector2f(FlounderDisplay.getAspectRatio() - SIZE, SIZE), "New Kosmos \n Alpha", 0.61f, FlounderFonts.CANDARA, 0.2f, GuiAlign.CENTRE);
		this.cornerVersion.setInScreenCoords(false);
		this.cornerVersion.setColour(new Colour(1.0f, 1.0f, 1.0f));
		this.cornerVersion.setRotationDriver(new ConstantDriver(45.0f));
	}

	@Override
	public void updateObject() {
		this.cornerAlpha.getPosition().x = FlounderDisplay.getAspectRatio() - SIZE;
		this.cornerVersion.getPosition().x = FlounderDisplay.getAspectRatio() - SIZE;

		this.cornerAlpha.setColourOffset(FlounderGuis.getGuiMaster().getPrimaryColour());
	}

	@Override
	public void deleteObject() {
	}
}
