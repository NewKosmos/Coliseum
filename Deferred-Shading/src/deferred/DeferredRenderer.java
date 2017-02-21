package deferred;

import deferred.entities.*;
import deferred.shadows.*;
import flounder.camera.*;
import flounder.devices.*;
import flounder.fbos.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.renderer.*;

/**
 * Created by matthew on 21/02/17.
 */
public class DeferredRenderer extends RendererMaster {
	private static final Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);

	private ShadowRenderer shadowRenderer;
	private EntitiesRenderer entitiesRenderer;
	private BoundingRenderer boundingRenderer;

	private FBO multisamplingFBO;
	private FBO nonsampledFBO;

	public DeferredRenderer() {
		super(FlounderProfiler.class, FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.shadowRenderer = new ShadowRenderer();
		this.entitiesRenderer = new EntitiesRenderer();
		this.boundingRenderer = new BoundingRenderer();

		this.multisamplingFBO = FBO.newFBO(1.0f).depthBuffer(DepthBufferType.TEXTURE).antialias(FlounderDisplay.getSamples()).create();
		this.nonsampledFBO = FBO.newFBO(1.0f).depthBuffer(DepthBufferType.TEXTURE).create();
	}

	@Override
	public void render() {
		shadowRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());

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
		if (FlounderDisplay.isAntialiasing()) {
			multisamplingFBO.bindFrameBuffer();
		} else {
			nonsampledFBO.bindFrameBuffer();
		}
	}

	private void unbindRelevantFBO() {
		if (FlounderDisplay.isAntialiasing()) {
			multisamplingFBO.unbindFrameBuffer();
			multisamplingFBO.resolveFBO(nonsampledFBO);
		} else {
			nonsampledFBO.unbindFrameBuffer();
		}

		nonsampledFBO.blitToScreen();
	}

	private void renderScene(Vector4f clipPlane) {
		/* Clear and update. */
		Camera camera = FlounderCamera.getCamera();
		OpenGlUtils.prepareNewRenderParse(DeferredShading.SKY_COLOUR_DAY);

		/* Renders each renderer. */
		entitiesRenderer.render(clipPlane, camera);
		boundingRenderer.render(clipPlane, camera);
	}

	public ShadowRenderer getShadowRenderer() {
		return shadowRenderer;
	}

	@Override
	public void dispose() {
		entitiesRenderer.dispose();
		boundingRenderer.dispose();

		multisamplingFBO.delete();
		nonsampledFBO.delete();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
