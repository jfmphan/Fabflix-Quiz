package com.jfmphan.fabflixquiz;

import android.app.Activity;
import android.os.SystemClock;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class QuizActivity extends Activity
{
    private TextView timer;
    private TextView question;
    private RadioButton ans1;
    private RadioButton ans2;
    private RadioButton ans3;
    private RadioButton ans4;
    private RadioGroup group;
    private String answer;
    private long timeRemaining;
    private DatabaseHandler handler;
    private Random random = new Random();
    private Handler qHandler = new Handler();
    private long qStart = 0L;
    private long offset =0L;
    private long elapsed =0L;
    private long time = 0L;
    private static int duration = 180000;

    private int pauseCount;


    private ArrayList<RadioButton> buttons;
    private int numCorrect;
    private int numWrong;

    private String curQuestion ="";
    private String a1 ="";
    private String a2 ="";
    private String a3 ="";
    private String a4 ="";

    private TextView feedback;
    private Button next_question;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        timer = (TextView) findViewById(R.id.timer);

        pauseCount = 0;
        offset = 0;

        question = (TextView) findViewById(R.id.question);
        ans1 = (RadioButton) findViewById(R.id.ans1);
        ans2 = (RadioButton) findViewById(R.id.ans2);
        ans3 = (RadioButton) findViewById(R.id.ans3);
        ans4 = (RadioButton) findViewById(R.id.ans4);
        group = (RadioGroup) findViewById(R.id.radioGroup);

        buttons = new ArrayList<RadioButton>();

        buttons.add(ans1);
        buttons.add(ans2);
        buttons.add(ans3);
        buttons.add(ans4);

        numCorrect = 0;
        numWrong = 0;

        handler = new DatabaseHandler(getApplicationContext());
        getQuestion();
        qStart = SystemClock.uptimeMillis();
        qHandler.post(updateTask);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if(pauseCount == 0)
        {
            offset = time;
        }

        pauseCount++;
        System.out.println("Paused");

        qHandler.removeCallbacks(updateTask);

    }


    @Override
    public void onResume()
    {
        super.onResume();
        System.out.println("Resumed");
        qHandler.postDelayed(updateTask, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickNext(View view)
    {

        boolean correct = false;
        for(RadioButton but: buttons)
        {
            if(but.isChecked())
            {
                System.out.println("Choice :" + but.getText().toString());
                System.out.println("Answer is:" + answer);

                if(but.getText().toString().equalsIgnoreCase(answer))
                {
                    numCorrect++;
                    System.out.println("Correct!");
                    correct = true;
                    break;

                }
                else
                {
                    numWrong++;
                    System.out.println("Wrong!");
                    break;
                }
            }
        }

        giveFeedback(correct);
        group.clearCheck();

    }

    private void giveFeedback(boolean correct) {
        setContentView(R.layout.feedback);
        timer = (TextView) findViewById(R.id.timer);
        this.feedback = (TextView)this.findViewById(R.id.feedback);
        this.next_question = (Button)this.findViewById(R.id.next);

        if (correct) {
            this.feedback.setText("Your answer was correct!\nYou have answered " + numCorrect + " questions correctly and " + numWrong + " wrong so far!");
        }
        else {
            this.feedback.setText("That is incorrect! The answer is: " + this.answer + "\n" + "You have answered " + numCorrect + " questions correctly and " + numWrong + " wrong so far!");
        }


        this.next_question.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Change the button image
                next_question.setBackgroundResource(R.drawable.btn_default_normal_green);
                QuizActivity.this.getQuestion();
            }
        });
    }

    private void newQuestion() {
        timer = (TextView) findViewById(R.id.timer);
        question = (TextView) findViewById(R.id.question);
        ans1 = (RadioButton) findViewById(R.id.ans1);
        ans2 = (RadioButton) findViewById(R.id.ans2);
        ans3 = (RadioButton) findViewById(R.id.ans3);
        ans4 = (RadioButton) findViewById(R.id.ans4);
        group = (RadioGroup) findViewById(R.id.radioGroup);

        buttons = new ArrayList<RadioButton>();

        buttons.add(ans1);
        buttons.add(ans2);
        buttons.add(ans3);
        buttons.add(ans4);
    }

    private void getQuestion()
    {
        setContentView(R.layout.activity_quiz);
        newQuestion();
        int choice = random.nextInt(8);

        switch (choice)
        {
            case 0: setQuestion1();
                break;
            case 1: setQuestion2();
                break;
            case 2: setQuestion3();
                break;
            case 3: setQuestion4();
                break;
            case 4: setQuestion5();
                break;
            case 5: setQuestion6();
                break;
            case 6: setQuestion7();
                break;
            case 7: setQuestion8();
                break;
        }
    }

    private void setQuestion1()
    {
        ArrayList<String> results = handler.getQuestion1();


        String q1 = "Who directed the movie, '" + results.get(0) + "'?";
        question.setText(q1);
        answer = results.get(1);
        results.remove(0);

        int ans = random.nextInt(results.size());
        ans1.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans2.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans3.setText(results.get(ans));
        results.remove(ans);
        ans4.setText(results.get(0));

    }

    private void setQuestion2()
    {
        ArrayList<String> results = handler.getQuestion2();


        String q1 = "When was the movie, '" + results.get(0) + "' released?";
        question.setText(q1);
        answer = results.get(1);
        results.remove(0);

        int ans = random.nextInt(results.size());
        ans1.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans2.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans3.setText(results.get(ans));
        results.remove(ans);
        ans4.setText(results.get(0));
    }

    private void setQuestion3(){
        ArrayList<String> results = handler.starInMovie();


        question.setText(results.get(0));
        results.remove(0);

        answer = results.get(1);
        int ans = random.nextInt(results.size());
        ans1.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans2.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans3.setText(results.get(ans));
        results.remove(ans);
        ans4.setText(results.get(0));
    }

    private void setQuestion4()
    {
        ArrayList<String> results = handler.starsAppearTogether();


        question.setText(results.get(0));
        results.remove(0);

        answer = results.get(1);
        int ans = random.nextInt(results.size());
        ans1.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans2.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans3.setText(results.get(ans));
        results.remove(ans);
        ans4.setText(results.get(0));
    }
    private void setQuestion5()
    {
        ArrayList<String> results = handler.whoDirectedStar();

        question.setText(results.get(0));
        results.remove(0);

        answer = results.get(1);
        int ans = random.nextInt(results.size());
        ans1.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans2.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans3.setText(results.get(ans));
        results.remove(ans);
        ans4.setText(results.get(0));
    }
    private void setQuestion6()
    {
        ArrayList<String> results = handler.starInBothMovies();

        question.setText(results.get(0));
        results.remove(0);

        answer = results.get(1);
        int ans = random.nextInt(results.size());
        ans1.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans2.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans3.setText(results.get(ans));
        results.remove(ans);
        ans4.setText(results.get(0));
    }
    private void setQuestion7()
    {
        ArrayList<String> results = handler.starsNotInSameMovie();

        question.setText(results.get(0));
        results.remove(0);

        answer = results.get(1);
        int ans = random.nextInt(results.size());
        ans1.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans2.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans3.setText(results.get(ans));
        results.remove(ans);
        ans4.setText(results.get(0));
    }
    private void setQuestion8()
    {
        ArrayList<String> results = handler.directedStarInYear();


        question.setText(results.get(0));
        results.remove(0);

        answer = results.get(1);
        int ans = random.nextInt(results.size());
        ans1.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans2.setText(results.get(ans));
        results.remove(ans);
        ans = random.nextInt(results.size());
        ans3.setText(results.get(ans));
        results.remove(ans);
        ans4.setText(results.get(0));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState)
    {
        a1 = ans1.getText().toString();
        a2 = ans2.getText().toString();
        a3 = ans3.getText().toString();
        a4 = ans4.getText().toString();
        curQuestion = question.getText().toString();


        savedInstanceState.putInt("pauseCount", pauseCount);
        savedInstanceState.putLong("offset", offset);
        savedInstanceState.putString("a1", a1);
        savedInstanceState.putString("a2", a2);
        savedInstanceState.putString("a3", a3);
        savedInstanceState.putString("a4", a4);
        savedInstanceState.putString("curQ", curQuestion);
        savedInstanceState.putString("answer", answer);
        savedInstanceState.putLong("time", timeRemaining);
        savedInstanceState.putInt("numCorrect", numCorrect);
        savedInstanceState.putInt("numWrong", numWrong);


        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {

        super.onRestoreInstanceState(savedInstanceState);

        ans1.setText(savedInstanceState.getString("a1"));
        ans2.setText(savedInstanceState.getString("a2"));
        ans3.setText(savedInstanceState.getString("a3"));
        ans4.setText(savedInstanceState.getString("a4"));
        question.setText(savedInstanceState.getString("curQ"));
        answer = savedInstanceState.getString("answer");
        timeRemaining = savedInstanceState.getLong("time");
        numCorrect = savedInstanceState.getInt("numCorrect");
        numWrong = savedInstanceState.getInt("numWrong");
        offset = savedInstanceState.getLong("offset");
        pauseCount = savedInstanceState.getInt("pauseCount");

    }

    private Runnable updateTask = new Runnable()
    {
        public void run()
        {
            long now = SystemClock.uptimeMillis();
            time = duration - (now - qStart);
            elapsed = time;

            if(offset > time)
            {
                elapsed = time + (offset - time);
                offset -=1000;
            }

            if (elapsed > 0)
            {
                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                if (seconds < 10) {
                    timer.setText("" + minutes + ":0" + seconds);
                } else {
                   timer.setText("" + minutes + ":" + seconds);
                }

                qHandler.postAtTime(this, now + 1000);
            }
            else
            {
                qHandler.removeCallbacks(this);

                timer.setText("Times up!");

                UserStats.addNumWrong(numWrong);
                UserStats.addNumCorrect(numCorrect);
                UserStats.incNumQuizzes();

                finish();

                elapsed = duration;
            }
        }
    };

}
