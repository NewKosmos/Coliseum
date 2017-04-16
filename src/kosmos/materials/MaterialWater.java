package kosmos.materials;

import flounder.devices.*;
import flounder.resources.*;
import flounder.sounds.*;

public class MaterialWater extends IMaterial {
	private static final Sound SOUND_WALK = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "materials", "walk.wav"), 1.0f, 1.0f); // waterWalk
	private static final Sound SOUND_RUN = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "materials", "run.wav"), 1.0f, 1.0f); // waterRun
	private static final Sound SOUND_PLACE = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "materials", "place.wav"), 1.0f, 1.0f); // waterPlace
	private static final Sound SOUND_HIT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "materials", "hit.wav"), 1.0f, 1.0f); // waterHit
	private static final Sound SOUND_DESTROY = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "materials", "destroy.wav"), 1.0f, 1.0f); // waterDestroy

	public MaterialWater() {
	}

	@Override
	public Sound getSoundWalk() {
		return SOUND_WALK;
	}

	@Override
	public Sound getSoundRun() {
		return SOUND_RUN;
	}

	@Override
	public Sound getSoundPlace() {
		return SOUND_PLACE;
	}

	@Override
	public Sound getSoundHit() {
		return SOUND_HIT;
	}

	@Override
	public Sound getSoundDestroy() {
		return SOUND_DESTROY;
	}
}
