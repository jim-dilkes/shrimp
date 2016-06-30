package com.seabass.shrimp.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.seabass.shrimp.Assets;
import com.seabass.shrimp.Settings;
import com.seabass.shrimp.Shrimp;
import com.seabass.shrimp.World;
import com.seabass.shrimp.systems.AnimationSystem;
import com.seabass.shrimp.systems.BackgroundSystem;
import com.seabass.shrimp.systems.BoundsSystem;
import com.seabass.shrimp.systems.CameraSystem;
import com.seabass.shrimp.systems.CollisionSystem;
import com.seabass.shrimp.systems.CollisionSystem.CollisionListener;
import com.seabass.shrimp.systems.MovementSystem;
import com.seabass.shrimp.systems.RenderingSystem;
import com.seabass.shrimp.systems.StateSystem;
import com.seabass.shrimp.systems.VehicleSystem;

public class GameScreen extends ScreenAdapter {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_LEVEL_END = 3;
	static final int GAME_OVER = 4;

	Shrimp game;
	OrthographicCamera guiCam;
	Vector3 touchPoint;
	World world;
	CollisionListener collisionListener;

	PooledEngine engine;

	private int state;

	public GameScreen(Shrimp game) {

		this.game = game;
		state = GAME_READY;

		guiCam = new OrthographicCamera(Assets.w, Assets.h);
		guiCam.position.set(Assets.w / 2, Assets.h / 2, 0);
		touchPoint = new Vector3();
		collisionListener = new CollisionListener() {
			@Override
			public void vehicleCollision() {
				// Assets.playSound(Assets.vehicleCollision);
			}

			@Override
			public void barrierCollision() {
				// Assets.playSound(Assets.barrierCollision);
			}
		};

		engine = new PooledEngine();

		world = new World(engine);

		engine.addSystem(new VehicleSystem(world));
		engine.addSystem(new CameraSystem());
		engine.addSystem(new BackgroundSystem());
		engine.addSystem(new MovementSystem());
		engine.addSystem(new BoundsSystem());
		engine.addSystem(new StateSystem());
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new RenderingSystem(game.batcher));
		engine.addSystem(new CollisionSystem(world, collisionListener));

		engine.getSystem(BackgroundSystem.class).setCamera(engine.getSystem(RenderingSystem.class).getCamera());

		world.create();

		pauseSystems();
	}

	private void pauseSystems() {
		engine.getSystem(VehicleSystem.class).setProcessing(false);
		engine.getSystem(MovementSystem.class).setProcessing(false);
		engine.getSystem(BoundsSystem.class).setProcessing(false);
		engine.getSystem(StateSystem.class).setProcessing(false);
		engine.getSystem(AnimationSystem.class).setProcessing(false);
		engine.getSystem(CollisionSystem.class).setProcessing(false);
	}

	private void resumeSystems() {
		engine.getSystem(VehicleSystem.class).setProcessing(true);
		engine.getSystem(MovementSystem.class).setProcessing(true);
		engine.getSystem(BoundsSystem.class).setProcessing(true);
		engine.getSystem(StateSystem.class).setProcessing(true);
		engine.getSystem(AnimationSystem.class).setProcessing(true);
		engine.getSystem(CollisionSystem.class).setProcessing(true);
	}

	@Override
	public void render(float delta) {
		update(delta);
		// drawUI();
	}

	public void update(float deltaTime) {
		if (deltaTime > 0.1f)
			deltaTime = 0.1f;
		engine.update(deltaTime);
		switch (state) {
		case GAME_READY:
			updateReady();
			break;
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_PAUSED:
			updatePaused();
			break;
		case GAME_LEVEL_END:
			updateLevelEnd();
			break;
		case GAME_OVER:
			updateGameOver();
			break;
		}
	}

	private void updateReady() {
			state = GAME_RUNNING;
			resumeSystems();
	}

	private void updateRunning (float deltaTime) {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
			touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			//Touch commands (pause button etc.)
			System.out.println("Init touch: x = " +touchPoint.x +", y = " + touchPoint.y + ", mid x = " + Assets.w/2);
		}

		ApplicationType appType = Gdx.app.getType();
		
		// should work also with Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)
		float rot = 0.0f, forceF=0.0f;
		
		if (appType == ApplicationType.Android || appType == ApplicationType.iOS) {
			if(Gdx.input.isTouched()) {
				guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
				touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				if (touchPoint.x > Assets.w / 2) rot = -0.035f;
				else if (touchPoint.x < Assets.w / 2) rot = 0.035f;
			}
			forceF=25000;

		} else {
			//if (Gdx.input.isKeyPressed(Keys.D)) rot = -0.035f;
			//if (Gdx.input.isKeyPressed(Keys.A)) rot = 0.035f;
			if (Gdx.input.isKeyPressed(Keys.W)) forceF = 30000f;
			if (Gdx.input.isKeyPressed(Keys.S)) forceF = -8000f;
		}
		touchPoint.setZero();
		engine.getSystem(VehicleSystem.class).setRot(rot);
		engine.getSystem(VehicleSystem.class).setForceF(forceF);
		
		if (world.state == World.WORLD_STATE_NEXT_LEVEL) {
			
		}
		if (world.state == World.WORLD_STATE_GAME_OVER) {
			state = GAME_OVER;
			pauseSystems();
			//Settings.save();
		}
	}

	private void updatePaused() {
		if (Gdx.input.justTouched()) {
			guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		}
	}

	private void updateLevelEnd() {
		if (Gdx.input.justTouched()) {
			engine.removeAllEntities();
			world = new World(engine);
			state = GAME_READY;
		}
	}

	private void updateGameOver() {
		if (Gdx.input.justTouched()) {
			game.setScreen(new GameScreen(game));
		}
	}

	public void drawUI() {
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);
		game.batcher.begin();
		switch (state) {
		case GAME_READY:
			presentReady();
			break;
		case GAME_RUNNING:
			presentRunning();
			break;
		case GAME_PAUSED:
			presentPaused();
			break;
		case GAME_LEVEL_END:
			presentLevelEnd();
			break;
		case GAME_OVER:
			presentGameOver();
			break;
		}
		game.batcher.end();
	}

	private void presentReady() {
	}

	private void presentRunning() {
	}

	private void presentPaused() {
	}

	private void presentLevelEnd() {
	}

	private void presentGameOver() {
	}

	@Override
	public void pause() {
		if (state == GAME_RUNNING) {
			state = GAME_PAUSED;
			pauseSystems();
		}
	}
}
