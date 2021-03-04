package org.tensorflow.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Homevol extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    public RequestQueue mRequestQue;
    public String URL = "https://fcm.googleapis.com/fcm/send";
    public static String subscriber;
    ImageView imgViewBell, imgViewDisable;
    static String  pushID;
    Switch aSwitch;
    boolean isChecked = false;

    //SwitchButton swithButton;
    SharedPreferences.Editor prefEditor;
    SharedPreferences prefs;

    AlertDialog.Builder builder;

    // Change
    private RecyclerView recyclerView;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    String notificationlastUid;
    private List<UserData> result;
    UserAdapter adapter;
    Button btnClear;
    String volUID;
    String isEnable;
    TextView m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homevol);

        aSwitch = findViewById(R.id.swithButtonn);

        Splashmn.ifVol = true;
        builder = new AlertDialog.Builder(this);

        m = findViewById(R.id.m);
        imgViewDisable = findViewById(R.id.imageView2);
        imgViewBell = findViewById(R.id.imageView);
        btnClear = findViewById(R.id.btnClear);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        subscriber = getIntent().getStringExtra("sub");

        mRequestQue = Volley.newRequestQueue(this);
   //     FirebaseMessaging.getInstance().subscribeToTopic("vol");

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("pNo","");

        mRef.child("volunteer").child("uid").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //    Toast.makeText(Homevol.this, "Enable", Toast.LENGTH_SHORT).show();
                volUID = dataSnapshot.getValue().toString();
                mRef.child("volunteer").child(volUID).child("enable").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                             isEnable = dataSnapshot.getValue().toString();
                            if (isEnable.equals("0")) {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("vol");
                                aSwitch.setChecked(false);
                                isChecked = false;
                                recyclerView.setVisibility(View.INVISIBLE);
                                imgViewDisable.setVisibility(View.VISIBLE);
                                m.setVisibility(View.VISIBLE);
                            } else {
                                FirebaseMessaging.getInstance().subscribeToTopic("vol");
                                isChecked = true;
                                aSwitch.setChecked(true);
                                recyclerView.setVisibility(View.VISIBLE);
                                imgViewDisable.setVisibility(View.INVISIBLE);
                                m.setVisibility(View.INVISIBLE);
                            }
                        }catch (Exception e){
                            Log.e("Error ","avsfv");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked){
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Homevol.this);
                    alertDialogBuilder.setTitle("Help");
                    alertDialogBuilder.setMessage("Do you want to disable help button ?");

                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("vol");
                            mRef.child("volunteer").child(volUID).child("enable").setValue(0);
                            aSwitch.setChecked(false);
                            recyclerView.setVisibility(View.INVISIBLE);
                            imgViewDisable.setVisibility(View.VISIBLE);
                            m.setVisibility(View.VISIBLE);
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aSwitch.setChecked(true);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Homevol.this);
                    alertDialogBuilder.setTitle("Help");
                    alertDialogBuilder.setMessage("You will receive help request on enabling switch. Do you want to enable switch");

                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseMessaging.getInstance().subscribeToTopic("vol");
                            mRef.child("volunteer").child(volUID).child("enable").setValue(1);
                            aSwitch.setChecked(true);
                            recyclerView.setVisibility(View.VISIBLE);
                            imgViewDisable.setVisibility(View.INVISIBLE);
                            m.setVisibility(View.INVISIBLE);
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aSwitch.setChecked(false);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

//    aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
//            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Homevol.this);
//            alertDialogBuilder.setTitle("Available");
//            alertDialogBuilder.setMessage("You will receive help request on enabling switch. Do you want to enable switch.");
//alertDialogBuilder.setPositiveButton("Yes",null);
//alertDialogBuilder.setNegativeButton("No",null);
//
//            if (count) {
//                if ((aSwitch.isChecked())) {
//                    alertDialogBuilder.setMessage("Do you want to enable help button ?")
//                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    aSwitch.setChecked(true);
//                                    isCheckedVar = true;
//                                }
//                            })
//                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    aSwitch.setChecked(false);
//                                    isCheckedVar = false;
//                                    dialog.cancel();
//                                }
//                            });
//
//                } else {
//                    alertDialogBuilder.setMessage("Do you want to disable help button ?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //  Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
//                                    //  Toast.LENGTH_SHORT).show();
//                                    aSwitch.setChecked(false);
//                                    //  switchCompat.set
//                                    //  switchCompat.setEnabled(true);
//
//                                }
//                            })
//                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    aSwitch.setChecked(true);
//                                    dialog.cancel();
//                                }
//                            });
//                }
//
//
//                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        aSwitch.setChecked(true);
//
//                        dialog.dismiss();
//                    }
//                });
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//            }
//        }
//    });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Homevol.this);
                alertDialogBuilder.setTitle("Clear Notifications");
                alertDialogBuilder.setMessage("You are sure you want to delete all notifications?");

                alertDialogBuilder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lastUid", pushID);
                        editor.apply();

                        result.clear();
                        adapter.notifyDataSetChanged();

                        setEmptyNotiMsg();
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                setEmptyNotiMsg();
            }
        });

//        if (getIntent().hasExtra("category")){
//            Toast.makeText(this, "In Vol Home", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(Homevol.this, NotificationActivity.class);
//            intent.putExtra("sub", getIntent().getStringExtra("sub"));
//            intent.putExtra("category", getIntent().getStringExtra("category"));
//            intent.putExtra("brandID", getIntent().getStringExtra("brandID"));
//            startActivity(intent);
//        }

//        findViewById(R.id.logOutVol).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
////                SharedPreferences.Editor editor = sharedPreferences.edit();
////                editor.clear();
////                editor.apply();
////
////                FirebaseAuth.getInstance().signOut();
////                startActivity(new Intent(Homevol.this, Selectopt.class));
////                finish();
//                startActivity(new Intent(Homevol.this, NotificationActivity.class));
//                finish();
//            }
//        });

        mRef.keepSynced(true);

        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        result = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);

        adapter = new UserAdapter(this  ,result);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getNotification();

        setEmptyNotiMsg();

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
    }

    private void getNotification() {

        Query notificationRef = mRef.child("notifications");

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        notificationlastUid = sharedPreferences.getString("lastUid","");

        if (notificationlastUid.isEmpty()) {
            notificationRef = notificationRef.orderByKey().limitToLast(1);
        }else {
            notificationRef = notificationRef.orderByKey().startAt(notificationlastUid);
        }

        notificationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Toast.makeText(Homevol.this, "Msg Retrived", Toast.LENGTH_SHORT).show();

                    String uName = dataSnapshot.child("username").getValue(String.class);
                    Integer newState = dataSnapshot.child("newState").getValue(Integer.class);
                    Long timeStamp = dataSnapshot.child("timestamp").getValue(Long.class);
                    String body = dataSnapshot.child("msg").getValue(String.class);
                    String subID = dataSnapshot.child("subID").getValue(String.class);
                    String link = dataSnapshot.child("link").getValue(String.class);
                    String msgid = dataSnapshot.getKey();
                    result.add(0, new UserData(uName, body, link, subID, timeStamp, newState, msgid));
                    adapter.notifyDataSetChanged();

                    setEmptyNotiMsg();

                    pushID = dataSnapshot.getKey();

                    if (notificationlastUid.isEmpty()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lastUid", pushID);
                        editor.apply();
                    }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
      NavigationView navigationView=(NavigationView)findViewById(R.id.drawer);
  //      final SwitchCompat switchCompat =(SwitchCompat) findViewById(R.id.swithButton);
    //    switchCompat.setChecked(false);
    //    switchCompat.setEnabled(false);

        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
//                Toast.makeText(this, "Sub : " +subscriber , Toast.LENGTH_SHORT).show();
//                sendNotification();
                FirebaseMessaging.getInstance().unsubscribeFromTopic("vol");

                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Homevol.this, Selectopt.class));
                finish();
                Toast.makeText(Homevol.this, "Logout Selected", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.nav_endis:
//                final SwitchCompat switchCompat =(SwitchCompat) findViewById(R.id.swithButtonn);
//                switchCompat.setChecked(true);

               /* swithButton= findViewById(R.id.swithButton);
                prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());*/

/*

                AlertDialog.Builder builder1 = new AlertDialog.Builder(Homevol.this);

                View views = LayoutInflater.from(Homevol.this).inflate(R.layout.activity_enadis, null);
*/

//                View views = getLayoutInflater().inflate(R.layout.activity_enadis,null);

             /*   swithButton=new SwitchButton(this);

                prefEditor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                swithButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                        if (isChecked){

                            // DO what ever you want here when button is enabled
                            Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
                            prefEditor.putString("checked","yes");
                            prefEditor.apply();
                            Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();

                        }else {

                            // DO what ever you want here when button is disabled
                            Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
                            prefEditor.putString("unchecked","yes");
                            prefEditor.apply();
                            Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
                        }


                    }
                });*/


              /*  if (prefs.getString("checked","no").equals("yes")){

                    swithButton.setChecked(true);
                    Toast.makeText(Homevol.this, "Enabled", Toast.LENGTH_SHORT).show();

                }else {

                    swithButton.setChecked(false);
                    Toast.makeText(Homevol.this, "Disabled", Toast.LENGTH_SHORT).show();
                }
*/
/*

                builder1.setView(views);
                builder1.create().show();
*/
//builder.setPositiveButton("yes",null);
//builder.setNegativeButton("No",null);
//              //  builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);
//                //Setting message manually and performing action on button click
//                if (!(switchCompat.isChecked()))
//                {
//                    builder.setMessage("Do you want to enable help button ?")
//                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //  Action for 'NO' Button
//                                    //   Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
//                                    //   Toast.LENGTH_SHORT).show();
//
//                                    switchCompat.setChecked(true);
//
//                                    ///   switchCompat.setEnabled(false);
//
//
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            dialog.cancel();
//
//                        }
//                    });
////builder.setNegativeButton("No",null);
//
//                }else
//                {
//                    builder.setMessage("Do you want to disable help button ?")
//
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //  Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
//                                    //  Toast.LENGTH_SHORT).show();
//                                    switchCompat.setChecked(false);
//                                    //  switchCompat.set
//                                    //  switchCompat.setEnabled(true);
//
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                               dialog.cancel();
//                                }
//                            });
//                }
//
////                        .setCancelable(false)
//
//
//                //Creating dialog box
//                AlertDialog alert = builder.create();
//                //Setting the title manually
//                alert.setTitle("AlertDialogExample");
//                alert.show();
//
//                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if(isChecked)
//                        {
//                            FirebaseMessaging.getInstance().subscribeToTopic("vol");
//                            Toast.makeText(Homevol.this, "You will receive help request from blind", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//                            FirebaseMessaging.getInstance().unsubscribeFromTopic("vol");
//                            Toast.makeText(Homevol.this, "You will not receive help request from blind", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//
//
//        // Toast.makeText(Homevol.this, "Enable/Disable Selected", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.nav_contact:
                Intent intent2 = new Intent(Homevol.this, Contactus.class);
                startActivity(intent2);
                break;
            case R.id.nav_abtus:
                Intent intent = new Intent(Homevol.this, Abtus.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    private void setEmptyNotiMsg() {
        if (result.isEmpty()) {
            imgViewBell.setVisibility(View.VISIBLE);
        } else {
            imgViewBell.setVisibility(View.INVISIBLE);
        }
    }

//    private void sendNotification(){
//        JSONObject mainObj = new JSONObject();
//        try {
//
//            mainObj.put("to", "/topics/"+subscriber);
//            JSONObject notificationObj = new JSONObject();
//            notificationObj.put("title","any title");
//            notificationObj.put("body", "any body");
//
//            JSONObject extraData = new JSONObject();
//            extraData.put("sub", "vol");
//            extraData.put("brandID", "puma");
//            extraData.put("category","shoes");
//
//            mainObj.put("notification", notificationObj);
//            mainObj.put("data",extraData);
//
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
//                    mainObj, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }
//            ){
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String,String> header = new HashMap<>();
//                    header.put("content-type","application/json");
//                    header.put("authorization","key=AIzaSyDcjZz_-JrVxswr0TsQ6_3F3S2e85QiSVE");
//                    return header;
//                }
//            };
//
//            mRequestQue.add(request);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

//    protected String getRandomString(){
//        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        StringBuilder salt = new StringBuilder();
//        Random random = new Random();
//        while (salt.length() < 6){
//            int index = (int)(random.nextFloat()*SALTCHARS.length());
//            salt.append(SALTCHARS.charAt(index));
//        }
//        String saltStr = salt.toString();
//        return saltStr;
//    }
//
//
}

