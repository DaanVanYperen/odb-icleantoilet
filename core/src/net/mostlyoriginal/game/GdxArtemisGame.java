package net.mostlyoriginal.game;

import com.badlogic.gdx.Game;
import net.mostlyoriginal.game.screen.LogoScreen;

public class GdxArtemisGame extends Game {

	private static GdxArtemisGame instance;

	@Override
	public void create() {
		instance = this;
		restart();
	}

	public void restart() {
		setScreen(new LogoScreen());
	}

	public static GdxArtemisGame getInstance()
	{
		return instance;
	}
}
