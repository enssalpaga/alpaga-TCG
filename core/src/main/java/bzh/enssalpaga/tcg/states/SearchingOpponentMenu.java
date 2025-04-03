package bzh.enssalpaga.tcg.states;

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
import bzh.enssalpaga.tcg.handlers.Card;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.selection.MultiplayerSelection;
import bzh.enssalpaga.tcg.utils.Constants;

public class SearchingOpponentMenu extends GameState{
    private final Stage stage;
    public SearchingOpponentMenu(GameStateManager gsm) {
        super(gsm);
        gsm.getConnectionManager().send("search_opponent", "");

        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
        Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);

        Button button = new TextButton("Annuler", skin);
        button.setHeight(64);
        button.setPosition(cam.viewportWidth/2f - button.getWidth()/2, cam.viewportHeight/8);
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                gsm.getConnectionManager().send("cancel_search", "");
            }
        });
        stage.addActor(button);

        Table label_table = new Table();
        label_table.setFillParent(true);
        label_table.add(new Label("Recherche d'un", Constants.labelStyle)).row();
        label_table.add(new Label("adversaire...", Constants.labelStyle)).row();
        stage.addActor(label_table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void message(String event, String message) {
        if(event.equals("cancel_search")){
            gsm.set(new MultiplayerSelection(gsm));
        }else if(event.equals("hand")){
            String[] cards = message.split(";");
            Card[] hand = new Card[3];
            for(int i=0; i<3; i++){
                hand[i] = new Card(Integer.parseInt(cards[i*2+1]), Integer.parseInt(cards[i*2]));
            }
            gsm.set(new DuelMenu(gsm, hand));
        }
    }
}
