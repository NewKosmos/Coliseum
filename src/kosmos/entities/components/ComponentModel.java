/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.entities.template.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.resources.*;
import flounder.textures.*;

/**
 * Creates a model with a texture that can be rendered into the world.
 */
public class ComponentModel extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private ModelObject model;
	private float scale;
	private Matrix4f modelMatrix;

	private TextureObject texture;
	private int textureIndex;

	private boolean ignoreShadows;
	private boolean ignoreFog;

	/**
	 * Creates a new ComponentModel.
	 *
	 * @param entity The entity this component is attached to.
	 * @param model The model that will be attached to this entity.
	 * @param scale The scale of the entity.
	 * @param texture The diffuse texture for the entity.
	 * @param textureIndex What texture index this entity should renderObjects from (0 default).
	 */
	public ComponentModel(Entity entity, ModelObject model, float scale, TextureObject texture, int textureIndex) {
		super(entity, ID);
		this.model = model;
		this.scale = scale;
		this.modelMatrix = new Matrix4f();

		this.texture = texture;
		this.textureIndex = textureIndex;

		this.ignoreShadows = false;
		this.ignoreFog = false;
	}

	/**
	 * Creates a new ComponentModel. From strings loaded from entity files.
	 *
	 * @param entity The entity this component is attached to.
	 * @param template The entity template to load data from.
	 */
	public ComponentModel(Entity entity, EntityTemplate template) {
		super(entity, ID);

		this.model = ModelFactory.newBuilder().setManual(new ModelLoadManual(template.getEntityName()) {
			@Override
			public float[] getVertices() {
				return EntityTemplate.toFloatArray(template.getSectionData(ComponentModel.this, "Vertices"));
			}

			@Override
			public float[] getTextureCoords() {
				return EntityTemplate.toFloatArray(template.getSectionData(ComponentModel.this, "TextureCoords"));
			}

			@Override
			public float[] getNormals() {
				return EntityTemplate.toFloatArray(template.getSectionData(ComponentModel.this, "Normals"));
			}

			@Override
			public float[] getTangents() {
				return EntityTemplate.toFloatArray(template.getSectionData(ComponentModel.this, "Tangents"));
			}

			@Override
			public int[] getIndices() {
				return EntityTemplate.toIntArray(template.getSectionData(ComponentModel.this, "Indices"));
			}

			@Override
			public boolean isSmoothShading() {
				return false;
			}

			@Override
			public AABB getAABB() {
				return null; // TODO: Load AABB.
			}

			@Override
			public QuickHull getHull() {
				return null; // TODO: Load hull.
			}
		}).create();

		this.scale = Float.parseFloat(template.getValue(this, "Scale"));
		this.modelMatrix = new Matrix4f();

		if (!template.getValue(this, "Texture").equals("null")) {
			this.texture = TextureFactory.newBuilder().setFile(new MyFile(template.getValue(this, "Texture"))).setNumberOfRows(Integer.parseInt(template.getValue(this, "TextureNumRows"))).create();
		}
	}

	@Override
	public void update() {
		if (getEntity().hasMoved()) {
			Matrix4f.transformationMatrix(super.getEntity().getPosition(), super.getEntity().getRotation(), scale, modelMatrix);
		}
	}

	public ModelObject getModel() {
		return model;
	}

	public void setModel(ModelObject model) {
		this.model = model;
		getEntity().setMoved();
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
		getEntity().setMoved();
	}

	/**
	 * Gets the entitys model matrix.
	 *
	 * @return The entitys model matrix.
	 */
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public TextureObject getTexture() {
		return texture;
	}

	public void setTexture(TextureObject texture) {
		this.texture = texture;
	}

	/**
	 * Gets the textures coordinate offset that is used in rendering the model.
	 *
	 * @return The coordinate offset used in rendering.
	 */
	public Vector2f getTextureOffset() {
		int column = textureIndex % texture.getNumberOfRows();
		int row = textureIndex / texture.getNumberOfRows();
		return new Vector2f((float) row / (float) texture.getNumberOfRows(), (float) column / (float) texture.getNumberOfRows());
	}

	public boolean isIgnoringShadows() {
		return ignoreShadows;
	}

	public void setIgnoreShadows(boolean ignoreShadows) {
		this.ignoreShadows = ignoreShadows;
	}

	public boolean isIgnoringFog() {
		return ignoreFog;
	}

	public void setIgnoreFog(boolean ignoreFog) {
		this.ignoreFog = ignoreFog;
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {
	}
}
