/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.world.water;

import flounder.camera.*;
import flounder.devices.*;
import flounder.fbos.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.post.filters.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import kosmos.world.chunks.*;

import static flounder.platform.Constants.*;

public class WaterRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "water", "waterVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "water", "waterFragment.glsl");

	private FBO reflectionFBO;
	private FilterMRT pipelineMRT;
	private ShaderObject shader;

	public WaterRenderer() {
		this.reflectionFBO = FBO.newFBO(KosmosWater.get().getReflectionQuality()).attachments(3).withAlphaChannel(true).depthBuffer(DepthBufferType.TEXTURE).create();
		this.pipelineMRT = new FilterMRT(FBO.newFBO(1.0f).disableTextureWrap().create());
		this.shader = ShaderFactory.newBuilder().setName("water").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
	}

	@Override
	public void render(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || KosmosWater.get().getWater() == null || !KosmosWater.get().getWater().isLoaded()) {
			return;
		}

		//if (KosmosWater.getWater().getCollider().inFrustum(camera.getViewFrustum())) {
		prepareRendering(clipPlane, camera);
		renderWater(KosmosWater.get().getWater());
		endRendering();
		//}
	}

	private void prepareRendering(Vector4f clipPlane, Camera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		Chunk chunk = KosmosChunks.get().getCurrent();

		if (chunk != null) {
			// Vector3f position = chunk.getPosition();
			Vector3f position = FlounderCamera.get().getPlayer().getPosition();
			shader.getUniformVec3("waterOffset").loadVec3(
					(float) (2.0f * Water.SQUARE_SIZE) * Math.round(position.x / (2.0f * Water.SQUARE_SIZE)),
					0.0f,
					(float) (2.0f * Water.SQUARE_SIZE) * Math.round(position.z / (2.0f * Water.SQUARE_SIZE)));
		}

		if (KosmosWater.get().reflectionsEnabled() && KosmosWater.get().getColourIntensity() != 1.0f) {
			// Update the quality scalar.
			if (reflectionFBO.getSizeScalar() != KosmosWater.get().getReflectionQuality()) {
				reflectionFBO.setSizeScalar(KosmosWater.get().getReflectionQuality());
			}

			// Binds the reflection FBO.
			if (pipelineMRT != null) {
				FlounderOpenGL.get().bindTexture(pipelineMRT.fbo.getColourTexture(0), GL_TEXTURE_2D, 0);
			} else {
				FlounderOpenGL.get().bindTexture(reflectionFBO.getColourTexture(0), GL_TEXTURE_2D, 0);
			}
		}

		FlounderOpenGL.get().antialias(FlounderDisplay.get().isAntialiasing());
		FlounderOpenGL.get().enableDepthTesting();
		FlounderOpenGL.get().cullBackFaces(true);
	}

	private void renderWater(Water water) {
		FlounderOpenGL.get().bindVAO(water.getVao(), 0);

		shader.getUniformMat4("modelMatrix").loadMat4(water.getModelMatrix());

		shader.getUniformVec4("diffuseColour").loadVec4(water.getColour());

		shader.getUniformFloat("waveTime").loadFloat(Framework.get().getTimeSec() / Water.WAVE_SPEED);
		shader.getUniformFloat("waveLength").loadFloat(Water.WAVE_LENGTH);
		shader.getUniformFloat("amplitude").loadFloat(Water.AMPLITUDE);
		shader.getUniformFloat("squareSize").loadFloat((float) Water.SQUARE_SIZE);
		shader.getUniformFloat("waterHeight").loadFloat(water.getPosition().y);

		shader.getUniformFloat("shineDamper").loadFloat(Water.SHINE_DAMPER);
		shader.getUniformFloat("reflectivity").loadFloat(Water.REFLECTIVITY);

		shader.getUniformBool("ignoreReflections").loadBoolean(!KosmosWater.get().reflectionsEnabled());

		FlounderOpenGL.get().renderArrays(GL_TRIANGLES, water.getVertexCount());

		FlounderOpenGL.get().disableBlending();
		FlounderOpenGL.get().unbindVAO(0);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void dispose() {
		reflectionFBO.delete();
		shader.delete();
	}

	public FBO getReflectionFBO() {
		return reflectionFBO;
	}

	public FilterMRT getPipelineMRT() {
		return pipelineMRT;
	}
}
