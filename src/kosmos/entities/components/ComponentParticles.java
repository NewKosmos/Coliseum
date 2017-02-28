package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.entities.template.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import kosmos.entities.components.particles.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;
import kosmos.particles.spawns.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class ComponentParticles extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private IEditorParticleSpawn[] spawns = new IEditorParticleSpawn[]{
			new EditorParticleCircle(),
			new EditorParticleLine(),
			new EditorParticlePoint(),
			new EditorParticleSphere(),
	};

	private ParticleSystem particleSystem;
	private Vector3f centreOffset;
	private Vector3f lastPosition;

	public IEditorParticleSpawn editorSystemSpawn;

	/**
	 * Creates a new ComponentParticles.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentParticles(Entity entity) {
		this(entity, new ArrayList<>(), null, 100.0f, 1.0f, 1.0f);
	}

	/**
	 * Creates a new ComponentParticles.
	 *
	 * @param entity The entity this component is attached to.
	 * @param types
	 * @param spawn
	 * @param pps
	 * @param speed
	 * @param gravityEffect
	 */
	public ComponentParticles(Entity entity, List<ParticleTemplate> types, IParticleSpawn spawn, float pps, float speed, float gravityEffect) {
		super(entity, ID);
		particleSystem = new ParticleSystem(types, spawn, pps, speed, gravityEffect);
		particleSystem.setSystemCentre(new Vector3f());
		centreOffset = new Vector3f();
		lastPosition = new Vector3f();
	}

	@Override
	public void update() {
		if (particleSystem != null) {
			if (particleSystem.getTypes().isEmpty()) {
				particleSystem.addParticleType(KosmosParticles.load("rain"));
				particleSystem.addParticleType(KosmosParticles.load("snow"));
			}

			if (super.getEntity().hasMoved()) {
				Vector3f translated = new Vector3f(centreOffset);
				Vector3f.rotate(translated, super.getEntity().getRotation(), translated);
				Vector3f.add(translated, super.getEntity().getPosition(), translated);

				Vector3f difference = Vector3f.subtract(lastPosition, translated, null);
				lastPosition.set(translated);

				particleSystem.getSystemCentre().set(translated);
				particleSystem.getVelocityCentre().set(difference);
			}
		}
	}

	public ParticleSystem getParticleSystem() {
		return particleSystem;
	}

	public Vector3f getCentreOffset() {
		return centreOffset;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// PPS Slider.
		JSlider ppsSlider = new JSlider(JSlider.HORIZONTAL, 0, 2500, (int) particleSystem.getPPS());
		ppsSlider.setToolTipText("Particles Per Second");
		ppsSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();
				particleSystem.setPps(reading);
			}
		});
		ppsSlider.setMajorTickSpacing(500);
		ppsSlider.setMinorTickSpacing(100);
		ppsSlider.setPaintTicks(true);
		ppsSlider.setPaintLabels(true);
		panel.add(ppsSlider);

		// Gravity Effect Slider.
		JSlider gravityEffectSlider = new JSlider(JSlider.HORIZONTAL, -150, 150, (int) (particleSystem.getGravityEffect() * 100.0f));
		gravityEffectSlider.setToolTipText("Gravity Effect");
		gravityEffectSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();
				particleSystem.setGravityEffect(reading / 100.0f);
			}
		});
		gravityEffectSlider.setMajorTickSpacing(50);
		gravityEffectSlider.setMinorTickSpacing(10);
		gravityEffectSlider.setPaintTicks(true);
		gravityEffectSlider.setPaintLabels(true);
		panel.add(gravityEffectSlider);

		// Speed Slider.
		JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 150, (int) (particleSystem.getAverageSpeed() * 10.0f));
		speedSlider.setToolTipText("Speed Slider");
		speedSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int reading = source.getValue();
				particleSystem.setAverageSpeed(reading / 10.0f);
			}
		});
		speedSlider.setMajorTickSpacing(30);
		speedSlider.setMinorTickSpacing(5);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		panel.add(speedSlider);

		// X Offset Field.
		JSpinner xOffsetField = new JSpinner(new SpinnerNumberModel((double) centreOffset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		xOffsetField.setToolTipText("Particle System X Offset");
		xOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				centreOffset.x = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(xOffsetField);

		// Y Offset Field.
		JSpinner yOffsetField = new JSpinner(new SpinnerNumberModel((double) centreOffset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		yOffsetField.setToolTipText("Particle System Y Offset");
		yOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				centreOffset.y = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(yOffsetField);

		// Z Offset Field.
		JSpinner zOffsetField = new JSpinner(new SpinnerNumberModel((double) centreOffset.x, Double.NEGATIVE_INFINITY + 1.0, Double.POSITIVE_INFINITY - 1.0, 0.1));
		yOffsetField.setToolTipText("Particle System Z Offset");
		zOffsetField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				centreOffset.z = (float) (double) ((JSpinner) e.getSource()).getValue();
			}
		});
		panel.add(zOffsetField);

		// Component Dropdown.
		JComboBox componentDropdown = new JComboBox();
		for (int i = 0; i < spawns.length; i++) {
			componentDropdown.addItem(spawns[i].getTabName());
		}
		panel.add(componentDropdown);

		// Component Add Button.
		JButton componentAdd = new JButton("Set Spawn");
		componentAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String spawn = (String) componentDropdown.getSelectedItem();
				IEditorParticleSpawn particleSpawn = null;

				for (int i = 0; i < spawns.length; i++) {
					if (spawns[i].getTabName().equals(spawn)) {
						try {
							FlounderLogger.log("Adding component: " + spawn);
							Class componentClass = Class.forName(spawns[i].getClass().getName());
							Class[] componentTypes = new Class[]{};
							@SuppressWarnings("unchecked") Constructor componentConstructor = componentClass.getConstructor(componentTypes);
							Object[] componentParameters = new Object[]{};
							particleSpawn = (IEditorParticleSpawn) componentConstructor.newInstance(componentParameters);
						} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
							FlounderLogger.error("While loading particle spawn" + spawns[i] + "'s constructor could not be found!");
							FlounderLogger.exception(ex);
						}
					}
				}

				if (particleSystem.getSpawn() != null) {
					String classname = particleSystem.getSpawn().getClass().getName();
					IComponentEditor.REMOVE_SIDE_TAB.add("Particles (" + classname.split("\\.")[ByteWork.getCharCount(classname, '.')].replace("Spawn", "") + ")");
				}

				if (particleSpawn != null) {
					particleSystem.setSpawn(particleSpawn.getComponent());
					editorSystemSpawn = particleSpawn;

					JPanel panel = IComponentEditor.makeTextPanel();
					particleSpawn.addToPanel(panel);
					IComponentEditor.ADD_SIDE_TAB.add(new Pair<>("Particles (" + particleSpawn.getTabName() + ")", panel));
				}
			}
		});
		panel.add(componentAdd);

		// TODO: Add selection list for particle templates to be used in the types list.
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public String[] getSavableValues(String entityName) {
		// TODO: Not use saver function here, only place using it.
		/*EntitySaverFunction saveTemplates = new EntitySaverFunction("Templates") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				for (ParticleTemplate template : particleSystem.getTypes()) {
					String s = template.getName() + ",";
					entityFileWriter.writeSegmentData(s);
				}
			}
		};

		EntitySaverFunction saveSpawnValues = new EntitySaverFunction("SpawnValues") {
			@Override
			public void writeIntoSection(FileWriterHelper entityFileWriter) throws IOException {
				if (particleSystem.getSpawn() != null) {
					for (String values : editorSystemSpawn.getSavableValues()) {
						String s = values + ",";
						entityFileWriter.writeSegmentData(s);
					}
				}
			}
		};*/

		String saveParticleSpawn = "Spawn: " + (particleSystem.getSpawn() == null ? null : particleSystem.getSpawn().getClass().getName());
		String saveParticlePPS = "PPS: " + particleSystem.getPPS();
		String saveParticleSpeed = "Speed: " + particleSystem.getAverageSpeed();
		String saveParticleGravity = "GravityEffect: " + particleSystem.getGravityEffect();
		String scaleParticleCentreOffset = "CentreOffset: " + ParticleTemplate.saveVector3f(centreOffset);

		return new String[]{saveParticleSpawn, saveParticlePPS, saveParticleSpeed, saveParticleGravity, scaleParticleCentreOffset};
	}

	@Override
	public void dispose() {
		KosmosParticles.removeSystem(particleSystem);
		particleSystem = null;
	}
}
