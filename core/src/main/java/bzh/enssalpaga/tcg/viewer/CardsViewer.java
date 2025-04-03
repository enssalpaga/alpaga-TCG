package bzh.enssalpaga.tcg.viewer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import bzh.enssalpaga.tcg.LaunchGame;
import bzh.enssalpaga.tcg.handlers.Card;
import bzh.enssalpaga.tcg.utils.Constants;
import bzh.enssalpaga.tcg.utils.Functions;

public class CardsViewer {
    private final Table contentTable = new Table();
    private float card_width;
    private float card_height;
    private float pad;
    private final Skin skin = LaunchGame.assetManager.get("data/uiskin.json", Skin.class);
    private float top_y;
    private Label.LabelStyle style;

    public CardsViewer(Stage stage, float height){
        float width = init(height);
        top_y = height/2;
        contentTable.setBounds((stage.getWidth()-width)/2, -height/2, width, height);
        stage.addActor(contentTable);
    }

    public CardsViewer(Stage stage, float y, float height){
        float width = init(height);

        top_y = y+height;
        contentTable.setBounds((stage.getWidth()-width)/2, y, width, height);
        stage.addActor(contentTable);
    }

    public CardsViewer(Stage stage, float x, float y, float width, float height){
        Texture texture = LaunchGame.assetManager.get("cards/none.png", Texture.class);
        card_width = width*2/7;
        card_height = card_width*texture.getHeight()/texture.getWidth();
        pad = (width - 3*card_width)/4;

        ScrollPane scrollPane = new ScrollPane(contentTable);
        scrollPane.setScrollingDisabled(true, false);
        Table container = new Table();
        container.add(scrollPane);
        container.setBounds(x, y, width, height);

        stage.addActor(container);
    }

    private float init(float height){
        Texture texture = LaunchGame.assetManager.get("cards/none.png", Texture.class);
        float width = height/(((float) texture.getHeight()/texture.getWidth()*2+1/2f)/7);
        card_width = width*2/7;
        card_height = card_width*texture.getHeight()/texture.getWidth();
        pad = (width/7)/4;

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) card_height/8;
        BitmapFont font = Constants.generator.generateFont(parameter);
        style = new Label.LabelStyle(font, Color.BLACK);

        return width;
    }

    public Cell<Stack> add(Card card, ClickListener clickListener){
        Stack stack = getStack(card.getRarity(), card.getId(), clickListener);

        CardViewer.addHp(stack, card, card_width, card_height, style);

        return add(stack);
    }

    public Cell<Stack> add(int rarity, int card, ClickListener clickListener){
        Stack stack = getStack(rarity, card, clickListener);
        return add(stack);
    }

    public Cell<Stack> add(){
        Stack stack = new Stack();
        stack.add(new Image(LaunchGame.assetManager.get("cards/none.png", Texture.class)));
        return add(stack);
    }

    public Cell<Stack> add(int rarity, int card, int quantity, ClickListener clickListener){
        Stack stack = getStack(rarity, card, clickListener);

        Group group = new Group();
        Image image = new Image(LaunchGame.assetManager.get("cards/quantity.png", Texture.class));
        image.setSize(card_width/3, card_height/8);
        group.addActor(image);

        Table label_table = new Table();
        label_table.setSize(card_width/3, card_height/8);
        Label label = new Label(String.valueOf(quantity), Constants.labelStyle);
        label_table.add(label);
        group.addActor(label_table);

        stack.add(group);
        return add(stack);
    }

    private Stack getStack(int rarity, int card, ClickListener clickListener){
        Stack stack = new Stack();
        stack.add(new Image(LaunchGame.assetManager.get(Functions.getPath(rarity, card), Texture.class)));
        stack.add(new Image(LaunchGame.assetManager.get("cards/r" + rarity + ".png", Texture.class)));
        stack.addListener(clickListener);
        return stack;
    }

    private Cell<Stack> add(Stack stack){
        Cell<Stack> cell = contentTable.add(stack).size(card_width, card_height).padLeft(pad).padTop(pad);
        if(cell.getColumn() == 2){
            cell.padRight(pad).row();
        }
        return cell;
    }

    public void clear(){
        contentTable.clear();
    }

    @SuppressWarnings("rawtypes")
    public void padBottom(){
        Array<Cell> cells = contentTable.getCells();
        if(cells.size > 0){
            int i = cells.size-1;
            Cell cell = cells.get(i);
            int last_row = cell.getRow();
            while (i >= 0 && cells.get(i).getRow() == last_row){
                cell = cells.get(i);
                cell.padBottom(pad);
                i--;
            }
        }
    }

    public float getTop(){
        return top_y;
    }

    public void dispose(){
        style.font.dispose();
    }
}
