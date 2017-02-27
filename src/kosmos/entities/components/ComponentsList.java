package kosmos.entities.components;

import flounder.entities.components.*;

public enum ComponentsList {
	ANIMATION(new ComponentAnimation(null)),
	CELESTIAL(new ComponentCelestial(null)),
	CLOUD(new ComponentCloud(null)),
	COLLIDER(new ComponentCollider(null)),
	COLLISION(new ComponentCollision(null)),
	LIGHT(new ComponentLight(null)),
	MODEL(new ComponentModel(null)),
	MULTIPLAYER(new ComponentMultiplayer(null)),
	PARTICLES(new ComponentParticles(null)),
	PLAYER(new ComponentPlayer(null)),
	SURFACE(new ComponentSurface(null)),
	SWAY(new ComponentSway(null));

	public static final ComponentsList[] LIST = ComponentsList.values();

	private final IComponentEntity component;

	ComponentsList(IComponentEntity component) {
		this.component = component;
	}

	public IComponentEntity getComponent() {
		return component;
	}
}
