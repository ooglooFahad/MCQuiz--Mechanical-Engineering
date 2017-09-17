package lovestar.com.mcquiz_mechanicalengineering;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import im.delight.apprater.AppRater;

public class MainActivity extends AppCompatActivity {
    GridView lv;
    private AdView mAdView;
    ProgressDialog pd;
    ArrayList<String> AR_SID, AR_SNAME;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        toolbarTop.setLogo(R.drawable.icon_mcquiz);
        SpannableString title = new SpannableString(" MCQuiz");
        title.setSpan(new TypefaceSpan(this, "BLKCHCRY.TTF"), 0, title.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbarTop.setTitle(title);
        toolbarTop.setTitleTextColor(getResources().getColor(R.color.text_color));
        setSupportActionBar(toolbarTop);

        AppRater appRater = new AppRater(this);
        appRater.setDaysBeforePrompt(1);
        appRater.setLaunchesBeforePrompt(3);
        appRater.setPhrases(R.string.mcquiz, R.string.Rate_discription, R.string.rate_now,R.string.rate_later, R.string.rate_never);


        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        AR_SID = new ArrayList<>();
        AR_SNAME = new ArrayList<>();
        lv = (GridView) findViewById(R.id.lv);
        pd = new ProgressDialog(MainActivity.this, R.style.pdtheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();
        db = openOrCreateDatabase("my data base", MODE_PRIVATE, null);
        db.execSQL("create table if not exists subjects(S_ID INTEGER PRIMARY KEY,subject_name varchar(30));");
        Cursor c = db.rawQuery("select * from subjects", null);
        while (c.moveToNext()) {
            AR_SID.add(c.getString(0));
            AR_SNAME.add(c.getString(1));
            Log.d("show", "" + AR_SNAME);
        }
        CustomAdapter ca = new CustomAdapter(MainActivity.this, R.layout.row2, R.id.tv_subject, AR_SNAME);
        lv.setAdapter(ca);
        pd.dismiss();

        if (AR_SID == null || AR_SID.size() == 0) {
            if (isConnected(getApplicationContext())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String RecievedString = "";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("f_id", "3");
                        Network network = new Network("mcquiz_subject.php", params);
                        try {
                            RecievedString = network.ToRecieveDataFromWeb();
                            JsonParsing jsonparsing = new JsonParsing(RecievedString);
                            ArrayList<HashMap<String, String>> convertedarraydata = jsonparsing.ParsejsonArray(RecievedString);
                            for (int i = 0; i < convertedarraydata.size(); i++) {
                                HashMap<String, String> positionHashmap;
                                positionHashmap = convertedarraydata.get(i);
                                String subject_name = positionHashmap.get("SUBJECT_NAME");
                                String Subj_id = positionHashmap.get("S_ID");
                                db.execSQL("insert into subjects values('" + Subj_id + "','" + subject_name + "');");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            finish();
                            startActivity(getIntent());
                        }
                    }
                }).start();
            } else {
                Show_Alert();
            }
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, TabsActivity.class);
                i.putExtra("Subid", AR_SID.get(position));
                i.putExtra("subjectName", AR_SNAME.get(position));
                startActivity(i);
            }
        });
        appRater.show();
    }

    private void Show_Alert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("INTERNET ERROR");
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage("INTERNET CONNECTION PROBLUM: DO YOU WANT TO CLOSE THE APP");
        alert.setCancelable(false);
        alert.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(getIntent());
            }
        });
        alert.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.create();
        alert.show();
    }

    class CustomAdapter extends ArrayAdapter<String> {

        CustomAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.row2, parent, false);
            final TextView subj_id = (TextView) v.findViewById(R.id.tv_subjectid);
            final TextView subj_name = (TextView) v.findViewById(R.id.tv_subject);
            subj_name.setEllipsize(TextUtils.TruncateAt.END);
            subj_name.setSelected(true);
            final ImageView iv = (ImageView) v.findViewById(R.id.iv_cust_subject);
            subj_name.setText(AR_SNAME.get(position));
            subj_id.setText(AR_SID.get(position));
            Picasso.with(getApplicationContext()).load("http://mcquiz.thewebsupportdesk.com/images/" + AR_SID.get(position) + ".png")
                    .into(iv);
            return v;
        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            if (netinfo.getType() == ConnectivityManager.TYPE_WIFI || netinfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
            else return false;
        } else return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            if (!isConnected(this)) {
                Toast.makeText(this, "No Connection Available", Toast.LENGTH_SHORT).show();
            }else {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MCQuiz");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,"MCQuiz - Mechanical Engineering \n"+"https://play.google.com/store/apps/details?id=lovestar.com.mcquiz_mechanicalengineering");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
        if (id == R.id.Rules) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.quizrulesdialoge, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Quiz Rules");
            dialogBuilder.setIcon(R.mipmap.icon);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("GOT IT!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }
        if (id == R.id.Info) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.devinfodialog, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Developer Info");
            dialogBuilder.setIcon(R.mipmap.icon);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("GOT IT!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }
        if (id == R.id.Other) {
            Intent  intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:OOGLOO"));
            startActivity(intent);
        }
        if (id == R.id.Close) {
            finish();
            moveTaskToBack(true);
        }
        return super.onOptionsItemSelected(item);
    }
}