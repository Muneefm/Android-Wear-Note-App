package mnf.android.wearnote.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;


/**
 * Created by muneef on 05/02/17.
 */
@Table(name = "reminder")
public class ReminderModel extends Model {
    @Column(name ="idn")
    public String idn;

    @Column(name ="noteid")
    public String noteid;

    @Column(name ="date")
    public Date date;

    @Column(name ="status")
    public int status;

  /*  @Column(name ="body")
    public String body;

    @Column(name ="title")
    public String title;
*/
    public ReminderModel() {
    }

    public ReminderModel(String id, String noteid, Date date, String title, String body) {
     //   this.title = title;
        this.idn = id;
     //   this.body = body;
        this.noteid = noteid;
        this.date = date;
    }


   /* public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
*/
    public String getIdn() {
        return idn;
    }

    public void setIdn(String id) {
        this.idn = id;
    }

  /*  public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }*/

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setNoteid(String noteid){
        this.noteid = noteid;
    }

    public String getNoteid(){
        return this.noteid;
    }

    public void setStatus(int bool){
        this.status =bool;
    }
    public int getStatus(){
        return this.status;
    }


}
