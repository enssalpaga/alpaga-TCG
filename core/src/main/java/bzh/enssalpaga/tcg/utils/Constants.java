package bzh.enssalpaga.tcg.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Constants {
    public final static int VIEWPORT_WIDTH = 512;
    public static int VIEWPORT_HEIGHT;
    public final static int WINDOW_WIDTH = 480;
    public final static int WINDOW_HEIGHT = 800;
    public final static String GAME_TITLE = "Alpaga TCG";

    public static boolean BOOSTER_AVAILABLE;
    public final static int CARD_NUMBER = 11;
    public final static int RARITY_NUMBER = 5;
    public final static int DECK_SIZE = 10;
    public final static int BENCH_SIZE = 3;
    public final static String ALPAGA_ADDRESS = "https://github.com/enssalpaga";
    public final static String ALLO_NUMBER = "+33 7 67 65 03 97";
    public static FreeTypeFontGenerator generator;
    public static BitmapFont font;
    public static Label.LabelStyle labelStyle;
}
