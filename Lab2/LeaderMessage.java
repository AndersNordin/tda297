
import mcgui.*;

public class LeaderMessage extends Message {

    private int leaderId;
    public static final long serialVersionUID = 0;

    /**
     * Constructor for sending a ack.
     */
    public LeaderMessage(int sender,int leader) {
      super(sender);
      leaderId = leader;
      
    }

    public int getLeader(){
      return leaderId;
    }
}
