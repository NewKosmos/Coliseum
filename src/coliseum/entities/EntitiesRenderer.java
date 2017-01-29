package coliseum.entities;

import coliseum.*;
import coliseum.entities.components.*;
import coliseum.shadows.*;
import coliseum.world.*;
import flounder.animation.*;
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
	private static final int NUMBER_LIGHTS = 4;

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

		shader.getUniformVec3("lightDirection").loadVec3(AnimationSettings.LIGHT_DIR);

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

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.enableAlphaBlending();
	}

	private void renderEntity(Entity entity) {
		ComponentModel componentModel = (ComponentModel) entity.getComponent(ComponentModel.ID);
		ComponentTerrain componentTerrain = (ComponentTerrain) entity.getComponent(ComponentTerrain.ID);

		if (componentModel == null || componentModel.getModel() == null) {
			return;
		}

		OpenGlUtils.bindVAO(componentModel.getModel().getVaoID(), 0, 1, 2, 3);

		if (componentModel.getTexture() != null) {
			OpenGlUtils.bindTexture(componentModel.getTexture(), 0);
			shader.getUniformFloat("atlasRows").loadFloat(componentModel.getTexture().getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(componentModel.getTextureOffset());

			// Face culling if the object has transparency.
			OpenGlUtils.cullBackFaces(true);
		} else {
			OpenGlUtils.bindTexture(textureUndefined, 0);
			shader.getUniformFloat("atlasRows").loadFloat(textureUndefined.getNumberOfRows());
			shader.getUniformVec2("atlasOffset").loadVec2(0, 0);
			OpenGlUtils.cullBackFaces(false);
		}

		if (componentTerrain != null) {
			shader.getUniformFloat("darkness").loadFloat(componentTerrain.getDarkness());
		} else {
			shader.getUniformFloat("darkness").loadFloat(0.0f);
		}

		OpenGlUtils.bindTexture(((ColiseumRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowMap(), GL_TEXTURE_2D, 1);

		shader.getUniformMat4("modelMatrix").loadMat4(componentModel.getModelMatrix());
		shader.getUniformBool("animated").loadBoolean(false);

		glDrawElements(GL_TRIANGLES, componentModel.getModel().getVaoLength(), GL_UNSIGNED_INT, 0);
		OpenGlUtils.unbindVAO(0, 1, 2, 3);
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