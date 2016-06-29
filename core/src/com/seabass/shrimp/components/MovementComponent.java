package com.seabass.shrimp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent extends Component {
	public final Vector2 velocity = new Vector2();
	public final Vector2 accel = new Vector2();
	public float rotAccel = 0.0f; //anti-clockwise rotational acceleration
}
