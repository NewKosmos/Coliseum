package coliseum.water;

import coliseum.entities.*;
import flounder.camera.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class WaterRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile(Shader.SHADERS_LOC, "water", "waterVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(Shader.SHADERS_LOC, "water", "waterFragment.glsl");

	private Shader shader;
	private Water water;
	private float waveTime;

	public WaterRenderer() {
		this.shader = Shader.newShader("water").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).create();
		this.water = new Water(Water.HEIGHT);
		this.waveTime = 0.0f;
	}

	@Override
	public void renderObjects(Vector4f clipPlane, ICamera camera) {
		if (!shader.isLoaded() || !water.isLoaded()) {
			return;
		}

		shader.start();
		OpenGlUtils.cullBackFaces(false);
		OpenGlUtils.antialias(true);
		OpenGlUtils.bindVAO(water.getVao(), 0);
		OpenGlUtils.enableAlphaBlending();

		updateWaveTime();

		shader.getUniformMat4("projectionMatrix").loadMat4(camera.getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(camera.getViewMatrix());

		shader.getUniformVec3("lightDirection").loadVec3(EntitiesRenderer.LIGHT_DIR);

		shader.getUniformFloat("waveLength").loadFloat(Water.WAVELENGTH);
		shader.getUniformFloat("amplitude").loadFloat(Water.AMPLITUDE);
		shader.getUniformFloat("squareSize").loadFloat(Water.SQUARE_SIZE);
		shader.getUniformFloat("waterHeight").loadFloat(water.getHeight());

		shader.getUniformFloat("waveTime").loadFloat(waveTime / Water.WAVE_SPEED);

		glDrawArrays(GL_TRIANGLES, 0, water.getVertexCount());

		OpenGlUtils.disableBlending();
		OpenGlUtils.unbindVAO(0);
		shader.stop();
	}

	private void updateWaveTime() {
		waveTime += FlounderFramework.getDelta();
		waveTime %= Water.WAVE_SPEED;
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Water", "Render Time", super.getRenderTime());
	}

	@Override
	public void dispose() {
		shader.dispose();
		water.delete();
	}
}
