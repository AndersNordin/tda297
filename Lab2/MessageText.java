import mcgui.*;
import java.io.Serializable;

public class MessageText implements Serializable {      
  
    /* 
        Constructor for creating a new ticket.        
    */
    private int id;
    private String msg;
    public MessageText(int id,String msg){
      this.id = id;
      this.msg = msg;
    }

    public int getId(){
      return id;
    }

    public String getMsg(){
      return msg;
    }
    public static final long serialVersionUID = 0;
}
