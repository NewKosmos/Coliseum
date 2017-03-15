/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.shadows;

import flounder.camera.*;
import flounder.entities.*;
import flounder.fbos.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import kosmos.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.world.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShadowRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "shadows", "shadowVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "shadows", "shadowFragment.glsl");

	public static final int SHADOW_MAP_SIZE = KosmosConfigs.configMain.getIntWithDefault("shadow_size", Math.min(FBO.getMaxFBOSize(), 8192), ShadowRenderer::getShadowMapSize);

	private FBO shadowFBO;
	private ShaderObject shader;

	private Matrix4f mvpReusableMatrix;

	private Matrix4f projectionMatrix;
	private Matrix4f lightViewMatrix;
	private Matrix4f projectionViewMatrix;
	private Matrix4f offset;

	private ShadowBox shadowBox;

	/**
	 * Creates a new entity renderer.
	 */
	public ShadowRenderer() {
		this.shadowFBO = FBO.newFBO(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE).noColourBuffer().disableTextureWrap().depthBuffer(DepthBufferType.TEXTURE).create();
		this.shader = ShaderFactory.newBuilder().setName("shadows").addType(new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();

		this.mvpReusableMatrix = new Matrix4f();

		this.projectionMatrix = new Matrix4f();
		this.lightViewMatrix = new Matrix4f();
		this.projectionViewMatrix = new Matrix4f();
		this.offset = createOffset();

		this.shadowBox = new ShadowBox(lightViewMatrix);
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || camera == null) {
			return;
		}

		prepareRendering(clipPlane, camera);

		if (FlounderEntities.getEntities() != null) {
			for (Entity entity : FlounderEntities.getEntities().queryInFrustum(FlounderCamera.getCamera().getViewFrustum())) {
				renderEntity(entity);
			}
		}

		if (KosmosChunks.getChunks() != null) {
			for (Entity entityc : KosmosChunks.getChunks().queryInFrustum(camera.getViewFrustum())) {
				Chunk chunk = (Chunk) entityc;

				if (chunk.isLoaded()) {
					renderEntity(entityc);

					for (Entity entity : chunk.getEntities().getAll()) {
						renderEntity(entity);
					}
				}
			}
		}

		endRendering();
	}

	private void prepareRendering(Vector4f clipPlane, Camera camera) {
		shadowBox.update(camera);
		updateOrthographicProjectionMatrix(shadowBox.getWidth(), shadowBox.getHeight(), shadowBox.getLength());
		updateLightViewMatrix(KosmosWorld.getSkyCycle().getLightPosition(), shadowBox.getCenter());
		Matrix4f.multiply(projectionMatrix, lightViewMatrix, projectionViewMatrix);

		shadowFBO.bindFrameBuffer();
		shader.start();

		OpenGlUtils.prepareNewRenderParse(0.0f, 0.0f, 0.0f);
		OpenGlUtils.antialias(true);
		OpenGlUtils.cullBackFaces(false);
		OpenGlUtils.enableDepthTesting();
	}

	/**
	 * Creates the orthographic projection matrix.
	 *
	 * @param width Shadow box width.
	 * @param height Shadow box height.
	 * @param length Shadow box length.
	 */
	private void updateOrthographicProjectionMatrix(float width, float height, float length) {
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = 2.0f / width;
		projectionMatrix.m11 = 2.0f / height;
		projectionMatrix.m22 = -2.0f / length;
		projectionMatrix.m33 = 1.0f;
	}

	/**
	 * Updates the "view" matrix of the light. The light itself has no position, so the "view" matrix is centered at the center of the shadow box.
	 *
	 * @param direction The light direct.
	 * @param position The center of the shadow box.
	 */
	private void updateLightViewMatrix(Vector3f direction, Vector3f position) {
		direction.normalize();
		position.negate();

		lightViewMatrix.setIdentity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		Matrix4f.rotate(lightViewMatrix, new Vector3f(1.0f, 0.0f, 0.0f), pitch, lightViewMatrix);
		float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
		yaw = direction.z > 0.0f ? yaw - 180.0f : yaw;
		Matrix4f.rotate(lightViewMatrix, new Vector3f(0.0f, 1.0f, 0.0f), (float) -Math.toRadians(yaw), lightViewMatrix);
		Matrix4f.translate(lightViewMatrix, position, lightViewMatrix);
	}

	/**
	 * Create the offset for part of the conversion to shadow map space.
	 *
	 * @return The offset as a matrix.
	 */
	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		Matrix4f.translate(offset, new Vector3f(0.5f, 0.5f, 0.5f), offset);
		Matrix4f.scale(offset, new Vector3f(0.5f, 0.5f, 0.5f), offset);
		return offset;
	}

	private void renderEntity(Entity entity) {
		if (entity == null) {
			return;
		}

		ComponentModel componentModel = (ComponentModel) entity.getComponent(ComponentModel.ID);
		ComponentAnimation componentAnimation = (ComponentAnimation) entity.getComponent(ComponentAnimation.ID);
		ComponentSway componentSway = (ComponentSway) entity.getComponent(ComponentSway.ID);
		final int vaoLength;

		if (componentModel != null && componentModel.getModel() != null && componentModel.getModel().isLoaded()) {
			OpenGlUtils.bindVAO(componentModel.getModel().getVaoID(), 0);
			shader.getUniformBool("animated").loadBoolean(false);

			if (componentModel.getModelMatrix() != null) {
				Matrix4f.multiply(projectionViewMatrix, componentModel.getModelMatrix(), mvpReusableMatrix);
				shader.getUniformMat4("mvpMatrix").loadMat4(mvpReusableMatrix);
			}

			if (componentModel.getModel().getAABB() != null) {
				shader.getUniformFloat("swayHeight").loadFloat((float) componentModel.getModel().getAABB().getHeight());
			}

			vaoLength = componentModel.getModel().getVaoLength();
		} else if (componentAnimation != null && componentAnimation.getModel() != null && componentAnimation.getModel().isLoaded()) {
			OpenGlUtils.bindVAO(componentAnimation.getModel().getVaoID(), 0, 4, 5);
			shader.getUniformBool("animated").loadBoolean(true);

			if (componentAnimation.getModelMatrix() != null) {
				Matrix4f.multiply(projectionViewMatrix, componentAnimation.getModelMatrix(), mvpReusableMatrix);
				shader.getUniformMat4("mvpMatrix").loadMat4(mvpReusableMatrix);
			}

			// Just stop if you are trying to apply a sway to a animated object, rethink life.
			shader.getUniformFloat("swayHeight").loadFloat(0.0f);
			vaoLength = componentAnimation.getModel().getVaoLength();

			// Loads joint transforms.
			Matrix4f[] jointMatrices = componentAnimation.getJointTransforms();

			for (int i = 0; i < jointMatrices.length; i++) {
				shader.getUniformMat4("jointTransforms[" + i + "]").loadMat4(jointMatrices[i]);
			}
		} else {
			// No model, so no render!
			return;
		}

		if (componentSway != null) {
			shader.getUniformBool("swaying").loadBoolean(true);
			shader.getUniformVec2("swayOffset").loadVec2(KosmosWorld.getSwayOffsetX(), KosmosWorld.getSwayOffsetY());

			if (componentSway.getTextureSway() != null && componentSway.getTextureSway().isLoaded()) {
				OpenGlUtils.bindTexture(componentSway.getTextureSway(), 1);
			}
		} else {
			shader.getUniformBool("swaying").loadBoolean(false);
		}

		if (vaoLength > 0) {
			glDrawElements(GL_TRIANGLES, vaoLength, GL_UNSIGNED_INT, 0);
		}

		OpenGlUtils.unbindVAO(0, 4, 5);
	}

	private void endRendering() {
		shader.stop();
		shadowFBO.unbindFrameBuffer();
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Shadows", "Render Time", super.getRenderTime());
	}

	/**
	 * @return The ID of the shadow map texture.
	 */
	public int getShadowMap() {
		return shadowFBO.getDepthTexture();
	}

	public float getShadowDistance() {
		return shadowBox.getShadowDistance();
	}

	/**
	 * @return The shadow box, so that it can be used by other class to test if engine.entities are inside the box.
	 */
	public ShadowBox getShadowBox() {
		return shadowBox;
	}

	/**
	 * This biased projection-view matrix is used to convert fragments into "shadow map space" when rendering the main renderObjects pass.
	 *
	 * @return The to-shadow-map-space matrix.
	 */
	public Matrix4f getToShadowMapSpaceMatrix() {
		return Matrix4f.multiply(offset, projectionViewMatrix, null);
	}

	public static int getShadowMapSize() {
		return SHADOW_MAP_SIZE;
	}

	/**
	 * @return The light's "view" matrix.
	 */
	protected Matrix4f getLightSpaceTransform() {
		return lightViewMatrix;
	}

	@Override
	public void dispose() {
		shader.delete();
		shadowFBO.delete();
	}
}
