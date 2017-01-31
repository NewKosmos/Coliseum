package coliseum.entities.components;

import flounder.camera.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.maths.vectors.*;
import flounder.physics.*;

public class ComponentPlayer extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f startPosition;
	private Vector3f startRotation;

	public ComponentPlayer(Entity entity) {
		super(entity, ID);
		this.startPosition = new Vector3f(entity.getPosition());
		this.startRotation = new Vector3f(entity.getRotation());
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() == null) {
			return;
		}

		getEntity().getPosition().set(FlounderCamera.getPlayer().getPosition());
		Vector3f.add(getEntity().getPosition(), startPosition, getEntity().getPosition());

		getEntity().getRotation().set(FlounderCamera.getPlayer().getRotation());
		Vector3f.add(getEntity().getRotation(), startRotation, getEntity().getRotation());
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {

	}
}
