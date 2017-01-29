package coliseum;

import coliseum.entities.*;
import coliseum.shadows.*;
import coliseum.skybox.*;
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
	private static final Colour CLEAR_COLOUR = new Colour(0.0f, 0.0f, 0.0f);

	private SkyboxRenderer skyboxRenderer;
	private ShadowRenderer shadowRenderer;
	private EntitiesRenderer entitiesRenderer;
	private BoundingRenderer boundingRenderer;
	private GuiRenderer guiRenderer;
	private FontRenderer fontRenderer;

	private FBO rendererFBO;

	private FilterFXAA filterFXAA;
	private FilterCRT filterCRT;
	private FilterTiltShift filterTiltShift;

	public ColiseumRenderer() {
		super(FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.skyboxRenderer = new SkyboxRenderer();
		this.shadowRenderer = new ShadowRenderer();
		this.entitiesRenderer = new EntitiesRenderer();
		this.boundingRenderer = new BoundingRenderer();
		this.guiRenderer = new GuiRenderer();
		this.fontRenderer = new FontRenderer();

		this.rendererFBO = FBO.newFBO(1.0f).depthBuffer(DepthBufferType.TEXTURE).create();

		this.filterFXAA = new FilterFXAA();
		this.filterCRT = new FilterCRT(0.175f, 0.175f, 920.0f);
		this.filterTiltShift = new FilterTiltShift(0.75f, 1.1f, 0.004f, 3.0f);
	}

	@Override
	public void render() {
		/* Shadow rendering. */
		shadowRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());

		/* Binds the relevant FBO. */
		bindRelevantFBO();

		/* Scene rendering. */
		renderScene(POSITIVE_INFINITY, CLEAR_COLOUR);

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

	public ShadowRenderer getShadowRenderer() {
		return shadowRenderer;
	}

	private void renderScene(Vector4f clipPlane, Colour clearColour) {
		/* Clear and update. */
		ICamera camera = FlounderCamera.getCamera();
		OpenGlUtils.prepareNewRenderParse(clearColour);

		skyboxRenderer.render(clipPlane, camera);
		entitiesRenderer.render(clipPlane, camera);
		boundingRenderer.render(clipPlane, camera);
	}

	private void renderPost(boolean isPaused, float blurFactor) {
		FBO output = rendererFBO;

		filterTiltShift.applyFilter(output.getColourTexture(0));
		output = filterTiltShift.fbo;

		//	filterCRT.applyFilter(output.getColourTexture(0));
		//	output = filterCRT.fbo;

		if (FlounderDisplay.isAntialiasing()) {
			filterFXAA.applyFilter(output.getColourTexture(0));
			output = filterFXAA.fbo;
		}

		output.blitToScreen();
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		skyboxRenderer.dispose();
		shadowRenderer.dispose();
		entitiesRenderer.dispose();
		boundingRenderer.dispose();
		guiRenderer.dispose();
		fontRenderer.dispose();

		rendererFBO.delete();

		filterFXAA.dispose();
		filterCRT.dispose();
		filterTiltShift.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
