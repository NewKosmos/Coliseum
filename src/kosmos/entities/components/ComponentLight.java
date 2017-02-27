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
import flounder.lights.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;

import javax.swing.*;

public class ComponentLight extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f offset;
	private Light light;

	/**
	 * Creates a new ComponentLight.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentLight(Entity entity) {
		this(entity, new Vector3f(), new Colour(1.0f, 1.0f, 1.0f), new Attenuation(1.0f, 0.0f, 0.0f));
	}

	/**
	 * Creates a new ComponentLight.
	 *
	 * @param entity The entity this component is attached to.
	 * @param offset
	 * @param colour
	 * @param attenuation
	 */
	public ComponentLight(Entity entity, Vector3f offset, Colour colour, Attenuation attenuation) {
		super(entity, ID);
		this.offset = offset;
		this.light = new Light(colour, Vector3f.add(entity.getPosition(), offset, null), attenuation);
	}

	@Override
	public void update() {
		Vector3f.add(super.getEntity().getPosition(), offset, light.getPosition());
		Vector3f.add(super.getEntity().getPosition(), offset, light.getBounding().getPosition());
		FlounderBounding.addShapeRender(light.getBounding());
	}

	public Light getLight() {
		return light;
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], EntitySaverFunction[]> getSavableValues(String entityName) {
		return new Pair<>(new String[]{}, new EntitySaverFunction[]{});
	}

	@Override
	public void dispose() {
	}
}
