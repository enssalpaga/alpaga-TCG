package bzh.enssalpaga.tcg.handlers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.time.LocalDate;
import java.util.Stack;

import bzh.enssalpaga.tcg.socket.SocketManager;
import bzh.enssalpaga.tcg.states.GameState;

public class GameStateManager {
    Stack<GameState> gameStates;
    private final ConnectionManager connectionManager;

    public GameStateManager(){
        gameStates = new Stack<>();
        connectionManager = new ConnectionManager(new SocketManager(), this);
    }

    public void push(GameState gameState){
        gameStates.push(gameState);
    }

    public void set(GameState gameState){
        gameStates.pop().dispose();
        gameStates.push(gameState);
    }

    public GameState get(){
        return gameStates.peek();
    }

    public void handleInput(){
        gameStates.peek().handleInput();
    }

    public void update(float dt){
        gameStates.peek().update(dt);
    }

    public void render(SpriteBatch sb, ShapeRenderer sr){
        connectionManager.trigger();
        gameStates.peek().render(sb, sr);
    }

    public void message(String event, String message) {
        gameStates.peek().message(event, message);
    }

    public ConnectionManager getConnectionManager(){
        return this.connectionManager;
    }
}
