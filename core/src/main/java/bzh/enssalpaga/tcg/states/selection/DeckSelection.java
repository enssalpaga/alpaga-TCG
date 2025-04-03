package bzh.enssalpaga.tcg.states.selection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import bzh.enssalpaga.tcg.handlers.Card;
import bzh.enssalpaga.tcg.viewer.CardsViewer;
import bzh.enssalpaga.tcg.handlers.CollectionManager;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.utils.Constants;

public class DeckSelection extends SelectionMenu{
    private final Stage stage;
    private final float viewer_height;
    private final CardsViewer cardViewer;
    private final CardsViewer deckViewer;
    public DeckSelection(GameStateManager gsm) {
        super(gsm);
        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));

        viewer_height = (cam.viewportHeight-selectionHeight)/2;

        cardViewer = new CardsViewer(stage, 0, selectionHeight+viewer_height, cam.viewportWidth, viewer_height);
        deckViewer = new CardsViewer(stage, 0, selectionHeight, cam.viewportWidth, viewer_height);

        this.setViewers();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr){
        stage.draw();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.DARK_GRAY);
        sr.rect(0, selectionHeight+viewer_height-selectionHeight/8, cam.viewportWidth, selectionHeight/4);
        sr.end();
        super.render(sb, sr);
    }

    @Override
    public void message(String event, String message) {

    }

    private void refreshViewers(){
        cardViewer.clear();
        deckViewer.clear();
        setViewers();
        gsm.getConnectionManager().send("deck", CollectionManager.getDeck());
    }

    private void setViewers(){
        for (int i = 0; i < Constants.RARITY_NUMBER; i++){
            for (int j = 0; j < Constants.CARD_NUMBER; j++){
                int quantity = CollectionManager.getQuantityWithoutDeck(i, j);
                if (quantity != 0){
                    int rarity = i;
                    int id = j;
                    cardViewer.add(i, j, quantity, new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                            CollectionManager.addDeck(rarity, id);
                            refreshViewers();
                        }
                    });
                }
            }
        }
        cardViewer.padBottom();

        for(int i=0; i<Constants.DECK_SIZE; i++){
            Card card = CollectionManager.getDeck(i);
            if(card == null){
                deckViewer.add();
            }else{
                int index = i;
                deckViewer.add(card.getRarity(), card.getId(), new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        CollectionManager.removeDeck(index);
                        refreshViewers();
                    }
                });
            }
        }
        deckViewer.padBottom();
    }

    @Override
    public void dispose(){
        this.stage.dispose();
        super.dispose();
    }
}
