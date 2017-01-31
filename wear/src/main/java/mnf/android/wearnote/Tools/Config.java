package mnf.android.wearnote.Tools;

import com.activeandroid.query.Select;

import java.util.List;

import mnf.android.wearnote.Model.Note;

/**
 * Created by muneef on 31/01/17.
 */

public class Config {

    public static List<Note> getDBItems(){
        List<Note> notes =new Select()
                .all()
                .from(Note.class)
                .execute();
        return  notes;
    }
}
