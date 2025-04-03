package bzh.enssalpaga.tcg.states.selection;

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
import bzh.enssalpaga.tcg.handlers.CollectionManager;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.BoosterMenu;
import bzh.enssalpaga.tcg.states.SearchingOpponentMenu;
import bzh.enssalpaga.tcg.utils.Constants;

public class MultiplayerSelection extends SelectionMenu{
    private final Stage stage;

    public MultiplayerSelection(GameStateManager gsm) {
        super(gsm);
        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
        Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);

        if(CollectionManager.deckIsFull()){
            Button button = new TextButton("Lancer un combat", skin);
            button.setHeight(64);
            button.setPosition(cam.viewportWidth/2f - button.getWidth()/2, this.selectionHeight*2);
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    gsm.set(new SearchingOpponentMenu(gsm));
                }
            });
            stage.addActor(button);
        }else{
            Table label_table = new Table();
            label_table.setFillParent(true);
            label_table.setY(this.selectionHeight*4);
            label_table.add(new Label("Votre deck doit", Constants.labelStyle)).row();
            label_table.add(new Label("etre complet", Constants.labelStyle)).row();
            label_table.add(new Label("pour pouvoir", Constants.labelStyle)).row();
            label_table.add(new Label("lancer un combat", Constants.labelStyle)).row();
            stage.addActor(label_table);
        }

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr){
        stage.draw();
        super.render(sb, sr);
    }

    @Override
    public void message(String event, String message) {

    }

    @Override
    public void dispose(){
        this.stage.dispose();
        super.dispose();
    }
}
