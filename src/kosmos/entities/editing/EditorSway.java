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

public class EditorSway extends IComponentEditor {
	public ComponentSway component;

	private MyFile pathTexture;

	public EditorSway(Entity entity) {
		this.component = new ComponentSway(entity, null);
	}

	public EditorSway(IComponentEntity component) {
		this.component = (ComponentSway) component;
	}

	@Override
	public String getTabName() {
		return "Sway";
	}

	@Override
	public ComponentSway getComponent() {
		return component;
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
				EditorSway.this.pathTexture = new MyFile(selectedFile.split("/"));
			}
		});
		panel.add(loadTexture);
	}

	@Override
	public void update() {
		if (component != null) {
			if (pathTexture != null && (component.getTextureSway() == null || !component.getTextureSway().getFile().getPath().equals(pathTexture.getPath()))) {
				if (pathTexture.getPath().contains(".png")) {
					TextureObject texture = TextureFactory.newBuilder().setFile(new MyFile(pathTexture)).create();
					component.setTextureSway(texture);
				}

				pathTexture = null;
			}
		}
	}

	@Override
	public Pair<String[], EntitySaverFunction[]> getSavableValues(String entityName) {
		if (component.getTextureSway() != null) {
			try {
				File file = new File("entities/" + entityName + "/" + entityName + "Sway.png");

				if (file.exists()) {
					file.delete();
				}

				file.createNewFile();

				InputStream input = component.getTextureSway().getFile().getInputStream();
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

		String saveTexture = "Texture: " + (component.getTextureSway() == null ? null : "res/entities/" + entityName + "/" + entityName + "Sway.png");
		String saveTextureNumRows = "TextureNumRows: " + (component.getTextureSway() == null ? 1 : component.getTextureSway().getNumberOfRows());

		return new Pair<>(
				new String[]{saveTexture, saveTextureNumRows},
				new EntitySaverFunction[]{}
		);
	}
}
