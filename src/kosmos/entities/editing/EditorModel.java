package kosmos.entities.editing;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.entities.template.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.entities.components.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;

public class EditorModel extends IComponentEditor {
	public ComponentModel component;

	private MyFile pathModel;
	private MyFile pathTexture;

	public EditorModel(Entity entity) {
		this.component = new ComponentModel(entity, null, 1.0f, TextureFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "undefined.png")).create(), 0);
	}

	public EditorModel(IComponentEntity component) {
		this.component = (ComponentModel) component;
	}

	@Override
	public String getTabName() {
		return "Model";
	}

	@Override
	public ComponentModel getComponent() {
		return component;
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
				EditorModel.this.pathModel = new MyFile(selectedFile.split("/"));
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
				EditorModel.this.pathTexture = new MyFile(selectedFile.split("/"));
			}
		});
		panel.add(loadTexture);

		// Scale Slider.
		//	panel.add(new JLabel("Scale Slider: "));
		JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL, 0, 150, (int) (component.getScale() * 25.0f));
		scaleSlider.setToolTipText("Model Scale");
		scaleSlider.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			component.setScale(reading / 25.0f);
		});
		scaleSlider.setMajorTickSpacing(25);
		scaleSlider.setMinorTickSpacing(10);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setPaintLabels(true);
		panel.add(scaleSlider);
	}

	@Override
	public void update() {
		if (component != null) {
			if (pathModel != null && (component.getModel() == null || !component.getModel().getName().equals(pathModel.getName()))) {
				if (pathModel.getPath().contains(".obj")) {
					ModelObject model = ModelFactory.newBuilder().setFile(pathModel).create();
					component.setModel(model);
				}

				pathModel = null;
			}

			if (pathTexture != null && (component.getTexture() == null || !component.getTexture().getFile().getPath().equals(pathTexture.getPath()))) {
				if (pathTexture.getPath().contains(".png")) {
					TextureObject texture = TextureFactory.newBuilder().setFile(pathTexture).create();
					component.setTexture(texture);
				}

				pathTexture = null;
			}
		}
	}

	@Override
	public Pair<String[], EntitySaverFunction[]> getSavableValues(String entityName) {
		if (component.getTexture() != null) {
			try {
				File file = new File("entities/" + entityName + "/" + entityName + "Diffuse.png");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = component.getTexture().getFile().getInputStream();
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
				if (component.getModel() != null) {
					for (float v : component.getModel().getVertices()) {
						String s = v + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveTextureCoords = new EntitySaverFunction("TextureCoords") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (component.getModel() != null) {
					for (float v : component.getModel().getTextures()) {
						String s = v + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveNormals = new EntitySaverFunction("Normals") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (component.getModel() != null) {
					for (float v : component.getModel().getNormals()) {
						String s = v + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveTangents = new EntitySaverFunction("Tangents") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (component.getModel() != null) {
					for (float v : component.getModel().getTangents()) {
						String s = v + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveIndices = new EntitySaverFunction("Indices") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (component.getModel() != null) {
					for (int i : component.getModel().getIndices()) {
						String s = i + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};
		EntitySaverFunction saveAABB = new EntitySaverFunction("AABB") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (component.getModel() != null && component.getModel() != null && component.getModel().getAABB() != null) {
					Vector3f min = component.getModel().getAABB().getMinExtents();
					Vector3f max = component.getModel().getAABB().getMaxExtents();
					String s = min.x + "," + min.y + "," + min.z + "," + max.x + "," + max.y + "," + max.z + ",";
					entityFileWriter.writeSegmentData(s);
				}
			}
		};
		EntitySaverFunction saveQuickHull = new EntitySaverFunction("QuickHull") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (component.getModel() != null && component.getModel() != null && component.getModel().getHull() != null && component.getModel().getHull().getHullPoints() != null) {
					for (Vector3f v : component.getModel().getHull().getHullPoints()) {
						String s = v.x + "," + v.y + "," + v.z + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};

		String saveScale = "Scale: " + component.getScale();

		String saveTexture = "Texture: " + (component.getTexture() == null ? null : "res/entities/" + entityName + "/" + entityName + "Diffuse.png");
		String saveTextureNumRows = "TextureNumRows: " + (component.getTexture() == null ? 1 : component.getTexture().getNumberOfRows());

		return new Pair<>(
				new String[]{saveScale, saveTexture, saveTextureNumRows},
				new EntitySaverFunction[]{saveVertices, saveTextureCoords, saveNormals, saveTangents, saveIndices, saveAABB, saveQuickHull}
		);
	}
}
