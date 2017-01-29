package coliseum.clouds;

import coliseum.world.*;
import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class CloudsRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile(Shader.SHADERS_LOC, "clouds", "cloudsVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(Shader.SHADERS_LOC, "clouds", "cloudsFragment.glsl");

	private Shader shader;
	private Texture textureClouds;
	private Model model;
	private Matrix4f modelMatrix;

	public CloudsRenderer() {
		shader = Shader.newShader("clouds").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).create();
		textureClouds = Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "clouds.png")).create();
		model = Model.newModel(new MyFile(MyFile.RES_FOLDER, "clouds.obj")).create();
		modelMatrix = new Matrix4f();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (!shader.isLoaded() || textureClouds == null) {
			return;
		}

		prepareRendering(clipPlane, camera);
		renderClouds();
		endRendering();
	}

	private void prepareRendering(Vector4f clipPlane, ICamera camera) {
		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);

		if (ColiseumWorld.getFog() != null) {
			shader.getUniformVec3("fogColour").loadVec3(ColiseumWorld.getFog().getFogColour());
			shader.getUniformFloat("fogDensity").loadFloat(ColiseumWorld.getFog().getFogDensity());
			shader.getUniformFloat("fogGradient").loadFloat(ColiseumWorld.getFog().getFogGradient());
		} else {
			shader.getUniformVec3("fogColour").loadVec3(1.0f, 1.0f, 1.0f);
			shader.getUniformFloat("fogDensity").loadFloat(0.003f);
			shader.getUniformFloat("fogGradient").loadFloat(2.0f);
		}

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.enableAlphaBlending();
	}

	private void renderClouds() {
		OpenGlUtils.bindVAO(model.getVaoID(), 0, 1);
		OpenGlUtils.cullBackFaces(false);

		OpenGlUtils.bindTexture(textureClouds, 1);

		Matrix4f.transformationMatrix(new Vector3f(), new Vector3f(), 100.0f, modelMatrix);

		shader.getUniformMat4("modelMatrix").loadMat4(modelMatrix);
		shader.getUniformFloat("darkness").loadFloat(1.0f);

		glDrawElements(GL_TRIANGLES, model.getVaoLength(), GL_UNSIGNED_INT, 0);
		OpenGlUtils.unbindVAO(0, 1);
	}

	private void endRendering() {
		shader.stop();
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Clouds", "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
