package com.jfmphan.fabflixquiz;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by Justin on 3/14/2015.
 */
public class UserStats
{
    private static int score;
    private static int numQuizzes;
    private static int numCorrect;
    private static int numWrong;
    private static long avgTime;


    public UserStats()
    {
        score = 0;
        numQuizzes = 0;
        numCorrect = 0;
        numWrong = 0;
        avgTime = 0;
    }

    public static void addScore(int newScore)
    {
      score += newScore;
    }

    public static void incNumQuizzes()
    {
        numQuizzes++;
    }
    public static void incCorrect(){numCorrect++;}
    public static void incWrong(){numWrong++;}

    public static void addNumCorrect(int correct)
    {
        numCorrect += correct;
    }

    public static void addNumWrong(int wrong)
    {
        numWrong += wrong;
    }

    public static void calcAvgTime()
    {
        if(numQuizzes+numWrong == 0)
        {
            avgTime = 0;
        }
        else
        {
            avgTime = ((3*numQuizzes)/(numCorrect+numWrong)) * 1000;
        }
    }

    public static void reset()
    {
        score = 0;
        numWrong = 0;
        numCorrect = 0;
        numQuizzes = 0;
        avgTime = 0;
    }

    public static int getScore()
    {return score;}

    public static int getNumQuizzes()
    {return numQuizzes;}

    public static int getNumCorrect()
    {return numCorrect;}

    public static int getNumWrong()
    {return numWrong;}
    public static long getAvgTime()
    {return avgTime;}



}
