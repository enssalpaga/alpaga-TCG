package bzh.enssalpaga.tcg.states.maintenance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.GameState;
import bzh.enssalpaga.tcg.utils.Constants;

public class ErrorMenu extends GameState {
    private Stage stage;
    public ErrorMenu(GameStateManager gsm) {
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

                Table label_table = new Table();
                label_table.setFillParent(true);
                label_table.add(new Label("Une erreur est survenue.", Constants.labelStyle)).row();
                label_table.add(new Label("La connexion", Constants.labelStyle)).row();
                label_table.add(new Label("au serveur a echoue.", Constants.labelStyle)).row();
                label_table.add(new Label("Assurez-vous d'avoir une bonne", Constants.labelStyle)).row();
                label_table.add(new Label("connexion internet et d'avoir la", Constants.labelStyle)).row();
                label_table.add(new Label("derniere version de l'application.", Constants.labelStyle)).row();
                label_table.add(new Label("Si le probleme persiste,", Constants.labelStyle)).row();
                label_table.add(new Label("merci d'appeler", Constants.labelStyle)).row();
                label_table.add(new Label("le allo support technique :", Constants.labelStyle)).row();
                label_table.add(new Label(Constants.ALLO_NUMBER, Constants.labelStyle)).row();
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
