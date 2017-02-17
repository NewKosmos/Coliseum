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
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import kosmos.*;
import kosmos.shadows.*;
import kosmos.world.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class WaterRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "water", "waterVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "water", "waterFragment.glsl");

	private FBO reflectionFBO;

	private ShaderObject shader;

	private Water water;
	private float waveTime;

	private boolean enableShadows;
	private boolean enableReflections;

	public WaterRenderer() {
		this.reflectionFBO = FBO.newFBO(0.4f).disableTextureWrap().depthBuffer(DepthBufferType.RENDER_BUFFER).create();

		this.shader = ShaderFactory.newBuilder().setName("water").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();

		this.water = new Water(new Vector3f(0.0f, -0.2f, 0.0f), new Vector3f(), 1.0f);
		this.waveTime = 0.0f;

		this.enableShadows = true;
		this.enableReflections = true;
	}

	private void prepareRendering(Vector4f clipPlane, Camera camera) {
		updateWaveTime();

		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		shader.getUniformVec3("lightDirection").loadVec3(KosmosWorld.getSkyCycle().getLightDirection());
		shader.getUniformVec2("lightBias").loadVec2(0.7f, 0.6f);

		if (enableShadows) {
			shader.getUniformFloat("shadowMapSize").loadFloat(ShadowRenderer.SHADOW_MAP_SIZE);
			shader.getUniformMat4("shadowSpaceMatrix").loadMat4(((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getToShadowMapSpaceMatrix());
			shader.getUniformFloat("shadowDistance").loadFloat(((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowDistance());
			shader.getUniformFloat("shadowTransition").loadFloat(10.0f);
			OpenGlUtils.bindTexture(((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowMap(), GL_TEXTURE_2D, 1);
		}

		if (enableReflections) {
			OpenGlUtils.bindTexture(reflectionFBO.getColourTexture(0), GL_TEXTURE_2D, 0);
		}

		if (KosmosWorld.getFog() != null) {
			shader.getUniformVec3("fogColour").loadVec3(KosmosWorld.getFog().getFogColour());
			shader.getUniformFloat("fogDensity").loadFloat(KosmosWorld.getFog().getFogDensity());
			shader.getUniformFloat("fogGradient").loadFloat(KosmosWorld.getFog().getFogGradient());
		} else {
			shader.getUniformVec3("fogColour").loadVec3(1.0f, 1.0f, 1.0f);
			shader.getUniformFloat("fogDensity").loadFloat(0.003f);
			shader.getUniformFloat("fogGradient").loadFloat(2.0f);
		}

		shader.getUniformVec3("dayNightColour").loadVec3(KosmosWorld.getSkyCycle().getSkyColour());

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || !water.isLoaded()) {
			return;
		}

		prepareRendering(clipPlane, camera);
		renderWater(water);
		endRendering();
	}

	private void renderWater(Water water) {
		OpenGlUtils.bindVAO(water.getVao(), 0);

		shader.getUniformMat4("modelMatrix").loadMat4(water.getModelMatrix());

		shader.getUniformVec4("diffuseColour").loadVec4(water.getColour());

		shader.getUniformFloat("waveTime").loadFloat(waveTime / Water.WAVE_SPEED);
		shader.getUniformFloat("waveLength").loadFloat(Water.WAVE_LENGTH);
		shader.getUniformFloat("amplitude").loadFloat(Water.AMPLITUDE);
		shader.getUniformFloat("squareSize").loadFloat((float) Water.SQUARE_SIZE);
		shader.getUniformFloat("waterHeight").loadFloat(water.getPosition().y);

		shader.getUniformBool("ignoreShadows").loadBoolean(!enableShadows);
		shader.getUniformBool("ignoreReflections").loadBoolean(!enableReflections);

		glDrawArrays(GL_TRIANGLES, 0, water.getVertexCount());

		OpenGlUtils.disableBlending();
		OpenGlUtils.unbindVAO(0);
	}

	private void endRendering() {
		//	FlounderBounding.addShapeRender(water.getAABB());
		shader.stop();
	}

	private void updateWaveTime() {
		waveTime += Framework.getDeltaRender();
		waveTime %= Water.WAVE_SPEED;
	}

	public FBO getReflectionFBO() {
		return reflectionFBO;
	}

	public Water getWater() {
		return water;
	}

	public boolean shadowsEnabled() {
		return enableShadows;
	}

	public void setShadowsEnabled(boolean enableShadows) {
		this.enableShadows = enableShadows;
	}

	public boolean reflectionsEnabled() {
		return enableReflections && water.getColour().a != 1.0f;
	}

	public void setReflectionsEnabled(boolean enableReflections) {
		this.enableReflections = enableReflections;
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Water", "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		reflectionFBO.delete();
		shader.delete();
		water.delete();
	}
}
