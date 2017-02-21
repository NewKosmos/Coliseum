package testing;

import testing.entities.*;
import flounder.entities.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.standards.*;
import flounder.textures.*;

/**
 * Created by matthew on 21/02/17.
 */
public class TestingInterface extends Standard {
	public TestingInterface() {
		super(FlounderEntities.class, FlounderTextures.class, FlounderModels.class);
	}

	@Override
	public void init() {
		new InstanceTree3(FlounderEntities.getEntities(), new Vector3f(), new Vector3f());
	}

	@Override
	public void update() {

	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
