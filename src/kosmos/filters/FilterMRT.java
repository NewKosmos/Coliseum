/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.filters;

import flounder.camera.*;
import flounder.entities.*;
import flounder.post.*;
import flounder.renderer.*;
import flounder.resources.*;
import kosmos.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.shadows.*;
import kosmos.world.*;

public class FilterMRT extends PostFilter {
	private static final int LIGHTS = 48;

	private int shadowPCF;
	private float shadowBias;
	private float shadowDarkness;

	public FilterMRT() {
		super("filterMrt", new MyFile(PostFilter.POST_LOC, "mrtFragment.glsl"));

		this.shadowPCF = KosmosConfigs.configMain.getIntWithDefault("shadow_pcf", 0, () -> shadowPCF);
		this.shadowBias = KosmosConfigs.configMain.getFloatWithDefault("shadow_bias", 0.001f, () -> shadowBias);
		this.shadowDarkness = KosmosConfigs.configMain.getFloatWithDefault("shadow_darkness", 0.6f, () -> shadowDarkness);
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("projectionMatrix").loadMat4(FlounderCamera.getCamera().getProjectionMatrix());
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.getCamera().getViewMatrix());

		int lightsLoaded = 0;

		if (FlounderEntities.getEntities() != null) {
			for (Entity entity : FlounderEntities.getEntities().getAll()) {
				ComponentLight componentLight = (ComponentLight) entity.getComponent(ComponentLight.ID);

				if (lightsLoaded < LIGHTS && componentLight != null) {
					shader.getUniformVec3("lightColour[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getColour());
					shader.getUniformVec3("lightPosition[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getPosition());
					shader.getUniformVec3("lightAttenuation[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getAttenuation());
					lightsLoaded++;
				}
			}
		}

		if (KosmosChunks.getChunks() != null) {
			for (Entity entityc : KosmosChunks.getChunks().getAll()) {
				Chunk chunk = (Chunk) entityc;

				if (chunk.isLoaded()) {
					for (Entity entity : chunk.getEntities().getAll()) {
						ComponentLight componentLight = (ComponentLight) entity.getComponent(ComponentLight.ID);

						if (lightsLoaded < LIGHTS && componentLight != null) {
							shader.getUniformVec3("lightColour[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getColour());
							shader.getUniformVec3("lightPosition[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getPosition());
							shader.getUniformVec3("lightAttenuation[" + lightsLoaded + "]").loadVec3(componentLight.getLight().getAttenuation());
							lightsLoaded++;
						}
					}
				}
			}
		}

		if (lightsLoaded < LIGHTS) {
			for (int i = lightsLoaded; i < LIGHTS; i++) {
				shader.getUniformVec3("lightColour[" + i + "]").loadVec3(0.0f, 0.0f, 0.0f);
				shader.getUniformVec3("lightPosition[" + i + "]").loadVec3(0.0f, 0.0f, 0.0f);
				shader.getUniformVec3("lightAttenuation[" + i + "]").loadVec3(1.0f, 0.0f, 0.0f);
			}
		}

		shader.getUniformMat4("shadowSpaceMatrix").loadMat4(((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getToShadowMapSpaceMatrix());
		shader.getUniformFloat("shadowDistance").loadFloat(((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowDistance());
		shader.getUniformFloat("shadowTransition").loadFloat(10.0f);
		shader.getUniformInt("shadowMapSize").loadInt(ShadowRenderer.getShadowMapSize());
		shader.getUniformInt("shadowPCF").loadInt(shadowPCF);
		shader.getUniformFloat("shadowBias").loadFloat(shadowBias);
		shader.getUniformFloat("shadowDarkness").loadFloat(shadowDarkness * KosmosWorld.getSkyCycle().getSinDay()); // TODO

		if (KosmosWorld.getFog() != null) {
			shader.getUniformVec3("fogColour").loadVec3(KosmosWorld.getFog().getFogColour());
			shader.getUniformFloat("fogDensity").loadFloat(KosmosWorld.getFog().getFogDensity());
			shader.getUniformFloat("fogGradient").loadFloat(KosmosWorld.getFog().getFogGradient());
		} else {
			shader.getUniformVec3("fogColour").loadVec3(1.0f, 1.0f, 1.0f);
			shader.getUniformFloat("fogDensity").loadFloat(0.003f);
			shader.getUniformFloat("fogGradient").loadFloat(2.0f);
		}
	}

	public int getShadowPCF() {
		return shadowPCF;
	}

	public float getShadowBias() {
		return shadowBias;
	}

	public float getShadowDarkness() {
		return shadowDarkness;
	}
}
