import mcgui.*;

public class ExampleMessage extends Message {
        
    private String text;  
    private Ticket ticket;
    private int senderId;
    private String state = null;
    private boolean flood = true;
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
    public void flood(){
      flood = false;
    }
    public boolean getFlood(){
      return flood;
    }
    public Integer getSeqNr(){
      return ticket.getSeqNr();
    }
    public int getRecipient() {
      return ticket.getRecipient();
    }
    public int getMessageId(){
      return ticket.getMessageId();
    }

    public int getSenderId(){
      return senderId;
    }
    public static final long serialVersionUID = 0;
}
