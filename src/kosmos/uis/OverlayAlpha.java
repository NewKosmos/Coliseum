package kosmos.uis;

import flounder.fonts.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

public class OverlayAlpha extends ScreenObject {
	private GuiObject cornerAlpha;
	private TextObject cornerVersion;

	public OverlayAlpha(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.cornerAlpha = new GuiObject(this, new Vector2f(0.972f, 0.044f), new Vector2f(0.30f, 0.06f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "cornerAlpha.png")).create(), 1);
		this.cornerAlpha.setInScreenCoords(true);
		this.cornerAlpha.setRotationDriver(new ConstantDriver(45.0f));

		this.cornerVersion = new TextObject(this, new Vector2f(0.972f, 0.044f), "New Kosmos \n Alpha", 0.61f, FlounderFonts.CANDARA, 0.2f, GuiAlign.CENTRE);
		this.cornerVersion.setInScreenCoords(true);
		this.cornerVersion.setColour(new Colour(1.0f, 1.0f, 1.0f));
		this.cornerVersion.setRotationDriver(new ConstantDriver(45.0f));
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}
}
