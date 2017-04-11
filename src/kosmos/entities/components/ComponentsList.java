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
import flounder.helpers.*;

import javax.swing.*;
import java.util.*;

public enum ComponentsList {
	ANIMATION(new ComponentAnimation(null)),
	CELESTIAL(new ComponentCelestial(null)),
	CLOUD(new ComponentCloud(null)),
	COLLISION(new ComponentCollision(null)),
	GLOW(new ComponentGlow(null)),
	LIGHT(new ComponentLight(null)),
	MODEL(new ComponentModel(null)),
	MULTIPLAYER(new ComponentMultiplayer(null)),
	PARTICLES(new ComponentParticles(null)),
	PLAYER(new ComponentPlayer(null)),
	SURFACE(new ComponentSurface(null)),
	SWAY(new ComponentSway(null)),
	WATER_BOB(new ComponentWaterBob(null));

	public static final List<Pair<String, JPanel>> ADD_SIDE_TAB = new ArrayList<>();
	public static final List<String> REMOVE_SIDE_TAB = new ArrayList<>();

	public static final ComponentsList[] LIST = ComponentsList.values();

	private final IComponentEntity component;

	ComponentsList(IComponentEntity component) {
		this.component = component;
	}

	public IComponentEntity getComponent() {
		return component;
	}
}
