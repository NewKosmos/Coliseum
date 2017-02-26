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
import kosmos.*;
import kosmos.chunks.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class WaterRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "water", "waterVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "water", "waterFragment.glsl");

	private float reflectionQuality;

	private FBO reflectionFBO;
	private ShaderObject shader;

	public WaterRenderer() {
		this.reflectionQuality = NewKosmos.configMain.getFloatWithDefault("water_quality", 0.3f, () -> reflectionQuality);
		this.reflectionFBO = FBO.newFBO(reflectionQuality).disableTextureWrap().depthBuffer(DepthBufferType.RENDER_BUFFER).create();
		this.shader = ShaderFactory.newBuilder().setName("water").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();

		/*FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW.GLFW_KEY_KP_ADD);
			@Override public boolean eventTriggered() {return k.wasDown();}
			@Override public void onEvent() {setReflectionQuality(getReflectionQuality() + 0.1f);}
		});
		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW.GLFW_KEY_KP_SUBTRACT);
			@Override public boolean eventTriggered() {return k.wasDown();}
			@Override public void onEvent() {setReflectionQuality(getReflectionQuality() - 0.1f);}
		});*/
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || !KosmosWater.getWater().isLoaded()) {
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
			shader.getUniformVec3("waterOffset").loadVec3(chunk.getPosition());
		}
		//shader.getUniformVec3("waterOffset").loadVec3(FlounderCamera.getPlayer().getPosition());

		if (KosmosWater.reflectionsEnabled()) {
			OpenGlUtils.bindTexture(reflectionFBO.getColourTexture(0), GL_TEXTURE_2D, 0);
		}

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.cullBackFaces(true);
	}

	private void renderWater(Water water) {
		OpenGlUtils.bindVAO(water.getVao(), 0);

		shader.getUniformMat4("modelMatrix").loadMat4(water.getModelMatrix());

		shader.getUniformVec4("diffuseColour").loadVec4(water.getColour());

		shader.getUniformFloat("waveTime").loadFloat(KosmosWater.getWaveTime() / Water.WAVE_SPEED);
		shader.getUniformFloat("waveLength").loadFloat(Water.WAVE_LENGTH);
		shader.getUniformFloat("amplitude").loadFloat(Water.AMPLITUDE);
		shader.getUniformFloat("squareSize").loadFloat((float) Water.SQUARE_SIZE);
		shader.getUniformFloat("waterHeight").loadFloat(water.getPosition().y);

		shader.getUniformFloat("shineDamper").loadFloat(Water.SHINE_DAMPER);
		shader.getUniformFloat("reflectivity").loadFloat(Water.REFLECTIVITY);

		shader.getUniformBool("ignoreReflections").loadBoolean(!KosmosWater.reflectionsEnabled());

		glDrawArrays(GL_TRIANGLES, 0, water.getVertexCount());

		OpenGlUtils.disableBlending();
		OpenGlUtils.unbindVAO(0);
	}

	private void endRendering() {
		//	FlounderBounding.addShapeRender(water.getAABB());
		shader.stop();
	}

	public float getReflectionQuality() {
		return reflectionQuality;
	}

	public void setReflectionQuality(float reflectionQuality) {
		this.reflectionQuality = reflectionQuality;
		this.reflectionFBO.setSizeScalar(reflectionQuality);
	}

	public FBO getReflectionFBO() {
		return reflectionFBO;
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
