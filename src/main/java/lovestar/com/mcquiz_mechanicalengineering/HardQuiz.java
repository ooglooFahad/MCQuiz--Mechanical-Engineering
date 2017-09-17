package lovestar.com.mcquiz_mechanicalengineering;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Asif ullah on 2/15/2017.
 */
public class HardQuiz extends Fragment {
    ArrayList<Question> mcqs;
    CustomAdapter c;
    ListView lv;
    String subid;
    InterstitialAd mInterstitialAd;
    public static int correct, wrong, allcorrectHard, allwrongHard;
    Button submitHard;
    private AdView mAdView;
    ProgressDialog pd;

    public HardQuiz() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_hard, container, false);
        mAdView = (AdView) v.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        lv = (ListView) v.findViewById(R.id.lvhard);
        mcqs = new ArrayList<>();
        Activity activity = getActivity();
        pd = new ProgressDialog(activity, R.style.pdtheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();

        Bundle args = getActivity().getIntent().getExtras();
        subid = args.getString("Subid");
        submitHard = (Button) v.findViewById(R.id.btnsubmitHard);
        submitHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        LoadDataHard();
        return v;
    }

    private void LoadDataHard() {
        mcqs.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String RecievedString = "";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("sub_id", subid);
                params.put("severity", "hard");
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
                        allcorrectHard++;
                    } else {
                        wrong++;
                        allwrongHard++;
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
        dialogBuilder.setIcon(R.drawable.icon_round);
        dialogBuilder.setMessage("       HARD QUIZ RESULT CARD : ");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sb.append(correct = 0);
                sb1.append(wrong = 0);
                allcorrectHard = 0;
                LoadDataHard();
            }
        });
        dialogBuilder.setNegativeButton("HOME", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sb.append(correct = 0);
                sb1.append(wrong = 0);
                getActivity().finish();
            }
        });
        dialogBuilder.setNeutralButton("FINAL RESULT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                sb.append(correct = 0);
                sb1.append(wrong = 0);
                showAllDialog();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    private void showAllDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView allcorrectAns = (TextView) dialogView.findViewById(R.id.txtcorrect);
        final TextView allwrongAns = (TextView) dialogView.findViewById(R.id.txtwrong);
        final TextView allresult = (TextView) dialogView.findViewById(R.id.txtresult);
        final TextView alltotlattemp = (TextView) dialogView.findViewById(R.id.txttotlattmp);
        final TextView allnotattmp = (TextView) dialogView.findViewById(R.id.txtnotattem);
        final StringBuffer sbr = new StringBuffer();
        final int allcorrect = NormalQuiz.allcorrectNor + MediumQuiz.allcorrectMed + allcorrectHard;
        final int allwrong = NormalQuiz.allwrongNor + MediumQuiz.allwrongMed + allwrongHard;
        allcorrectAns.setText(sbr.append(allcorrect));
        final StringBuffer sbr1 = new StringBuffer();
        allwrongAns.setText(sbr1.append(allwrong));
        final StringBuffer sbr2 = new StringBuffer();
        allresult.setText(sbr2.append(allcorrect * 2));
        final StringBuffer sbr3 = new StringBuffer();
        alltotlattemp.setText(sbr3.append(allcorrect + allwrong));
        final StringBuffer sbr4 = new StringBuffer();
        allnotattmp.setText(sbr4.append(90 - (allcorrect + allwrong)));

        dialogBuilder.setTitle("FINAL RESULT");
        dialogBuilder.setMessage("FINAL RESULT CARD OF ALL TESTS");
        dialogBuilder.setIcon(R.drawable.icon_round);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("START AGAIN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                interstitionalADD();
                NormalQuiz.allcorrectNor = 0;
                MediumQuiz.allcorrectMed = 0;
                allcorrectHard = 0;
                allwrongHard = 0;
                NormalQuiz.allwrongNor = 0;
                MediumQuiz.allwrongMed = 0;
                TabsActivity.pager.setCurrentItem(1);
            }
        });
        dialogBuilder.setNegativeButton("HOME", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                interstitionalADD();
                NormalQuiz.allcorrectNor = 0;
                MediumQuiz.allcorrectMed = 0;
                allcorrectHard = 0;
                allwrongHard = 0;
                NormalQuiz.allwrongNor = 0;
                MediumQuiz.allwrongMed = 0;
                getActivity().finish();
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

    private void interstitionalADD() {
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.int_ad_unit_id));
        AdRequest adRequestInter = new AdRequest.Builder().build();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });
        mInterstitialAd.loadAd(adRequestInter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }
}