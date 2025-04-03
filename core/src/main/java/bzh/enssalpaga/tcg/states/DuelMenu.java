package bzh.enssalpaga.tcg.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.List;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.enums.DuelEnum;
import bzh.enssalpaga.tcg.handlers.Card;
import bzh.enssalpaga.tcg.handlers.DuelManager;
import bzh.enssalpaga.tcg.states.selection.MultiplayerSelection;
import bzh.enssalpaga.tcg.viewer.CardViewer;
import bzh.enssalpaga.tcg.viewer.CardsViewer;
import bzh.enssalpaga.tcg.handlers.GameStateManager;
import bzh.enssalpaga.tcg.utils.Constants;
import bzh.enssalpaga.tcg.viewer.ZoomViewer;

public class DuelMenu extends GameState{
    private final DuelManager duelManager;
    private final Stage stage;
    private final Label label;
    private final ZoomViewer zoomViewer;
    private float active_width;
    private final float height;
    private DuelEnum state;
    private Button finish_button;
    private Texture score;
    private Texture score_opponent;

    private final CardsViewer hand_viewer;
    private final CardsViewer bench_viewer;
    private final CardsViewer bench_viewer_opponent;
    private final CardViewer active_viewer;
    private final CardViewer active_opponent_viewer;

    private boolean attack1_allowed;
    private boolean attack2_allowed;
    public DuelMenu(GameStateManager gsm, Card[] hand) {
        super(gsm);
        stage = new Stage(new ExtendViewport(cam.viewportWidth, 0, cam));
        Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);
        score = LaunchGame.assetManager.get("icons/ko0.png", Texture.class);
        score_opponent = LaunchGame.assetManager.get("icons/ko0.png", Texture.class);
        zoomViewer = new ZoomViewer(stage);
        state = DuelEnum.ACTIVE_STARTING;
        duelManager = new DuelManager();
        duelManager.setHand(hand);

        Table label_table = new Table();
        label_table.setFillParent(true);
        label_table.top();
        finish_button = new TextButton("Finir le tour", skin);
        finish_button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                switch (state) {
                    case BENCH_STARTING: {
                        state = DuelEnum.WAITING;
                        label.setText("L'autre joueur est\nen train de jouer");
                        finish_button.setVisible(false);

                        String message = duelManager.getActive().toString() + "|";
                        for (Card card : duelManager.getBench()) {
                            if (card != null) {
                                message += ";" + card;
                            }
                        }
                        gsm.getConnectionManager().send("active_bench", message);
                        break;
                    }
                    case PLAYING: {
                        attack("0");
                        break;
                    }
                    case END: {
                        gsm.set(new MultiplayerSelection(gsm));
                        break;
                    }
                }
            }
        });
        finish_button.setVisible(false);
        label = new Label("Choisissez votre\nalpaga actif", Constants.labelStyle);
        label_table.add(label).width(cam.viewportWidth-finish_button.getWidth()-32);
        label_table.add(finish_button).height(64).row();
        stage.addActor(label_table);

        height = cam.viewportHeight/6;

        hand_viewer = new CardsViewer(stage, height);
        bench_viewer = new CardsViewer(stage, hand_viewer.getTop(), height);
        bench_viewer_opponent = new CardsViewer(stage, bench_viewer.getTop()+height*3+height/16, height);

        Image none = new Image(LaunchGame.assetManager.get("cards/none.png", Texture.class));
        active_width = (height*3/2)*none.getWidth()/none.getHeight();
        active_viewer = new CardViewer(stage, (cam.viewportWidth-active_width)/2, bench_viewer.getTop(), active_width, height*3/2);
        active_opponent_viewer = new CardViewer(stage, (cam.viewportWidth-active_width)/2, bench_viewer.getTop()+height*3/2+height/16, active_width, height*3/2);

        refreshViewer();

        Gdx.input.setInputProcessor(stage);
    }

    private void refreshViewer(){
        hand_viewer.clear();
        bench_viewer.clear();
        bench_viewer_opponent.clear();
        for(Card card : duelManager.getHand()){
            hand_viewer.add(card.getRarity(), card.getId(), new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    zoomViewer.enable(card, getHandButtonText(), new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y){
                            switch (state){
                                case ACTIVE_STARTING: {
                                    duelManager.handToActive(card);
                                    state = DuelEnum.BENCH_STARTING;
                                    label.setText("Ajoutez des alpagas\na votre banc");
                                    finish_button.setVisible(true);
                                    refreshViewer();
                                    break;
                                }
                                case BENCH_STARTING:
                                case PLAYING: {
                                    duelManager.handToBench(card);
                                    refreshViewer();
                                    if(state == DuelEnum.PLAYING){
                                        gsm.getConnectionManager().send("bench", card.getId() + ";" + card.getRarity());
                                    }
                                    break;
                                }
                            }
                        }
                    });
                }
            });
        }
        hand_viewer.padBottom();

        for(int i = 0; i < duelManager.getBench().length; i++){
            Card card = duelManager.getBench()[i];
            if(card == null){
                bench_viewer.add();
            }else{
                int index = i;
                bench_viewer.add(card, new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        if(state.equals(DuelEnum.ACTIVE_PLAYING)){
                            zoomViewer.enable(card, "choisir comme alpaga actif", new ClickListener(){
                                @Override
                                public void clicked(InputEvent event, float x, float y){
                                    state = DuelEnum.WAITING;
                                    duelManager.benchToActive(index);
                                    refreshViewer();
                                    gsm.getConnectionManager().send("bench_to_active", String.valueOf(index));
                                }
                            });
                        }else{
                            zoomViewer.enable(card);
                        }
                    }
                });
            }
        }
        bench_viewer.padBottom();

        Card active = duelManager.getActive();
        if(active == null){
            active_viewer.set();
        }else{
            active_viewer.set(active, new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    if(state.equals(DuelEnum.PLAYING)){
                        List<String> attacks = new ArrayList<>();
                        List<ClickListener> listeners = new ArrayList<>();
                        if(attack1_allowed){
                            attacks.add("Attaque 1");
                            listeners.add(new ClickListener(){
                                @Override
                                public void clicked(InputEvent event, float x, float y){
                                    attack("1");
                                }
                            });
                        }if(attack2_allowed){
                            attacks.add("Attaque 2");
                            listeners.add(new ClickListener(){
                                @Override
                                public void clicked(InputEvent event, float x, float y){
                                    attack("2");
                                }
                            });
                        }

                        zoomViewer.enable(active, attacks, listeners);
                    }else{
                        zoomViewer.enable(active);
                    }
                }
            });
        }

        Card opponent_active = duelManager.getOpponentActive();
        if(opponent_active == null){
            active_opponent_viewer.set();
        }else{
            active_opponent_viewer.set(opponent_active, new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    zoomViewer.enable(opponent_active);
                }
            });
        }

        for(Card card : duelManager.getOpponentBench()){
            if(card == null){
                bench_viewer_opponent.add();
            }else{
                bench_viewer_opponent.add(card, new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y){
                        zoomViewer.enable(card);
                    }
                });
            }
        }
        bench_viewer_opponent.padBottom();
    }

    @Override
    public void handleInput() {
        zoomViewer.handleInput();
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb, ShapeRenderer sr) {
        stage.draw();

        sb.begin();
        float score_width = (stage.getWidth()-active_width)/3;
        float score_height = score_width*score.getHeight()/score.getWidth();
        float space_after_active = (stage.getWidth()-active_width)/2;
        sb.draw(score, stage.getWidth() - space_after_active + (space_after_active-score_width)/2, bench_viewer.getTop()+height/16, score_width, score_height);
        sb.draw(score_opponent, stage.getWidth() - space_after_active + (space_after_active-score_width)/2, height*9/2-score_height, score_width, score_height);
        sb.end();

        zoomViewer.render(sr);
    }

    @Override
    public void dispose() {
        bench_viewer.dispose();
        bench_viewer_opponent.dispose();
        active_viewer.dispose();
        active_opponent_viewer.dispose();
        hand_viewer.dispose();
        this.stage.dispose();
    }

    @Override
    public void message(String event, String message) {
        switch (event) {
            case "opponent_turn": {
                refreshTurn(message);
                break;
            }
            case "turn": {
                state = DuelEnum.PLAYING;
                label.setText("C'est ton tour !");
                finish_button.setVisible(true);

                refreshTurn(message);
                break;
            }
            case "active": {
                state = DuelEnum.ACTIVE_PLAYING;
                refreshTurn(message);
                label.setText("Choisissez votre\nalpaga actif");
                break;
            }
        }
    }

    private void refreshTurn(String message){
        String[] gameInfo = message.split("\\|");
        duelManager.getActive().setHp(Integer.parseInt(gameInfo[0]));

        String[] bench_hp = gameInfo[1].split(";");
        for(int i = 0; i < Constants.BENCH_SIZE; i++){
            Card card = duelManager.getBench()[i];
            if(card != null){
                card.setHp(Integer.parseInt(bench_hp[i]));
            }
        }

        String[] active_opponent = gameInfo[2].split(";");
        Card active_opponent_card = new Card(Integer.parseInt(active_opponent[1]), Integer.parseInt(active_opponent[0]));
        active_opponent_card.setHp(Integer.parseInt(active_opponent[2]));
        duelManager.setOpponentActive(active_opponent_card);

        String[] bench_opponent = gameInfo[3].split(";");
        Card[] bench_opponent_cards = new Card[Constants.BENCH_SIZE];
        for(int i = 0; i < bench_opponent.length/3; i++){
            Card card = new Card(Integer.parseInt(bench_opponent[i*3+1]), Integer.parseInt(bench_opponent[i*3]));
            card.setHp(Integer.parseInt(bench_opponent[i*3+2]));
            bench_opponent_cards[i] = card;
        }
        duelManager.setOpponentBench(bench_opponent_cards);

        String[] hand = gameInfo[4].split(";");
        Card[] hand_cards = new Card[hand.length/2];
        for(int i = 0; i < hand.length/2; i++){
            Card card = new Card(Integer.parseInt(hand[i*2+1]), Integer.parseInt(hand[i*2]));
            hand_cards[i] = card;
        }
        duelManager.setHand(hand_cards);

        String[] scores = gameInfo[5].split(";");
        int score_number = Integer.parseInt(scores[0]);
        int opponent_score_number = Integer.parseInt(scores[1]);
        score = LaunchGame.assetManager.get("icons/ko" + score_number + ".png", Texture.class);
        score_opponent = LaunchGame.assetManager.get("icons/ko" + opponent_score_number + ".png", Texture.class);
        if(score_number == 3){
            state = DuelEnum.END;
            label.setText("Vous avez gagné !");
            finish_button.setVisible(true);
        }else if(opponent_score_number == 3){
            state = DuelEnum.END;
            label.setText("Vous avez perdu !");
            finish_button.setVisible(true);
        }

        attack1_allowed = Boolean.parseBoolean(gameInfo[6]);
        attack2_allowed = Boolean.parseBoolean(gameInfo[7]);

        refreshViewer();
    }

    private void attack(String n){
        state = DuelEnum.WAITING;
        label.setText("L'autre joueur est\nen train de jouer");
        finish_button.setVisible(false);
        gsm.getConnectionManager().send("attack", n);
    }

    private String getHandButtonText(){
        switch (state) {
            case ACTIVE_STARTING: {
                return "choisir comme alpaga actif";
            }
            case BENCH_STARTING:
            case PLAYING: {
                return "ajouter au banc";
            }
        }
        return null;
    }
}
