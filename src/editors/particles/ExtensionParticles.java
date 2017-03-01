package editors.particles;

import editors.editor.*;
import flounder.camera.*;
import flounder.devices.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;
import kosmos.particles.spawns.*;
import kosmos.world.*;

import java.util.*;

public class ExtensionParticles extends IEditorType {
	private static boolean ACTIVE = false;

	public ParticleTemplate particleTemplate;
	public ParticleSystem particleSystem;
	public String loadFromParticle;

	public ExtensionParticles() {
		super(FlounderMouse.class, KosmosParticles.class, KosmosWorld.class);
		ACTIVE = true;
	}

	@Override
	public void init() {
		// Sets the engine up for the editor.
		// FlounderProfiler.toggle(true);
		FlounderMouse.setCursorHidden(false);
		OpenGlUtils.goWireframe(false);

		// Sets the world to constant fog and a sun.
		//	KosmosWorld.addFog(new Fog(new Colour(1.0f, 1.0f, 1.0f), 0.003f, 2.0f, 0.0f, 50.0f));
		//	KosmosWorld.addSun(new Light(new Colour(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 2000.0f, 2000.0f)));

		List<ParticleTemplate> templates = new ArrayList<>();
		templates.add(KosmosParticles.load("rain"));
		particleSystem = new ParticleSystem(templates, new SpawnCircle(20.0f, new Vector3f(0.0f, 1.0f, 0.0f)), 100, 0.5f, 0.5f);
		particleSystem.setSystemCentre(FlounderCamera.getPlayer().getPosition());

		/*// The template to edit.
		particleTemplate = KosmosParticles.load("rain"); // new ParticleTemplate("testing", null, 1.0f, 1.0f);

		// The testing particle system.
		particleSystem = new ParticleSystem(new ArrayList<>(), null, 376.0f, 1.0f, 0.1f);
		particleSystem.addParticleType(particleTemplate);
		particleSystem.randomizeRotation();
		particleSystem.setSpawn(new SpawnSphere(2.0f));
		particleSystem.setSystemCentre(FlounderCamera.getPlayer().getPosition());*/
	}

	@Override
	public void update() {
		if (loadFromParticle != null) {
			ParticleTemplate template = KosmosParticles.load("" + loadFromParticle);
			particleSystem.removeParticleType(particleTemplate);
			particleTemplate = template;
			particleSystem.addParticleType(particleTemplate);

			FrameParticles.nameField.setText(template == null ? "null" : template.getName());
			FrameParticles.rowSlider.setValue(template == null ? -1 : (template.getTexture() != null ? template.getTexture().getNumberOfRows() : 0));
			FrameParticles.scaleSlider.setValue((int) (template == null ? -1.0f : template.getScale() * 100.0f));
			FrameParticles.lifeSlider.setValue((int) (template == null ? 0.0f : template.getLifeLength() * 10.0f));

			loadFromParticle = null;
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		ACTIVE = false;
	}

	@Override
	public boolean isActive() {
		return ACTIVE;
	}
}
