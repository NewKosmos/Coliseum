package kosmos;

import flounder.fonts.*;
import flounder.guis.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;

public class KosmosGuis extends GuiMaster {
	private GuiObject cornerAlpha;

	public KosmosGuis() {
		super();
	}

	@Override
	public void init() {
		this.cornerAlpha = new GuiObject(FlounderGuis.getContainer(), new Vector2f(0.06f, 0.06f), new Vector2f(0.12f, 0.12f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "cornerAlpha.png")).create(), 1);
		this.cornerAlpha.setInScreenCoords(true);

		FontType cf = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "crazyFont.png"),new MyFile(MyFile.RES_FOLDER, "fonts", "crazyFont.fnt"));
		FontType hf = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "harrington.png"),new MyFile(MyFile.RES_FOLDER, "fonts", "harrington.fnt"));
		FontType h2f = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "harry.png"),new MyFile(MyFile.RES_FOLDER, "fonts", "harry.fnt"));
		FontType vf = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "verdana.png"),new MyFile(MyFile.RES_FOLDER, "fonts", "verdana.fnt"));

		TextObject to = new TextObject("Hello World", 2.0f, vf, new Vector2f(0.5f, 0.5f), 1.0f, false);
		to.setColour(1.0f, 0.6f, 0.1f);
	}

	@Override
	public void update() {

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
