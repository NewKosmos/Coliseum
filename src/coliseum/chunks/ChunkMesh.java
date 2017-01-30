package coliseum.chunks;

import coliseum.entities.components.*;
import flounder.entities.*;
import flounder.framework.*;
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
		this.aabb = new AABB();
	}

	protected void rebuildMesh() {
		//
		List<Float> verticesList = new ArrayList<>();
		List<Float> textureCoordsList = new ArrayList<>();
		List<Float> normalsList = new ArrayList<>();
		List<Float> tangentsList = new ArrayList<>();
		List<Integer> indicesList = new ArrayList<>();

		//
		int iterativeSize = 0;

		for (Tile tile : chunk.getTiles()) {
			Model model = tile.getModel();
			int currentPosID = 0;
			int maxIndex = 0;

			for (int i = 0; i < model.getMeshData().getVertices().length; i++) {
				float vertexOffset = (currentPosID == 0) ? tile.getPosition().x : (currentPosID == 1) ? tile.getPosition().y : tile.getPosition().z;
				currentPosID = (currentPosID == 2) ? 0 : currentPosID + 1;
				verticesList.add(model.getMeshData().getVertices()[i] + (vertexOffset / 2.0f));
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

			iterativeSize += maxIndex + 1;
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
				return aabb;
			}

			@Override
			public QuickHull getHull() {
				return null;
			}
		};

		this.model = Model.newModel(manual).create();


		Entity e = new Entity(FlounderEntities.getEntities(), new Vector3f(), new Vector3f());
		new ComponentModel(e, model, 2.0f, chunk.getTiles().get(0).getTexture(), 0);
		//	new ComponentCollider(e);
		//	new ComponentCollision(e);
	}

	protected void rebuildAABB() {
		// TODO: Make AABB from mesh!
		/*for (Tile tile : chunk.getTiles()) {
			AABB aabb = (AABB) tile.getBounding();

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
		}*/
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
