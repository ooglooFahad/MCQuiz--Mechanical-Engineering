package lovestar.com.mcquiz_mechanicalengineering;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Asif ullah on 2/15/2017.
 */
public class MediumQuiz extends Fragment {
    ProgressDialog pd;
    ArrayList<Question> mcqs;
    CustomAdapter c;
    ListView lv;
    private AdView mAdView;
    String subid;
    Button submitMedium;
    public static int correct, wrong, allcorrectMed, allwrongMed;

    public MediumQuiz() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_medium, container, false);
        mAdView = (AdView) v.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        pd = new ProgressDialog(getActivity(), R.style.pdtheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();

        lv = (ListView) v.findViewById(R.id.lvmedium);
        mcqs = new ArrayList<>();
        Bundle args = getActivity().getIntent().getExtras();
        subid = args.getString("Subid");
        submitMedium = (Button) v.findViewById(R.id.btnSumbitMedium);
        submitMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        LoadDataMedium();
        if (!isConnected(getActivity())) {
            Toast.makeText(getActivity(), "TO Start Quiz You Have To Connect to Internet", Toast.LENGTH_LONG).show();
        }
        return v;
    }

    private void LoadDataMedium() {
        mcqs.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String RecievedString = "";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("sub_id", subid);
                params.put("severity", "medium");
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


                        Question question = new Question();
                        question.subject_id = Subj_id;
                        question.mcqs_id = mcq_id;
                        question.severity_level = Severity;
                        question.question = str_question;
                        question.option1 = str_opta;
                        question.option2 = str_optb;
                        question.option3 = str_optc;
                        question.option4 = str_optd;
                        question.correctanxer = str_answ;
                        mcqs.add(question);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            c = new CustomAdapter(getActivity(), R.layout.custom_mcqs, R.id.mcqsText, mcqs);
                            lv.setAdapter(c);
                            pd.dismiss();
                        }
                    });
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
            final View v = inflater.inflate(R.layout.custom_mcqs, parent, false);
            TextView mcqsText = (TextView) v.findViewById(R.id.mcqsText);
            final TextView correct_ans = (TextView) v.findViewById(R.id.answer);

            final RadioGroup rg = (RadioGroup) v.findViewById(R.id.radioGroup);
            final RadioButton opt1 = (RadioButton) v.findViewById(R.id.optA);
            final RadioButton opt2 = (RadioButton) v.findViewById(R.id.optB);
            final RadioButton opt3 = (RadioButton) v.findViewById(R.id.optC);
            final RadioButton opt4 = (RadioButton) v.findViewById(R.id.optD);

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
                String correctans = correct_ans.getText().toString();
                String selected = question.selectedanxer + "";
                if (opt_1.equals("" + selected)) {
                    opt1.setChecked(true);
                    opt2.setClickable(false);
                    opt3.setClickable(false);
                    opt4.setClickable(false);
                    if (opt_1.equals(correctans)) {
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
                    opt1.setClickable(false);
                    opt3.setClickable(false);
                    opt4.setClickable(false);
                    if (opt_2.equals(correctans)) {
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
                    opt2.setClickable(false);
                    opt1.setClickable(false);
                    opt4.setClickable(false);
                    if (opt_3.equals(correctans)) {
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
                    opt2.setClickable(false);
                    opt3.setClickable(false);
                    opt1.setClickable(false);
                    if (opt_4.equals(correctans)) {
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
            rg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rg.getCheckedRadioButtonId() != -1) {
                        Toast.makeText(getContext(), "answer cannot be changed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int radioButtonID = group.getCheckedRadioButtonId();
                    View radio = group.findViewById(radioButtonID);
                    int pos = group.indexOfChild(radio);
                    RadioButton btn = (RadioButton) rg.getChildAt(pos);
                    String selection = (String) btn.getText();
                    if (selection.equals(correct_ans.getText().toString())) {
                        correct++;
                        allcorrectMed++;
                    } else {
                        wrong++;
                        allwrongMed++;
                    }
                    try {
                    Question question1 = mcqs.get(position);
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    question1.selectedanxer = radioButton.getText().toString();
                    notifyDataSetChanged();
                    } catch (RuntimeException e) {
                    }
                }
            });

            return v;
        }
    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView correctAns = (TextView) dialogView.findViewById(R.id.txtcorrect);
        final TextView wrongAns = (TextView) dialogView.findViewById(R.id.txtwrong);
        final TextView result = (TextView) dialogView.findViewById(R.id.txtresult);
        final TextView totlattemp = (TextView) dialogView.findViewById(R.id.txttotlattmp);
        final TextView notattmp = (TextView) dialogView.findViewById(R.id.txtnotattem);
        final StringBuffer sb = new StringBuffer();
        correctAns.setText(sb.append(correct));
        final StringBuffer sb1 = new StringBuffer();
        wrongAns.setText(sb1.append(wrong));
        final StringBuffer sb2 = new StringBuffer();
        result.setText(sb2.append(correct * 2));
        final StringBuffer sb3 = new StringBuffer();
        totlattemp.setText(sb3.append(correct + wrong));
        final StringBuffer sb4 = new StringBuffer();
        notattmp.setText(sb4.append(30 - (correct + wrong)));

        dialogBuilder.setTitle("RESULT");
        dialogBuilder.setMessage(" MEDIUM QUIZ RESULT CARD : ");
        dialogBuilder.setIcon(R.drawable.icon_round);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sb.append(correct = 0);
                sb1.append(wrong = 0);
                allcorrectMed = 0;
                allwrongMed = 0;
                LoadDataMedium();
            }
        });
        dialogBuilder.setNegativeButton("HOME", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sb.append(correct = 0);
                sb1.append(wrong = 0);
                getActivity().finish();
            }
        });
        dialogBuilder.setNeutralButton("NEXT QUIZ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sb.append(correct = 0);
                sb1.append(wrong = 0);
                TabsActivity.pager.setCurrentItem(3);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

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
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else return false;
    }
}