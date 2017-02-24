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
import flounder.post.*;
import flounder.renderer.*;
import flounder.resources.*;
import kosmos.*;
import kosmos.shadows.*;
import kosmos.world.*;

public class FilterMRT extends PostFilter {
	public FilterMRT() {
		super("filterMrt", new MyFile(PostFilter.POST_LOC, "mrtFragment.glsl"));
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.getCamera().getViewMatrix());

		shader.getUniformVec3("lightDirection").loadVec3(KosmosWorld.getSkyCycle().getLightDirection());
		shader.getUniformVec2("lightBias").loadVec2(0.7f, 0.6f); //  * (1.0f - KosmosWorld.getSkyCycle().getDayFactor())

		if (KosmosWorld.getFog() != null) {
			shader.getUniformVec3("fogColour").loadVec3(KosmosWorld.getFog().getFogColour());
			shader.getUniformFloat("fogDensity").loadFloat(KosmosWorld.getFog().getFogDensity());
			shader.getUniformFloat("fogGradient").loadFloat(KosmosWorld.getFog().getFogGradient());
		} else {
			shader.getUniformVec3("fogColour").loadVec3(1.0f, 1.0f, 1.0f);
			shader.getUniformFloat("fogDensity").loadFloat(0.003f);
			shader.getUniformFloat("fogGradient").loadFloat(2.0f);
		}

		shader.getUniformMat4("shadowSpaceMatrix").loadMat4(((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getToShadowMapSpaceMatrix());
		shader.getUniformFloat("shadowDistance").loadFloat(((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowDistance());
		shader.getUniformFloat("shadowTransition").loadFloat(10.0f);
		shader.getUniformFloat("shadowMapSize").loadFloat(ShadowRenderer.getShadowMapSize());
	}
}
