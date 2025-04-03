package bzh.enssalpaga.tcg.states.maintenance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.GameState;
import bzh.enssalpaga.tcg.utils.Constants;

public class UpdateMenu  extends GameState {
    private Stage stage;
    public UpdateMenu(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        if(LaunchGame.assetManager.update()){
            if(stage == null) {
                stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
                Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);

                Table label_table = new Table();
                label_table.setFillParent(true);
                label_table.add(new Label("Une nouvelle version de", Constants.labelStyle)).row();
                label_table.add(new Label("l'application est disponible !", Constants.labelStyle)).row();
                label_table.add(new Label("Merci de la telecharger", Constants.labelStyle)).row();
                label_table.add(new Label("a l'adresse suivante :", Constants.labelStyle)).row();
                label_table.add(new Label(Constants.ALPAGA_ADDRESS, Constants.labelStyle)).row();

                Button button = new TextButton("Telecharger l'application", skin);
                button.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        System.out.println("clicked");
                        Gdx.net.openURI(Constants.ALPAGA_ADDRESS);
                    }
                });
                label_table.add(button).height(64);
                stage.addActor(label_table);

                Gdx.input.setInputProcessor(stage);
            }

            stage.draw();
        }
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }

    @Override
    public void message(String event, String message) {

    }
}
