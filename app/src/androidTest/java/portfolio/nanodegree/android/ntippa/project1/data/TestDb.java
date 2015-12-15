/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package portfolio.nanodegree.android.ntippa.project1.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDBHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.Movies.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.MovieDetails.TABLE_NAME);

        mContext.deleteDatabase(MoviesDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the Movies entry and details entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.Movies.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> MoviesHashSet = new HashSet<String>();
        MoviesHashSet.add(MoviesContract.Movies.COLUMN_MOVIE_ID);
        MoviesHashSet.add(MoviesContract.Movies.COLUMN_TITLE);
        MoviesHashSet.add(MoviesContract.Movies.COLUMN_POSTER_URL);
        MoviesHashSet.add(MoviesContract.Movies.COLUMN_DESC);
        MoviesHashSet.add(MoviesContract.Movies.COLUMN_RATING);
        MoviesHashSet.add(MoviesContract.Movies.COLUMN_POPULARITY);
        MoviesHashSet.add(MoviesContract.Movies.COLUMN_RELEASE_DATE);



        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            MoviesHashSet.remove(columnName);
            assertFalse("deleted:" + columnName, columnNameIndex == -1);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required Movies
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required Movies entry columns.It contains" + MoviesHashSet.size() + "coulmns",
                MoviesHashSet.isEmpty());

        final HashSet<String> DetailsHashSet = new HashSet<String>();
        DetailsHashSet.add(MoviesContract.MovieDetails.COLUMN_MOVIE_KEY);
        DetailsHashSet.add(MoviesContract.MovieDetails.COLUMN_MOVIE_REVIEWS);
        DetailsHashSet.add(MoviesContract.MovieDetails.COLUMN_MOVIE_TRAILERS_URL);

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieDetails.TABLE_NAME + ")",
                null);

         columnNameIndex = c.getColumnIndex("name");
        if(c.moveToFirst() && c.getCount() >= 1) {
            do {
                String columnName = c.getString(columnNameIndex);
                DetailsHashSet.remove(columnName);
                assertFalse("deleted:" + columnName, columnNameIndex == -1);
            } while (c.moveToNext());
        }

        // if this fails, it means that your database doesn't contain all of the required Movies
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required Details entry columns.It contains" + DetailsHashSet.size() + "coulmns",
                DetailsHashSet.isEmpty());

        c.close();

        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
//    public void testMovieTable() {
//        insertMovies();
//    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
//    public void testDetailsTable() {
//        // First insert the location, and then use the locationRowId to insert
//        // the weather. Make sure to cover as many failure cases as you can.
//
//        // Instead of rewriting all of the code we've already written in testLocationTable
//        // we can move this code to insertLocation and then call insertLocation from both
//        // tests. Why move it? We need the code to return the ID of the inserted location
//        // and our testLocationTable can only return void because it's a test.
//
//        long locationRowId = insertMovies();
//
//        // Make sure we have a valid row ID.
//        assertFalse("Error: Movie Details Not Inserted Correctly", locationRowId == -1L);
//
//        // First step: Get reference to writable database
//        // If there's an error in those massive SQL table creation Strings,
//        // errors will be thrown here when you try to get a writable database.
//        MoviesDBHelper dbHelper = new MoviesDBHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        // Second Step (Weather): Create weather values
//        ContentValues detailsValues = TestUtilities.createDetailsValues();//(locationRowId);
//
//        // Third Step (Weather): Insert ContentValues into database and get a row ID back
//        long weatherRowId = db.insert(MoviesContract.MovieDetails.TABLE_NAME, null, detailsValues);
//        assertTrue(weatherRowId != -1);
//
//        // Fourth Step: Query the database and receive a Cursor back
//        // A cursor is your primary interface to the query results.
//        Cursor detailsCursor = db.query(
//                MoviesContract.MovieDetails.TABLE_NAME,  // Table to Query
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null, // columns to group by
//                null, // columns to filter by row groups
//                null  // sort order
//        );
//
//        // Move the cursor to the first valid database row and check to see if we have any rows
//        assertTrue( "Error: No Records returned from details query", detailsCursor.moveToFirst() );
//
//        // Fifth Step: Validate the location Query
//        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
//                detailsCursor, detailsValues);
//
//        //todo:uncomment this
//        // Move the cursor to demonstrate that there is only one record in the database
////        assertFalse( "Error: More than one record returned from weather query",
////                detailsCursor.moveToNext() );
//
//        // Sixth Step: Close cursor and database
//        detailsCursor.close();
//        dbHelper.close();
//    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
//    public long insertMovies() {
//        long id;
//        id = 6;
//        // First step: Get reference to writable database
//        // If there's an error in those massive SQL table creation Strings,
//        // errors will be thrown here when you try to get a writable database.
//        MoviesDBHelper dbHelper = new MoviesDBHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        // Second Step: Create ContentValues of what you want to insert
//        // (you can use the createNorthPoleLocationValues if you wish)
//        ContentValues testValues = TestUtilities.createMoviesValues(id);
//
//        // Third Step: Insert ContentValues into database and get a row ID back
//        long locationRowId;
//        locationRowId = db.insert(MoviesContract.Movies.TABLE_NAME, null, testValues);
//
//        // Verify we got a row back.
//        assertTrue(locationRowId != -1);
//
//        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
//        // the round trip.
//
//        // Fourth Step: Query the database and receive a Cursor back
//        // A cursor is your primary interface to the query results.
//        Cursor cursor = db.query(
//                MoviesContract.Movies.TABLE_NAME,  // Table to Query
//                null, // all columns
//                null, // Columns for the "where" clause
//                null, // Values for the "where" clause
//                null, // columns to group by
//                null, // columns to filter by row groups
//                null // sort order
//        );
//
//        // Move the cursor to a valid database row and check to see if we got any records back
//        // from the query
//        assertTrue( "Error: No Records returned from Movien query", cursor.moveToFirst() );
//
//        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
//        // (you can use the validateCurrentRecord function in TestUtilities to validate the
//        // query if you like)
//        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed",
//                cursor, testValues);
//
////todo:uncomment this
//        // Move the cursor to demonstrate that there is only one record in the database
////        assertFalse( "Error: More than one record returned from movie query",
////                cursor.moveToNext() );
//
//        // Sixth Step: Close Cursor and Database
//        cursor.close();
//        db.close();
//        return locationRowId;
//    }
}
