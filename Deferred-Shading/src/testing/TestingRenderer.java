package testing;

import flounder.camera.*;
import flounder.devices.*;
import flounder.fbos.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.renderer.*;
import testing.entities.*;
import testing.filters.*;

public class TestingRenderer extends RendererMaster {
	private static final Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);

	private EntitiesRenderer entitiesRenderer;
	private BoundingRenderer boundingRenderer;

	private GuisRenderer guisRenderer;
	private FontRenderer fontRenderer;

	private PipelineMRT pipelineMRT;

	private FBO mrtFBO;

	public TestingRenderer() {
		super(FlounderProfiler.class, FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.entitiesRenderer = new EntitiesRenderer();
		this.boundingRenderer = new BoundingRenderer();

		this.guisRenderer = new GuisRenderer();
		this.fontRenderer = new FontRenderer();

		this.pipelineMRT = new PipelineMRT();

		this.mrtFBO = FBO.newFBO(1.0f).attachments(4).depthBuffer(DepthBufferType.TEXTURE).create();
	}

	@Override
	public void render() {
		/* Binds the relevant FBO. */
		bindRelevantFBO();

		/* Scene rendering. */
		renderScene(POSITIVE_INFINITY);

		/* Unbinds the FBO. */
		unbindRelevantFBO();
	}

	@Override
	public void profile() {

	}

	private void bindRelevantFBO() {
		mrtFBO.bindFrameBuffer();
	}

	private void unbindRelevantFBO() {
		mrtFBO.unbindFrameBuffer();

		pipelineMRT.renderPipeline(mrtFBO);
		pipelineMRT.getOutput().blitToScreen();

		guisRenderer.render(null, null);
		fontRenderer.render(null, null);
	}

	private void renderScene(Vector4f clipPlane) {
		/* Clear and update. */
		Camera camera = FlounderCamera.getCamera();
		OpenGlUtils.prepareNewRenderParse(0.0f, 0.0f, 0.0f);

		/* Renders each renderer. */
		entitiesRenderer.render(clipPlane, camera);
		boundingRenderer.render(clipPlane, camera);
	}

	@Override
	public void dispose() {
		entitiesRenderer.dispose();
		boundingRenderer.dispose();

		guisRenderer.dispose();
		fontRenderer.dispose();

		pipelineMRT.dispose();

		mrtFBO.delete();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
