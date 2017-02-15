package shaders;

import flounder.factory.*;
import flounder.processing.*;

/**
 * Class that represents a loaded model.
 */
public class ShaderObject extends FactoryObject {
	private String name;
	private boolean loaded;

	private int shaderID;

	public ShaderObject() {
		super();
		this.name = null;
		this.loaded = false;
	}

	protected void loadData(String name) {
		this.name = name;
		this.loaded = true;
	}

	protected void loadGL(int shaderID) {
		this.shaderID = shaderID;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	public int getShaderID() {
		return shaderID;
	}

	/**
	 * Deletes the model from OpenGL memory.
	 */
	public void delete() {
		if (loaded) {
			FlounderProcessors.sendRequest(new ShaderDeleteRequest(shaderID));
			this.loaded = false;
		}
	}
}
