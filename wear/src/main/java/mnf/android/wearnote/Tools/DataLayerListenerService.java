package mnf.android.wearnote.Tools;

import android.net.Uri;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.List;

import mnf.android.wearnote.AppController;
import mnf.android.wearnote.Model.BaseModel;
import mnf.android.wearnote.Model.Note;

/**
 * Created by muneef on 30/01/17.
 */

public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.e("TAG", "onDataChanged ");
        super.onDataChanged(dataEventBuffer);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);
        for (DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri != null ? uri.getPath() : null;
            final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

            if ("/notes".equals(path)) {
                // read your values from map:
                // int color = map.getInt("color");
                String stringExample = map.getString("database");
                Log.e("TAG", "the data got = " + stringExample);
                GsonBuilder builder = new GsonBuilder();
                builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                Gson gson = builder.create();
                BaseModel baseModel =  gson.fromJson(stringExample, BaseModel.class);
                Log.e("TAG", "baseModel = " + baseModel.getNote().size());
                saveDataToDB(baseModel);
            }
            if("/pref".equals(path)){
                Log.e("TAG","pref f_size = "+map.getString("font_size")+" theme "+map.getBoolean("theme")+" color = "+map.getInt("font_color")+" style = "+map.getString("font_style"));
                WearPreferenceHandler pref = new WearPreferenceHandler(AppController.getInstance());
                pref.setFontSize(map.getString("font_size"));
                pref.setTheme(map.getBoolean("theme"));
                pref.setFontColor(map.getInt("font_color"));
                pref.setFontStyle(map.getString("font_style"));

            }
        }
    }

    public void saveDataToDB(BaseModel model){

        Note.truncate(Note.class);

        ActiveAndroid.beginTransaction();
        try {
            for (Note modelNote : model.getNote()) {
                Note newNote = new Note();
                newNote.setIdn( modelNote.getIdn());
                newNote.setTitle(modelNote.getTitle());
                newNote.setBody(modelNote.getBody());
                newNote.setDate(modelNote.getDate());
                newNote.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
            Log.e("TAG","Active Android end transation");
        }
    }
}



