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

public enum ComponentsList {
	// Flounder Engine.
	ANIMATION(new ComponentAnimation(null)),
	CHILD(new ComponentChild(null)),
	COLLIDER(new ComponentCollider(null)),
	COLLISION(new ComponentCollision(null)),
	GLOW(new ComponentGlow(null)),
	LIGHT(new ComponentLight(null)),
	MODEL(new ComponentModel(null)),
	PARTICLES(new ComponentParticles(null)),
	SURFACE(new ComponentSurface(null)),
	SWAY(new ComponentSway(null)),

	// New Kosmos.
	CELESTIAL(new ComponentCelestial(null)),
	CLOUD(new ComponentCloud(null)),
	MULTIPLAYER(new ComponentMultiplayer(null)),
	PLAYER(new ComponentPlayer(null)),
	WATER_BOB(new ComponentWaterBob(null));

	public static final ComponentsList[] LIST = ComponentsList.values();

	private final IComponentEntity component;

	ComponentsList(IComponentEntity component) {
		this.component = component;
	}

	public IComponentEntity getComponent() {
		return component;
	}
}
