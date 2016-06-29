package com.seabass.shrimp.components;

import com.badlogic.ashley.core.Component;

public class VehicleComponent extends Component {

	public static final int STATE_STATIONARY = 0;
	public static final int STATE_CRUISE = 1;
	public static final int STATE_ACCELERATING = 2;
	public static final int STATE_BRAKING = 3;

	public final float WIDTH = 2f;
	public final float HEIGHT = 2f;
	public final float DRAG_COEFFICIENT = 4*1.225f; //drag coefficient * drag area
	public final float FRICTION_COEFFICIENT =1.5f;
	public final float ROLLING_COEFFICIENT =0.1f;
	public final float DRAG_AREA = 4f;
	public float MASS = 1100f;
	

}
