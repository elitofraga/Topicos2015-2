package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Screen;

/**
 * Created by Elito Fraga on 03/08/2015.
 */
public abstract class TelaBase implements Screen{

    protected MainGme game;

    public TelaBase(MainGme game) {
        this.game = game;
    }

    @Override
    public void hide() {
        dispose();
    }

}
