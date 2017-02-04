package coliseum.entities.components;

import coliseum.world.*;
import flounder.camera.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.maths.vectors.*;
import flounder.physics.*;

public class ComponentCloud extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f startPosition;

	public ComponentCloud(Entity entity) {
		super(entity, ID);
		this.startPosition = new Vector3f(entity.getPosition());
	}

	@Override
	public void update() {
		Vector3f.rotate(startPosition, new Vector3f(0.0f, ColiseumWorld.getSkyCycle().getDayFactor() * 180.0f, 0.0f), getEntity().getPosition());
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {

	}
}
