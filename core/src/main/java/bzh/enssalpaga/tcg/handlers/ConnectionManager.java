package bzh.enssalpaga.tcg.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.InputStream;

import bzh.enssalpaga.tcg.socket.SocketManager;
import bzh.enssalpaga.tcg.socket.TriggerSocketListener;
import bzh.enssalpaga.tcg.states.LoginMenu;
import bzh.enssalpaga.tcg.states.maintenance.ErrorMenu;
import bzh.enssalpaga.tcg.states.selection.BoosterSelection;
import bzh.enssalpaga.tcg.utils.Constants;

public class ConnectionManager extends TriggerSocketListener {
    GameStateManager gsm;

    public ConnectionManager(SocketManager socketManager, GameStateManager gsm) {
        super(socketManager);
        this.gsm = gsm;
    }

    public void startConnection(){
        InputStream inputStream = Gdx.files.internal("KeyPair/publicKey").read();
//        socketManager.startClient(this, "152.228.163.97", 60863, inputStream);
        socketManager.startClient(this, "localhost", 60863, inputStream);
    }

    @Override
    public void onTriggerJoin() {
        String login = load();
        if(login == null){
            new_connection();
        }else{
            socketManager.send("login", login);
        }
    }

    @Override
    public void onTriggerQuit() {
        System.out.println("Déconnexion du serveur");
        gsm.set(new ErrorMenu(gsm));
    }

    @Override
    public void onTriggerMessage(String event, String message) {
        switch (event) {
            case "login_failed": {
                new_connection();
                break;
            }
            case "password": {
                save(message);
                socketManager.send("login", message);
                break;
            }
            case "mail_error": {
                // TO DO
                break;
            }
            case "join_info": {
                int border_index = message.indexOf(";");
                int border_deck = message.indexOf("|");
                Constants.BOOSTER_AVAILABLE = Boolean.parseBoolean(message.substring(0, border_index));
                CollectionManager.setCollection(message.substring(border_index+1, border_deck), message.substring(border_deck+1));
                gsm.set(new BoosterSelection(gsm));
                break;
            }
            case "new_booster": {
                Constants.BOOSTER_AVAILABLE = true;
                if(gsm.get() instanceof BoosterSelection){
                    gsm.set(new BoosterSelection(gsm));
                }
                break;
            }
            default:
                gsm.message(event, message);
        }
    }

    private void new_connection(){
        gsm.set(new LoginMenu(gsm));
    }

    private String load(){
        FileHandle file = Gdx.files.local("login.txt");
        if(!file.exists()) {
            return null;
        }else{
            return file.readString();
        }
    }

    private void save(String login){
        FileHandle file = Gdx.files.local("login.txt");
        file.writeString(login, false);
    }

    public void send(String event, String message){
        socketManager.send(event, message, true);
    }
}
