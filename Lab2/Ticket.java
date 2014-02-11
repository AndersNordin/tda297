
import mcgui.*;

public class Ticket extends Message {      
    private Long seqNr = null;   

    /* 
        Constructor for creating a new ticket.        
    */
    public Ticket(int sender){
      super(sender);  
    }
    public Ticket(int sender, Long seqNr){
        super(sender);
        this.seqNr = seqNr;
    }
    
    public void setSeqNr(long seqNr) {
        this.seqNr = seqNr;
    }

    public Long getSeqNr(){
      return seqNr;
    }

    public static final long serialVersionUID = 0;
}
