/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities;

import flounder.camera.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.world.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * A renderer that is used to render entity's.
 */
public class EntitiesRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "entities", "entityVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "entities", "entityFragment.glsl");

	private ShaderObject shader;
	private TextureObject textureUndefined;
	private int renderedCount;

	/**
	 * Creates a new entity renderer.
	 */
	public EntitiesRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("entities").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
		this.textureUndefined = TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "undefined.png")).create();
		this.renderedCount = 0;
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || FlounderEntities.getEntities() == null) {
			return;
		}

		prepareRendering(clipPlane, camera);

		for (Entity entityc : KosmosChunks.getChunks().queryInFrustum(camera.getViewFrustum())) {
			Chunk chunk = (Chunk) entityc;

			if (chunk.isLoaded()) {
				renderEntity(entityc);

				for (Entity entity : chunk.getEntities().getAll()) {
					renderEntity(entity);
				}
			}
		}

		for (Entity entity : FlounderEntities.getEntities().queryInFrustum(FlounderCamera.getCamera().getViewFrustum())) {
			renderEntity(entity);
		}

		endRendering();
	}

	private void prepareRendering(Vector4f clipPlane, Camera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.enableAlphaBlending();

		renderedCount = 0;
	}

	private void renderEntity(Entity entity) {
		ComponentModel componentModel = (ComponentModel) entity.getComponent(ComponentModel.ID);
		ComponentAnimation componentAnimation = (ComponentAnimation) entity.getComponent(ComponentAnimation.ID);
		ComponentSway componentSway = (ComponentSway) entity.getComponent(ComponentSway.ID);
		final int vaoLength;

		if (componentModel != null && componentModel.getModel() != null) {
			OpenGlUtils.bindVAO(componentModel.getModel().getVaoID(), 0, 1, 2, 3);
			shader.getUniformBool("animated").loadBoolean(false);
			shader.getUniformMat4("modelMatrix").loadMat4(componentModel.getModelMatrix());
			vaoLength = componentModel.getModel().getVaoLength();
			//	shader.getUniformBool("ignoreShadows").loadBoolean(componentModel.isIgnoringShadows());
			//	shader.getUniformBool("ignoreFog").loadBoolean(componentModel.isIgnoringFog());
		} else if (componentAnimation != null && componentAnimation.getModel() != null) {
			OpenGlUtils.bindVAO(componentAnimation.getModel().getVaoID(), 0, 1, 2, 3, 4, 5);
			shader.getUniformBool("animated").loadBoolean(true);
			shader.getUniformMat4("modelMatrix").loadMat4(componentAnimation.getModelMatrix());
			vaoLength = componentAnimation.getModel().getVaoLength();
			//	shader.getUniformBool("ignoreShadows").loadBoolean(false);
			//	shader.getUniformBool("ignoreFog").loadBoolean(false);
		} else {
			// No model, so no render!
			return;
		}

		if (componentModel != null && componentModel.getTexture() != null && componentModel.getTexture().isLoaded()) {
			shader.getUniformFloat("atlasRows").loadFloat(componentModel.getTexture().getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(componentModel.getTextureOffset());
			shader.getUniformFloat("swayHeight").loadFloat((float) componentModel.getModel().getAABB().getHeight());
			OpenGlUtils.cullBackFaces(!componentModel.getTexture().hasAlpha());
			OpenGlUtils.bindTexture(componentModel.getTexture(), 0);
		} else if (componentAnimation != null && componentAnimation.getTexture() != null && componentAnimation.getTexture().isLoaded()) {
			shader.getUniformFloat("atlasRows").loadFloat(componentAnimation.getTexture().getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(componentAnimation.getTextureOffset());
			OpenGlUtils.cullBackFaces(!componentAnimation.getTexture().hasAlpha());
			OpenGlUtils.bindTexture(componentAnimation.getTexture(), 0);
		} else if (textureUndefined != null && textureUndefined.isLoaded()) {
			// No texture, so load a 'undefined' texture.
			shader.getUniformFloat("atlasRows").loadFloat(textureUndefined.getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(0, 0);
			OpenGlUtils.cullBackFaces(!textureUndefined.hasAlpha());
			OpenGlUtils.bindTexture(textureUndefined, 0);
		}

		if (componentAnimation != null) {
			for (int i = 0; i < componentAnimation.getJointTransforms().length; i++) {
				shader.getUniformMat4("jointTransforms[" + i + "]").loadMat4(componentAnimation.getJointTransforms()[i]);
			}
		}

		if (componentSway != null) {
			shader.getUniformBool("swaying").loadBoolean(true);

			if (componentSway.getTextureSway() != null && componentSway.getTextureSway().isLoaded()) {
				OpenGlUtils.bindTexture(componentSway.getTextureSway(), 1);
			}
		} else {
			shader.getUniformBool("swaying").loadBoolean(false);
		}

		shader.getUniformFloat("systemTime").loadFloat(Framework.getTimeSec());
		shader.getUniformFloat("windPower").loadFloat(KosmosWorld.getWindPower());

		glDrawElements(GL_TRIANGLES, vaoLength, GL_UNSIGNED_INT, 0);
		OpenGlUtils.unbindVAO(0, 1, 2, 3, 4, 5);
		renderedCount++;
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void profile() {
		FlounderProfiler.add(FlounderEntities.PROFILE_TAB_NAME, "Render Time", super.getRenderTime());
		FlounderProfiler.add(FlounderEntities.PROFILE_TAB_NAME, "Rendered Count", renderedCount);
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}