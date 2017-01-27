package coliseum;

import coliseum.entities.*;
import flounder.camera.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.renderer.*;

public class ColiseumRenderer extends IRendererMaster {
	private static final Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);

	private EntitiesRenderer entitiesRenderer;
	private BoundingRenderer boundingRenderer;
	private GuiRenderer guiRenderer;
	private FontRenderer fontRenderer;

	public ColiseumRenderer() {
		super();
	}

	@Override
	public void init() {
		entitiesRenderer = new EntitiesRenderer();
		boundingRenderer = new BoundingRenderer();
		guiRenderer = new GuiRenderer();
		fontRenderer = new FontRenderer();
	}

	@Override
	public void render() {
		OpenGlUtils.prepareNewRenderParse(0.8f, 0.8f, 0.8f);
		entitiesRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());
		boundingRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());
		guiRenderer.render(null, null);
		fontRenderer.render(null, null);
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		entitiesRenderer.dispose();
		boundingRenderer.dispose();
		guiRenderer.dispose();
		fontRenderer.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
