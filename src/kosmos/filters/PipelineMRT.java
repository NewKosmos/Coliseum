/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.filters;

import flounder.fbos.*;
import flounder.post.*;
import flounder.renderer.*;
import kosmos.*;

public class PipelineMRT extends PostPipeline {
	private FilterMRT filterMRT;

	public PipelineMRT() {
		filterMRT = new FilterMRT();
	}

	@Override
	public void renderPipeline(FBO startFBO) {
		filterMRT.applyFilter(
				startFBO.getColourTexture(0),
				startFBO.getColourTexture(1),
				startFBO.getColourTexture(2),
				((KosmosRenderer) FlounderRenderer.getRendererMaster()).getShadowRenderer().getShadowMap()
		);
	}

	@Override
	public FBO getOutput() {
		return filterMRT.fbo;
	}

	@Override
	public void dispose() {
		filterMRT.dispose();
	}
}