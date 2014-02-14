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
    private LinkedList<ExampleMessage> buffer;
    private LinkedList<String> sendBuffer;
    private int debugTester = 0;

    public void init() {
        sendBuffer = new LinkedList<String>();
        buffer = new LinkedList<ExampleMessage>();
        mcui.debug("The network has "+hosts+" hosts!");
        seqNr = 0;
        leader = Integer.MAX_VALUE;
        //Leader election
        leader = 0;
        hasLeader = true;
    }
    /**
     * The GUI calls this module to multicast a message
     */
    public void cast(String messagetext) {
      sendBuffer.add(messagetext);
      if(hasLeader){
        askForTicket(messagetext);
      }else{
        mcui.debug("Adding to sendBuffer");
      }
    }
    /**
     * Receive a basic message
     * @param message  The message received
     */
    public void basicreceive(int peer,Message message) {
      if(message instanceof LeaderMessage){
        LeaderMessage lmsg = (LeaderMessage)message;
        leaderElection(lmsg.getLeader());

      }else if(message instanceof Ticket){
        Ticket ticket = (Ticket)message;
        handleTicket(peer,ticket);

      }else if (message instanceof ExampleMessage){
        ExampleMessage msg = (ExampleMessage)message;
        int seq = msg.getSeqNr();
        if(msg.getFlood()){
          mcui.debug("seq: "+seq+" buffersize: "+buffer.size()+" SendSize: "+sendBuffer.size());
          flood(msg,peer);
        }
        mcui.debug("seq: "+seq+ " lastAck "+lastAck);
        if(seq > lastAck){
          if(canDeliver(msg)){
            mcui.deliver(msg.getSender(), msg.getText());    
            tryBuffer();
          }else{
            mcui.debug("Buffer message: "+msg.getSeqNr());
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
                bcom.basicsend(i,msg);
            }
        }
        //mcui.debug("Flooded message: \""+msg.getText()+"\"");
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
        if(ticket.getSeqNr() == null) {
          if(leader == id){
            mcui.debug("Populating ticket, and sending back: "+seqNr);
            ticket.setSeqNr(seqNr++);
            bcom.basicsend(peer,ticket);
          }
        }else{
          debugTester++;
          mcui.debug("ticket++: "+debugTester);
          mcui.debug("Revcieved a ticket for: "+ticket.getSeqNr());
          //Send message with ticket
          String message = sendBuffer.remove();
          multicastMessage(ticket,message);
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
    private void multicastMessage(Ticket ticket,String newMessage){
        ExampleMessage msg = new ExampleMessage(id, newMessage,ticket);
        for(int i=0; i < hosts; i++) {
             //Sends to everyone except itself 
            if(i != id) {
                bcom.basicsend(i,msg);
            }
        }
        //ExampleMessage msg = new ExampleMessage(id,newMessage,ticket);

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
      if(id < proposedLeader && proposedLeader<leader){
        leader = id; 
      }else if(proposedLeader < leader){
        leader = proposedLeader;
      }else if(proposedLeader  == leader){
        mcui.debug("Has leader");
        hasLeader = true;      
        multicastFromBuffer();
        return;
      }
      bcom.basicsend(next,new LeaderMessage(next,leader));
      return;
    }

    private void askForTicket(String message){
      mcui.debug("Asking for ticket");
      bcom.basicsend(leader,new Ticket(id));
      //nextMessage = message;
    }

}
