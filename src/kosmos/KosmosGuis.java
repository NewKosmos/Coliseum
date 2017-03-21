package kosmos;

import flounder.fonts.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

public class KosmosGuis extends GuiMaster {
	private GuiObject cornerAlpha;
	private GuiObject crossHair;
	private TextObject helloWorld;

	public KosmosGuis() {
		super();
	}

	@Override
	public void init() {
		this.cornerAlpha = new GuiObject(FlounderGuis.getContainer(), new Vector2f(0.06f, 0.06f), new Vector2f(0.12f, 0.12f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "cornerAlpha.png")).create(), 1);
		this.cornerAlpha.setInScreenCoords(true);
		//this.cornerAlpha.setRotationDriver(new LinearDriver(0.0f, 360.0f, 5.0f));
		//this.cornerAlpha.setScaleDriver(new SinWaveDriver(0.0f, 2.0f, 2.5f));

		this.crossHair = new GuiObject(FlounderGuis.getContainer(), new Vector2f(0.5f, 0.5f), new Vector2f(0.04f, 0.04f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "crosshair.png")).setNumberOfRows(4).create(), 1);
		this.crossHair.setInScreenCoords(false);
		this.crossHair.setColourOffset(new Colour(0.1f, 0.8f, 0.2f));
		this.crossHair.setRotationDriver(new LinearDriver(0.0f, 360.0f, 5.0f));
		this.crossHair.setScaleDriver(new SinWaveDriver(0.0f, 3.0f, 2.5f));

		String s = "Hello World!"; // "I'm Harambe, and this is my zoo enclosure. I work here with my zoo keeper and my friend, cecil the lion. Everything in here has a story and a price. One thing I've learned after 21 years - you never know WHO is gonna come over that fence.";
		this.helloWorld = new TextObject(FlounderGuis.getContainer(), new Vector2f(0.5f, 0.5f), s, 2.0f, FlounderFonts.BERLIN_SANS, 0.7f, false);
		this.helloWorld.setInScreenCoords(false);
		this.helloWorld.setColour(new Colour(1.0f, 0.6f, 0.1f));
		this.helloWorld.setBorderColour(new Colour(1.0f, 0.6f, 0.1f));
		//this.helloWorld.setBorder(new SinWaveDriver(0.1f, 0.3f, 3.0f));
		this.helloWorld.setGlowing(new SinWaveDriver(0.1f, 0.5f, 4.0f));
		this.helloWorld.setRotationDriver(new LinearDriver(0.0f, 360.0f, 5.0f));
		//this.helloWorld.setScaleDriver(new SinWaveDriver(0.0f, 2.0f, 8.5f));
	}

	@Override
	public void update() {
		if (FlounderGuis.getSelector().isSelected(helloWorld)) {
			helloWorld.setColour(new Colour(0.0f, 0.6f, 1.0f));
		} else {
			helloWorld.setColour(new Colour(1.0f, 0.6f, 0.1f));
		}
	}

	@Override
	public void profile() {

	}

	@Override
	public boolean isGamePaused() {
		return false;
	}

	@Override
	public float getBlurFactor() {
		return 0;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
