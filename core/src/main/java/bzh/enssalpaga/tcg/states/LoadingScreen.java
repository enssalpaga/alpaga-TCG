package bzh.enssalpaga.tcg.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.sun.org.apache.bcel.internal.Const;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.utils.Constants;
import bzh.enssalpaga.tcg.utils.Functions;

public class LoadingScreen extends GameState {
    private boolean loading_asset;
    private final Stage stage;
    private final BitmapFont tipsFont;

    // merci d'appeler le alo support technique pour reporter les bugs ou suggérer une suggéstion (numéro)
    public LoadingScreen(GameStateManager gsm) {
        super(gsm);

        this.loading_asset = true;

        //Setup font
        Constants.generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/gamefont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;

        Constants.font = Constants.generator.generateFont(parameter);
        Constants.labelStyle = new Label.LabelStyle(Constants.font, Color.BLACK);

        parameter.size = 12;
        tipsFont = Constants.generator.generateFont(parameter);
        Label.LabelStyle labelStyle = new Label.LabelStyle(tipsFont, Color.BLACK);

        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
        Table label_table = new Table();
        label_table.setFillParent(true);
        label_table.bottom();
        label_table.add(new Label("Si vous avez une suggestion ou si vous voulez reporter un bug,", labelStyle)).row();
        label_table.add(new Label("n'hesitez pas a contacter un membre d'Enssalpaga", labelStyle)).row();
        label_table.add(new Label("", labelStyle)).row();
        stage.addActor(label_table);
        Gdx.input.setInputProcessor(stage);

        queueAssets();
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        if(this.loading_asset && LaunchGame.assetManager.update()){

            this.loading_asset = false;
            gsm.getConnectionManager().startConnection();
        }
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        stage.draw();

        //Setup sb cam
        sb.setProjectionMatrix(cam.combined);

        sb.begin();

        //draw font
        Constants.font.draw(sb, "LOADING", LaunchGame.hudCam.viewportWidth*0.05f, LaunchGame.hudCam.viewportHeight*0.2f);

        sb.end();
    }

    @Override
    public void dispose() {
        this.stage.dispose();
        tipsFont.dispose();
    }

    @Override
    public void message(String event, String message) {
    }

    public void queueAssets(){
        LaunchGame.assetManager.load("booster_down.png", Texture.class);
        LaunchGame.assetManager.load("booster_up.png", Texture.class);
        LaunchGame.assetManager.load("cards/none.png", Texture.class);
        LaunchGame.assetManager.load("cards/quantity.png", Texture.class);
        LaunchGame.assetManager.load("cards/hp.png", Texture.class);
        LaunchGame.assetManager.load("icons/new.png", Texture.class);
        for (int i = 0; i < 4; i++){
            LaunchGame.assetManager.load("icons/ko" + i + ".png", Texture.class);
        }
        for (int r = 0; r < Constants.RARITY_NUMBER; r++){
            LaunchGame.assetManager.load("cards/r" + r + ".png", Texture.class);
            for (int i = 0; i < Constants.CARD_NUMBER; i++){
                LaunchGame.assetManager.load(Functions.getPath(r, i), Texture.class);
            }
        }
        LaunchGame.assetManager.load("icons/booster.png", Texture.class);
        LaunchGame.assetManager.load("icons/booster_selected.png", Texture.class);
        LaunchGame.assetManager.load("icons/collection.png", Texture.class);
        LaunchGame.assetManager.load("icons/collection_selected.png", Texture.class);
        LaunchGame.assetManager.load("icons/multiplayer.png", Texture.class);
        LaunchGame.assetManager.load("icons/multiplayer_selected.png", Texture.class);
        LaunchGame.assetManager.load("icons/deck.png", Texture.class);
        LaunchGame.assetManager.load("icons/deck_selected.png", Texture.class);
        LaunchGame.assetManager.load("icons/trade.png", Texture.class);
        LaunchGame.assetManager.load("icons/trade_selected.png", Texture.class);

        LaunchGame.assetManager.load("data/uiskin.json", Skin.class);
    }
}
