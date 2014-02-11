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
    public void init() {
        mcui.debug("The network has "+hosts+" hosts!");
        leader = leaderElection();
    }
        
    /**
     * The GUI calls this module to multicast a message
     */
    public void cast(String messagetext) {
        for(int i=0; i < hosts; i++) {
            /* Sends to everyone except itself */
            if(i != id) {
                bcom.basicsend(i,new ExampleMessage(id, messagetext));
            }
        }
        mcui.debug("Sent message: \""+messagetext+"\"");
        mcui.deliver(id, messagetext, "from myself!");
    }
    
    /**
     * Receive a basic message
     * @param message  The message received
     */
    public void basicreceive(int peer,Message message) {
      if(LeaderMessage.class.isInstance(message)){
        LeaderMessage lmsg = (LeaderMessage)message;
        mcui.debug("LeaderMessage");
        int otherId = lmsg.getLeader();
        int next = (id+1) % hosts;
        if(otherId < leader){
          leader = otherId;
          bcom.basicsend(next,new LeaderMessage(next,leader));
        }else if (leader < otherId) {
          bcom.basicsend(next,new LeaderMessage(next,leader));
        }else{
          mcui.debug("Leader is chosen: "+leader);
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
    public int leaderElection(){
      int next = (id+1) % hosts;
      mcui.debug("next: " +next);
      bcom.basicsend(next,new LeaderMessage(next,leader));
      return 1;
    }
}
