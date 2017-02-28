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
import flounder.logger.*;
import flounder.resources.*;
import flounder.textures.*;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class ComponentSway extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private TextureObject textureSway;

	private MyFile editorPathTexture;

	/**
	 * Creates a new ComponentSway.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentSway(Entity entity) {
		this(entity, null);
	}

	/**
	 * Creates a new ComponentSway.
	 *
	 * @param entity The entity this component is attached to.
	 * @param textureSway
	 */
	public ComponentSway(Entity entity, TextureObject textureSway) {
		super(entity, ID);
		this.textureSway = textureSway;
	}

	@Override
	public void update() {
	}

	public TextureObject getTextureSway() {
		return textureSway;
	}

	public void setTextureSway(TextureObject textureSway) {
		this.textureSway = textureSway;
	}

	@Override
	public void addToPanel(JPanel panel) {
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
	}

	@Override
	public void editorUpdate() {
		if (editorPathTexture != null && (textureSway == null || !textureSway.getFile().getPath().equals(editorPathTexture.getPath()))) {
			if (editorPathTexture.getPath().contains(".png")) {
				textureSway = TextureFactory.newBuilder().setFile(new MyFile(editorPathTexture)).create();
			}

			editorPathTexture = null;
		}
	}

	@Override
	public String[] getSaveParameters(String entityName) {
		if (textureSway != null) {
			try {
				File file = new File("entities/" + entityName + "/" + entityName + "Sway.png");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = textureSway.getFile().getInputStream();
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

		String saveTexture = (textureSway != null) ? ("TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, \"" + entityName + "\", \"" + entityName + "Diffuse.png\")).setNumberOfRows(" + textureSway.getNumberOfRows() + ").create()") : null;

		return new String[]{saveTexture};
	}

	@Override
	public void dispose() {
	}
}
