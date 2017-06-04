package kosmos.post;

import flounder.framework.*;
import kosmos.*;

public class KosmosPost extends Module {
	private boolean effectsEnabled;

	private boolean bloomEnabled;
	private boolean motionBlurEnabled;
	private boolean tiltShiftEnabled;
	private boolean lensFlareEnabled;
	private boolean crtEnabled;
	private boolean grainEnabled;

	public KosmosPost() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.effectsEnabled = KosmosConfigs.POST_EFFECTS_ENABLED.getBoolean();

		this.bloomEnabled = KosmosConfigs.POST_BLOOM_ENABLED.getBoolean();
		this.motionBlurEnabled = KosmosConfigs.POST_MOTIONBLUR_ENABLED.getBoolean();
		this.tiltShiftEnabled = KosmosConfigs.POST_TILTSHIFT_ENABLED.getBoolean();
		this.lensFlareEnabled = KosmosConfigs.POST_LENSFLARE_ENABLED.getBoolean();
		this.crtEnabled = KosmosConfigs.POST_CRT_ENABLED.getBoolean();
		this.grainEnabled = KosmosConfigs.POST_GRAIN_ENABLED.getBoolean();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	public boolean isEffectsEnabled() {
		return this.effectsEnabled;
	}

	public void setEffectsEnabled(boolean effectsEnabled) {
		this.effectsEnabled = effectsEnabled;
	}

	public boolean isBloomEnabled() {
		return this.bloomEnabled;
	}

	public void setBloomEnabled(boolean bloomEnabled) {
		this.bloomEnabled = bloomEnabled;
	}

	public boolean isMotionBlurEnabled() {
		return this.motionBlurEnabled;
	}

	public void setMotionBlurEnabled(boolean motionBlurEnabled) {
		this.motionBlurEnabled = motionBlurEnabled;
	}

	public boolean isTiltShiftEnabled() {
		return this.tiltShiftEnabled;
	}

	public void setTiltShiftEnabled(boolean tiltShiftEnabled) {
		this.tiltShiftEnabled = tiltShiftEnabled;
	}

	public boolean isLensFlareEnabled() {
		return this.lensFlareEnabled;
	}

	public void setLensFlareEnabled(boolean lensFlareEnabled) {
		this.lensFlareEnabled = lensFlareEnabled;
	}

	public boolean isCrtEnabled() {
		return this.crtEnabled;
	}

	public void setCrtEnabled(boolean crtEnabled) {
		this.crtEnabled = crtEnabled;
	}

	public boolean isGrainEnabled() {
		return this.grainEnabled;
	}

	public void setGrainEnabled(boolean grainEnabled) {
		this.grainEnabled = grainEnabled;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@Module.Instance
	public static KosmosPost get() {
		return (KosmosPost) Framework.get().getInstance(KosmosPost.class);
	}
}
