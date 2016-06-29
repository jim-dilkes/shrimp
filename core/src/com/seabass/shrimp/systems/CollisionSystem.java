package com.seabass.shrimp.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.seabass.shrimp.World;
import com.seabass.shrimp.components.BoundsComponent;
import com.seabass.shrimp.components.MovementComponent;
import com.seabass.shrimp.components.StateComponent;
import com.seabass.shrimp.components.TransformComponent;

public class CollisionSystem extends EntitySystem{
	private ComponentMapper<BoundsComponent> bm;
	private ComponentMapper<MovementComponent> mm;
	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	
	private Engine engine;
	private World world;
	private CollisionListener listener;

	public static interface CollisionListener {		
		public void vehicleCollision ();
		public void barrierCollision ();
	}
	
	public CollisionSystem(World world, CollisionListener listener){
		this.world = world;
		this.listener = listener;
		
		bm = ComponentMapper.getFor(BoundsComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
	}
	
}
