
import mcgui.*;

public class LeaderMessage extends Message {
    private int leaderId;
    /**
     * constructor for sending a ack.
     */
    public LeaderMessage(int sender,int leader) {
      super(sender);
      leaderId = leader;
      
    }
    public int getLeader(){
      return leaderId;
    }
    /**
     * Returns the text of the message only. The toString method can
     * be implemented to show additional things useful for debugging
     * purposes.
     */

    public static final long serialVersionUID = 0;
}
