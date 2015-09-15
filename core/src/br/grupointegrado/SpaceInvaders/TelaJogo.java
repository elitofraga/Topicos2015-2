package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;



/**
 * Created by Elito Fraga on 03/08/2015.
 */
public class TelaJogo extends TelaBase{

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private Stage palcoInformacoes;
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Label lbGameOver;
    private Image jogador;
    private Texture texturaJogador;
    private Texture texturaJogadorDireita;
    private Texture texteraJogadorEsquerda;
    private Boolean indoDireita;
    private Boolean indoEsquerda;
    private Boolean indoCima;
    private Boolean indoBaixo;
    private Boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture texturaTiro;
    private Texture texturaMeteoro1;
    private Texture texturaMeteoro2;
    private Array<Image> meteoros1 = new Array<Image>();
    private Array<Image> meteoros2 = new Array<Image>();

    private Array<Texture> texturasExplosao = new Array<Texture>();
    private Array<Explosao> explosoes = new Array<Explosao>();

    private Sound somTiro;
    private Sound somExplosao;
    private Sound somGameOver;
    private Music musicaFundo;

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
        palcoInformacoes = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initSons();
        initTexturas();
        initFonte();
        initInformacoes();
        initJogador();
    }

    private void initSons() {

        somTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);

    }

    private void initTexturas() {
        texturaTiro = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");

        for (int i = 1; i <= 17; i ++) {
            Texture textExplosao = new Texture("sprites/explosion-" + i + ".png");
            texturasExplosao.add(textExplosao);
        }
    }

    /**
     * Instancia os objetos do jogador o adiciona no palco
     */
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

    /**
     * Instancia os objetos de fonte
     */
    private void initFonte() {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = Color.WHITE;
        param.size = 24;
        param.shadowOffsetX = 2;
        param.shadowOffsetY = 2;
        param.shadowColor = Color.BLUE;

        fonte = generator.generateFont(param);

        generator.dispose();

        //fonte = new BitmapFont();
    }

    /**
     * instancia as informações na tela
     */
    private void initInformacoes() {
        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbPontuacao = new Label("0 pontos", lbEstilo);
        palcoInformacoes.addActor(lbPontuacao);

        lbGameOver = new Label("Game Over!", lbEstilo);
        lbGameOver.setVisible(false);
        palcoInformacoes.addActor(lbGameOver);


    }

    /**
     * Chamado a todo quadro de atualização do jogo (FPS)
     * @param delta tempo entre um quadro e outro em segundos
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - lbPontuacao.getHeight() - 20);
        lbPontuacao.setText(pontuacao + " pontos");

        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getWidth() / 2, camera.viewportHeight / 2);
        lbGameOver.setVisible(gameOver == true);
        atualizarExplosoes(delta);

        if (gameOver == false) {
            if (!musicaFundo.isPlaying()) //se nao esta tocando
                musicaFundo.play();// inicia a musica
                // capturaTeclas();
            atualizarJogador(delta);
            atualizarTiros(delta);
            atualizarMeteoros(delta);
            detectarColisoes(meteoros1, 5);
            detectarColisoes(meteoros2, 15);
        }else {
            if (musicaFundo.isPlaying()) {//se esta tocando
                musicaFundo.stop();// para a musica
            }
        }
        // atualiza a situação do palco
        palco.act(delta);
        // desenha o palco na tela
        palco.draw();
        //desenha palco de informacoes
        palcoInformacoes.act(delta);
        palcoInformacoes.draw();
    }

    private void atualizarExplosoes(float delta) {
        for (Explosao explosao : explosoes) {
            if (explosao.getEstagio() >= 16) {
                explosoes.removeValue(explosao, true); // remove a explosao do array
                explosao.getAtor().remove(); // remove o ator do palco
            }else {
                explosao.atualizar(delta);
            }
        }

    }

    private Rectangle recJogador = new Rectangle();
    private Rectangle recTiro = new Rectangle();
    private Rectangle recMeteoro = new Rectangle();
    private int pontuacao = 0;
    private boolean gameOver = false;

    private void detectarColisoes(Array<Image> meteoros, int valePonto) {
        recJogador.set(jogador.getX(), jogador.getY(), jogador.getWidth(), jogador.getHeight());

        for (Image meteoro : meteoros) {
            recMeteoro.set(meteoro.getX(), meteoro.getY(), meteoro.getWidth(), meteoro.getHeight());
            //detecta colisoes com tiros
            for (Image tiro : tiros) {
                recTiro.set(tiro.getX(), tiro.getY(), tiro.getWidth(), tiro.getHeight());
                if (recMeteoro.overlaps(recTiro)) {
                    //aqui ocorre uma colisao do tiro com o meteoro
                    pontuacao += valePonto;
                    tiro.remove();
                    tiros.removeValue(tiro, true);
                    meteoro.remove();
                    meteoros.removeValue(meteoro, true);
                    criarExplosao(meteoro.getX() + meteoro.getWidth() / 2, meteoro.getY() + meteoro.getHeight() / 2);
                }

            }
            //detecta colisao com o player
            if (recJogador.overlaps(recMeteoro)) {
                // ocorre colisao de jogador com meteoro
                gameOver = true;
                somGameOver.play();
            }
        }

    }

    /**
     * Criar explosao na posicao x e y
     * @param x
     * @param y
     */
    private void criarExplosao(float x, float y) {
        Image ator = new Image(texturasExplosao.get(0));
        ator.setPosition(x - ator.getWidth() / 2,  y - ator.getHeight() / 2);
        palco.addActor(ator);

        Explosao explosao = new Explosao(ator, texturasExplosao);
        explosoes.add(explosao);
        somExplosao.play();
    }

    private void atualizarMeteoros(float delta) {
        int qtdMeteoros = meteoros1.size + meteoros2.size; // retorna a quantidade de meteoros criados

        if (qtdMeteoros < 10) {

            int tipo = MathUtils.random(1, 4); // retorna 1 ou 2 aleatoriamente
            if (tipo == 1) { // cria meteoro1
                Image meteoro = new Image(texturaMeteoro1);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoros1.add(meteoro);
                palco.addActor(meteoro);

            } else if(tipo == 2) { // cria meteoro2
                Image meteoro = new Image(texturaMeteoro2);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoros2.add(meteoro);
                palco.addActor(meteoro);
            }
        }

        float velocidade1 = 100; //200 pixels por segundo
        for (Image meteoro : meteoros1) {
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade1 * delta;
            meteoro.setPosition(x, y); //atualiza a posição do meteoro
            if (meteoro.getY() + meteoro.getHeight() < 0) {
                meteoro.remove(); //remove do palco
                meteoros1.removeValue(meteoro, true); //remove da lista
            }
        }

        float velocidade2 = 150; //200 pixels por segundo
        for (Image meteoro : meteoros2) {
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade2 * delta;
            meteoro.setPosition(x, y); //atualiza a posição do meteoro
            if (meteoro.getY() + meteoro.getHeight() < 0) {
                meteoro.remove(); //remove do palco
                meteoros2.removeValue(meteoro, true); //remove da lista
            }
        }

    }

    private  float intervaloTiros = 0;
    private final float min_intervalo_tiros = 0.3f; //tempo minimo entre os tiros

    private void atualizarTiros(float delta) {
        intervaloTiros = intervaloTiros + delta; //acumula o tempo percorrido
        //cria um novo tiro se necessario
        if (atirando) {
            if (intervaloTiros >= min_intervalo_tiros) {
                Image tiro = new Image(texturaTiro);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);

                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;
                somTiro.play();
            }
        }
        float velocidade = 200; // velocidade de movimentação do tiro
        for (Image tiro : tiros) {
            //movimenta o tiro em direção ao topo
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x, y);
            //remove os tiros que sairam da tela
            if (tiro.getY() > camera.viewportHeight) {
                tiros.removeValue(tiro, true); //remove da lista
                tiro.remove(); //remove do palco

            }
        }

    }

    private void atualizarJogador(float delta) {

        float velocidade = 200;
        if (indoDireita) {
            //verifica  se o jogador esta dentro da tela
            if (jogador.getX() < camera.viewportWidth - jogador.getImageWidth()){
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }
        }
        if (indoEsquerda) {
            //verifica  se o jogador esta dentro da tela
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
        atirando = false;

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
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {

            atirando = true;
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
        palcoInformacoes.dispose();
        texturaJogador.dispose();
        texturaJogadorDireita.dispose();
        texteraJogadorEsquerda.dispose();
        texturaTiro.dispose();
        texturaMeteoro1.dispose();
        texturaMeteoro2.dispose();
        for (Texture textE : texturasExplosao) {
            textE.dispose();
        }
        somTiro.dispose();
        somExplosao.dispose();
        somGameOver.dispose();
        musicaFundo.dispose();
    }
}
