
import mcgui.*;

/**
 * Message implementation for ExampleCaster.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleMessage extends Message {
        
    private String text;  
    private long seqNr;
    private boolean ack;
    private int senderId;
    /**
     * constructor for sending a ack.
     */
    public ExampleMessage(int sender, boolean ack,long seqNr) {
	super(sender);
	this.ack = ack;
	this.senderId = sender;
	this.seqNr = seqNr;
	text ="";
    }

    public ExampleMessage(int sender,String text,long seqNr) {
        super(sender);
	senderId=sender;
        this.text = text;
	this.seqNr = seqNr;
	ack = false;
    }
    
    /**
     * Returns the text of the message only. The toString method can
     * be implemented to show additional things useful for debugging
     * purposes.
     */
    public String getText() {
        return text;
    }
    public long getSeqNr() { 
	return seqNr;
    }
    public boolean isAck(){
	return ack;
    }
    public int getSenderId(){
	return senderId;
    }
    public static final long serialVersionUID = 0;
}
