package kosmos.uis;

import flounder.guis.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.world.*;

public class OverlayHUD extends ScreenObject {
	private GuiObject cornerAlpha;
	private GuiObject crossHair;

	private TextureObject hudTexture;
	private TextureObject hudProgress;
	private HudStatus statusHealth;
	private HudStatus statusThirst;
	private HudStatus statusHunger;

	public OverlayHUD(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.hudTexture = TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "guis", "hudSprites.png")).setNumberOfRows(3).create();
		this.hudProgress = TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "guis", "circularProgress.png")).setNumberOfRows(4).create();
		this.statusHealth = new HudStatus(this, hudTexture, hudProgress, 2, 0.0f, new Colour(1.0f, 0.2f, 0.2f));
		this.statusThirst = new HudStatus(this, hudTexture, hudProgress, 3, 0.1f, new Colour(0.2f, 0.2f, 1.0f));
		this.statusHunger = new HudStatus(this, hudTexture, hudProgress, 4, 0.2f, new Colour(1.0f, 0.4f, 0.0f));

		this.cornerAlpha = new GuiObject(FlounderGuis.getContainer(), new Vector2f(0.06f, 0.06f), new Vector2f(0.12f, 0.12f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "cornerAlpha.png")).create(), 1);
		this.cornerAlpha.setInScreenCoords(true);

		this.crossHair = new GuiObject(FlounderGuis.getContainer(), new Vector2f(0.5f, 0.5f), new Vector2f(0.04f, 0.04f), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "crosshair.png")).setNumberOfRows(4).create(), 1);
		this.crossHair.setInScreenCoords(false);
		this.crossHair.setColourOffset(new Colour(0.1f, 0.8f, 0.2f));
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {

	}

	private static class HudStatus extends ScreenObject {
		private GuiObject background;
		private GuiObject foreground;
		private GuiObject progress;
		private GuiObject mainIcon;

		private HudStatus(ScreenObject parent, TextureObject hudTexture, TextureObject hudProgress, int main, float offset, Colour colour) {
			super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));

			this.background = new GuiObject(this, new Vector2f(0.06f + offset, 0.94f), new Vector2f(0.1f, 0.1f), hudTexture, 0);

			this.foreground = new GuiObject(this, new Vector2f(0.06f + offset, 0.94f), new Vector2f(0.08f, 0.08f), hudTexture, 1);

			this.progress = new GuiObject(this, new Vector2f(0.06f + offset, 0.94f), new Vector2f(0.08f, 0.08f), hudProgress, 1);
			this.progress.setColourOffset(colour);

			this.mainIcon = new GuiObject(this, new Vector2f(0.06f + offset, 0.94f), new Vector2f(0.06f, 0.06f), hudTexture, main);
		}

		@Override
		public void updateObject() {
			float p = KosmosWorld.getDayFactor();
			progress.setSelectedRow((int) Math.floor(p * Math.pow(progress.getTexture().getNumberOfRows(), 2)));
		}

		@Override
		public void deleteObject() {
		}
	}
}
