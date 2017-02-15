/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package shaders;

import flounder.factory.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.resources.*;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

public class ShaderFactory extends Factory {
	private static final ShaderFactory INSTANCE = new ShaderFactory();

	private ShaderFactory() {
		super("model");
	}

	/**
	 * Gets a new builder to be used to create information for build a object from.
	 *
	 * @return A new factory builder.
	 */
	public static ShaderBuilder newBuilder() {
		return new ShaderBuilder(INSTANCE);
	}

	@Override
	public ShaderObject newObject() {
		return new ShaderObject();
	}

	@Override
	public void loadData(FactoryObject object, FactoryBuilder builder, String name) {
		ShaderBuilder b = (ShaderBuilder) builder;
		ShaderObject o = (ShaderObject) object;

		o.loadData(name);
	}

	@Override
	protected void create(FactoryObject object, FactoryBuilder builder) {
		// Takes OpenGL compatible data and loads it to the GPU and factory object.
		ShaderBuilder b = (ShaderBuilder) builder;
		ShaderObject o = (ShaderObject) object;

		o.loadGL(shaderID);
	}

	@Override
	public Map<String, SoftReference<FactoryObject>> getLoaded() {
		return FlounderShaders.getLoaded();
	}
}
