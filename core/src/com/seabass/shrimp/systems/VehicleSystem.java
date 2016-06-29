package com.seabass.shrimp.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.seabass.shrimp.World;
import com.seabass.shrimp.components.MovementComponent;
import com.seabass.shrimp.components.StateComponent;
import com.seabass.shrimp.components.TransformComponent;
import com.seabass.shrimp.components.VehicleComponent;

import javafx.scene.transform.Scale;

public class VehicleSystem extends IteratingSystem {

	private static final Family family = Family
			.all(VehicleComponent.class, StateComponent.class, TransformComponent.class, MovementComponent.class).get();

	private Vector2 tmp = new Vector2();

	private float rot = 0.0f, forceF = 0.0f;
	private World world;

	private ComponentMapper<VehicleComponent> vm;
	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;

	public VehicleSystem(World world) {
		super(family);

		this.world = world;

		vm = ComponentMapper.getFor(VehicleComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
	}

	public void setRot(float rot) {
		this.rot = rot;
	}

	public void setForceF(float forceF) {
		this.forceF = forceF;
	}
	
	public void setAccelerating(){
		
	}
	
	public void setBraking(){
		
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent t = tm.get(entity);
		StateComponent state = sm.get(entity);
		MovementComponent mov = mm.get(entity);
		VehicleComponent veh = vm.get(entity);
		mov.rotAccel = rot;

		if (Math.abs(forceF) > 0)
			state.set(VehicleComponent.STATE_ACCELERATING);
		else if (Math.abs(mov.velocity.x) > 0 || Math.abs(mov.velocity.y) > 0)
			state.set(VehicleComponent.STATE_CRUISE);
		else
			state.set(VehicleComponent.STATE_STATIONARY);

		Vector2 travelDir = new Vector2(mov.velocity).nor();
		Vector2 orientation = new Vector2((float) -Math.sin(t.rotation), (float) Math.cos(t.rotation)).nor();
		
		Vector2 driveForceVec = new Vector2((float) (-forceF * Math.sin(t.rotation)),
				(float) (forceF * Math.cos(t.rotation)));
		
		Vector2 dragForceVec = new Vector2(travelDir)
				.scl(-veh.DRAG_AREA * veh.DRAG_COEFFICIENT * (float) Math.pow(mov.velocity.len(), 2));

		/** ### LATERAL FRICTION CALCULATION ### */
		float lateralFriction = (float) (veh.FRICTION_COEFFICIENT * veh.MASS * 9.8);
		Vector2 lateralOrient = new Vector2(orientation).rotate90(-1);
		Vector2 lateralFricForceVec = new Vector2();
		if (Math.abs(travelDir.dot(lateralOrient)) > 0) // if lat movement
			lateralFricForceVec = new Vector2(lateralOrient)
					.scl(Math.signum(travelDir.dot(lateralOrient)) * -lateralFriction);
		else
			lateralFricForceVec.setZero();

		/** ### ROLLING RESISTANCE CALCULATION ### */
		Vector2 rollResistForceVec = new Vector2();
		float rollResistance = (float) (veh.ROLLING_COEFFICIENT * veh.MASS * 9.8);
		if (Math.abs(travelDir.dot(orientation)) > 0) { // if parallel movement
			rollResistForceVec = new Vector2(orientation)
					.scl(Math.signum(travelDir.dot(orientation)) * -rollResistance);
		} else
			rollResistForceVec.setZero();

		/** ### NET FORCE CALCULATION ### */
		Vector2 netForceVec = new Vector2(driveForceVec).add(lateralFricForceVec).add(dragForceVec).add(rollResistForceVec);

		mov.accel.set(netForceVec.scl(1 / veh.MASS));
		mov.accel.add((float) (-Math.signum(mov.velocity.x) * 0.1), (float) (-Math.signum(mov.velocity.y) * 0.1));

		// STOP vehicle when velocity very small
		if (state.get() == VehicleComponent.STATE_CRUISE) {
			if (mov.velocity.len() < 0.1)
				mov.velocity.setZero();
		}

	}

	private void printVector2(Vector2 tmp) {
		System.out.println("x: " + tmp.x + ", y: " + tmp.y);
	}
}