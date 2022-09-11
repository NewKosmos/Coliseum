/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos;

import com.flounder.camera.*;
import com.flounder.devices.*;
import com.flounder.entities.*;
import com.flounder.fbos.*;
import com.flounder.fonts.*;
import com.flounder.guis.*;
import com.flounder.helpers.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.particles.*;
import com.flounder.physics.bounding.*;
import com.flounder.post.filters.*;
import com.flounder.post.piplines.*;
import com.flounder.renderer.*;
import com.flounder.shadows.*;
import com.flounder.skybox.*;
import com.kosmos.post.*;
import com.kosmos.world.*;
import com.kosmos.world.water.*;

import static com.flounder.platform.Constants.*;

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
	private FilterGain filterGain;

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

		this.rendererFBO = FBO.newFBO(1.0f).attachments(3).withAlphaChannel(false).depthBuffer(DepthBufferType.TEXTURE).create();

		this.pipelineMRT = new PipelineMRT();
		this.pipelineBloom = new PipelineBloom();
		this.filterBlurMotion = new FilterBlurMotion();
		this.filterTiltShift = new FilterTiltShift(0.5f, 1.1f, 0.002f, 5.0f);
		this.filterLensFlare = new FilterLensFlare();
		this.filterPixel = new FilterPixel(2.0f);
		this.filterCRT = new FilterCRT(new Colour(0.5f, 1.0f, 0.5f), 0.175f, 0.175f, 1024.0f, 0.09f);
		this.pipelinePaused = new PipelinePaused();
		this.filterGain = new FilterGain(2.3f);
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
		if (FlounderGuis.get().getGuiMaster() != null) {
			renderPost(FlounderGuis.get().getGuiMaster().isGamePaused(), FlounderGuis.get().getGuiMaster().getBlurFactor());
		}
	}

	private void renderWater() {
		// Water Reflection & Refraction
		if (KosmosWater.get().reflectionsEnabled() && KosmosWater.get().getColourIntensity() != 1.0f && KosmosWater.get().getWater() != null) {
			FlounderCamera.get().getCamera().reflect(KosmosWater.get().getWater().getPosition().y);

			if (KosmosWater.get().reflectionShadows()) {
				renderShadows();
			}

			FlounderOpenGL.get().enable(GL_CLIP_DISTANCE0);
			{
				waterRenderer.getReflectionFBO().bindFrameBuffer();
				renderScene(new Vector4f(0.0f, 1.0f, 0.0f, -KosmosWater.get().getWater().getPosition().y), true);
				waterRenderer.getReflectionFBO().unbindFrameBuffer();
			}
			FlounderOpenGL.get().disable(GL_CLIP_DISTANCE0);

			waterRenderer.getPipelineMRT().applyFilter(
					waterRenderer.getReflectionFBO().getColourTexture(0), // Colours
					waterRenderer.getReflectionFBO().getColourTexture(1), // Normals
					waterRenderer.getReflectionFBO().getColourTexture(2), // Extras
					waterRenderer.getReflectionFBO().getDepthTexture(), // Depth
					shadowRenderer.getShadowMap() // Shadow Map
			);

			//	if (KosmosPost.isBloomEnabled()) {
			//		pipelineBloom.setBloomThreshold(KosmosWorld.getBloomThreshold());
			//		pipelineBloom.renderPipeline(waterRenderer.getPipelineMRT().fbo.getColourTexture(0));
			//	}

			FlounderCamera.get().getCamera().reflect(KosmosWater.get().getWater().getPosition().y);
		}
	}

	private void renderShadows() {
		// Renders the shadows.
		shadowRenderer.render(POSITIVE_INFINITY, FlounderCamera.get().getCamera());
	}

	private void renderScene(Vector4f clipPlane, boolean waterPass) {
		// Clears and renders.
		Camera camera = FlounderCamera.get().getCamera();
		FlounderOpenGL.get().prepareNewRenderParse(0.0f, 0.0f, 0.0f);

		skyboxRenderer.render(clipPlane, camera);
		entitiesRenderer.render(clipPlane, camera);

		if (!waterPass) {
			waterRenderer.render(clipPlane, camera);
			//	boundingRenderer.render(clipPlane, camera);
		}

		particleRenderer.render(clipPlane, camera);
	}

	private void renderPost(boolean isPaused, float blurFactor) {
		FBO output = rendererFBO;
		pipelineMRT.renderPipeline(
				output.getColourTexture(0), // Colours
				output.getColourTexture(1), // Normals
				output.getColourTexture(2), // Extras
				output.getDepthTexture(), // Depth
				shadowRenderer.getShadowMap() // Shadow Map
		);
		output = pipelineMRT.getOutput();

		// Render post effects if enabled.
		if (KosmosPost.get().isEffectsEnabled()) {
			// Render Bloom Filter.
			if (KosmosPost.get().isBloomEnabled()) {
				pipelineBloom.renderPipeline(output.getColourTexture(0));
				output = pipelineBloom.getOutput();
			}

			// Render Motion Blur Filter.
			if (KosmosPost.get().isMotionBlurEnabled()) {
				filterBlurMotion.applyFilter(output.getColourTexture(0), rendererFBO.getDepthTexture());
				output = filterBlurMotion.fbo;
			}

			// Render Tilt Shift Filter.
			if (KosmosPost.get().isTiltShiftEnabled()) {
				filterTiltShift.applyFilter(output.getColourTexture(0));
				output = filterTiltShift.fbo;
			}

			// Render Lens Flare Filter.
			if (KosmosPost.get().isLensFlareEnabled() && KosmosWorld.get().getEntitySun() != null) {
				filterLensFlare.setSunPosition(KosmosWorld.get().getEntitySun().getPosition());
				filterLensFlare.setWorldHeight(KosmosWorld.get().getSunHeight());
				filterLensFlare.applyFilter(output.getColourTexture(0));
				output = filterLensFlare.fbo;
			}

			// Render Pause Pipeline.
			if (isPaused || blurFactor != 0.0f) {
				pipelinePaused.setBlurFactor(blurFactor);
				pipelinePaused.renderPipeline(output.getColourTexture(0));
				output = pipelinePaused.getOutput();
			}

			// Scene independents.
			renderIndependents(output);

			// Render CRT Filter.
			if (KosmosPost.get().isCrtEnabled()) {
				filterPixel.applyFilter(output.getColourTexture(0));
				output = filterPixel.fbo;
				filterCRT.applyFilter(output.getColourTexture(0));
				output = filterCRT.fbo;
			}
		} else {
			// Scene independents.
			renderIndependents(output);
		}

		// Applies grain to the final image.
		if (KosmosPost.get().isGrainEnabled()) {
			filterGain.applyFilter(output.getColourTexture(0));
			output = filterGain.fbo;
		}

		// Displays the image to the screen.
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
		filterGain.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
