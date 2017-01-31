package mnf.android.wearnote.Model;

/**
 * Created by muneef on 31/01/17.
 */

public class NoteJson {
    public Integer idn;
    public String title;
    public String body;
    public String date;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIdn() {
        return idn;
    }

    public void setIdn(Integer id) {
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
