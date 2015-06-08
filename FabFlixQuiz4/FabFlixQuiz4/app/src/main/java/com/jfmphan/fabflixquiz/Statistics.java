package com.jfmphan.fabflixquiz;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;


public class Statistics extends ActionBarActivity
{
    private TextView score;
    private TextView numQuiz;
    private TextView numCorrect;
    private TextView numWrong;
    private TextView avgTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        score = (TextView) findViewById(R.id.score);
        numQuiz = (TextView) findViewById(R.id.numQuiz);
        numCorrect = (TextView) findViewById(R.id.numCorrect);
        numWrong = (TextView) findViewById(R.id.numWrong);
        avgTime = (TextView) findViewById(R.id.time);

//        UserStats.calcAvgTime();

        score.setText(""+UserStats.getScore());
        numQuiz.setText(""+UserStats.getNumQuizzes());
        numCorrect.setText(""+UserStats.getNumCorrect());
        numWrong.setText(""+UserStats.getNumWrong());
        avgTime.setText(""+UserStats.getAvgTime());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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
}
