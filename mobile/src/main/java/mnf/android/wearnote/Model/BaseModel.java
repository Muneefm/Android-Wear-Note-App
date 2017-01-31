package mnf.android.wearnote.Model;

import java.util.List;

/**
 * Created by muneef on 31/01/17.
 */

public class BaseModel {
    private List<Note> note;
    public void setNote(List<Note> note) {
        this.note = note;
    }
    public List<Note> getNote() {
          return this.note;
    }
}

