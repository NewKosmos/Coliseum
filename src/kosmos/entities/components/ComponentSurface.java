/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.entities.template.*;
import flounder.helpers.*;
import flounder.physics.bounding.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 * Creates a set of lighting data for a entity.
 */
public class ComponentSurface extends IComponentEntity implements IComponentEditor {
	public static final int ID = EntityIDAssigner.getId();

	private float shineDamper;
	private float reflectivity;

	private boolean ignoreShadows;
	private boolean ignoreFog;

	/**
	 * Creates a new ComponentSurface.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentSurface(Entity entity) {
		this(entity, 1.0f, 0.0f, false, false);
	}

	/**
	 * Creates a new ComponentSurface.
	 *
	 * @param entity The entity this component is attached to.
	 * @param shineDamper The rendered objects shine damper when lighted.
	 * @param reflectivity The rendered objects reflectivity when lighted.
	 * @param ignoreShadows If the rendered object will ignore shadows.
	 * @param ignoreFog If the rendered object will ignore fog.
	 */
	public ComponentSurface(Entity entity, float shineDamper, float reflectivity, boolean ignoreShadows, boolean ignoreFog) {
		super(entity, ID);
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;

		this.ignoreShadows = ignoreShadows;
		this.ignoreFog = ignoreFog;
	}

	@Override
	public void update() {
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isIgnoreShadows() {
		return ignoreShadows;
	}

	public void setIgnoreShadows(boolean ignoreShadows) {
		this.ignoreShadows = ignoreShadows;
	}

	public boolean isIgnoreFog() {
		return ignoreFog;
	}

	public void setIgnoreFog(boolean ignoreFog) {
		this.ignoreFog = ignoreFog;
	}

	@Override
	public void addToPanel(JPanel panel) {
		// Shinde Damper Slider.
		JSlider sliderShineDamper = new JSlider(JSlider.HORIZONTAL, 0, 500, (int) (shineDamper * 100.0f));
		sliderShineDamper.setToolTipText("Shine Damper");
		sliderShineDamper.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			shineDamper = reading / 100.0f;
		});
		sliderShineDamper.setMajorTickSpacing(25);
		sliderShineDamper.setMinorTickSpacing(10);
		sliderShineDamper.setPaintTicks(true);
		sliderShineDamper.setPaintLabels(true);
		panel.add(sliderShineDamper);

		// Reflectivity Slider.
		JSlider sliderReflectivity = new JSlider(JSlider.HORIZONTAL, 0, 500, (int) (reflectivity * 100.0f));
		sliderReflectivity.setToolTipText("Reflectivity");
		sliderReflectivity.addChangeListener((ChangeEvent e) -> {
			JSlider source = (JSlider) e.getSource();
			int reading = source.getValue();
			reflectivity = reading / 100.0f;
		});
		sliderReflectivity.setMajorTickSpacing(25);
		sliderReflectivity.setMinorTickSpacing(10);
		sliderReflectivity.setPaintTicks(true);
		sliderReflectivity.setPaintLabels(true);
		panel.add(sliderReflectivity);

		// Ignore Fog Checkbox.
		JCheckBox boxIgnoreFog = new JCheckBox("Ignore Fog");
		boxIgnoreFog.setSelected(FlounderBounding.renders());
		boxIgnoreFog.addItemListener((ItemEvent e) -> {
			this.ignoreFog = boxIgnoreFog.isSelected();
		});
		panel.add(boxIgnoreFog);

		// Ignore Shadows Checkbox.
		JCheckBox boxIgnoreShadows = new JCheckBox("Ignore Shadows");
		boxIgnoreShadows.setSelected(FlounderBounding.renders());
		boxIgnoreShadows.addItemListener((ItemEvent e) -> {
			this.ignoreShadows = boxIgnoreShadows.isSelected();
		});
		panel.add(boxIgnoreShadows);
	}

	@Override
	public void editorUpdate() {

	}

	@Override
	public Pair<String[], EntitySaverFunction[]> getSavableValues(String entityName) {
		return new Pair<>(new String[]{}, new EntitySaverFunction[]{});
	}

	@Override
	public void dispose() {
	}
}
