package com.seabass.shrimp.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.seabass.shrimp.components.MovementComponent;
import com.seabass.shrimp.components.TransformComponent;

public class MovementSystem extends IteratingSystem{
	private Vector2 tmp = new Vector2();

	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	
	public MovementSystem() {
		super(Family.all(TransformComponent.class, MovementComponent.class).get());
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent pos = tm.get(entity);
		MovementComponent mov = mm.get(entity);;
		
		pos.rotation+=mov.rotAccel;
		while(pos.rotation>=Math.PI)
			pos.rotation-=2*Math.PI;
		while(pos.rotation<-Math.PI)
			pos.rotation+=2*Math.PI;

		tmp.set(mov.accel).scl(deltaTime);
		mov.velocity.add(tmp);

		tmp.set(mov.velocity).scl(deltaTime);
		pos.pos.add(tmp.x, tmp.y, 0.0f);

	}
}
