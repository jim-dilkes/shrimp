package com.seabass.shrimp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	
	public static Texture background, vehicle;
	public static TextureRegion backgroundRegion, vehicleRegion;
	
	public static float w, h;

	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}
	
	public static void load(){
		w=Gdx.graphics.getWidth();
		h=Gdx.graphics.getHeight();
		
		background = loadTexture("background.png");
		backgroundRegion = new TextureRegion(background, 0, 0, 2133, 2133);
		vehicle = loadTexture("vehicle.png");
		vehicleRegion = new TextureRegion(vehicle, 0, 0, 267, 267);
	}
	
}


