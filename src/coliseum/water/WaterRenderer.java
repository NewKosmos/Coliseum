package coliseum.water;

import coliseum.*;
import coliseum.shadows.*;
import coliseum.world.*;
import flounder.camera.*;
import flounder.fbos.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.renderer.*;
import flounder.resources.*;
import flounder.shaders.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class WaterRenderer extends IRenderer {
	private static final MyFile VERTEX_SHADER = new MyFile(Shader.SHADERS_LOC, "water", "waterVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile(Shader.SHADERS_LOC, "water", "waterFragment.glsl");

	private FBO reflectionFBO;
	private Shader shader;
	private Water water;
	private float waveTime;

	private boolean enableShadows;
	private boolean enableReflections;

	public WaterRenderer() {
		this.reflectionFBO = FBO.newFBO(0.5f).disableTextureWrap().create();
		this.shader = Shader.newShader("water").setShaderTypes(
				new ShaderType(GL_VERTEX_SHADER, VERTEX_SHADER),
				new ShaderType(GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
		).create();
		this.water = new Water(new Vector3f(0.0f, -0.4f, 0.0f), new Vector3f(), 1.0f);
		this.waveTime = 0.0f;

		this.enableShadows = true;
		this.enableReflections = true;
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
		shader.getUniformVec4("clipPlane").loadVec4(clipPlane);
		shader.getUniformMat4("modelMatrix").loadMat4(water.getModelMatrix());

		shader.getUniformVec3("lightDirection").loadVec3(ColiseumWorld.getSkyCycle().getLightDir());
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

		shader.getUniformBool("ignoreShadows").loadBoolean(!enableShadows);
		shader.getUniformBool("ignoreReflections").loadBoolean(!enableReflections);

		if (enableShadows) {
			shader.getUniformFloat("shadowMapSize").loadFloat(ShadowRenderer.SHADOW_MAP_SIZE);
			shader.getUniformMat4("shadowSpaceMatrix").loadMat4(((ColiseumRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getToShadowMapSpaceMatrix());
			shader.getUniformFloat("shadowDistance").loadFloat(((ColiseumRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowDistance());
			shader.getUniformFloat("shadowTransition").loadFloat(10.0f);
			OpenGlUtils.bindTexture(((ColiseumRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowMap(), GL_TEXTURE_2D, 1);
		}

		if (enableReflections) {
			OpenGlUtils.bindTexture(reflectionFBO.getColourTexture(0), GL_TEXTURE_2D, 0);
		}

		shader.getUniformFloat("waveTime").loadFloat(waveTime / Water.WAVE_SPEED);
		shader.getUniformFloat("waveLength").loadFloat(Water.WAVE_LENGTH);
		shader.getUniformFloat("amplitude").loadFloat(Water.AMPLITUDE);
		shader.getUniformFloat("squareSize").loadFloat(Water.SQUARE_SIZE);
		shader.getUniformFloat("waterHeight").loadFloat(water.getPosition().y);

		shader.getUniformVec4("diffuseColour").loadVec4(water.getColour());

		glDrawArrays(GL_TRIANGLES, 0, water.getVertexCount());

		OpenGlUtils.disableBlending();
		OpenGlUtils.unbindVAO(0);
		shader.stop();

		FlounderBounding.addShapeRender(water.getAABB());
	}

	private void updateWaveTime() {
		waveTime += FlounderFramework.getDeltaRender();
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
		return enableReflections;
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
		shader.dispose();
		water.delete();
	}
}
