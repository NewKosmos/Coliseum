package coliseum.entities.components;

import coliseum.world.*;
import flounder.camera.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.maths.vectors.*;
import flounder.physics.*;

public class ComponentCelestial extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f startPosition;
	private Vector3f startRotation;

	public ComponentCelestial(Entity entity) {
		super(entity, ID);
		this.startPosition = new Vector3f(entity.getPosition());
		this.startRotation = new Vector3f(entity.getRotation());
	}

	@Override
	public void update() {
		getEntity().getPosition().set(ColiseumWorld.getSkyCycle().getLightDirection());
		Vector3f.multiply(getEntity().getPosition(), startPosition, getEntity().getPosition());

	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {

	}
}
