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
import flounder.fbos.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.post.filters.*;
import flounder.post.piplines.*;
import flounder.renderer.*;
import kosmos.camera.*;
import kosmos.entities.*;
import kosmos.particles.*;
import kosmos.post.*;
import kosmos.post.filters.*;
import kosmos.shadows.*;
import kosmos.skybox.*;
import kosmos.water.*;
import kosmos.world.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class KosmosRenderer extends RendererMaster {
	private static final Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);

	private ShadowRenderer shadowRenderer;
	private SkyboxRenderer skyboxRenderer;
	private EntitiesRenderer entitiesRenderer;
	private ParticleRenderer particleRenderer;
	private WaterRenderer waterRenderer;
	private BoundingRenderer boundingRenderer;
	private GuisRenderer guisRenderer;
	private FontRenderer fontRenderer;

	private FBO rendererFBO;

	private PipelineMRT pipelineMRT;
	private PipelineBloom pipelineBloom;
	private FilterBlurMotion filterBlurMotion;
	private FilterTiltShift filterTiltShift;
	private FilterLensFlare filterLensFlare;
	private FilterPixel filterPixel;
	private FilterCRT filterCRT;
	private PipelinePaused pipelinePaused;

	public KosmosRenderer() {
		super(FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.shadowRenderer = new ShadowRenderer();
		this.skyboxRenderer = new SkyboxRenderer();
		this.entitiesRenderer = new EntitiesRenderer();
		this.particleRenderer = new ParticleRenderer();
		this.waterRenderer = new WaterRenderer();
		this.boundingRenderer = new BoundingRenderer();
		this.guisRenderer = new GuisRenderer();
		this.fontRenderer = new FontRenderer();

		this.rendererFBO = FBO.newFBO(1.0f).attachments(3).withAlphaChannel(true).depthBuffer(DepthBufferType.TEXTURE).create();

		this.pipelineMRT = new PipelineMRT();
		this.pipelineBloom = new PipelineBloom();
		this.filterBlurMotion = new FilterBlurMotion();
		this.filterTiltShift = new FilterTiltShift(0.8f, 1.1f, 0.002f, 5.0f);
		this.filterLensFlare = new FilterLensFlare();
		this.filterPixel = new FilterPixel(2.0f);
		this.filterCRT = new FilterCRT(new Colour(0.5f, 1.0f, 0.5f), 0.175f, 0.175f, 1024.0f, 0.09f);
		this.pipelinePaused = new PipelinePaused();
	}

	@Override
	public void render() {
		// Water rendering.
		renderWater();

		// Shadow rendering.
		renderShadows();

		// Binds the render FBO.
		rendererFBO.bindFrameBuffer();

		// Scene rendering.
		renderScene(POSITIVE_INFINITY, false);

		// Unbinds the render FBO.
		rendererFBO.unbindFrameBuffer();

		// Post rendering.
		renderPost(FlounderGuis.getGuiMaster().isGamePaused(), FlounderGuis.getGuiMaster().getBlurFactor());
	}

	private void renderWater() {
		// Sets the player model to render.
		entitiesRenderer.setRenderPlayer(true);

		// Water Reflection & Refraction
		if (KosmosWater.reflectionsEnabled() && KosmosWater.getColourIntensity() != 1.0f && KosmosWater.getWater() != null) {
			FlounderCamera.getCamera().reflect(KosmosWater.getWater().getPosition().y);

			if (KosmosWater.reflectionShadows()) {
				renderShadows();
			}

			glEnable(GL_CLIP_DISTANCE0);
			{
				waterRenderer.getReflectionFBO().bindFrameBuffer();
				renderScene(new Vector4f(0.0f, 1.0f, 0.0f, -KosmosWater.getWater().getPosition().y), true);
				waterRenderer.getReflectionFBO().unbindFrameBuffer();
			}
			glDisable(GL_CLIP_DISTANCE0);

			waterRenderer.getPipelineMRT().applyFilter(
					waterRenderer.getReflectionFBO().getColourTexture(0), // Colours
					waterRenderer.getReflectionFBO().getColourTexture(1), // Normals
					waterRenderer.getReflectionFBO().getColourTexture(2), // Extras
					waterRenderer.getReflectionFBO().getDepthTexture(), // Depth
					((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowMap() // Shadow Map
			);

			if (KosmosPost.isBloomEnabled()) {
				pipelineBloom.setBloomThreshold(KosmosSkybox.getBloomThreshold());
				pipelineBloom.renderMRT(rendererFBO, waterRenderer.getPipelineMRT().fbo);
			}

			FlounderCamera.getCamera().reflect(KosmosWater.getWater().getPosition().y);
		}
	}

	private void renderShadows() {
		// Sets the player model to render.
		entitiesRenderer.setRenderPlayer(true);

		// Renders the shadows.
		shadowRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());
	}

	private void renderScene(Vector4f clipPlane, boolean waterPass) {
		// Sets the player model to render in first person view.
		entitiesRenderer.setRenderPlayer(!KosmosCamera.isFirstPerson());

		// Clears and renders.
		Camera camera = FlounderCamera.getCamera();
		OpenGlUtils.prepareNewRenderParse(0.0f, 0.0f, 0.0f);

		skyboxRenderer.render(clipPlane, camera);
		entitiesRenderer.render(clipPlane, camera);

		if (!waterPass) {
			waterRenderer.render(clipPlane, camera);
			boundingRenderer.render(clipPlane, camera);
		}

		particleRenderer.render(clipPlane, camera);
	}

	private void renderPost(boolean isPaused, float blurFactor) {
		pipelineMRT.renderPipeline(rendererFBO);
		FBO output = pipelineMRT.getOutput();

		// Render post effects if enabled.
		if (KosmosPost.isEffectsEnabled()) {
			// Render Bloom Filter.
			if (KosmosPost.isBloomEnabled()) {
				pipelineBloom.setBloomThreshold(KosmosSkybox.getBloomThreshold());
				pipelineBloom.renderMRT(rendererFBO, output);
				output = pipelineBloom.getOutput();
			}

			// Render Motion Blur Filter.
			if (KosmosPost.isMotionBlurEnabled()) {
				filterBlurMotion.applyFilter(output.getColourTexture(0), rendererFBO.getDepthTexture());
				output = filterBlurMotion.fbo;
			}

			// Render Tilt Shift Filter.
			if (KosmosPost.isTiltShiftEnabled()) {
				filterTiltShift.applyFilter(output.getColourTexture(0));
				output = filterTiltShift.fbo;
			}

			// Render Lens Flare Filter.
			if (KosmosPost.isLensFlareEnabled() && KosmosWorld.getEntitySun() != null) {
				filterLensFlare.setSunPosition(KosmosWorld.getEntitySun().getPosition());
				filterLensFlare.setWorldHeight(KosmosSkybox.getSunHeight());
				filterLensFlare.applyFilter(output.getColourTexture(0));
				output = filterLensFlare.fbo;
			}

			// Render Pause Pipeline.
			if (isPaused || blurFactor != 0.0f) {
				pipelinePaused.setBlurFactor(blurFactor);
				pipelinePaused.renderPipeline(output);
				output = pipelinePaused.getOutput();
			}

			// Scene independents.
			renderIndependents(output);

			// Render CRT Filter.
			if (KosmosPost.isCrtEnabled()) {
				filterPixel.applyFilter(output.getColourTexture(0));
				output = filterPixel.fbo;
				filterCRT.applyFilter(output.getColourTexture(0));
				output = filterCRT.fbo;
			}
		} else {
			// Scene independents.
			renderIndependents(output);
		}

		output.blitToScreen();
	}

	private void renderIndependents(FBO output) {
		output.bindFrameBuffer();
		guisRenderer.render(null, null);
		fontRenderer.render(null, null);
		output.unbindFrameBuffer();
	}

	@Override
	public void profile() {
	}

	public ShadowRenderer getShadowRenderer() {
		return shadowRenderer;
	}

	@Override
	public void dispose() {
		shadowRenderer.dispose();
		skyboxRenderer.dispose();
		entitiesRenderer.dispose();
		particleRenderer.dispose();
		waterRenderer.dispose();
		boundingRenderer.dispose();
		guisRenderer.dispose();
		fontRenderer.dispose();

		rendererFBO.delete();

		pipelineMRT.dispose();
		pipelineBloom.dispose();
		filterBlurMotion.dispose();
		filterTiltShift.dispose();
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
