package kosmos;

import flounder.devices.*;
import flounder.events.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.uis.*;

import static org.lwjgl.glfw.GLFW.*;

public class KosmosGuis extends GuiMaster {
	protected static final float SLIDE_TIME = 0.7f;

	private OverlayHUD overlayHUD;
	private OverlayDebug overlayDebug;
	private OverlayChat overlayChat;

	private TextObject to;
	private GuiObject go;

	public KosmosGuis() {
		super();
	}

	@Override
	public void init() {
		this.overlayHUD = new OverlayHUD(FlounderGuis.getContainer());
		this.overlayDebug = new OverlayDebug(FlounderGuis.getContainer());
		this.overlayChat = new OverlayChat(FlounderGuis.getContainer());

		this.overlayHUD.setVisible(true);
		this.overlayDebug.setVisible(false);
		this.overlayChat.setVisible(false);
		this.overlayChat.setAlphaDriver(new ConstantDriver(0.0f));

		String s = "I'm Harambe, and this is my zoo enclosure. I work here with my zoo keeper and my friend, cecil the lion. Everything in here has a story and a price. One thing I've learned after 21 years - you never know WHO is gonna come over that fence.";
		to = new TextObject(FlounderGuis.getContainer(), new Vector2f(0.5f, 0.5f), s, 1.5f, FlounderFonts.CANDARA, 0.5f, GuiAlign.CENTRE);
		to.setInScreenCoords(true);
		to.setColour(new Colour(1.0f, 1.0f, 1.0f));
		to.setBorderColour(new Colour(1.0f, 0.3f, 0.3f));
		to.setGlowing(new SinWaveDriver(0.35f, 0.5f, 3.0f));
		//to.setRotationDriver(new SinWaveDriver(0.0f, 360.0f, 6.0f));

		go = new GuiObject(FlounderGuis.getContainer(), new Vector2f(0.5f, 0.5f), new Vector2f(), TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "undefined.png")).create(), 1);
		go.setInScreenCoords(true);
		//go.setRotationDriver(new SinWaveDriver(0.0f, 360.0f, 6.0f));

		to.setVisible(false);
		go.setVisible(false);

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_ENTER);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				overlayDebug.setVisible(false);
				overlayHUD.setVisible(false);
				overlayChat.setVisible(true);
				overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 1.0f, SLIDE_TIME));
				overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_ESCAPE);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				if (overlayChat.isVisible()) {
					overlayDebug.setVisible(false);
					overlayHUD.setVisible(true);
					overlayChat.setVisible(false);
				} else {
					// TODO: Toggle pause!
				}

				if (isGamePaused()) {
					overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 1.0f, SLIDE_TIME));
					overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				} else {
					overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
					overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
				}
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_F3);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				if (!isGamePaused()) {
					overlayDebug.setVisible(!overlayDebug.isVisible());
				}
			}
		});
	}

	@Override
	public void update() {
		Vector2f.multiply(to.getDimensions(), to.getMeshSize(), go.getDimensions());
		go.getDimensions().scale(2.0f * to.getScale());
		go.getPositionOffsets().set(to.getPositionOffsets());

		if (FlounderGuis.getSelector().isSelected(to)) {
			to.setColour(new Colour(1.0f, 0.0f, 0.0f));
		} else {
			to.setColour(new Colour(1.0f, 1.0f, 1.0f));
		}

		if (!isGamePaused() && FlounderMouse.isDisplaySelected() && FlounderDisplay.isFocused()) {
			FlounderMouse.setCursorHidden(KosmosConfigs.CAMERA_MOUSE_LOCKED.getBoolean());
		} else {
			FlounderMouse.setCursorHidden(false);
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public boolean isGamePaused() {
		return overlayChat.isVisible();
	}

	@Override
	public float getBlurFactor() {
		return overlayChat.getAlpha();
	}

	public OverlayHUD getOverlayHUD() {
		return overlayHUD;
	}

	public OverlayDebug getOverlayDebug() {
		return overlayDebug;
	}

	public OverlayChat getOverlayChat() {
		return overlayChat;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
