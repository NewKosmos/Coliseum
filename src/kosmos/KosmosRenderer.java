/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos;

import flounder.camera.*;
import flounder.devices.*;
import flounder.events.*;
import flounder.fbos.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.post.filters.*;
import flounder.post.piplines.*;
import flounder.profiling.*;
import flounder.renderer.*;
import kosmos.chunks.*;
import kosmos.entities.*;
import kosmos.filters.*;
import kosmos.particles.*;
import kosmos.shadows.*;
import kosmos.water.*;
import kosmos.world.*;
import org.lwjgl.glfw.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class KosmosRenderer extends RendererMaster {
	private static final Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);

	private ShadowRenderer shadowRenderer;
	private EntitiesRenderer entitiesRenderer;
	private ParticleRenderer particleRenderer;
	private WaterRenderer waterRenderer;
	private BoundingRenderer boundingRenderer;
	private GuisRenderer guisRenderer;
	private FontRenderer fontRenderer;

	private float displayScale;
	private FBO rendererFBO;

	private PipelineMRT pipelineMRT;
	private PipelineBloom pipelineBloom;
	private FilterBlurMotion filterBlurMotion;
	private FilterLensFlare filterLensFlare;
	private FilterPixel filterPixel;
	private FilterCRT filterCRT;
	private PipelinePaused pipelinePaused;
	private int effect;

	public KosmosRenderer() {
		super(FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.shadowRenderer = new ShadowRenderer();
		this.entitiesRenderer = new EntitiesRenderer();
		this.particleRenderer = new ParticleRenderer();
		this.waterRenderer = new WaterRenderer();
		this.boundingRenderer = new BoundingRenderer();
		this.guisRenderer = new GuisRenderer();
		this.fontRenderer = new FontRenderer();

		this.displayScale = KosmosConfigs.configMain.getFloatWithDefault("render_scale", 1.0f, this::getDisplayScale);
		this.rendererFBO = FBO.newFBO(displayScale).attachments(3).withAlphaChannel(true).disableTextureWrap().depthBuffer(DepthBufferType.TEXTURE).create();

		this.pipelineMRT = new PipelineMRT();
		this.pipelineBloom = new PipelineBloom();
		this.filterBlurMotion = new FilterBlurMotion();
		this.filterLensFlare = new FilterLensFlare();
		this.filterPixel = new FilterPixel(4.0f);
		this.filterCRT = new FilterCRT(new Colour(0.5f, 1.0f, 0.5f), 0.175f, 0.175f, 1024.0f, 0.05f);
		this.pipelinePaused = new PipelinePaused();
		this.effect = 1;

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton c = new KeyButton(GLFW.GLFW_KEY_C);

			@Override
			public boolean eventTriggered() {
				return c.wasDown();
			}

			@Override
			public void onEvent() {
				if (FlounderGuis.getGuiMaster() != null && !FlounderGuis.getGuiMaster().isGamePaused()) {
					effect++;

					if (effect > 2) {
						effect = 0;
					}
				}
			}
		});
	}

	@Override
	public void render() {
		/* Water Reflection & Refraction */
		if (KosmosWater.reflectionsEnabled()) {
			FlounderCamera.getCamera().reflect(KosmosWater.getWater().getPosition().y);

			glEnable(GL_CLIP_DISTANCE0);
			{
				waterRenderer.getReflectionFBO().bindFrameBuffer();
				renderScene(new Vector4f(0.0f, 1.0f, 0.0f, -KosmosWater.getWater().getPosition().y), true);
				waterRenderer.getReflectionFBO().unbindFrameBuffer();
			}
			glDisable(GL_CLIP_DISTANCE0);

			FlounderCamera.getCamera().reflect(KosmosWater.getWater().getPosition().y);
		}

		/* Shadow rendering. */
		shadowRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());

		/* Binds the relevant FBO. */
		bindRelevantFBO();

		/* Scene rendering. */
		renderScene(POSITIVE_INFINITY, false);

		/* Post rendering. */
		renderPost(FlounderGuis.getGuiMaster().isGamePaused(), FlounderGuis.getGuiMaster().getBlurFactor());

		/* Scene independents. */
		renderIndependents();

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

	private void renderScene(Vector4f clipPlane, boolean waterPass) {
		/* Clear and recalculateRay. */
		Camera camera = FlounderCamera.getCamera();
		OpenGlUtils.prepareNewRenderParse(0.0f, 0.0f, 0.0f);

		entitiesRenderer.render(clipPlane, camera);

		if (!waterPass) {
			waterRenderer.render(clipPlane, camera);
			boundingRenderer.render(clipPlane, camera);
		}

		particleRenderer.render(clipPlane, camera);
	}

	private void renderIndependents() {
		guisRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());
		fontRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());
	}

	private void renderPost(boolean isPaused, float blurFactor) {
		pipelineMRT.renderPipeline(rendererFBO);
		FBO output = pipelineMRT.getOutput();

		switch (effect) {
			case 0:
				break;
			case 1:
				pipelineBloom.setBloomThreshold(0.6f);
				pipelineBloom.renderMRT(rendererFBO, output);
				output = pipelineBloom.getOutput();

				filterBlurMotion.applyFilter(output.getColourTexture(0), rendererFBO.getDepthTexture());
				output = filterBlurMotion.fbo;

				if (KosmosWorld.getEntitySun() != null) {
					filterLensFlare.setSunPosition(KosmosWorld.getEntitySun().getPosition());
					filterLensFlare.applyFilter(output.getColourTexture(0));
					output = filterLensFlare.fbo;;
				}
				break;
			case 2:
				filterPixel.applyFilter(output.getColourTexture(0));
				output = filterPixel.fbo;
				filterCRT.applyFilter(output.getColourTexture(0));
				output = filterCRT.fbo;
				break;
		}

		if (isPaused || blurFactor != 0.0f) {
			pipelinePaused.setBlurFactor(blurFactor);
			pipelinePaused.renderPipeline(output);
			output = pipelinePaused.getOutput();
		}

		output.blitToScreen();
	}

	@Override
	public void profile() {
	}

	public float getDisplayScale() {
		return displayScale;
	}

	public void setDisplayScale(float displayScale) {
		this.displayScale = displayScale;
		rendererFBO.setSizeScalar(displayScale);
	}

	@Override
	public void dispose() {
		shadowRenderer.dispose();
		particleRenderer.dispose();
		entitiesRenderer.dispose();
		waterRenderer.dispose();
		boundingRenderer.dispose();
		guisRenderer.dispose();
		fontRenderer.dispose();

		rendererFBO.delete();

		pipelineMRT.dispose();
		pipelineBloom.dispose();
		filterBlurMotion.dispose();
		filterLensFlare.dispose();
		filterPixel.dispose();
		filterCRT.dispose();
		pipelinePaused.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
