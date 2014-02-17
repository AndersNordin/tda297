import mcgui.*;

public class Ticket extends Message {  

    private Integer seqNr = null;   
    private int recipient;
    private boolean flood;
    private MessageText message;    
    public static final long serialVersionUID = 0;

    /* 
    *  First constructor for creating a new ticket.       
    */
    public Ticket(int sender,MessageText msg){
      super(sender);  
      flood = true;
      message = msg;
      recipient = sender;
    }

    /*
    * Second constructor for creating a new ticket.
    */
    public Ticket(int sender, Integer seqNr){
        super(sender);
        this.seqNr = seqNr;
    }
    
    /*
    * Get and Set methods.
    */ 
    public void setRecipient(int rec){
      recipient = rec;
    }

    public int getRecipient(){
      return recipient;
    }

    public String getMessage(){
      return message.getMsg();
    }

    public int getMessageId(){
      return message.getId();
    }

    public void setSeqNr(int seqNr) {
        this.seqNr = seqNr;
    }

    public Integer getSeqNr(){
      return seqNr;
    }

    /* 
    * A ticket is initially flood = true.
    * Tickets may only be flooded once.
    */
    public void flood() {
      flood = false; 
    }

    public boolean getFlood() {
      return flood;
    }
}