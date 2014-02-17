import mcgui.*;
import java.io.Serializable;

public class MessageText implements Serializable {

    private int id;
    private String msg;
    public static final long serialVersionUID = 0;

    public MessageText(int id,String msg){
      this.id = id;
      this.msg = msg;
    }

    /*
    * Get and set methods
    */
    public int getId(){
      return id;
    }

    public String getMsg(){
      return msg;
    }
}
