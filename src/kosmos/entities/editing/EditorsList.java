package kosmos.entities.editing;

import flounder.entities.*;
import flounder.entities.components.*;

public enum EditorsList {
	ANIMATION(new EditorAnimation((Entity) null)),
	COLLIDER(new EditorCollider((Entity) null)),
	COLLISION(new EditorCollision((Entity) null)),
	MODEL(new EditorModel((Entity) null)),
	PARTICLES(new EditorParticleSystem((Entity) null)),
	SWAY(new EditorSway((Entity) null));

	private final IComponentEditor editor;

	EditorsList(IComponentEditor editor) {
		this.editor = editor;
	}

	public IComponentEditor getEditor() {
		return editor;
	}
}
