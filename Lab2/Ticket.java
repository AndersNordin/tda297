
import mcgui.*;

public class Ticket extends Message {      
    private long seqNr;   

    /* 
        Constructor for creating a new ticket.        
    */
    public Ticket(int sender, long seqNr){
        super(sender);
        this.seqNr = seqNr;
    }
    
    public void setSeqNr(long seqNr) {
        this.seqNr = seqNr;
    }

    public int getSeqNr(){
      return seqNr;
    }

    public static final long serialVersionUID = 0;
}