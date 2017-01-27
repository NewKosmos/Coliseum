package coliseum;

import coliseum.entities.*;
import coliseum.world.*;
import flounder.camera.*;
import flounder.devices.*;
import flounder.fbos.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.post.filters.*;
import flounder.profiling.*;
import flounder.renderer.*;

public class ColiseumRenderer extends IRendererMaster {
	private static final Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);

	private EntitiesRenderer entitiesRenderer;
	private BoundingRenderer boundingRenderer;
	private GuiRenderer guiRenderer;
	private FontRenderer fontRenderer;

	private FBO rendererFBO;

	private FilterFXAA filterFXAA;
	private FilterTiltShift filterTiltShift;

	public ColiseumRenderer() {
		super(FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.entitiesRenderer = new EntitiesRenderer();
		this.boundingRenderer = new BoundingRenderer();
		this.guiRenderer = new GuiRenderer();
		this.fontRenderer = new FontRenderer();

		this.rendererFBO = FBO.newFBO(1.0f).depthBuffer(DepthBufferType.TEXTURE).create();

		this.filterFXAA = new FilterFXAA();
		this.filterTiltShift = new FilterTiltShift(0.75f, 1.1f, 0.004f, 3.0f);
	}

	@Override
	public void render() {
		/* Binds the relevant FBO. */
		bindRelevantFBO();

		/* Scene rendering. */
		renderScene(POSITIVE_INFINITY, ColiseumWorld.getSkyColour());

		/* Post rendering. */
		renderPost(FlounderGuis.getGuiMaster().isGamePaused(), FlounderGuis.getGuiMaster().getBlurFactor());

		/* Scene independents. */
		guiRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());
		fontRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());

		/* Unbinds the FBO. */
		unbindRelevantFBO();
	}

	private void bindRelevantFBO() {
		rendererFBO.bindFrameBuffer();
	}

	private void unbindRelevantFBO() {
		rendererFBO.unbindFrameBuffer();
	}

	private void renderScene(Vector4f clipPlane, Colour clearColour) {
		/* Clear and update. */
		ICamera camera = FlounderCamera.getCamera();
		OpenGlUtils.prepareNewRenderParse(clearColour);

		entitiesRenderer.render(clipPlane, camera);
		boundingRenderer.render(clipPlane, camera);
	}

	private void renderPost(boolean isPaused, float blurFactor) {
		FBO output = rendererFBO;

		if (FlounderDisplay.isAntialiasing()) {
			filterFXAA.applyFilter(output.getColourTexture(0));
			output = filterFXAA.fbo;
		}

		filterTiltShift.applyFilter(output.getColourTexture(0));
		output = filterTiltShift.fbo;

		output.blitToScreen();
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

		rendererFBO.delete();

		filterFXAA.dispose();
		filterTiltShift.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
