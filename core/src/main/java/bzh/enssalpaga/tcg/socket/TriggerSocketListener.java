package bzh.enssalpaga.tcg.socket;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public abstract class TriggerSocketListener extends SocketListener{
    private Queue<Message> messages;
    // semaphore for the queue
    private Semaphore semaphore;
    private boolean disconnected;
    private boolean joined;

    public TriggerSocketListener(SocketManager socketManager) {
        super(socketManager);
        this.disconnected = false;
        this.semaphore = new Semaphore(1, true);
        this.messages = new LinkedList<>();
    }

    public void trigger(){
        try {
            semaphore.acquire();
            while(!messages.isEmpty()){
                Message message = messages.remove();
                semaphore.release();
                this.onTriggerMessage(message.getEvent(), message.getContent());
                semaphore.acquire();
            }
            if(disconnected){
                disconnected = false;
                this.onTriggerQuit();
            }
            if(joined){
                joined = false;
                this.onTriggerJoin();
            }
            semaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(String event, String content) {
        Message message = new Message(event, content);
        try {
            semaphore.acquire();
            messages.add(message);
            semaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onJoin() {
        try {
            semaphore.acquire();
            joined = true;
            semaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onQuit() {
        try {
            semaphore.acquire();
            disconnected = true;
            semaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void onTriggerMessage(String event, String message);
    public abstract void onTriggerJoin();
    public abstract void onTriggerQuit();
}
