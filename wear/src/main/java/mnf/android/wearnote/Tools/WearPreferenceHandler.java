package mnf.android.wearnote.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import mnf.android.wearnote.R;

/**
 * Created by muneef on 03/02/17.
 */

public class WearPreferenceHandler {
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context c;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "settings_pref";
    final String fontSize= "font_size";
    final String theme= "theme";
    final String fontColor="color";
    final String fontStyle="font_style";




    @SuppressLint("CommitPrefEdits")
    public WearPreferenceHandler(Context context) {
        this.c = context;
        pref = c.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setFontSize(String var){
        Log.e("TAG","setFontSize Set"+var );
        editor.putString(fontSize, var);
        editor.commit();
    }
    public String getFontSize(){
        return pref.getString(fontSize, "2");
    }

    public void setTheme(boolean var){
        Log.e("TAG","setTheme Set"+var );
        editor.putBoolean(theme, var);
        editor.commit();
    }
    public boolean getTheme(){
        return pref.getBoolean(theme, false);
    }


    public void setFontColor(int var){
        Log.e("TAG","setFontColor Set"+var );
        editor.putInt(fontColor, var);
        editor.commit();
    }
    public int getFontColor(){
        return pref.getInt(fontColor, c.getColor(R.color.grey800));
    }


    public void setFontStyle(String var){
        Log.e("TAG","setFontStyle Set"+var );
        editor.putString(fontStyle, var);
        editor.commit();
    }
    public String getFontStyle(){
        return pref.getString(fontStyle, "1");
    }

}
