package shaders;

import flounder.factory.*;
import flounder.resources.*;

public class ShaderBuilder extends FactoryBuilder {
	private MyFile file;

	public ShaderBuilder(Factory factory) {
		super(factory);
		this.file = null;
	}

	/**
	 * Sets the shaders source file.
	 *
	 * @param file The source file.
	 *
	 * @return this.
	 */
	public ShaderBuilder setFile(MyFile file) {
		this.file = file;
		return this;
	}

	/**
	 * Gets the source file.
	 *
	 * @return The source file.
	 */
	public MyFile getFile() {
		return file;
	}

	@Override
	public ShaderObject create() {
		if (file != null) {
			return (ShaderObject) builderCreate(file.getName());
		}

		return null;
	}

	@Override
	public String toString() {
		return "ShaderBuilder{" +
				"file=" + file +
				'}';
	}
}
