package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.physics.*;

import javax.swing.*;
import java.util.*;

public class ComponentCollider extends IComponentEntity implements IComponentEditor {
	private List<Pair<Collider, Collider>> colliders;

	public ComponentCollider(Entity entity) {
		this(entity, new ArrayList<>());
	}

	public ComponentCollider(Entity entity, List<Collider> colliders) {
		super(entity);
		this.colliders = new ArrayList<>();
		colliders.forEach((collider -> this.colliders.add(new Pair<>(collider, collider.clone()))));
	}

	@Override
	public void update() {
		if (getEntity().hasMoved()) {
			// Updates each collider and stores the new data in the second pair.
			for (Pair<Collider, Collider> pair : colliders) {
				pair.getFirst().update(getEntity().getPosition(), getEntity().getRotation(), getEntity().getScale(), pair.getSecond());
			}
		}
	}

	public void addCollider(Collider collider) {
		this.colliders.add(new Pair<>(collider, collider.clone()));
		getEntity().setMoved();
	}

	public void removeCollider(Collider collider) {
		colliders.removeIf((object) -> object.getFirst().equals(collider));
		getEntity().setMoved();
	}

	protected List<Pair<Collider, Collider>> getColliders() {
		return colliders;
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
