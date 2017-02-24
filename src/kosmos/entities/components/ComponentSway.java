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
import flounder.physics.*;
import flounder.textures.*;

public class ComponentSway extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private TextureObject textureSway;

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
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {
	}
}
