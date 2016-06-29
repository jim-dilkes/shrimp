package com.seabass.shrimp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.seabass.shrimp.components.AnimationComponent;
import com.seabass.shrimp.components.BackgroundComponent;
import com.seabass.shrimp.components.BoundsComponent;
import com.seabass.shrimp.components.CameraComponent;
import com.seabass.shrimp.components.MovementComponent;
import com.seabass.shrimp.components.StateComponent;
import com.seabass.shrimp.components.TextureComponent;
import com.seabass.shrimp.components.TransformComponent;
import com.seabass.shrimp.components.VehicleComponent;
import com.seabass.shrimp.systems.RenderingSystem;

public class World {

	public static final float WORLD_WIDTH = 100;
	public static final float WORLD_HEIGHT = 100;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;

	private PooledEngine engine;

	public int state;

	public World(PooledEngine engine) {
		this.engine = engine;
	}

	public void create() {
		Entity player = createVehicle();
		createCamera(player);
		createBackground();
		this.state = WORLD_STATE_RUNNING;
	}

	private void createCamera(Entity target) {
		Entity entity = engine.createEntity();

		CameraComponent camera = new CameraComponent();
		camera.camera = engine.getSystem(RenderingSystem.class).getCamera();
		camera.target = target;

		entity.add(camera);

		engine.addEntity(entity);
	}

	private Entity createVehicle() {
		Entity entity = engine.createEntity();

		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		VehicleComponent vehicle = engine.createComponent(VehicleComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		MovementComponent movement = engine.createComponent(MovementComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);

		bounds.bounds.width = vehicle.WIDTH;
		bounds.bounds.height = vehicle.HEIGHT;

		position.pos.set(0.0f, 0.0f, 0.0f);
		texture.region = Assets.vehicleRegion;
		float textureScaler = 2 / (RenderingSystem.PIXELS_TO_METRES * Assets.vehicleRegion.getRegionHeight());
		position.scale.set(textureScaler, textureScaler);

		entity.add(animation);
		entity.add(vehicle);
		entity.add(bounds);
		entity.add(movement);
		entity.add(position);
		entity.add(state);
		entity.add(texture);

		engine.addEntity(entity);

		return entity;
	}

	private void createBackground() {
		int iMax = 8, jMax = 8;
		for (int i = 0; i < iMax; i++) {
			for (int j = 0; j < jMax; j++) {
				Entity entity = engine.createEntity();

				BackgroundComponent background = engine.createComponent(BackgroundComponent.class);
				TransformComponent position = engine.createComponent(TransformComponent.class);
				TextureComponent texture = engine.createComponent(TextureComponent.class);

				texture.region = Assets.backgroundRegion;
				position.scale.set(10 / (RenderingSystem.PIXELS_TO_METRES * texture.region.getRegionWidth()),
						10 / (RenderingSystem.PIXELS_TO_METRES * texture.region.getRegionHeight()));
				
				position.pos.set((i - iMax / 2) * 10+5, (j - jMax / 2) * 10+5, 10.0f);

				entity.add(background);
				entity.add(position);
				entity.add(texture);

				engine.addEntity(entity);

			}
		}
	}
}
