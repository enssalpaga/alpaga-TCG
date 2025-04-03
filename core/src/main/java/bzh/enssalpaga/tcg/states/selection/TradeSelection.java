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
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.SearchingOpponentMenu;
import bzh.enssalpaga.tcg.states.TradeMenu;
import bzh.enssalpaga.tcg.utils.Constants;

public class TradeSelection extends SelectionMenu{
    private final Stage stage;
    private final Table table;
    public TradeSelection(GameStateManager gsm) {
        super(gsm);
        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
        table = new Table();
        table.top();
        table.setFillParent(true);
        table.add(new Label("Echange des cartes avec tes amis", Constants.labelStyle));
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        gsm.getConnectionManager().send("start_trading", "");
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        stage.draw();
        super.render(sb, sr);
    }

    @Override
    public void message(String event, String message) {
        if(event.equals("trading_players")){
            Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);
            table.clear();
            String[] traders = message.split("@");
            table.add(new Label("Echange des cartes", Constants.labelStyle)).row();
            table.add(new Label("avec tes amis !", Constants.labelStyle)).row();
            if(traders[0].isEmpty()){
                table.add(new Label("Aucun autre joueur n'est", Constants.labelStyle)).padTop(cam.viewportHeight/8).row();
                table.add(new Label("sur la page de trade :(", Constants.labelStyle)).row();
            }else{
                for(String trader : traders){
                    Button button = new TextButton(trader, skin);
                    button.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                            gsm.getConnectionManager().send("trade", trader);
                            gsm.set(new TradeMenu(gsm));
                        }
                    });
                    table.add(button).height(64).fillX().row();
                }
            }
        }
    }

    @Override
    public void dispose(){
        gsm.getConnectionManager().send("stop_trading", "");
        this.stage.dispose();
        super.dispose();
    }
}
