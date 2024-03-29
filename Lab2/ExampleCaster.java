import java.util.*;
import mcgui.*;

public class ExampleCaster extends Multicaster {
    private int leader;
    private int[] alive;
    private static int seqNr;
    private int lastAck = -1;
    private LinkedList<ExampleMessage> receiveBuffer;
    private LinkedList<MessageText> sendBuffer;
    private int aliveHosts = 0;
    private int messageId;

    /*
    * Runs when the programs starts.
    */
    public void init() {
      // Number of hosts that are alive. 1 if alive and 0 if dead.
      alive = new int[hosts];
      for(int i = 0; i<hosts;i++){
        alive[i] = 1;
      }

      sendBuffer = new LinkedList<MessageText>();
      receiveBuffer = new LinkedList<ExampleMessage>();
      seqNr = 0;
      leader = 0;
      messageId = 0;
    }

    /**
     * The GUI calls this module to multicast a message
     * @param messagetext   The message to be multicasted
     */
    public void cast(String messagetext) {
      MessageText msg = new MessageText(messageId++,messagetext);
      sendBuffer.add(msg);
      askForTicket(msg);
    }
    public int getAliveHosts(){
      int r=0;
      for(int i = 0;i<hosts;i++){
        if(alive[i] == 1){
          r++;
        }
      }
      return r;
    }

    /**
     * Receive a basic message
     * @param message  The message received
     */
    public void basicreceive(int peer,Message message) {
      if(message instanceof LeaderMessage){
        LeaderMessage lmsg = (LeaderMessage)message;
        //leader =  lmsg.getLeader();
        if(lmsg.getLeader() == -1){
          aliveHosts++;
          if(aliveHosts == getAliveHosts()){
            aliveHosts = 0;
            mcui.debug("Leader is elected and all nodes agree");
            for ( int i = 0;i<hosts;i++){
              if(alive[i] == 1){
                bcom.basicsend(i,new LeaderMessage(id,id));
              }
            }
          }
          
        }else{
          leader =  lmsg.getLeader();
          for(MessageText s : sendBuffer){
            askForTicket(s);
          }
        }

      }else if(message instanceof Ticket){
        Ticket ticket = (Ticket)message;
        handleTicket(peer,ticket);
      // A normal message is received
      }else if (message instanceof ExampleMessage){
        ExampleMessage msg = (ExampleMessage)message;
        int seq = msg.getSeqNr();
        if(msg.getFlood()){
          flood(msg,peer);
        }
        if(seq > lastAck){
          if(canDeliver(msg)){
            if(msg.getRecipient() == id){
              
              int messageId = findInSendBuffer(msg.getMessageId());
              sendBuffer.remove(messageId);
            }
            if(id != leader) {
              seqNr = msg.getSeqNr()+1;
            }
            mcui.deliver(msg.getSender(), msg.getText());    
            tryBuffer();
          }else{
            receiveBuffer.add(msg);
          }
        }
      }else{
        mcui.debug("ERROR: unkown message!!");
      }
    }
    private int findInSendBuffer(int id){
      for(int i =0;i<sendBuffer.size();i++){
        if(id == sendBuffer.get(i).getId()){
          return i;
        }
      }
      return -1;
    }
    /*
    * Flooding message. Sent to everyone except itself and the person who created it
    */
    private void flood(ExampleMessage msg,int peer) {
      msg.flood();
      for(int i=0; i < hosts; i++) {         
        if(i != id && i != peer) {
            bcom.basicsend(i,msg);
        }
      }
    }
    /*
    * Can the message be delivered or are we waiting for another message to arrive.
    */
    private boolean canDeliver(ExampleMessage msg){
      if(lastAck+1 == msg.getSeqNr()){
        lastAck++;
        return true;
      }
      return false;
    }
    /*
    * Checking the buffer to see if we can deliver any messages
    */
    private void tryBuffer(){
      for(ExampleMessage msg : receiveBuffer) {
        if(lastAck+1 == msg.getSeqNr()){
          seqNr = msg.getSeqNr()+1;
          if(msg.getRecipient()==id){
            sendBuffer.remove();
          }
          mcui.deliver(msg.getSender(), msg.getText());    
          receiveBuffer.remove(msg);
          lastAck++;
          tryBuffer();
          return;
        }
      }
      //Nothing found
      return;
    }
    /*
    * The leader populates a ticket with a sequencenumber.
    */
    private void handleTicket(int peer, Ticket ticket) {
      if(ticket.getSeqNr() == null) {
        if(leader == id){
          ticket.setSeqNr(seqNr++);
          multicastMessage(ticket,ticket.getMessage());
        }
      }else{
        mcui.debug("Something weird happened, only leader is supposed to do stuff!");
      }
    }
    /**
     * Signals that a peer is down and has been down for a while to
     * allow for messages taking different paths from this peer to
     * arrive.
     * @param peer  The dead peer
     */
    public void basicpeerdown(int peer) {
      mcui.debug("Peer "+peer+" has been dead for a while now!");
      alive[peer]=0;
      if(peer == leader) {
        mcui.debug("The leader is dead, lets elect a new leader.");
        int newleader = leaderElection(id);
        mcui.debug("New leader is: "+ leader);
        bcom.basicsend(newleader,new LeaderMessage(id,-1));
      }
    }
    /*
    * Sending newMessage to everyone.
    */
    private void multicastMessage(Ticket ticket,String newMessage){
        ExampleMessage msg = new ExampleMessage(id, newMessage,ticket);
        for(int i=0; i < alive.length; i++) {
          if(i != id) {
            if(alive[i] == 1){
              bcom.basicsend(i,msg);
            }
          }
        }

        if(canDeliver(msg)){
          if(msg.getRecipient()==id){
            sendBuffer.remove();
          }
          mcui.deliver(msg.getSender(), msg.getText());    
          tryBuffer();
        }else{
          receiveBuffer.add(msg);
        }
    }
    /*
    * Elect the next host which is alive and return id.
    * If all are dead return -1.
    */
    private int leaderElection(int proposedLeader){
      for(int i = 0; i < hosts ; i++){
        if(alive[i] == 1) {
          return i;
        }
      }
      return -1; 
    }

    private void askForTicket(MessageText message){
      if(leader == 1){
      }
      bcom.basicsend(leader,new Ticket(id,message));
    }
}
