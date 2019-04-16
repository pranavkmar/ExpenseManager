package com.informbytes.expensemanager;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    SQLiteDatabase ExpenseManagerDataBase = null;
    Button incomeSaveBtn =null;
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


        // Create Database if necessary or open it if exists

        ExpenseManagerDataBase = openOrCreateDatabase("checkbook.db", MODE_PRIVATE, null); // in Mode SQLiteDatabase.CREATE_IF_NECESSARY
        boolean tableok = false;
// Check if table exists
        Cursor c = ExpenseManagerDataBase.query(

                "sqlite_master", null,

                "type=? and name=?",

                new String[]{"table", "checks"},

                null, null, null);
        if (c.getCount() > 0)
            tableok = true;
        if (!tableok) {

            ExpenseManagerDataBase.execSQL("create table ExpenseManagerTable(income real primary key not null,  savedIncome real, item text ,expenditureAmount real, balance real)");

// Income , savedIncome, item, expenditureAmount
        }

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

    public void AlertDilg(MenuItem item)  {
        callAlert();
    }

    public void callAlert() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.group_one, null);

        final EditText etIncome = alertLayout.findViewById(R.id.editText2);
        incomeSaveBtn =alertLayout.findViewById(R.id.incomeSaveBtnView);
        incomeSaveBtn.setOnClickListener(this);
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
                String incomeValue = etIncome.getText().toString();

                Toast.makeText(getBaseContext(), "Income: " + incomeValue + " is Successfully saved in Database", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.incomeSaveBtnView:

                // TODO: do your code
                Toast tsave = Toast.makeText(getApplicationContext(), "SAVE BTN CLICKED", Toast.LENGTH_SHORT);
                tsave.show();

                break;

            case R.id.expenseAppendBtnView:
                // TODO: do your code
                Toast.makeText(getApplicationContext(), "Expenses Added in databases", Toast.LENGTH_SHORT).show();

                break;

            case R.id.retrieveUpdateBtnView:
                // TODO: do your code

                Toast.makeText(getApplicationContext(), "Update Button Pressed", Toast.LENGTH_SHORT).show();
//                String key = number.getText().toString();
//
//                Cursor c = checkbook.query("checks", null, "cheque_number=?",
//
//                        new String[]{key}, null, null, null, null);
//
//                if (c.getCount() == 0) {
//
//                    Toast.makeText(myContext, "no record exists",
//
//                            Toast.LENGTH_SHORT).show();

                break;

            default:
                break;
        }

    }

}

