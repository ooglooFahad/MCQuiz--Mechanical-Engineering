package lovestar.com.mcquiz_mechanicalengineering;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Asif ullah on 2/8/2017.
 */
public class SimpleMcqs extends Fragment {
    ProgressDialog pd;
    ArrayList<Question> mcqs;
    ListView lv;

    private AdView mAdView;
    String subid;
    SQLiteDatabase db;

    public SimpleMcqs() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_simple, container, false);
        mAdView = (AdView) v.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        db = getActivity().openOrCreateDatabase("my data base", getActivity().MODE_PRIVATE, null);
        db.execSQL("create table if not exists mcqs(M_ID INTEGER PRIMARY KEY,S_ID INTEGER," +
                "question varchar(255),optionA varchar(255),optionB varchar(255),optionC varchar(255)," +
                "optionD varchar(255),answer varchar(255));");
        lv = (ListView) v.findViewById(R.id.lvsimple);
        pd = new ProgressDialog(getActivity(), R.style.pdtheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();

        mcqs = new ArrayList<>();
        Bundle args = getActivity().getIntent().getExtras();
        subid = args.getString("Subid");
        showData();
        if (mcqs == null || mcqs.size() == 0) {
            if (isConnected(getActivity())) {
                copyData();
            } else {
                Show_Alert();
            }
        }
        return v;
    }

    private void showData() {
        Cursor c = db.rawQuery("select * from mcqs where S_ID = '" + subid + "'", null);
        while (c.moveToNext()) {
            Question question = new Question();
            question.mcqs_id = c.getString(0);
            question.subject_id = c.getString(1);
            question.question = c.getString(2);
            question.option1 = c.getString(3);
            question.option2 = c.getString(4);
            question.option3 = c.getString(5);
            question.option4 = c.getString(6);
            question.correctanxer = c.getString(7);
            mcqs.add(question);
        }

        CustomAdapter ca = new CustomAdapter(getActivity(), R.layout.custom_mcqs, R.id.mcqsText, mcqs);
        lv.setAdapter(ca);
        pd.dismiss();
    }

    private void copyData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String RecievedString = "";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("sub_id", subid);
                params.put("severity", "simple");
                Network network = new Network("mcquiz_mcqs.php", params);

                try {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    RecievedString = network.ToRecieveDataFromWeb();
                    JsonParsing jsonparsing = new JsonParsing(RecievedString);
                    ArrayList<HashMap<String, String>> convertedarraydata = jsonparsing.ParsejsonArray(RecievedString);

                    for (int i = 0; i < convertedarraydata.size(); i++) {
                        HashMap<String, String> positionHashmap;
                        positionHashmap = convertedarraydata.get(i);
                        String mcq_id = positionHashmap.get("M_ID");
                        String Subj_id = positionHashmap.get("S_ID");
                        String Severity = positionHashmap.get("SEVERITY_LEVEL");
                        String str_question = positionHashmap.get("QUESTION");
                        String str_opta = positionHashmap.get("OPTION_A");
                        String str_optb = positionHashmap.get("OPTION_B");
                        String str_optc = positionHashmap.get("OPTION_C");
                        String str_optd = positionHashmap.get("OPTION_D");
                        String str_answ = positionHashmap.get("ANSWER");
                        db.execSQL("insert into mcqs values('" + mcq_id + "','" + Subj_id + "','" + str_question + "'," +
                                "'" + str_opta + "','" + str_optb + "','" + str_optc + "','" + str_optd + "','" + str_answ + "');");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }
            }
        }).start();
    }

    class CustomAdapter extends ArrayAdapter<Question> {
        public CustomAdapter(Context context, int resource, int textViewResourceId, ArrayList<Question> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.custom_mcqs, parent, false);
            TextView mcqsText, correct_ans;
            RadioGroup rg;
            RadioButton opt1, opt2, opt3, opt4;
            mcqsText = (TextView) v.findViewById(R.id.mcqsText);
            correct_ans = (TextView) v.findViewById(R.id.answer);

            rg = (RadioGroup) v.findViewById(R.id.radioGroup);
            opt1 = (RadioButton) v.findViewById(R.id.optA);
            opt2 = (RadioButton) v.findViewById(R.id.optB);
            opt3 = (RadioButton) v.findViewById(R.id.optC);
            opt4 = (RadioButton) v.findViewById(R.id.optD);

            try {
                Question question = mcqs.get(position);
                mcqsText.setText("Q "+question.question);
                opt1.setText(question.option1);
                opt2.setText(question.option2);
                opt3.setText(question.option3);
                opt4.setText(question.option4);
                correct_ans.setText(question.correctanxer);

                String opt_1 = opt1.getText().toString();
                String opt_2 = opt2.getText().toString();
                String opt_3 = opt3.getText().toString();
                String opt_4 = opt4.getText().toString();
                String correct = correct_ans.getText().toString();
                String selected = question.selectedanxer + "";

                if (opt_1.equals("" + selected)) {
                    opt1.setChecked(true);
                    if (opt_1.equals(correct)) {
                        opt1.setTextColor(Color.GREEN);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            opt1.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
                        }
                    } else {
                        opt1.setTextColor(Color.RED);
                    }

                }
                if (selected.equals(opt2.getText() + "")) {
                    opt2.setChecked(true);
                    if (opt_2.equals(correct)) {
                        opt2.setTextColor(Color.GREEN);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            opt2.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
                        }
                    } else {
                        opt2.setTextColor(Color.RED);
                    }
                }
                if (selected.equals(opt3.getText() + "")) {
                    opt3.setChecked(true);
                    if (opt_3.equals(correct)) {
                        opt3.setTextColor(Color.GREEN);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            opt3.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
                        }
                    } else {
                        opt3.setTextColor(Color.RED);
                    }
                }
                if (selected.equals(opt4.getText() + "")) {
                    opt4.setChecked(true);
                    if (opt_4.equals(correct)) {
                        opt4.setTextColor(Color.GREEN);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            opt4.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
                        }
                    } else {
                        opt4.setTextColor(Color.RED);
                    }
                }
            } catch (Exception e) {
            }
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Question question1 = mcqs.get(position);
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    question1.selectedanxer = radioButton.getText().toString();
                    notifyDataSetChanged();
                }
            });

            return v;
        }
    }

    public class Question {
        String subject_id;
        String mcqs_id;
        String severity_level;
        String question;
        String option1;
        String option2;
        String option3;
        String option4;
        String selectedanxer;
        String correctanxer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pd != null) {
            pd.dismiss();
            pd = null;
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

    private void Show_Alert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("INTERNET ERROR");
        alert.setIcon(R.drawable.icon_round);
        alert.setMessage("INTERNET CONNECTION PROBLUM: DO YOU WANT TO CLOSE THE APP");
        alert.setCancelable(false);
        alert.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });
        alert.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        alert.create();
        alert.show();
    }
}