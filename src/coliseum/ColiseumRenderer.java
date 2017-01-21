package coliseum;

import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.physics.bounding.*;
import flounder.renderer.*;

public class ColiseumRenderer extends IRendererMaster {
	private BoundingRenderer boundingRenderer;
	private GuiRenderer guiRenderer;
	private FontRenderer fontRenderer;

	public ColiseumRenderer() {
		super();
	}

	@Override
	public void init() {
		boundingRenderer = new BoundingRenderer();
		guiRenderer = new GuiRenderer();
		fontRenderer = new FontRenderer();
	}

	@Override
	public void render() {
		OpenGlUtils.prepareNewRenderParse(0.1f, 0.1f, 0.1f);
		guiRenderer.render(null, null);
		fontRenderer.render(null, null);
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		boundingRenderer.dispose();
		guiRenderer.dispose();
		fontRenderer.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
