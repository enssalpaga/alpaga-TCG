package bzh.enssalpaga.tcg.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.Card;
import bzh.enssalpaga.tcg.handlers.CollectionManager;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.states.selection.TradeSelection;
import bzh.enssalpaga.tcg.utils.Constants;
import bzh.enssalpaga.tcg.viewer.CardsViewer;

public class TradeMenu extends GameState{
    private final Stage stage;

    private final CardsViewer friendTradeViewer;
    private final CardsViewer tradeViewer;
    private final CardsViewer collectionViewer;

    public TradeMenu(GameStateManager gsm) {
        super(gsm);
        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
        CollectionManager.resetTrade();

        float buttonsHeight = cam.viewportHeight /12f;
        float viewer_height = (cam.viewportHeight-buttonsHeight)/3;

        friendTradeViewer = new CardsViewer(stage, 0, buttonsHeight+viewer_height*2, cam.viewportWidth, viewer_height);
        tradeViewer = new CardsViewer(stage, 0, buttonsHeight+viewer_height, cam.viewportWidth, viewer_height);
        collectionViewer = new CardsViewer(stage, 0, buttonsHeight, cam.viewportWidth, viewer_height);

        Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);
        Button cancel_button = new TextButton("Annuler", skin);
        cancel_button.setSize(stage.getWidth()/2, buttonsHeight);
        cancel_button.setPosition(0, 0);
        cancel_button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                gsm.getConnectionManager().send("cancel_trade", "");
                gsm.set(new TradeSelection(gsm));
            }
        });
        stage.addActor(cancel_button);
        Button ok_button = new TextButton("Echanger", skin);
        ok_button.setSize(stage.getWidth()/2, buttonsHeight);
        ok_button.setPosition(stage.getWidth()/2, 0);
        ok_button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                gsm.getConnectionManager().send("accept_trade", "");
            }
        });
        stage.addActor(ok_button);

        this.setViewers();

        Gdx.input.setInputProcessor(stage);
    }

    private void refreshViewers(){
        friendTradeViewer.clear();
        tradeViewer.clear();
        collectionViewer.clear();
        setViewers();
        gsm.getConnectionManager().send("giving_trade", CollectionManager.getTrade());
    }

    private void setViewers(){
        for (int i = 0; i < Constants.RARITY_NUMBER; i++){
            for (int j = 0; j < Constants.CARD_NUMBER; j++){
                int quantity = CollectionManager.getQuantityWithoutTrade(i, j);
                if (quantity != 0){
                    int rarity = i;
                    int id = j;
                    collectionViewer.add(i, j, quantity, new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                            CollectionManager.addTrade(rarity, id);
                            refreshViewers();
                        }
                    });
                }
            }
        }
        collectionViewer.padBottom();

        for (int i = 0; i < Constants.RARITY_NUMBER; i++){
            for (int j = 0; j < Constants.CARD_NUMBER; j++){
                int quantity = CollectionManager.getTrade(i, j);
                if (quantity != 0){
                    int rarity = i;
                    int id = j;
                    tradeViewer.add(i, j, quantity, new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                            CollectionManager.removeTrade(rarity, id);
                            refreshViewers();
                        }
                    });
                }
            }
        }
        tradeViewer.padBottom();
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        stage.draw();
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }

    @Override
    public void message(String event, String message) {
        if(event.equals("giving_trade")){
            int[][] friend_trade = CollectionManager.stringToCollection(message);
            for (int i = 0; i < Constants.RARITY_NUMBER; i++){
                for (int j = 0; j < Constants.CARD_NUMBER; j++){
                    int quantity = friend_trade[i][j];
                    if (quantity != 0){
                        friendTradeViewer.add(i, j, quantity, new ClickListener(){
                            @Override
                            public void clicked(InputEvent event, float x, float y){
                                System.out.println("clicked");
                            }
                        });
                    }
                }
            }
            friendTradeViewer.padBottom();
        }else if(event.equals("accept_trade")){
            gsm.set(new TradeSelection(gsm));
        }
    }
}
