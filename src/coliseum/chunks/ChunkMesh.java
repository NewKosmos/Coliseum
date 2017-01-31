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
		AABB modelAABB = new AABB();
		List<Float> verticesList = new ArrayList<>();
		List<Float> textureCoordsList = new ArrayList<>();
		List<Float> normalsList = new ArrayList<>();
		List<Float> tangentsList = new ArrayList<>();
		List<Integer> indicesList = new ArrayList<>();

		//
		int iterativeSize = 0;

		for (Tile tile : chunk.getTiles().keySet()) {
			for (Vector3f vector : chunk.getTiles().get(tile)) {
				Model model = tile.getModel();
				int currentPosID = 0;
				int maxIndex = 0;

				if (model == null || model.getMeshData() == null) {
					return;
				}

				//
				for (int i = 0; i < model.getMeshData().getVertices().length; i++) {
					float vertexOffset = (currentPosID == 0) ? vector.x : (currentPosID == 1) ? vector.y : vector.z;
					float vertex = model.getMeshData().getVertices()[i] + (vertexOffset / 2.0f);
					verticesList.add(vertex);

					switch (currentPosID) {
						case (0):
							if (vertex > modelAABB.getMaxExtents().x) {
								modelAABB.getMaxExtents().x = vertex;
							} else if (vertex < modelAABB.getMinExtents().x) {
								modelAABB.getMinExtents().x = vertex;
							}
							break;
						case (1):
							if (vertex > modelAABB.getMaxExtents().y) {
								modelAABB.getMaxExtents().y = vertex;
							} else if (vertex < modelAABB.getMinExtents().y) {
								modelAABB.getMinExtents().y = vertex;
							}
							break;
						case (2):
							if (vertex > modelAABB.getMaxExtents().z) {
								modelAABB.getMaxExtents().z = vertex;
							} else if (vertex < modelAABB.getMinExtents().z) {
								modelAABB.getMinExtents().z = vertex;
							}
							break;
					}

					currentPosID = (currentPosID == 2) ? 0 : currentPosID + 1;
				}

				for (int i = 0; i < model.getMeshData().getTextures().length; i++) {
					textureCoordsList.add(model.getMeshData().getTextures()[i]);
				}

				for (int i = 0; i < model.getMeshData().getNormals().length; i++) {
					normalsList.add(model.getMeshData().getNormals()[i]);
				}

				for (int i = 0; i < model.getMeshData().getTangents().length; i++) {
					tangentsList.add(model.getMeshData().getTangents()[i]);
				}

				for (int i = 0; i < model.getMeshData().getIndices().length; i++) {
					if (maxIndex < model.getMeshData().getIndices()[i]) {
						maxIndex = model.getMeshData().getIndices()[i];
					}

					indicesList.add(model.getMeshData().getIndices()[i] + iterativeSize);
				}

				//
				iterativeSize += maxIndex + 1;
			}
		}

		//
		float[] vertices = new float[verticesList.size()];
		float[] textureCoords = new float[textureCoordsList.size()];
		float[] normals = new float[normalsList.size()];
		float[] tangents = new float[tangentsList.size()];
		int[] indices = new int[indicesList.size()];

		//
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = verticesList.get(i);
		}

		for (int i = 0; i < textureCoords.length; i++) {
			textureCoords[i] = textureCoordsList.get(i);
		}

		for (int i = 0; i < normals.length; i++) {
			normals[i] = normalsList.get(i);
		}

		for (int i = 0; i < tangents.length; i++) {
			tangents[i] = tangentsList.get(i);
		}

		for (int i = 0; i < indices.length; i++) {
			indices[i] = indicesList.get(i);
		}

		//
		ModelBuilder.LoadManual manual = new ModelBuilder.LoadManual() {
			@Override
			public String getModelName() {
				return "chunk" + chunk.getPosition().x + "u" + chunk.getPosition().y + FlounderFramework.getTimeSec();
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
				return modelAABB;
			}

			@Override
			public QuickHull getHull() {
				return null;
			}
		};

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
