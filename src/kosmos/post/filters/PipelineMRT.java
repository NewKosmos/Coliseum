/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.post.filters;

import flounder.devices.*;
import flounder.fbos.*;
import flounder.post.*;
import flounder.post.filters.*;
import flounder.renderer.*;
import kosmos.*;

public class PipelineMRT extends PostPipeline {
	private FilterMRT filterMRT;
	private FilterFXAA filterFXAA;
	private boolean runFXAA;

	public PipelineMRT() {
		this.filterMRT = new FilterMRT();
		this.filterFXAA = new FilterFXAA();
	}

	@Override
	public void renderPipeline(FBO startFBO) {
		runFXAA = FlounderDisplay.isAntialiasing();

		filterMRT.applyFilter(
				startFBO.getColourTexture(0), // Colours
				startFBO.getColourTexture(1), // Normals
				startFBO.getColourTexture(2), // Extras
				startFBO.getDepthTexture(), // Depth
				((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowMap() // Shadow Map
		);

		if (runFXAA) {
			filterFXAA.applyFilter(filterMRT.fbo.getColourTexture(0));
		}
	}

	@Override
	public FBO getOutput() {
		if (runFXAA) {
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
