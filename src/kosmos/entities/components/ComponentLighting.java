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
import flounder.physics.*;

/**
 * Creates a set of lighting data for a entity.
 */
public class ComponentLighting extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private float shineDamper;
	private float reflectivity;

	private boolean ignoreShadows;
	private boolean ignoreFog;

	/**
	 * Creates a new ComponentLighting.
	 *
	 * @param entity The entity this component is attached to.
	 * @param shineDamper The rendered objects shine damper when lighted.
	 * @param reflectivity The rendered objects reflectivity when lighted.
	 * @param ignoreShadows If the rendered object will ignore shadows.
	 * @param ignoreFog If the rendered object will ignore fog.
	 */
	public ComponentLighting(Entity entity, float shineDamper, float reflectivity, boolean ignoreShadows, boolean ignoreFog) {
		super(entity, ID);
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;

		this.ignoreShadows = ignoreShadows;
		this.ignoreFog = ignoreFog;
	}

	/**
	 * Creates a new ComponentModel. From strings loaded from entity files.
	 *
	 * @param entity The entity this component is attached to.
	 * @param template The entity template to load data from.
	 */
	public ComponentLighting(Entity entity, EntityTemplate template) {
		super(entity, ID);
	}

	@Override
	public void update() {
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isIgnoreShadows() {
		return ignoreShadows;
	}

	public void setIgnoreShadows(boolean ignoreShadows) {
		this.ignoreShadows = ignoreShadows;
	}

	public boolean isIgnoreFog() {
		return ignoreFog;
	}

	public void setIgnoreFog(boolean ignoreFog) {
		this.ignoreFog = ignoreFog;
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {
	}
}
