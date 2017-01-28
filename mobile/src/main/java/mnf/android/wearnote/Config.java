package mnf.android.wearnote;

import com.activeandroid.query.Select;

import java.util.List;
import java.util.Random;

import mnf.android.wearnote.Model.Note;

/**
 * Created by muneef on 26/01/17.
 */

public class Config {
   static int min = 1000;
    static int max = 999999999;
    public static int generateRandomNumberGenerate(){





        Random r = new Random();
        int ran = r.nextInt(max - min + 1) + min;

        List<Note> notes =new Select()
                .all()
                .from(Note.class)
                .execute();
        if (notes!=null&&notes.size()!=0)
        for (Note note: notes) {
            if (note.idn==ran){
                generateRandomNumberGenerate();
            }
        }


        return ran;


    }




}
