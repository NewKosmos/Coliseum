/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.water;

import flounder.camera.*;
import flounder.devices.*;
import flounder.fbos.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import kosmos.chunks.*;
import kosmos.filters.*;
import org.lwjgl.opengl.*;

public class WaterRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "water", "waterVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "water", "waterFragment.glsl");

	private FBO reflectionFBO;
	private FilterMRT pipelineMRT;
	private ShaderObject shader;

	public WaterRenderer() {
		this.reflectionFBO = FBO.newFBO(KosmosWater.getReflectionQuality()).attachments(3).withAlphaChannel(true).depthBuffer(DepthBufferType.TEXTURE).create();
		this.pipelineMRT = new FilterMRT(FBO.newFBO(1.0f).disableTextureWrap().create());
		this.shader = ShaderFactory.newBuilder().setName("water").addType(new ShaderType(GL20.GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || KosmosWater.getWater() == null || !KosmosWater.getWater().isLoaded()) {
			return;
		}

		//if (KosmosWater.getWater().getAABB().inFrustum(camera.getViewFrustum())) {
		prepareRendering(clipPlane, camera);
		renderWater(KosmosWater.getWater());
		endRendering();
		//}
	}

	private void prepareRendering(Vector4f clipPlane, Camera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		Chunk chunk = KosmosChunks.getCurrent();

		if (chunk != null) {
			// Vector3f position = chunk.getPosition();
			Vector3f position = FlounderCamera.getPlayer().getPosition();
			shader.getUniformVec3("waterOffset").loadVec3(
					(float) (2.0f * Water.SQUARE_SIZE) * Math.round(position.x / (2.0f * Water.SQUARE_SIZE)),
					0.0f,
					(float) (2.0f * Water.SQUARE_SIZE) * Math.round(position.z / (2.0f * Water.SQUARE_SIZE)));
		}

		/*if (FlounderCamera.getPlayer() != null) {
			Vector3f position = FlounderCamera.getPlayer().getPosition();
			shader.getUniformVec3("waterOffset").loadVec3(Math.round(position.x), 0.0f, Math.round(position.z));
		}*/

		if (KosmosWater.reflectionsEnabled()) {
			// Update the quality scalar.
			if (reflectionFBO.getSizeScalar() != KosmosWater.getReflectionQuality()) {
				reflectionFBO.setSizeScalar(KosmosWater.getReflectionQuality());
			}

			// Binds the reflection FBO.
			if (pipelineMRT != null) {
				OpenGlUtils.bindTexture(pipelineMRT.fbo.getColourTexture(0), GL11.GL_TEXTURE_2D, 0);
			} else {
				OpenGlUtils.bindTexture(reflectionFBO.getColourTexture(0), GL11.GL_TEXTURE_2D, 0);
			}
		}

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.cullBackFaces(true);
	}

	private void renderWater(Water water) {
		OpenGlUtils.bindVAO(water.getVao(), 0);

		shader.getUniformMat4("modelMatrix").loadMat4(water.getModelMatrix());

		shader.getUniformVec4("diffuseColour").loadVec4(water.getColour().r, water.getColour().g, water.getColour().b, KosmosWater.reflectionsEnabled() ? water.getColour().a : 1.0f);

		shader.getUniformFloat("waveTime").loadFloat(KosmosWater.getWaveTime() / Water.WAVE_SPEED);
		shader.getUniformFloat("waveLength").loadFloat(Water.WAVE_LENGTH);
		shader.getUniformFloat("amplitude").loadFloat(Water.AMPLITUDE);
		shader.getUniformFloat("squareSize").loadFloat((float) Water.SQUARE_SIZE);
		shader.getUniformFloat("waterHeight").loadFloat(water.getPosition().y);

		shader.getUniformFloat("shineDamper").loadFloat(Water.SHINE_DAMPER);
		shader.getUniformFloat("reflectivity").loadFloat(Water.REFLECTIVITY);

		shader.getUniformBool("ignoreReflections").loadBoolean(!KosmosWater.reflectionsEnabled());

		OpenGlUtils.renderArrays(GL11.GL_TRIANGLES, water.getVertexCount());

		OpenGlUtils.disableBlending();
		OpenGlUtils.unbindVAO(0);
	}

	private void endRendering() {
		shader.stop();
	}

	public FBO getReflectionFBO() {
		return reflectionFBO;
	}

	public FilterMRT getPipelineMRT() {
		return pipelineMRT;
	}

	@Override
	public void profile() {
		FlounderProfiler.add(KosmosWater.PROFILE_TAB_NAME, "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		reflectionFBO.delete();
		shader.delete();
	}
}
