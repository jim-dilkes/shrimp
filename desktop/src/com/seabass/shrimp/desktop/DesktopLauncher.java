package com.seabass.shrimp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.seabass.shrimp.Shrimp;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Shrimp(), config);
		config.title = "Shrimp";
		config.width = 576	;
		config.height = 1024;
	}
}
