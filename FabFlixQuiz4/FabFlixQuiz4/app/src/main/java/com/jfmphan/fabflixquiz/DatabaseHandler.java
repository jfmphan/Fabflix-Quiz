package com.jfmphan.fabflixquiz;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Justin on 3/14/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper implements Serializable
{
    private static final String DATABASE_NAME = "Fabflix";
    private static final String TABLE_MOVIES = "movies";
    private static final String TABLE_STARS = "stars";
    private static final String TABLE_STARS_IN_MOVIES = "stars_in_movies";

    private static final int MOVIE_COUNT = 242;
    private static final int STARS_COUNT = 257;
    private static final int STARS_IN_MOVIES_COUNT = 531;
    private static final int DIRECTOR_COUNT = 163;

    private Random rng = new Random();

    private Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_MOVIES = "CREATE TABLE movies(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT NOT NULL," +
            "year INTEGER NOT NULL," +
            "director TEXT NOT NULL," +
            "banner_url TEXT," +
            "trailer_url TEXT);";

    private static final String CREATE_TABLE_STARS = "CREATE TABLE stars(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "first_name TEXT NOT NULL," +
            "last_name TEXT NOT NULL," +
            "dob date," +
            "photo_url TEXT);";

    private static final String CREATE_TABLE_STARS_IN_MOVIES = "CREATE TABLE stars_in_movies(" +
            "star_id INTEGER," +
            "movie_id INTEGER," +
            "PRIMARY KEY(star_id, movie_id)," +
            "FOREIGN KEY(star_id) REFERENCES stars(id) ON DELETE CASCADE," +
            "FOREIGN KEY(movie_id) REFERENCES movies(id) ON DELETE CASCADE);";



    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        System.out.println("Linking database complete");
        create();

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        System.out.println("Creating the tables.");

        db.execSQL(CREATE_TABLE_MOVIES);
        db.execSQL(CREATE_TABLE_STARS);
        db.execSQL(CREATE_TABLE_STARS_IN_MOVIES);

        System.out.println("Finished creating the tables.");

        AssetManager manager = context.getAssets();
        try
        {


            InputStream is = manager.open("movies.csv");
            populateTables(db, new InputStreamReader(is), "movies");
            is.close();

            is = manager.open("stars.csv");
            populateTables(db, new InputStreamReader(is), "stars");
            is.close();

            is = manager.open("stars_in_movies.csv");
            populateTables(db, new InputStreamReader(is), "stars_in_movies");
            is.close();



        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String drop = "DROP TABLE IF EXISTS ";
        db.execSQL(drop + TABLE_MOVIES);
        db.execSQL(drop + TABLE_STARS);
        db.execSQL(drop + TABLE_STARS_IN_MOVIES);


        onCreate(db);
    }

    public ArrayList<String> getQuestion1()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT title, director FROM movies ORDER BY RANDOM() LIMIT 4;";


        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> list = new ArrayList<String>();

        cursor.moveToFirst();
        System.out.println(cursor.getString(1));
        list.add(cursor.getString(0));
        list.add(cursor.getString(1));

        while(cursor.moveToNext())
        {
            list.add(cursor.getString(1));
        }

        return list;
    }

    public ArrayList<String> getQuestion2()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT title, year FROM movies ORDER BY RANDOM() LIMIT 4;";


        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> list = new ArrayList<String>();

        cursor.moveToFirst();

        System.out.println(cursor.getString(1));

        list.add(cursor.getString(0));
        list.add(cursor.getString(1));
        while(cursor.moveToNext())
        {
            list.add(cursor.getString(1));
        }

        return list;
    }

    public ArrayList<String> starInMovie() {

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> question = new ArrayList<String>();
        int offset = this.rng.nextInt(STARS_IN_MOVIES_COUNT - 30) + 1;
        String query = "SELECT m.title, s.first_name, s.last_name FROM Movies m, Stars s, Stars_in_movies sm WHERE m.id=sm.movie_id AND sm.star_id=s.id LIMIT " + offset + ",1";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();

        String title = cur.getString(0);
        String first_name = cur.getString(1);
        String last_name = cur.getString(2);
        String full_name = first_name + " " + last_name;

        question.add("Which star was in " + title + "?"); // question text  at INDEX 0
        question.add(full_name);// answer INDEX 1

        int offset2 = this.rng.nextInt(STARS_COUNT - 50) + 1;
        Cursor cur2 = db.query(true, "Stars", new String[] {"first_name", "last_name"}, null, null, null, null, null, offset2 + ",5");
        cur2.moveToFirst();
        int choice = 2;
        while(!cur2.isAfterLast() && choice < 5){
            String current_star = cur2.getString(0) + " " + cur2.getString(1);
            if (!current_star.equals(full_name)) {
                question.add(current_star);
                choice++;
            }
            cur2.moveToNext();
        }
        cur.close();
        cur2.close();

        return question;
    }

    public ArrayList<String> starsAppearTogether() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> question = new ArrayList<String>();
        int offset = this.rng.nextInt(50) + 1;
        String query = "SELECT title, GROUP_CONCAT(DISTINCT (s.first_name || ' ' || s.last_name)) AS stars FROM Movies m, Stars s, Stars_in_movies sm WHERE m.id=sm.movie_id AND sm.star_id=s.id GROUP BY m.title HAVING COUNT(s.id) > 1 LIMIT " + offset + ",1";

        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();

        String title = cur.getString(0);
        String[] stars = cur.getString(1).split(",");
        String star1 = stars[0];
        String star2 = stars[1];
        String two_stars = star1 + " and " + star2;

        question.add("Which two stars appeared in " + title + "?"); // question text
        question.add(two_stars);// answer

        int offset2 = this.rng.nextInt(STARS_COUNT - 80) + 1;
        Cursor cur2 = db.query(true, "Stars", new String[] {"first_name", "last_name"}, null, null, null, null, null, offset2 + ",60");
        cur2.moveToFirst();
        int choice = 2;
        while(!cur2.isAfterLast() && choice < 5){
            String current_star1 = cur2.getString(0) + " " + cur2.getString(1);
            cur2.moveToNext();
            String current_star2 = cur2.getString(0) + " " + cur2.getString(1);

            if (!current_star1.equals(star1) && !current_star1.equals(star2) && !current_star2.equals(star1) && !current_star2.equals(star2)) {
                question.add(current_star1 + " and " + current_star2);
                choice++;
            }
            cur2.moveToNext();
        }
        cur.close();
        cur2.close();

        return question;
    }

    public ArrayList<String> whoDirectedStar() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> question = new ArrayList<String>();
        int offset = this.rng.nextInt(STARS_IN_MOVIES_COUNT - 1) + 1;
        String query = "SELECT m.director, s.first_name, s.last_name FROM Movies m, Stars s, Stars_in_movies sm WHERE m.id=sm.movie_id AND sm.star_id=s.id LIMIT " + offset + ",1";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();

        String director = cur.getString(0);
        String first_name = cur.getString(1);
        String last_name = cur.getString(2);
        String full_name = first_name + " " + last_name;

        question.add("Who directed the star " + full_name + "?"); // question text
        question.add(director);// answer

        int offset2 = this.rng.nextInt(DIRECTOR_COUNT - 5) + 1;
        Cursor cur2 = db.query(true, "Movies", new String[] {"director"}, null, null, null, null, null, offset2 + ",5");
        cur2.moveToFirst();
        int choice = 2;
        while(!cur2.isAfterLast() && choice < 5){
            String current_director = cur2.getString(0);
            if (!current_director.equals(director)) {
                question.add(current_director);
                choice++;
            }
            cur2.moveToNext();
        }
        cur.close();
        cur2.close();

        return question;
    }

    public ArrayList<String> starInBothMovies() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> question = new ArrayList<String>();
        int offset = this.rng.nextInt(50) + 1;
        String query = "SELECT first_name, last_name, GROUP_CONCAT(DISTINCT (m.title)) AS movies FROM Movies m, Stars s, Stars_in_movies sm WHERE m.id=sm.movie_id AND sm.star_id=s.id GROUP BY s.id HAVING COUNT(m.id) > 1 LIMIT " + offset + ",1";

        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();

        String first_name = cur.getString(0);
        String last_name = cur.getString(1);
        String full_name = first_name + " " + last_name;
        String[] movies = cur.getString(2).split(",");
        String movie1 = movies[0];
        String movie2 = movies[1];
        String two_movies = movie1 + " and " + movie2;

        question.add("Which star appeared in both " + two_movies + "?"); // question text
        question.add(full_name);// answer

        int offset2 = this.rng.nextInt(STARS_COUNT - 50) + 1;
        Cursor cur2 = db.query(true, "Stars", new String[] {"first_name", "last_name"}, null, null, null, null, null, offset2 + ",5");
        cur2.moveToFirst();
        int choice = 2;
        while(!cur2.isAfterLast() && choice < 5){
            String current_star = cur2.getString(0) + " " + cur2.getString(1);
            if (!current_star.equals(full_name)) {
                question.add(current_star);
                choice++;
            }
            cur2.moveToNext();
        }
        cur.close();
        cur2.close();

        return question;
    }

    public ArrayList<String> starsNotInSameMovie() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> question = new ArrayList<String>();
        int offset = this.rng.nextInt(20) + 1;
        String query = "SELECT title, GROUP_CONCAT(DISTINCT (s.first_name || ' ' || s.last_name)) AS stars FROM Movies m, Stars s, Stars_in_movies sm WHERE m.id=sm.movie_id AND sm.star_id=s.id GROUP BY m.title HAVING COUNT(s.id) > 3 LIMIT " + offset + ",1";

        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();

        String title = cur.getString(0);
        String[] stars = cur.getString(1).split(",");
        String star1 = stars[0];
        String star2 = stars[1];
        String star3 = stars[2];
        String star4 = stars[3];


        question.add("Which star did not appear in " + title + " with " + star1 + "?"); // question text


        int offset2 = this.rng.nextInt(STARS_COUNT - 80) + 1;
        Cursor cur2 = db.query(true, "Stars", new String[] {"first_name", "last_name"}, null, null, null, null, null, offset2 + ",60");
        cur2.moveToFirst();
        while(!cur2.isAfterLast()){
            String current_star = cur2.getString(0) + " " + cur2.getString(1);

            if (!current_star.equals(star1) && !current_star.equals(star2) && !current_star.equals(star3) && !current_star.equals(star4)) {
                question.add(current_star);// answer

                break;
            }
            cur2.moveToNext();
        }

        question.add(star2);
        question.add(star3);
        question.add(star4);

        cur.close();
        cur2.close();

        return question;
    }

    public ArrayList<String> directedStarInYear() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> question = new ArrayList<String>();
        int offset = this.rng.nextInt(STARS_IN_MOVIES_COUNT - 1) + 1;
        String query = "SELECT m.director, s.first_name, s.last_name, m.year FROM Movies m, Stars s, Stars_in_movies sm WHERE m.id=sm.movie_id AND sm.star_id=s.id LIMIT " + offset + ",1";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();

        String director = cur.getString(0);
        String first_name = cur.getString(1);
        String last_name = cur.getString(2);
        String full_name = first_name + " " + last_name;
        int year = cur.getInt(3);

        question.add("Who directed the star " + full_name + " in the year " + year + "?"); // question text
        question.add(director);// answer


        int offset2 = this.rng.nextInt(DIRECTOR_COUNT - 5) + 1;
        Cursor cur2 = db.query(true, "Movies", new String[] {"director"}, null, null, null, null, null, offset2 + ",5");
        cur2.moveToFirst();
        int choice = 2;
        while(!cur2.isAfterLast() && choice < 5){
            String current_director = cur2.getString(0);
            if (!current_director.equals(director)) {
                question.add(current_director);
                choice++;
            }
            cur2.moveToNext();
        }
        cur.close();
        cur2.close();

        return question;
    }

    private void populateTables(SQLiteDatabase db, InputStreamReader is, String tableName)
    {
        System.out.println("Populating table " + tableName);

        BufferedReader csvReader = new BufferedReader(is);
        db.beginTransaction();

        try
        {

            String line = csvReader.readLine();
            String[] columns = line.split(",");
            while((line = csvReader.readLine()) != null)
            {
                String[] data = line.split(",");
                ContentValues values = new ContentValues();
                for(int i = 0; i < columns.length; i++)
                {
                    if(i >= data.length)
                    {
                        values.put(columns[i], "null");
                    }
                    else
                        values.put(columns[i], data[i].trim());
                }
                db.insert(tableName, null, values);

            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        System.out.println("Finished populating table " + tableName);
    }

    private void create()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }

}
