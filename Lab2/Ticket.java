
import mcgui.*;

public class Ticket extends Message {
        
    private String text;  
    private long seqNr;
    private int senderId;
    /**
     * constructor for sending a ack.
     */
    public ExampleMessage(int sender,String text) {
        super(sender);
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

    public int getSenderId(){
      return senderId;
    }
    public static final long serialVersionUID = 0;
}
