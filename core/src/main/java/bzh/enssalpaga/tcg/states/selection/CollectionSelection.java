package bzh.enssalpaga.tcg.states.selection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import bzh.enssalpaga.tcg.viewer.CardsViewer;
import bzh.enssalpaga.tcg.handlers.CollectionManager;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.utils.Constants;
import bzh.enssalpaga.tcg.viewer.ZoomViewer;

public class CollectionSelection extends SelectionMenu{
    private final Stage stage;
    private ZoomViewer zoomViewer;

    public CollectionSelection(GameStateManager gsm) {
        super(gsm);
        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
        zoomViewer = new ZoomViewer(stage);

        CardsViewer cardViewer = new CardsViewer(stage, 0, selectionHeight, cam.viewportWidth, cam.viewportHeight-selectionHeight);
        for (int i = 0; i < Constants.RARITY_NUMBER; i++){
            Cell<Stack> cell = null;
            for (int j = 0; j < Constants.CARD_NUMBER; j++){
                if (CollectionManager.getQuantity(i, j) == 0){
                    cell = cardViewer.add();
                }else{
                    int rarity = i;
                    int card = j;
                    cell = cardViewer.add(rarity, card, new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                            zoomViewer.enable(rarity, card);
                        }
                    });
                }
            }
            if(cell!=null) cell.row();
        }
        cardViewer.padBottom();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void handleInput() {
        zoomViewer.handleInput();
        super.handleInput();
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr){
        stage.draw();
        zoomViewer.render(sr);
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
