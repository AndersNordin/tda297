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
    private static int seqNr;
    private int lastAck = -1;
    private String nextMessage = null;
    private ArrayList<ExampleMessage> buffer;

    public void init() {
        buffer = new ArrayList<ExampleMessage>();
        mcui.debug("The network has "+hosts+" hosts!");
        seqNr = 0;
        leader = Integer.MAX_VALUE;
        //Allow for everyone to start!
        try{
          Thread.sleep(500);
        }catch(Exception e){
          mcui.debug("Something happened with initial wait!");
        }
        leaderElection(id);
    }
        
    /**
     * The GUI calls this module to multicast a message
     */
    public void cast(String messagetext) {
      askForTicket(messagetext);
    }
    
    /**
     * Receive a basic message
     * @param message  The message received
     */
    public void basicreceive(int peer,Message message) {
      if(message instanceof LeaderMessage){
        LeaderMessage lmsg = (LeaderMessage)message;
        mcui.debug("Got leadermessage! proposed: " + lmsg.getLeader());

        leaderElection(lmsg.getLeader());
      }else if(message instanceof Ticket){
        Ticket ticket = (Ticket)message;
        handleTicket(peer,ticket);

      }else if (message instanceof ExampleMessage){
        ExampleMessage msg = (ExampleMessage)message;
        int seq = msg.getSeqNr();
        if(canDeliver(msg)){
          mcui.deliver(peer, msg.getText());    
          tryBuffer();
        }else{
          buffer.add(msg);
        }
      }else{
        mcui.debug("ERROR: unkown message!!");
      }
    }
    private boolean canDeliver(ExampleMessage msg){
      if(lastAck+1 == msg.getSeqNr()){
        lastAck++;
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
            ticket.setSeqNr(seqNr++);
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
    }
    private void multicastMessage(Ticket ticket){
      mcui.debug("Multicasting message");
        for(int i=0; i < hosts; i++) {
             //Sends to everyone except itself 
            if(i != id) {
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
    
    private void leaderElection(int proposedLeader){
      //mcui.debug("Leader election proposed: "+ proposedLeader);
      int next = (id+1) % hosts;
      if(id < proposedLeader && proposedLeader<leader){
        mcui.debug("leader = "+id);
        leader = id; 
      }else if(proposedLeader < leader){
        leader = proposedLeader;
      }else if(proposedLeader  == leader){
        mcui.debug("Leader is found! "+leader);
        return;
      }
      mcui.debug("Proposed leader: "+ proposedLeader+" current leader: "+leader);
      bcom.basicsend(next,new LeaderMessage(next,leader));
      return;
    }
    private void askForTicket(String message){
      /*
      if(!hasLeader){
        leader = leaderElection(id);  
        hasLeader = true;     
      }
      */
      mcui.debug("Asking for ticket");
      bcom.basicsend(leader,new Ticket(id));
      nextMessage = message;

    }

}
