package mnf.android.wearnote.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import mnf.android.wearnote.R;

/**
 * Created by muneef on 04/02/17.
 */

public class MobilePreferenceHandler {
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context c;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "settings_pref";
    final String fontSizem= "font_size_m";
   // final String theme= "themem";
  //  final String fontColor="colorm";
    final String fontStylem="font_style_m";




    @SuppressLint("CommitPrefEdits")
    public MobilePreferenceHandler(Context context) {
        this.c = context;
        pref = c.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setFontSize(String var){
        Log.e("TAG","setFontSize Set"+var );
        editor.putString(fontSizem, var);
        editor.commit();
    }
    public String getFontSize(){
        return pref.getString(fontSizem, "2");
    }


    public void setFontStyle(String var){
        Log.e("TAG","setFontStyle Set"+var );
        editor.putString(fontStylem, var);
        editor.commit();
    }
    public String getFontStyle(){
        return pref.getString(fontStylem, "1");
    }

 /*   public void setTheme(boolean var){
        Log.e("TAG","setTheme Set"+var );
        editor.putBoolean(theme, var);
        editor.commit();
    }
    public boolean getTheme(){
        return pref.getBoolean(theme, false);
    }*/

  /*  public void setFontColor(int var){
        Log.e("TAG","setFontColor Set"+var );
        editor.putInt(fontColor, var);
        editor.commit();
    }
    public int getFontColor(){
        return pref.getInt(fontColor, R.color.grey800);
    }
*/




}
