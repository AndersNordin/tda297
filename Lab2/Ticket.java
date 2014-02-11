
import mcgui.*;

public class Ticket extends Message {      
    private Integer seqNr = null;   

    /* 
        Constructor for creating a new ticket.        
    */
    public Ticket(int sender){
      super(sender);  
    }
    public Ticket(int sender, Integer seqNr){
        super(sender);
        this.seqNr = seqNr;
    }
    
    public void setSeqNr(int seqNr) {
        this.seqNr = seqNr;
    }

    public Integer getSeqNr(){
      return seqNr;
    }

    public static final long serialVersionUID = 0;
}
