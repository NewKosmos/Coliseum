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
import flounder.maths.matrices.*;
import flounder.post.*;
import flounder.renderer.*;
import flounder.resources.*;
import kosmos.*;
import kosmos.shadows.*;
import kosmos.world.*;

public class FilterMRT extends PostFilter {
	private Matrix4f viewInverseMatrix;
	private Matrix4f projectionInverseMatrix;

	public FilterMRT() {
		super("filterMrt", new MyFile(PostFilter.POST_LOC, "mrtFragment.glsl"));
		this.viewInverseMatrix = new Matrix4f(); // View inverse matrix.
		this.projectionInverseMatrix = new Matrix4f(); // Projection inverse matrix.
	}

	@Override
	public void storeValues() {
		updateVPIMatrix();

		shader.getUniformMat4("viewInverseMatrix").loadMat4(viewInverseMatrix);
		shader.getUniformMat4("projectionInverseMatrix").loadMat4(projectionInverseMatrix);
		shader.getUniformMat4("viewMatrix").loadMat4(FlounderCamera.getCamera().getViewMatrix());

		shader.getUniformVec3("lightDirection").loadVec3(KosmosWorld.getSkyCycle().getLightDirection());
		shader.getUniformVec2("lightBias").loadVec2(0.7f, 0.6f * KosmosWorld.getSkyCycle().getSinDay());

		shader.getUniformFloat("shadowIntensity").loadFloat(KosmosWorld.getSkyCycle().getSinDay());

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

		shader.getUniformFloat("nearPlane").loadFloat(FlounderCamera.getCamera().getNearPlane());
		shader.getUniformFloat("farPlane").loadFloat(FlounderCamera.getCamera().getFarPlane());
	}

	private void updateVPIMatrix() {
		viewInverseMatrix.setIdentity();
		viewInverseMatrix.set(FlounderCamera.getCamera().getViewMatrix());
		viewInverseMatrix.invert();

		projectionInverseMatrix.setIdentity();
		projectionInverseMatrix.set(FlounderCamera.getCamera().getProjectionMatrix());
		projectionInverseMatrix.invert();
	}
}
