package kosmos;

import flounder.devices.*;
import flounder.events.*;
import flounder.guis.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.visual.*;
import kosmos.uis.*;

import static org.lwjgl.glfw.GLFW.*;

public class KosmosGuis extends GuiMaster {
	// private static final Colour COLOUR_PRIMARY = new Colour(0.90196078431f, 0.08235294117f, 0.08235294117f); // Charger Red.
	// private static final Colour COLOUR_PRIMARY = new Colour(0.1f, 0.8f, 0.2f); // Neon Green.
	private static final Colour COLOUR_PRIMARY = new Colour(0.0824f, 0.396f, 0.753f); // Water Blue.

	public static final float SLIDE_TIME = 0.7f;

	private OverlayAlpha overlayAlpha;
	private OverlayHUD overlayHUD;
	private OverlayUsernames overlayUsernames;
	private OverlayDebug overlayDebug;
	private OverlayChat overlayChat;
	private OverlayPause overlayPause;

	public KosmosGuis() {
		super();
	}

	@Override
	public void init() {
		this.overlayAlpha = new OverlayAlpha(FlounderGuis.getContainer());
		this.overlayHUD = new OverlayHUD(FlounderGuis.getContainer());
		this.overlayUsernames = new OverlayUsernames(FlounderGuis.getContainer());
		this.overlayDebug = new OverlayDebug(FlounderGuis.getContainer());
		this.overlayChat = new OverlayChat(FlounderGuis.getContainer());
		this.overlayPause = new OverlayPause(FlounderGuis.getContainer());

		this.overlayAlpha.setAlphaDriver(new ConstantDriver(1.0f));
		this.overlayHUD.setAlphaDriver(new ConstantDriver(1.0f));
		this.overlayUsernames.setAlphaDriver(new ConstantDriver(1.0f));
		this.overlayDebug.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayChat.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayPause.setAlphaDriver(new ConstantDriver(0.0f));

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW_KEY_ENTER);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				if (overlayChat.getAlpha() < 0.5f && !isGamePaused()) {
					overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 1.0f, SLIDE_TIME));
					overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
					overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
				}
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
				if (overlayChat.getAlpha() == 1.0f) {
					overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
					overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
					overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
				} else if (isGamePaused()) {
					overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
					overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 1.0f, SLIDE_TIME));
					overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
					overlayPause.setAlphaDriver(new SlideDriver(overlayPause.getAlpha(), 0.0f, SLIDE_TIME));
				} else {
					overlayHUD.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
					overlayUsernames.setAlphaDriver(new SlideDriver(overlayHUD.getAlpha(), 0.0f, SLIDE_TIME));
					overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 0.0f, SLIDE_TIME));
					overlayChat.setAlphaDriver(new SlideDriver(overlayChat.getAlpha(), 0.0f, SLIDE_TIME));
					overlayPause.setAlphaDriver(new SlideDriver(overlayPause.getAlpha(), 1.0f, SLIDE_TIME));
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
				if (overlayChat.getAlpha() != 1.0f && !isGamePaused()) {
					if (overlayDebug.getAlpha() < 0.5f) {
						overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 1.0f, SLIDE_TIME));
					} else {
						overlayDebug.setAlphaDriver(new SlideDriver(overlayDebug.getAlpha(), 0.0f, SLIDE_TIME));
					}
				}
			}
		});
	}

	@Override
	public void update() {
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
		return overlayPause.getAlpha() > 0.1f || overlayChat.getAlpha() >= 0.1f;
	}

	@Override
	public float getBlurFactor() {
		return overlayPause.getAlpha();
	}

	@Override
	public Colour getPrimaryColour() {
		return COLOUR_PRIMARY;
	}

	public OverlayAlpha getOverlayAlpha() {
		return overlayAlpha;
	}

	public OverlayHUD getOverlayHUD() {
		return overlayHUD;
	}

	public OverlayUsernames getOverlayUsernames() {
		return overlayUsernames;
	}

	public OverlayDebug getOverlayDebug() {
		return overlayDebug;
	}

	public OverlayChat getOverlayChat() {
		return overlayChat;
	}

	public OverlayPause getOverlayPause() {
		return overlayPause;
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
