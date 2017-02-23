/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package testing.filters;

import flounder.camera.*;
import flounder.post.*;
import flounder.resources.*;
import testing.*;

public class FilterMRT extends PostFilter {
	public FilterMRT() {
		super("filterMrt", new MyFile(PostFilter.POST_LOC, "mrtFragment.glsl"));
	}

	@Override
	public void storeValues() {
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.getCamera().getViewMatrix());

		shader.getUniformVec3("lightDirection").loadVec3(Testing.LIGHT_DIRECTION);
		shader.getUniformVec2("lightBias").loadVec2(0.7f, 0.6f);

		shader.getUniformVec3("fogColour").loadVec3(Testing.SKY_COLOUR_DAY);
		shader.getUniformFloat("fogDensity").loadFloat(0.02f);
		shader.getUniformFloat("fogGradient").loadFloat(2.0f);
	}
}
