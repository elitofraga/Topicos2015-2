package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;


/**
 * Created by Elito Fraga on 03/08/2015.
 */
public class TelaJogo extends TelaBase{

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Image jogador;
    private Texture texturaJogador;
    private Texture texturaJogadorDireita;
    private Texture texteraJogadorEsquerda;
    private Boolean indoDireita;
    private Boolean indoEsquerda;
    private Boolean indoCima;
    private Boolean indoBaixo;

    /**
     * Construtor padrao da tela do jogo
     * @param game Referencia para a classe principal
     */
    public TelaJogo(MainGme game) {
        super(game);
    }

    /**
     * Chamado quando a tela e exibida
     */
    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initFonte();
        initInformacoes();
        initJogador();
    }

    private void initJogador() {

        texturaJogador = new Texture("sprites/player.png");
        texturaJogadorDireita = new Texture("sprites/player-right.png");
        texteraJogadorEsquerda = new Texture("sprites/player-left.png");

        jogador = new Image(texturaJogador);
        float x = camera.viewportWidth / 2 - jogador.getWidth() / 2;
        float y = 15;
        jogador.setPosition(x, y);
        palco.addActor(jogador);

    }

    private void initFonte() {

        fonte = new BitmapFont();
    }

    private void initInformacoes() {
        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbPontuacao = new Label("0 pontos", lbEstilo);
        palco.addActor(lbPontuacao);
    }

    /**
     * Chamado a todo quadro de atualização do jogo (FPS)
     * @param delta tempo entre um quadro e outro em segundos
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - 20);
        capturaTeclas();
        atualizarJogador(delta);

        palco.act(delta);
        palco.draw();
    }

    private void atualizarJogador(float delta) {

        float velocidade = 200;
        if (indoDireita) {
            if (jogador.getX() < camera.viewportWidth - jogador.getImageWidth()){
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }
        }
        if (indoEsquerda) {
            if (jogador.getX() > 0) {
                float x = jogador.getX() - velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }
        }

        if (indoCima) {
            float x = jogador.getX();
            float y = jogador.getY() + velocidade * delta;
            jogador.setPosition(x, y);
        }
        if (indoBaixo) {
            float x = jogador.getX();
            float y = jogador.getY() - velocidade * delta;
            jogador.setPosition(x, y);
        }

        if (indoDireita) { // trocar imagem
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorDireita)));
        }else if (indoEsquerda) {
            jogador.setDrawable(new SpriteDrawable(new Sprite(texteraJogadorEsquerda)));
        }else {
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogador)));
        }
    }

    private void capturaTeclas() {
        indoDireita = false;
        indoEsquerda = false;
        indoCima = false;
        indoBaixo = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            indoEsquerda = true;

        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

            indoDireita = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {

            indoCima = true;

        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {

            indoBaixo = true;
        }
    }

    /**
     * É chamado sempre que ja uma atualizaçõa no tamanho da tela
     * @param width novo valor da altura da tela
     * @param height novo valor da largura da tela
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    /**
     * É chamado sempre que o jogo for minimizado
     */
    @Override
    public void pause() {

    }

    /**
     * É chamado sempre que o jogo voltar para o primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * É chamado quando a tela for destruida
     */
    @Override
    public void dispose() {
        batch.dispose();
        palco.dispose();
        fonte.dispose();
        texturaJogador.dispose();
        texturaJogadorDireita.dispose();
        texteraJogadorEsquerda.dispose();
    }
}
