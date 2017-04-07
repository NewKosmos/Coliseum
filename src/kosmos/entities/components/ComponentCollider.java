package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.physics.*;
import flounder.physics.bounding.*;

import javax.swing.*;
import java.util.*;

public class ComponentCollider extends IComponentEntity implements IComponentEditor {
	private Map<Pair<Collider, Vector3f>, Collider> colliders;

	public ComponentCollider(Entity entity) {
		this(entity, new ArrayList<>());
	}

	public ComponentCollider(Entity entity, List<Pair<Collider, Vector3f>> colliders) {
		super(entity);
		this.colliders = new HashMap<>();
		colliders.forEach((collider -> this.colliders.put(collider, collider.getFirst().clone())));
	}

	@Override
	public void update() {
		if (getEntity().hasMoved()) {
			// Updates each collider and stores the new data in the second pair.
			for (Pair<Collider, Vector3f> pair : colliders.keySet()) {
				Vector3f offset = new Vector3f(pair.getSecond());
				offset.scale(getEntity().getScale());
				Vector3f.rotate(offset, getEntity().getRotation(), offset);
				Vector3f.add(offset, getEntity().getPosition(), offset);
				pair.getFirst().update(offset, new Vector3f(), getEntity().getScale(), colliders.get(pair)); // getEntity().getRotation()
			}
		}

		colliders.keySet().forEach((pair) -> FlounderBounding.addShapeRender(colliders.get(pair)));
	}

	public void addCollider(Collider collider, Vector3f position) {
		this.colliders.put(new Pair<>(collider, position), collider.clone());
		getEntity().setMoved();
	}

	//public void removeCollider(Collider collider) {
	//	colliders.removeIf((object) -> object.getFirst().equals(collider));
	//	getEntity().setMoved();
	//}

	protected Map<Pair<Collider, Vector3f>, Collider> getColliders() {
		return colliders;
	}

	protected boolean isEmpty() {
		return colliders.isEmpty();
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
