package mnf.android.wearnote;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InstallNoteActivity extends WearableActivity {


    TextView tvInstallNote;
    Button installBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_note);
        tvInstallNote = (TextView) findViewById(R.id.install_note);
        installBtn = (Button) findViewById(R.id.inst_btn);
        Typeface faceCabin=Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Cabin-Regular.ttf");
        tvInstallNote.setTypeface(faceCabin);
        installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppController().openAppInStoreOnPhone();

            }
        });
    }
}
