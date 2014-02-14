
import mcgui.*;

public class Ticket extends Message {      
    private Integer seqNr = null;   
    private int recipient;

    /* 
        Constructor for creating a new ticket.        
    */
    public Ticket(int sender){
      super(sender);  
      recipient = sender;
    }
    public Ticket(int sender, Integer seqNr){
        super(sender);
        this.seqNr = seqNr;
    }
    
    public void setRecipient(int rec){
      recipient = rec;
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

    public static final long serialVersionUID = 0;
}
