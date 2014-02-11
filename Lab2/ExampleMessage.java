import mcgui.*;

/**
 * Message implementation for ExampleCaster.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleMessage extends Message {
        
    private String text;  
    private Ticket ticket;
    private int senderId;
    private String state = null;
    /**
     * constructor for sending a ack.
     */
    public ExampleMessage(int sender,String text,Ticket ticket) {
        super(sender);
        this.ticket = ticket;
        senderId=sender;
        this.text = text;
    }
    
    /**
     * Returns the text of the message only. The toString method can
     * be implemented to show additional things useful for debugging
     * purposes.
     */
    public String getText() {
        return text;
    }
    public Integer getSeqNr(){
      return ticket.getSeqNr();
    }

    public int getSenderId(){
      return senderId;
    }
    public static final long serialVersionUID = 0;
}
