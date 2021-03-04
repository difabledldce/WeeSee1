package org.tensorflow.demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.suke.widget.SwitchButton;

public class Enadis extends AppCompatActivity {
    SwitchCompat swithButton;
    SharedPreferences.Editor prefEditor;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enadis);
        swithButton= findViewById(R.id.swithButtonn);
        prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

      /*  swithButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                        if (isChecked){

                            // DO what ever you want here when button is enabled
                           Toast.makeText(Enadis.this, "Enabled", Toast.LENGTH_SHORT).show();
                            prefEditor.putString("checked","yes");
                            prefEditor.apply();
                           Toast.makeText(Enadis.this, "Enabled", Toast.LENGTH_SHORT).show();

                        }else {

                            // DO what ever you want here when button is disabled
                            Toast.makeText(Enadis.this, "Disabled", Toast.LENGTH_SHORT).show();
                            prefEditor.putString("checked","false");
                            prefEditor.apply();
                            Toast.makeText(Enadis.this, "Enabled", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
*/

//                if (prefs.getString("checked","no").equals("yes")){
//
//                    swithButton.setChecked(true);
//                    Toast.makeText(Enadis.this, "Enabled", Toast.LENGTH_SHORT).show();
//
//                }else {
//
//                    swithButton.setChecked(false);
//                    Toast.makeText(Enadis.this, "Disabled", Toast.LENGTH_SHORT).show();
//                }

    }
}
