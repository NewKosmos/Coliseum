package kosmos;

import flounder.guis.*;
import flounder.logger.*;
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
