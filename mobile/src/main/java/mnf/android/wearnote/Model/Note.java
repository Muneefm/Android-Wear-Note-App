package mnf.android.wearnote.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by muneef on 26/01/17.
 */
@Table(name = "note")
public class Note extends Model {
    @Column(name ="idn")
    public String idn;
    @Column(name ="title")
    public String title;
    @Column(name ="body")
    public String body;
    @Column(name ="date")
    public String date;

    public Note() {
    }

    public Note(String id, String title, String body) {
        this.title = title;
        this.idn = id;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdn() {
        return idn;
    }

    public void setIdn(String id) {
        this.idn = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}