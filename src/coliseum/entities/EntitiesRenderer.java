package coliseum.entities;

import coliseum.*;
import coliseum.entities.components.*;
import coliseum.shadows.*;
import coliseum.world.*;
import flounder.camera.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * A renderer that is used to render entity's.
 */
public class EntitiesRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile(Shader.SHADERS_LOC, "entities", "entityVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(Shader.SHADERS_LOC, "entities", "entityFragment.glsl");

	private Shader shader;
	private Texture textureUndefined;

	/**
	 * Creates a new entity renderer.
	 */
	public EntitiesRenderer() {
		shader = Shader.newShader("entities").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).create();
		textureUndefined = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "undefined.png")).create();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (!shader.isLoaded() || FlounderEntities.getEntities() == null) {
			return;
		}

		prepareRendering(clipPlane, camera);

		for (Entity entity : FlounderEntities.getEntities().queryInFrustum(new ArrayList<>(), FlounderCamera.getCamera().getViewFrustum())) {
			renderEntity(entity);
		}

		endRendering();
	}

	private void prepareRendering(Vector4f clipPlane, ICamera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		shader.getUniformVec3("lightDirection").loadVec3(ColiseumWorld.getSkyCycle().getLightDirection());
		shader.getUniformVec2("lightBias").loadVec2(0.7f, 0.6f);

		if (ColiseumWorld.getFog() != null) {
			shader.getUniformVec3("fogColour").loadVec3(ColiseumWorld.getFog().getFogColour());
			shader.getUniformFloat("fogDensity").loadFloat(ColiseumWorld.getFog().getFogDensity());
			shader.getUniformFloat("fogGradient").loadFloat(ColiseumWorld.getFog().getFogGradient());
		} else {
			shader.getUniformVec3("fogColour").loadVec3(1.0f, 1.0f, 1.0f);
			shader.getUniformFloat("fogDensity").loadFloat(0.003f);
			shader.getUniformFloat("fogGradient").loadFloat(2.0f);
		}

		shader.getUniformFloat("shadowMapSize").loadFloat(ShadowRenderer.SHADOW_MAP_SIZE);
		shader.getUniformMat4("shadowSpaceMatrix").loadMat4(((ColiseumRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getToShadowMapSpaceMatrix());
		shader.getUniformFloat("shadowDistance").loadFloat(((ColiseumRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowDistance());
		shader.getUniformFloat("shadowTransition").loadFloat(10.0f);

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.enableAlphaBlending();
	}

	private void renderEntity(Entity entity) {
		ComponentModel componentModel = (ComponentModel) entity.getComponent(ComponentModel.ID);
		ComponentAnimation componentAnimation = (ComponentAnimation) entity.getComponent(ComponentAnimation.ID);
		ComponentTerrain componentTerrain = (ComponentTerrain) entity.getComponent(ComponentTerrain.ID);
		final int vaoLength;

		if (componentModel != null && componentModel.getModel() != null) {
			OpenGlUtils.bindVAO(componentModel.getModel().getVaoID(), 0, 1, 2, 3);
			shader.getUniformBool("animated").loadBoolean(false);
			shader.getUniformMat4("modelMatrix").loadMat4(componentModel.getModelMatrix());
			vaoLength = componentModel.getModel().getVaoLength();
			shader.getUniformBool("ignoreShadows").loadBoolean(componentModel.isIgnoringShadows());
			shader.getUniformBool("ignoreFog").loadBoolean(componentModel.isIgnoringFog());
		} else if (componentAnimation != null && componentAnimation.getModel() != null) {
			OpenGlUtils.bindVAO(componentAnimation.getModel().getVaoID(), 0, 1, 2, 3, 4, 5);
			shader.getUniformBool("animated").loadBoolean(true);
			shader.getUniformMat4("modelMatrix").loadMat4(componentAnimation.getModelMatrix());
			vaoLength = componentAnimation.getModel().getVaoLength();
			shader.getUniformBool("ignoreShadows").loadBoolean(false);
			shader.getUniformBool("ignoreFog").loadBoolean(false);
		} else {
			// No model, so no render!
			return;
		}

		if (componentModel != null && componentModel.getTexture() != null) {
			OpenGlUtils.bindTexture(componentModel.getTexture(), 0);
			shader.getUniformFloat("atlasRows").loadFloat(componentModel.getTexture().getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(componentModel.getTextureOffset());
			OpenGlUtils.cullBackFaces(!componentModel.getTexture().hasTransparency());
		} else if (componentAnimation != null && componentAnimation.getTexture() != null) {
			OpenGlUtils.bindTexture(componentAnimation.getTexture(), 0);
			shader.getUniformFloat("atlasRows").loadFloat(componentAnimation.getTexture().getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(componentAnimation.getTextureOffset());
			OpenGlUtils.cullBackFaces(!componentAnimation.getTexture().hasTransparency());
		} else {
			// No texture, so load a 'undefined' texture.
			OpenGlUtils.bindTexture(textureUndefined, 0);
			shader.getUniformFloat("atlasRows").loadFloat(textureUndefined.getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(0, 0);
			OpenGlUtils.cullBackFaces(!textureUndefined.hasTransparency());
		}

		OpenGlUtils.bindTexture(((ColiseumRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowMap(), GL_TEXTURE_2D, 1);

		if (componentAnimation != null) {
			for (int i = 0; i < componentAnimation.getJointTransforms().length; i++) {
				shader.getUniformMat4("jointTransforms[" + i + "]").loadMat4(componentAnimation.getJointTransforms()[i]);
			}
		}

		if (componentTerrain != null) {
			shader.getUniformFloat("darkness").loadFloat(componentTerrain.getDarkness());
		} else {
			shader.getUniformFloat("darkness").loadFloat(0.0f);
		}

		glDrawElements(GL_TRIANGLES, vaoLength, GL_UNSIGNED_INT, 0);
		OpenGlUtils.unbindVAO(0, 1, 2, 3, 4, 5);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void profile() {
		FlounderProfiler.add(FlounderEntities.PROFILE_TAB_NAME, "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}