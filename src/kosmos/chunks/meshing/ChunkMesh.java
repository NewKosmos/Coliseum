/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.meshing;

import flounder.entities.components.*;
import flounder.logger.*;
import flounder.models.*;
import flounder.processing.*;
import kosmos.chunks.*;

public class ChunkMesh {
	protected Chunk chunk;
	protected ModelObject chunkModel;

	protected boolean built;
	protected boolean sent;

	protected float minX, minY, minZ;
	protected float maxX, maxY, maxZ;
	protected float maxRadius;

	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		this.chunkModel = null;
	}

	public void update() {
		// Makes sure all chunk, model, and biome info is good.
		if (chunk == null || chunk.getBiome() == null || KosmosChunks.get().getModelHexagon() == null || !KosmosChunks.get().getModelHexagon().isLoaded()) {
			return;
		}

		// If not built, build.
		if (!sent && !built) {
			FlounderProcessors.get().sendRequest(new MeshBuildRequest(this, KosmosChunks.get().getModelHexagon()));
			sent = true;
			built = true;
		}
	}

	public ModelObject getModel() {
		return chunkModel;
	}

	public void delete() {
		if (chunkModel != null) {
			chunkModel.delete();
			chunkModel = null;
			sent = false;
			built = false;
		}
	}
}
