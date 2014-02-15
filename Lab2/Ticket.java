import mcgui.*;

public class Ticket extends Message {      
    private Integer seqNr = null;   
    private int recipient;
    private boolean flood;
    private String message;

    /* 
        Constructor for creating a new ticket.        
    */
    public Ticket(int sender,String msg){
      super(sender);  
      flood = true;
      message = msg;
      recipient = sender;
    }
    public Ticket(int sender, Integer seqNr){
        super(sender);
        this.seqNr = seqNr;
    }
    
    public void setRecipient(int rec){
      recipient = rec;
    }
    public String getMessage(){
      return message;
    }
    public int getRecipient(){
      return recipient;
    }
    public void setSeqNr(int seqNr) {
        this.seqNr = seqNr;
    }

    public Integer getSeqNr(){
      return seqNr;
    }
    public void flood() {
      flood = false; 
    }
    public boolean getFlood() {
      return flood;
    }

    public static final long serialVersionUID = 0;
}
