package kosmos.post;

import flounder.framework.*;
import flounder.profiling.*;
import kosmos.*;

public class KosmosPost extends Module {
	private static final KosmosPost INSTANCE = new KosmosPost();
	public static final String PROFILE_TAB_NAME = "Kosmos Post";

	private boolean effectsEnabled;

	private boolean bloomEnabled;
	private boolean motionBlurEnabled;
	private boolean lensFlareEnabled;
	private boolean crtEnabled;

	public KosmosPost() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME);
	}

	@Override
	public void init() {
		this.effectsEnabled = KosmosConfigs.POST_EFFECTS_ENABLED.getBoolean();

		this.bloomEnabled = KosmosConfigs.POST_BLOOM_ENABLED.getBoolean();
		this.motionBlurEnabled = KosmosConfigs.POST_MOTIONBLUR_ENABLED.getBoolean();
		this.lensFlareEnabled = KosmosConfigs.POST_LENSFLARE_ENABLED.getBoolean();
		this.crtEnabled = KosmosConfigs.POST_CRT_ENABLED.getBoolean();
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Effects Enabled", effectsEnabled);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Bloom Enabled", bloomEnabled);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Motion Blur Enabled", motionBlurEnabled);
		FlounderProfiler.add(PROFILE_TAB_NAME, "Lens Flare Enabled", lensFlareEnabled);
		FlounderProfiler.add(PROFILE_TAB_NAME, "CRT Filter Enabled", crtEnabled);
	}

	public static boolean isEffectsEnabled() {
		return INSTANCE.effectsEnabled;
	}

	public static void setEffectsEnabled(boolean effectsEnabled) {
		INSTANCE.effectsEnabled = effectsEnabled;
	}

	public static boolean isBloomEnabled() {
		return INSTANCE.bloomEnabled;
	}

	public static void setBloomEnabled(boolean bloomEnabled) {
		INSTANCE.bloomEnabled = bloomEnabled;
	}

	public static boolean isMotionBlurEnabled() {
		return INSTANCE.motionBlurEnabled;
	}

	public static void setMotionBlurEnabled(boolean motionBlurEnabled) {
		INSTANCE.motionBlurEnabled = motionBlurEnabled;
	}

	public static boolean isLensFlareEnabled() {
		return INSTANCE.lensFlareEnabled;
	}

	public static void setLensFlareEnabled(boolean lensFlareEnabled) {
		INSTANCE.lensFlareEnabled = lensFlareEnabled;
	}

	public static boolean isCrtEnabled() {
		return INSTANCE.crtEnabled;
	}

	public static void setCrtEnabled(boolean crtEnabled) {
		INSTANCE.crtEnabled = crtEnabled;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
