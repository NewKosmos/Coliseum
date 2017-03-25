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

	public KosmosGuis() {
		super();
	}

	@Override
	public void init() {
		this.overlayHUD = new OverlayHUD(FlounderGuis.getContainer());
		this.overlayDebug = new OverlayDebug(FlounderGuis.getContainer());
		this.overlayChat = new OverlayChat(FlounderGuis.getContainer());

		this.overlayHUD.setAlphaDriver(new ConstantDriver(1.0f));
		this.overlayDebug.setAlphaDriver(new ConstantDriver(0.0f));
		this.overlayChat.setAlphaDriver(new ConstantDriver(0.0f));

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
				if (overlayChat.isVisible()) {
				//	overlayDebug.setVisible(false);
				//	overlayHUD.setVisible(true);
				//	overlayChat.setVisible(false);
				} else {
					// TODO: Toggle pause!
				}

				if (overlayChat.getAlpha() == 1.0f) {
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
		return overlayChat.getAlpha() == 1.0f;
	}

	@Override
	public float getBlurFactor() {
		return 0.0f; // overlayChat.getAlpha();
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
