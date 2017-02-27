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
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.textures.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;

/**
 * Creates a model with a texture that can be rendered into the world.
 */
public class ComponentModel extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private ModelObject model;
	private float scale;
	private Matrix4f modelMatrix;

	private TextureObject texture;
	private int textureIndex;

	private MyFile editorPathModel;
	private MyFile editorPathTexture;

	/**
	 * Creates a new ComponentModel.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentModel(Entity entity) {
		this(entity, null, 1.0f, TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "undefined.png")).create(), 0);
	}

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

	@Override
	public void addToPanel(JPanel panel) {
		// Load Model.
		JButton loadModel = new JButton("Select Model");
		loadModel.addActionListener((ActionEvent ae) -> {
			JFileChooser fileChooser = new JFileChooser();
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileChooser.setCurrentDirectory(workingDirectory);
			int returnValue = fileChooser.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String selectedFile = fileChooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
				this.editorPathModel = new MyFile(selectedFile.split("/"));
			}
		});
		panel.add(loadModel);

		// Load Texture.
		JButton loadTexture = new JButton("Select Texture");
		loadTexture.addActionListener((ActionEvent ae) -> {
			JFileChooser fileChooser = new JFileChooser();
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileChooser.setCurrentDirectory(workingDirectory);
			int returnValue = fileChooser.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String selectedFile = fileChooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
				this.editorPathTexture = new MyFile(selectedFile.split("/"));
			}
		});
		panel.add(loadTexture);

		// Scale Slider.
		//	panel.add(new JLabel("Scale Slider: "));
		JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL, 0, 150, (int) (scale * 25.0f));
		scaleSlider.setToolTipText("Model Scale");
		scaleSlider.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			this.scale = reading / 25.0f;
		});
		scaleSlider.setMajorTickSpacing(25);
		scaleSlider.setMinorTickSpacing(10);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setPaintLabels(true);
		panel.add(scaleSlider);
	}

	@Override
	public void editorUpdate() {
		if (editorPathModel != null && (model == null || !model.getName().equals(editorPathModel.getName()))) {
			if (editorPathModel.getPath().contains(".obj")) {
				this.model = ModelFactory.newBuilder().setFile(new MyFile(editorPathModel)).create();
			}

			editorPathModel = null;
		}

		if (editorPathTexture != null && (texture == null || !texture.getFile().getPath().equals(editorPathTexture.getPath()))) {
			if (editorPathTexture.getPath().contains(".png")) {
				this.texture = TextureFactory.newBuilder().setFile(new MyFile(editorPathTexture)).create();
			}

			editorPathTexture = null;
		}
	}

	@Override
	public Pair<String[], EntitySaverFunction[]> getSavableValues(String entityName) {
		if (texture != null) {
			try {
				File file = new File("entities/" + entityName + "/" + entityName + "Diffuse.png");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = texture.getFile().getInputStream();
				OutputStream output = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int bytesRead;

				while ((bytesRead = input.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
				}

				input.close();
				output.close();
			} catch (IOException e) {
				FlounderLogger.exception(e);
			}
		}


		EntitySaverFunction saveVertices = new EntitySaverFunction("Vertices") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (model != null) {
					for (float v : model.getVertices()) {
						String s = v + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveTextureCoords = new EntitySaverFunction("TextureCoords") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (model != null) {
					for (float v : model.getTextures()) {
						String s = v + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveNormals = new EntitySaverFunction("Normals") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (model != null) {
					for (float v : model.getNormals()) {
						String s = v + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveTangents = new EntitySaverFunction("Tangents") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (model != null) {
					for (float v : model.getTangents()) {
						String s = v + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveIndices = new EntitySaverFunction("Indices") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (model != null) {
					for (int i : model.getIndices()) {
						String s = i + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveAABB = new EntitySaverFunction("AABB") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (model != null && model.getAABB() != null) {
					Vector3f min = model.getAABB().getMinExtents();
					Vector3f max = model.getAABB().getMaxExtents();
					String s = min.x + "," + min.y + "," + min.z + "," + max.x + "," + max.y + "," + max.z + ",";
					entityFileWriter.writeSegmentData(s);
				}
			}
		};
		EntitySaverFunction saveQuickHull = new EntitySaverFunction("QuickHull") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (model != null && model.getHull() != null && model.getHull().getHullPoints() != null) {
					for (Vector3f v : model.getHull().getHullPoints()) {
						String s = v.x + "," + v.y + "," + v.z + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};

		String saveScale = "Scale: " + scale;

		String saveTexture = "Texture: " + (texture == null ? null : "res/entities/" + entityName + "/" + entityName + "Diffuse.png");
		String saveTextureNumRows = "TextureNumRows: " + (texture == null ? 1 : texture.getNumberOfRows());

		return new Pair<>(
				new String[]{saveScale, saveTexture, saveTextureNumRows},
				new EntitySaverFunction[]{saveVertices, saveTextureCoords, saveNormals, saveTangents, saveIndices, saveAABB, saveQuickHull}
		);
	}

	@Override
	public void dispose() {
	}
}
