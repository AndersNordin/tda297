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
    private int leader = id;
    private static int seqNr;
    private String nextMessage = null;
    public void init() {
        mcui.debug("The network has "+hosts+" hosts!");
        leader = leaderElection();
        seqNr = 0;
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
      if(message instanceof Ticket){
        mcui.debug("Revcieved a ticket");
        Ticket ticket = (Ticket)message;
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
      }else{
        ExampleMessage msg = (ExampleMessage)message;
        mcui.deliver(peer, msg.getText());    
      }
    }

    /**
     * Signals that a peer is down and has been down for a while to
     * allow for messages taking different paths from this peer to
     * arrive.
     * @param peer	The dead peer
     */
    public void basicpeerdown(int peer) {
        mcui.debug("Peer "+peer+" has been dead for a while now!");
    }
    public void multicastMessage(Ticket ticket){
      mcui.debug("Multicasting message");

        for(int i=0; i < hosts; i++) {
             //Sends to everyone except itself 
            if(i != id) {
                bcom.basicsend(i,new ExampleMessage(id, nextMessage,ticket));
            }
        }
        mcui.debug("Sent message: \""+nextMessage+"\"");
        mcui.deliver(id, nextMessage, "from myself!");
    }
    
    public int leaderElection(){
      /*
      int next = (id+1) % hosts;
      mcui.debug("next: " +next);
      bcom.basicsend(next,new LeaderMessage(next,leader));
      return 1;
      */
      return 0;
    }
    public void askForTicket(String message){
      mcui.debug("Asking for ticket");
      bcom.basicsend(leader,new Ticket(id));
      nextMessage = message;

    }

}
