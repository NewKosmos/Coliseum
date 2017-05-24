package kosmos.materials;

import flounder.sounds.*;

public abstract class IMaterial {
	public enum Materials {
		GRASS(new MaterialGrass()), METAL(new MaterialMetal()), SAND(new MaterialSand()), SNOW(new MaterialSnow()), STONE(new MaterialStone()), WATER(new MaterialWater());

		private IMaterial material;

		Materials(IMaterial material) {
			this.material = material;
		}

		public IMaterial getMaterial() {
			return material;
		}
	}

	public abstract Sound getSoundWalk();

	public abstract Sound getSoundRun();

	public abstract Sound getSoundPlace();

	public abstract Sound getSoundHit();

	public abstract Sound getSoundDestroy();
}
