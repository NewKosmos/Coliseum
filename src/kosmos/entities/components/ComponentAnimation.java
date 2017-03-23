/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.animation.*;
import flounder.collada.*;
import flounder.collada.animation.*;
import flounder.collada.joints.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.matrices.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Creates a animation used to set animation properties.
 */
public class ComponentAnimation extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private ModelAnimated model;
	private float scale;
	private Matrix4f modelMatrix;

	private TextureObject texture;
	private int textureIndex;

	private Animator animator;

	private MyFile editorPathCollada;
	private MyFile editorPathTexture;

	private boolean wasLoaded;

	/**
	 * Creates a new ComponentAnimation.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentAnimation(Entity entity) {
		this(entity, (ModelAnimated) null, 1.0f, null, 1);
	}

	/**
	 * Creates a new ComponentAnimation.
	 *
	 * @param entity The entity this component is attached to.
	 * @param file The animated model file to load from.
	 * @param scale The scale of the entity.
	 * @param texture The diffuse texture for the entity.
	 * @param textureIndex What texture index this entity should renderObjects from (0 default).
	 */
	public ComponentAnimation(Entity entity, MyFile file, float scale, TextureObject texture, int textureIndex) {
		super(entity, ID);
		ModelAnimated modelAnimated = FlounderCollada.loadCollada(file);

		AnimationData animationData = FlounderCollada.loadAnimation(file);
		Animation animation = FlounderAnimation.loadAnimation(animationData);

		this.model = modelAnimated;
		this.scale = scale;
		this.modelMatrix = new Matrix4f();

		this.texture = texture;
		this.textureIndex = textureIndex;

		this.wasLoaded = false;

		if (model != null) {
			model.getHeadJoint().calculateInverseBindTransform(Matrix4f.rotate(new Matrix4f(), new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(-90.0f), null));
			this.animator = new Animator(model.getHeadJoint());
		}

		doAnimation(animation);
	}

	/**
	 * Creates a new ComponentAnimation.
	 *
	 * @param entity The entity this component is attached to.
	 * @param model The animated model to use when animating and rendering.
	 * @param scale The scale of the entity.
	 * @param texture The diffuse texture for the entity.
	 * @param textureIndex What texture index this entity should renderObjects from (0 default).
	 */
	public ComponentAnimation(Entity entity, ModelAnimated model, float scale, TextureObject texture, int textureIndex) {
		super(entity, ID);
		this.model = model;
		this.scale = scale;
		this.modelMatrix = new Matrix4f();

		this.texture = texture;
		this.textureIndex = textureIndex;

		if (model != null) {
			model.getHeadJoint().calculateInverseBindTransform(Matrix4f.rotate(new Matrix4f(), new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(-90.0f), null));
			this.animator = new Animator(model.getHeadJoint());
		}
	}

	/**
	 * Adds children from a JointData/Children map.
	 *
	 * @param datasJoint The joint to add children too.
	 * @param dataChild The children to be searching for.
	 * @param allJoints The joint map to match the child's name with, and to get the JointData from.
	 */
	private void addChildren(JointData datasJoint, List<String> dataChild, Map<JointData, List<String>> allJoints) {
		for (JointData data : allJoints.keySet()) {
			if (dataChild.contains(data.getNameId())) {
				datasJoint.addChild(data);
			}
		}

		for (JointData child : datasJoint.getChildren()) {
			addChildren(child, allJoints.get(child), allJoints);
		}
	}

	@Override
	public void update() {
		if (model != null && model.isLoaded() != wasLoaded) {
			getEntity().setMoved();
			wasLoaded = model.isLoaded();
		}

		if (animator != null) {
			animator.update();
		}

		if (getEntity().hasMoved()) {
			Matrix4f.transformationMatrix(super.getEntity().getPosition(), super.getEntity().getRotation(), scale, modelMatrix);
		}
	}

	/**
	 * Instructs this entity to carry out a given animation.
	 *
	 * @param animation The animation to be carried out.
	 */
	public void doAnimation(Animation animation) {
		animator.doAnimation(animation);
	}

	/**
	 * Gets the scale for this model.
	 *
	 * @return The scale for this model.
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Sets the scale for this model.
	 *
	 * @param scale The new scale.
	 */
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

	/**
	 * Gets the animated model for this entity.
	 *
	 * @return The animated model for this entity.
	 */
	public ModelAnimated getModel() {
		return model;
	}

	public void setModel(ModelAnimated model) {
		if (this.model != model) {
			this.model = model;
			this.model.getHeadJoint().calculateInverseBindTransform(Matrix4f.rotate(new Matrix4f(), new Vector3f(1.0f, 0.0f, 0.0f), (float) Math.toRadians(-90.0f), null));
			this.animator = new Animator(this.model.getHeadJoint());
			getEntity().setMoved();
		}
	}

	/**
	 * Gets an array of the model-space transforms of all the joints (with the current animation pose applied) in the entity.
	 * The joints are ordered in the array based on their joint index.
	 * The position of each joint's transform in the array is equal to the joint's index.
	 *
	 * @return The array of model-space transforms of the joints in the current animation pose.
	 */
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[model.getJointsData().getJointCount()];
		addJointsToArray(model.getHeadJoint(), jointMatrices);
		return jointMatrices;
	}

	/**
	 * This adds the current model-space transform of a joint (and all of its descendants) into an array of transforms.
	 * The joint's transform is added into the array at the position equal to the joint's index.
	 *
	 * @param headJoint The head joint to add children to.
	 * @param jointMatrices The matrices transformation to add with.
	 */
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
	//	if (headJoint.getIndex() >= jointMatrices.length) {
	//		return;
	//	}

		jointMatrices[headJoint.getIndex()] = headJoint.getAnimatedTransform();

		for (Joint childJoint : headJoint.getChildren()) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

	/**
	 * Gets the diffuse texture for this entity.
	 *
	 * @return The diffuse texture for this entity.
	 */
	public TextureObject getTexture() {
		return texture;
	}

	public void setTexture(TextureObject texture) {
		this.texture = texture;
	}

	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
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

	public Animator getAnimator() {
		return animator;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Load Collada.
		JButton loadCollada = new JButton("Select Collada");
		loadCollada.addActionListener((ActionEvent ae) -> {
			JFileChooser fileChooser = new JFileChooser();
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileChooser.setCurrentDirectory(workingDirectory);
			int returnValue = fileChooser.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String selectedFile = fileChooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
				this.editorPathCollada = new MyFile(selectedFile.split("/"));
			}
		});
		panel.add(loadCollada);

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
		if (editorPathCollada != null/*  && (model == null|| !model.getFile().equals(editorPathCollada.getPath()))*/) {
			if (editorPathCollada.getPath().contains(".dae")) {
				ModelAnimated modelAnimated = FlounderCollada.loadCollada(new MyFile(editorPathCollada));
				AnimationData animationData = FlounderCollada.loadAnimation(new MyFile(editorPathCollada));
				Animation animation = FlounderAnimation.loadAnimation(animationData);
				setModel(modelAnimated);
				doAnimation(animation);
			}

			editorPathCollada = null;
		}

		if (editorPathTexture != null && (texture == null || !texture.getFile().getPath().equals(editorPathTexture.getPath()))) {
			if (editorPathTexture.getPath().contains(".png")) {
				this.texture = TextureFactory.newBuilder().setFile(new MyFile(editorPathTexture)).create();
			}

			editorPathTexture = null;
		}
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		if (model != null) {
			try {
				File file = new File("entities/" + entityName + "/" + entityName + ".dae");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = model.getFile().getInputStream();
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

		String saveModel = (model != null) ? ("new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + entityName + ".dae\")") : null;
		String saveTexture = (texture != null) ? ("TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + entityName + "Diffuse.png\")).setNumberOfRows(" + texture.getNumberOfRows() + ").create()") : null;

		String saveScale = scale + "f";

		return new Pair<>(
				new String[]{"private static final MyFile COLLADA = " + saveModel, "private static final TextureObject TEXTURE = " + saveTexture}, // Static variables
				new String[]{saveScale, "COLLADA", "TEXTURE"} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
