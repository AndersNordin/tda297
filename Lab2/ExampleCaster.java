import java.util.*;
import mcgui.*;

/**
 * Simple example of how to use the Multicaster interface.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleCaster extends Multicaster {
    /**
     * No initializations needed for this simple one
     */
    private int leader;//Denna borde vi kunna sätta till hosts, dvs den med störst id?
    private boolean hasLeader;
    private static int seqNr;
    private int lastAck = -1;
    private String nextMessage = null;
    private ArrayList<ExampleMessage> buffer;
    private ArrayList<String> sendBuffer;

    public void init() {
        sendBuffer = new ArrayList<String>();
        buffer = new ArrayList<ExampleMessage>();
        mcui.debug("The network has "+hosts+" hosts!");
        seqNr = 0;
        leader = Integer.MAX_VALUE;
        //Allow for everyone to start! A possible solution to this is to 
        //assume 0 as the first leader, and if it isnt a leader select a new one
        /*
        try{
          Thread.sleep(500);
        }catch(Exception e){
          mcui.debug("Something happened with initial wait!");
        }
        leaderElection(id);
        */
        //Leader election
        leader = 0;
        hasLeader = true;
    }
        
    /**
     * The GUI calls this module to multicast a message
     */
    public void cast(String messagetext) {
      if(hasLeader){
        askForTicket(messagetext);
      }else{
        mcui.debug("Adding to sendBuffer");
        sendBuffer.add(0,messagetext);
      }
    }
    
    /**
     * Receive a basic message
     * @param message  The message received
     */
    public void basicreceive(int peer,Message message) {
      if(message instanceof LeaderMessage){
        LeaderMessage lmsg = (LeaderMessage)message;
        //mcui.debug("Got leadermessage! proposed: " + lmsg.getLeader());
        leaderElection(lmsg.getLeader());
      }else if(message instanceof Ticket){
        Ticket ticket = (Ticket)message;
        handleTicket(peer,ticket);

      }else if (message instanceof ExampleMessage){
        ExampleMessage msg = (ExampleMessage)message;
        if(msg.getFlood()){
          mcui.debug("Flooding");
          flood(msg,peer);
        }
        int seq = msg.getSeqNr();
        mcui.debug("seq: "+seq);
        if(seq!=lastAck){
          if(canDeliver(msg)){
            mcui.deliver(peer, msg.getText());    
            tryBuffer();
          }else{
            buffer.add(msg);
          }
        }
      }else{
        mcui.debug("ERROR: unkown message!!");
      }
    }
    private void flood(ExampleMessage msg,int peer) {
      msg.flood();
        for(int i=0; i < hosts; i++) {
             //Sends to everyone except itself 
            if(i != id && i != peer) {
                mcui.debug("Sending flood to: "+i);
                bcom.basicsend(i,msg);
            }
        }
        mcui.debug("Flooded message: \""+msg.getText()+"\"");
    }
    private boolean canDeliver(ExampleMessage msg){
      if(lastAck+1 == msg.getSeqNr()){
        lastAck++;
        seqNr++;
        return true;
      }
      return false;
    }
    private void tryBuffer(){
      for(ExampleMessage msg : buffer) {
        //Hey i found the next one, lets deliver it and see if we find any more!
        if(lastAck+1 == msg.getSeqNr()){
          mcui.debug("Hey i found something in the buffer!");
          mcui.deliver(msg.getSender(), msg.getText());    
          buffer.remove(msg);
          lastAck++;
          tryBuffer();
          return;
        }
      }
      //Nothing was found, better luck next time!
      return;
    }
    private void handleTicket(int peer, Ticket ticket) {
        mcui.debug("Revcieved a ticket");
        if(ticket.getSeqNr() == null) {
          if(leader == id){
            mcui.debug("Populating ticket, and sending back");
            ticket.setSeqNr(seqNr);
            bcom.basicsend(peer,ticket);
          }
        }else{
          //Send message with ticket
          multicastMessage(ticket);
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
        if(peer == leader) {
          mcui.debug("New leader is needed!");
          leader = Integer.MAX_VALUE;
          leaderElection(id);
        }
        sendBuffer.add(nextMessage);
    }
    private void multicastMessage(Ticket ticket){
      mcui.debug("Multicasting message");
        for(int i=0; i < hosts; i++) {
             //Sends to everyone except itself 
            if(i != id) {
                mcui.debug("Sending to: "+i+" id: " + id);
                bcom.basicsend(i,new ExampleMessage(id, nextMessage,ticket));
            }
        }
        mcui.debug("Sent message: \""+nextMessage+"\"");
        ExampleMessage msg = new ExampleMessage(id,nextMessage,ticket);

        if(canDeliver(msg)){
          mcui.deliver(msg.getSender(), msg.getText());    
          tryBuffer();
        }else{
          buffer.add(msg);
        }
    }
    private void multicastFromBuffer() {
      for(String msg : sendBuffer) {
        mcui.debug("Sending from sendBuffer: "+msg);
        cast(msg);
      }
    }
    private void leaderElection(int proposedLeader){
      int next = (id+1) % hosts;
      mcui.debug("next: "+next);
      if(id < proposedLeader && proposedLeader<leader){
      //  mcui.debug("leader = "+id);
        leader = id; 
      }else if(proposedLeader < leader){
        leader = proposedLeader;
      }else if(proposedLeader  == leader){
        mcui.debug("Has leader");
        hasLeader = true;      
        multicastFromBuffer();
       // mcui.debug("Leader is found! "+leader);
        return;
      }
      //mcui.debug("Proposed leader: "+ proposedLeader+" current leader: "+leader);
      bcom.basicsend(next,new LeaderMessage(next,leader));
      return;
    }

    private void askForTicket(String message){
      mcui.debug("Asking for ticket");
      bcom.basicsend(leader,new Ticket(id));
      nextMessage = message;
    }

}
