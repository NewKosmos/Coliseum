package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.physics.*;

import javax.swing.*;

public class ComponentCollider extends IComponentEntity implements IComponentEditor {
	private ConvexHull convexHull;

	public ComponentCollider(Entity entity) {
		this(entity, new ConvexHull());
	}

	public ComponentCollider(Entity entity, ConvexHull convexHull) {
		super(entity);
		this.convexHull = convexHull;
	}

	@Override
	public void update() {
		if (getEntity().hasMoved() && convexHull != null) {
			convexHull.update(getEntity().getPosition(), getEntity().getRotation(), getEntity().getScale(), convexHull);
		}
	}

	public ConvexHull getConvexHull() {
		return convexHull;
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
