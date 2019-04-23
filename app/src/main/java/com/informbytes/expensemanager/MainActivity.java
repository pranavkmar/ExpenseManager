package com.informbytes.expensemanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    SQLiteDatabase ExpenseManagerDataBase = null;
    Button incomeSaveBtn = null;
    Cursor cursor;
    double inc_total;
    double exp_total;
    double bal_total;
    TextView income, expense, balance;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDilg(view);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        incomeSaveBtn = (Button) findViewById(R.id.incomeSaveBtnView);
//        incomeSaveBtn.setOnClickListener((View.OnClickListener) getApplicationContext());
        Button expenseAppendBtn = (Button) findViewById(R.id.expenseAppendBtnView);
        expenseAppendBtn.setOnClickListener(this);
        Button retrieveUpdateDbBtn = (Button) findViewById(R.id.retrieveUpdateBtnView);
        retrieveUpdateDbBtn.setOnClickListener(this);
        createDbHelperMethod();
        income = (TextView) findViewById(R.id.inctv);
        expense = (TextView) findViewById(R.id.exptv);
        balance = (TextView) findViewById(R.id.baltv);
        swipeRefresh = findViewById(R.id.swipe_container);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getIncomeData();
                getExpData();
                getBalData();
                income.setText(String.format("₹. %s", inc_total));
                expense.setText(String.format("₹. %s", exp_total));
                balance.setText(String.format("₹. %s", bal_total));
                swipeRefresh.setRefreshing(false);
            }
        });


    }

    @SuppressLint("WrongConstant")
    void createDbHelperMethod() {
        // Create Database if necessary or open it if exists

        ExpenseManagerDataBase = openOrCreateDatabase("ExpenseManagerDB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null); // in Mode  MODE_PRIVATE || SQLiteDatabase.CREATE_IF_NECESSARY
        boolean tableok = false;
// Check if table exists
        Cursor c = ExpenseManagerDataBase.query(

                "sqlite_master", null,

                "type=? and name=?",

                new String[]{"table", "ExpenseManagerTable"},

                null, null, null);
        if (c.getCount() > 0)
            tableok = true;
        if (!tableok) {

            ExpenseManagerDataBase.execSQL("create table ExpenseManagerTable(item text ,savedIncome real,ExpenseAmount real)");

// Income , savedIncome, item, expenditureAmount
        }
    }


    private void getIncomeData() {
        cursor = ExpenseManagerDataBase.rawQuery("SELECT sum(savedIncome) FROM ExpenseManagerTable where item='i' ", null);
        if (cursor.moveToFirst()) {
            inc_total = cursor.getDouble(0);
        }
    }

    private void getExpData() {

        cursor = ExpenseManagerDataBase.rawQuery("SELECT sum(ExpenseAmount) FROM ExpenseManagerTable where item='e' ", null);
        if (cursor.moveToFirst()) {

            exp_total = cursor.getDouble(0);
        }
    }

    private void getBalData() {
        bal_total = inc_total - exp_total;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//         AlertDilg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void AlertDilg(View view) {

        callAlert();

    }

    public void AlertDilg(MenuItem item) {
        callAlert();
    }

    public void callAlert() {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.group_one, null);

//       EditText amount =
        incomeSaveBtn = alertLayout.findViewById(R.id.incomeSaveBtnView);
        incomeSaveBtn.setVisibility(View.INVISIBLE);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("SET INCOME in DATABASE");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getBaseContext(), "Operation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                createDbHelperMethod();
                EditText amountInflatedOnclickEditText = alertLayout.findViewById(R.id.IncomeSaveAmount);
                ContentValues cv = new ContentValues();
                cv.put("savedIncome", Double.parseDouble(amountInflatedOnclickEditText.getText().toString()));
                cv.put("item", "i");
                cv.put("ExpenseAmount", 0);
                ExpenseManagerDataBase.insert("ExpenseManagerTable", null, cv);
                Toast.makeText(getBaseContext(), "Income: is Successfully saved in Database", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.incomeSaveBtnView:
//
//
//
//                Toast tsave = Toast.makeText(getApplicationContext(), "SAVE BTN CLICKED", Toast.LENGTH_SHORT);
//                tsave.show();
//
//                break;

            case R.id.expenseAppendBtnView:

                createDbHelperMethod();
                EditText ExpenseAmountET = findViewById(R.id.expenseAddET);
                ContentValues cv = new ContentValues();
                cv.put("savedIncome", 0);
                cv.put("item", "e");
                cv.put("ExpenseAmount", Double.parseDouble(ExpenseAmountET.getText().toString()));
                ExpenseManagerDataBase.insert("ExpenseManagerTable", null, cv);
                Toast.makeText(getBaseContext(), "Expense: is saved in Database", Toast.LENGTH_LONG).show();
                break;

            case R.id.retrieveUpdateBtnView:

                getIncomeData();
                getExpData();
                getBalData();
                income.setText(String.format("₹. %s", inc_total));
                expense.setText(String.format("₹. %s", exp_total));
                balance.setText(String.format("₹. %s", bal_total));
                Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onPause() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+getPackageName() +"//databases//ExpenseManagerDB.db";
                String backupDBPath = "ExpenseManagerDB.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        super.onPause();
    }
}

