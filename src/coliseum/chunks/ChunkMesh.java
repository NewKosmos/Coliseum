package coliseum.chunks;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.logger.*;
import flounder.materials.*;
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
		this.aabb = new AABB();
	}

	protected void rebuildMesh() {
		/*List<Model> tileModels = new ArrayList<>();

		for (Entity tile : chunk.getTiles()) {
			ComponentModel componentModel = (ComponentModel) tile.getComponent(ComponentModel.ID);

			if (componentModel != null && componentModel.getModel() != null) {
				tileModels.add(componentModel.getModel());
			}
		}

		float[] vertices = null;
		float[] textureCoords = null;
		float[] normals = null;
		float[] tangents = null;
		int[] indices = null;

		ModelBuilder.LoadManual manual = new ModelBuilder.LoadManual() {
			@Override
			public String getModelName() {
				return "chunk" + chunk.getPosition().x + "u" + chunk.getPosition().y;
			}

			@Override
			public float[] getVertices() {
				return vertices;
			}

			@Override
			public float[] getTextureCoords() {
				return textureCoords;
			}

			@Override
			public float[] getNormals() {
				return normals;
			}

			@Override
			public float[] getTangents() {
				return tangents;
			}

			@Override
			public int[] getIndices() {
				return indices;
			}

			@Override
			public Material[] getMaterials() {
				return null;
			}

			@Override
			public AABB getAABB() {
				return aabb;
			}

			@Override
			public QuickHull getHull() {
				return null;
			}
		};

		this.model = Model.newModel(manual).create();*/
	}

	protected void rebuildAABB() {
		// TODO: Make AABB from mesh!
		for (Entity tile : chunk.getTiles()) {
			AABB aabb = (AABB) tile.getBounding();

			FlounderLogger.log(tile);

			if (aabb.getMinExtents().x < this.aabb.getMinExtents().x) {
				this.aabb.getMinExtents().x = aabb.getMinExtents().x;
			} else if (aabb.getMaxExtents().x > this.aabb.getMaxExtents().x) {
				this.aabb.getMaxExtents().x = aabb.getMaxExtents().x;
			}

			if (aabb.getMinExtents().y < this.aabb.getMinExtents().y) {
				this.aabb.getMinExtents().y = aabb.getMinExtents().y;
			} else if (aabb.getMaxExtents().y > this.aabb.getMaxExtents().y) {
				this.aabb.getMaxExtents().y = aabb.getMaxExtents().y;
			}

			if (aabb.getMinExtents().z < this.aabb.getMinExtents().z) {
				this.aabb.getMinExtents().z = aabb.getMinExtents().z;
			} else if (aabb.getMaxExtents().z > this.aabb.getMaxExtents().z) {
				this.aabb.getMaxExtents().z = aabb.getMaxExtents().z;
			}
		}
	}

	public Chunk getChunk() {
		return chunk;
	}

	public Model getModel() {
		return model;
	}

	public AABB getAABB() {
		return aabb;
	}
}
