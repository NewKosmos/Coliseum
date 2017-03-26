package kosmos.skybox;

import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;
import kosmos.world.*;
import org.lwjgl.opengl.*;

public class SkyboxRenderer extends Renderer {
	private static final MyFile VERTEX_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "skybox", "skyboxVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(FlounderShaders.SHADERS_LOC, "skybox", "skyboxFragment.glsl");

	private static final float SIZE = 150.0f;
	private static final float[] VERTICES = {-SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, -SIZE, SIZE, -SIZE, -SIZE, -SIZE, -SIZE, SIZE, SIZE, -SIZE, SIZE};

	private ShaderObject shader;
	private Matrix4f viewMatrix;
	private int vao;

	public SkyboxRenderer() {
		this.shader = ShaderFactory.newBuilder().setName("skybox").addType(new ShaderType(GL20.GL_VERTEX_SHADER, VERTEX_SHADER)).addType(new ShaderType(GL20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)).create();
		this.viewMatrix = new Matrix4f();
		this.vao = FlounderLoader.createVAO();
		FlounderLoader.storeDataInVBO(vao, VERTICES, 0, 3);
		OpenGlUtils.unbindVAO();
	}

	@Override
	public void renderObjects(Vector4f clipPlane, Camera camera) {
		if (!shader.isLoaded() || vao == -1) {
			return;
		}

		this.viewMatrix.set(camera.getViewMatrix());
		this.viewMatrix.m30 = 0.0f;
		this.viewMatrix.m31 = 0.0f;
		this.viewMatrix.m32 = 0.0f;

		shader.start();
		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(viewMatrix);
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);
		shader.getUniformVec3("skyColour").loadVec3(KosmosWorld.getSkyColour());
		shader.getUniformFloat("blendFactor").loadFloat(1.0f - KosmosWorld.getShadowFactor());

		OpenGlUtils.antialias(FlounderDisplay.isAntialiasing());
		OpenGlUtils.enableDepthTesting();
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.disableBlending();

		OpenGlUtils.bindVAO(vao, 0);

		OpenGlUtils.renderArrays(GL11.GL_TRIANGLES, VERTICES.length);

		OpenGlUtils.unbindVAO(0);
		shader.stop();
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Kosmos Skybox", "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		shader.delete();
	}
}
