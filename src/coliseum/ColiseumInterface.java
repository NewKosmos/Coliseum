package coliseum;

import flounder.devices.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.standard.*;

public class ColiseumInterface extends IStandard {
	public ColiseumInterface() {
		super(FlounderDisplay.class, FlounderKeyboard.class);
	}

	@Override
	public void init() {
		FlounderBounding.toggle(Coliseum.configMain.getBooleanWithDefault("boundings_render", false, FlounderBounding::renders));
		FlounderProfiler.toggle(Coliseum.configMain.getBooleanWithDefault("profiler_open", false, FlounderProfiler::isOpen));
	}

	@Override
	public void update() {

	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {
		Coliseum.closeConfigs();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
