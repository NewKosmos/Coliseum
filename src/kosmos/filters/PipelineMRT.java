/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.filters;

import flounder.devices.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.post.filters.*;
import flounder.renderer.*;
import kosmos.*;

public class PipelineMRT extends PostPipeline {
	private FilterMRT filterMRT;
	private FilterFXAA filterFXAA;

	public PipelineMRT() {
		filterMRT = new FilterMRT();
		filterFXAA = new FilterFXAA();
	}

	@Override
	public void renderPipeline(FBO startFBO) {
		filterMRT.applyFilter(
				startFBO.getColourTexture(0),
				startFBO.getColourTexture(1),
				startFBO.getDepthTexture(),
				((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowMap()
		);

		if (FlounderDisplay.isAntialiasing()) {
			filterFXAA.applyFilter(filterMRT.fbo.getColourTexture(0));
		}
	}

	@Override
	public FBO getOutput() {
		if (FlounderDisplay.isAntialiasing()) {
			return filterFXAA.fbo;
		} else {
			return filterMRT.fbo;
		}
	}

	@Override
	public void dispose() {
		filterMRT.dispose();
		filterFXAA.dispose();
	}
}
