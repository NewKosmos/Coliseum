package coliseum.chunks;

import coliseum.entities.components.*;
import flounder.framework.*;
import flounder.logger.*;
import flounder.materials.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;

import java.util.*;

public class ChunkMesh {
	private Chunk chunk;
	private Model model;
	private AABB aabb;

	protected ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		this.model = null;
		this.aabb = null;
	}

	protected void rebuild() {
		//
		if (model != null) {
			model.delete();
		}

		model = null;
		aabb = null;

		//
		List<TilesMesh> tilesMeshes = new ArrayList<>();
		chunk.getTiles().keySet().forEach(tile -> {
			if (tile.getModel() != null && tile.getModel().getMeshData() != null) {
				tilesMeshes.add(new TilesMesh(tile, chunk.getTiles().get(tile)));
			}
		});

		//
		if (tilesMeshes.size() != chunk.getTiles().size()) {
			return;
		}

		//
		float[] vertices = TilesMesh.mergeF(tilesMeshes, TilesMesh::getVertices);
		float[] textures = TilesMesh.mergeF(tilesMeshes, TilesMesh::getTextures);
		float[] normals = TilesMesh.mergeF(tilesMeshes, TilesMesh::getNormals);
		float[] tangents = TilesMesh.mergeF(tilesMeshes, TilesMesh::getTangents);
		int[] indices = TilesMesh.mergeI(tilesMeshes, TilesMesh::getIndices);

		//
		AABB modelAABB = new AABB();
		int currentPosID = 0;

		for (int i = 0; i < vertices.length; i++) {
			switch (currentPosID) {
				case (0):
					if (vertices[i] > modelAABB.getMaxExtents().x) {
						modelAABB.getMaxExtents().x = vertices[i];
					} else if (vertices[i] < modelAABB.getMinExtents().x) {
						modelAABB.getMinExtents().x = vertices[i];
					}

					break;
				case (1):
					if (vertices[i] > modelAABB.getMaxExtents().y) {
						modelAABB.getMaxExtents().y = vertices[i];
					} else if (vertices[i] < modelAABB.getMinExtents().y) {
						modelAABB.getMinExtents().y = vertices[i];
					}

					break;
				case (2):
					if (vertices[i] > modelAABB.getMaxExtents().z) {
						modelAABB.getMaxExtents().z = vertices[i];
					} else if (vertices[i] < modelAABB.getMinExtents().z) {
						modelAABB.getMinExtents().z = vertices[i];
					}

					break;
			}

			//
			currentPosID++;

			if (currentPosID > 2) {
				currentPosID = 0;
			}
		}

		//
		ModelBuilder.LoadManual manual = new ModelBuilder.LoadManual() {
			@Override public String getModelName() { return "chunk" + chunk.getPosition().x + "u" + chunk.getPosition().y + FlounderFramework.getTimeSec(); }
			@Override public float[] getVertices() { return vertices; }
			@Override public float[] getTextureCoords() { return textures; }
			@Override public float[] getNormals() { return normals; }
			@Override public float[] getTangents() { return tangents; }
			@Override public int[] getIndices() { return indices; }
			@Override public Material[] getMaterials() { return null; }
			@Override public AABB getAABB() { return modelAABB; }
			@Override public QuickHull getHull() { return null; }
		};

		//
		FlounderLogger.log("Vertices = " + (vertices.length / 3) + ", Indices = " + indices.length);

		//
		this.model = Model.newModel(manual).create();
		new ComponentModel(chunk, model, 2.0f, Tile.TILE_GRASS.getTexture(), 0);
		//	new ComponentCollider(chunk);
		//	new ComponentCollision(chunk);
	}

	public Chunk getChunk() {
		return chunk;
	}

	public Model getModel() {
		return model;
	}

	public AABB getAABB() {
		if (aabb == null && model != null && model.getMeshData() != null) {
			this.aabb = new AABB();
			AABB.recalculate(model.getMeshData().getAABB(), new Vector3f(), new Vector3f(), 2.0f, aabb);
		}

		return aabb;
	}

	public void delete() {
		model.delete();
	}
}
