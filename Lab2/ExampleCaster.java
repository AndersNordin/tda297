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
    private static long seqNr;
    private static ArrayList<ExampleMessage> history = null;
    public void init() {
	history = new ArrayList<ExampleMessage>();
        mcui.debug("The network has "+hosts+" hosts!");
	seqNr = 0;
    }
        
    /**
     * The GUI calls this module to multicast a message
     */
    public void cast(String messagetext) {
	long seqNumber = ++seqNr;
        for(int i=0; i < hosts; i++) {
            /* Sends to everyone except itself */
            if(i != id) {
                bcom.basicsend(i,new ExampleMessage(id, messagetext,seqNumber));
            }
        }
        mcui.debug("Sent message: \""+messagetext+"\" seq: "+seqNr);
        mcui.deliver(id, messagetext, "from myself!");
	history.add(new ExampleMessage(id,messagetext,seqNumber));
    }
    
    /**
     * Receive a basic message
     * @param message  The message received
     */
    public void basicreceive(int peer,Message message) {
	ExampleMessage msg = (ExampleMessage)message;
	if(msg.getSeqNr()>seqNr) {
	    seqNr = msg.getSeqNr();
	}
	/**
	 * if ack, then acknowledge this, else, treat it as a normal message
	 * and answer with ack
	 */
	mcui.debug("Got message, is ack: "+msg.isAck());
	if(msg.isAck()) {
	    mcui.debug("Ack: "+msg.getSeqNr()+ " from: "+msg.getSenderId());
	}else {
	    bcom.basicsend(msg.getSenderId(),new ExampleMessage(id,true,seqNr));
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
}
