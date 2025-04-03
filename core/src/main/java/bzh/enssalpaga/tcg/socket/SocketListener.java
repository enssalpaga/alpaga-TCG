package bzh.enssalpaga.tcg.socket;

public abstract class SocketListener {
	protected SocketManager socketManager;

    public SocketListener(SocketManager socketManager) {
    	this.socketManager = socketManager;
	}

    public abstract void onJoin();
    public abstract void onQuit();
    public abstract void onMessage(String event, String message);
}
