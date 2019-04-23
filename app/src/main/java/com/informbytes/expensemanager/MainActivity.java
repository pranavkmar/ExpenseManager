package com.informbytes.expensemanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        implements View.OnClickListener {
    SQLiteDatabase ExpenseManagerDataBase = null;
    Button incomeSaveBtn = null;
    Cursor cursor;
    double inc_total, exp_total, bal_total;
    TextView income, expense, balance;
    SwipeRefreshLayout swipeRefresh;
    EditText incomeEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDilg(view);
            }
        });
        incomeEditView = (EditText) findViewById(R.id.incomeViewET);
        incomeEditView.setVisibility(View.INVISIBLE);
        Button incomeSaveButton = (Button) findViewById(R.id.incomeSaveBtnView);
        incomeSaveButton.setOnClickListener(this);
        Button expenseAppendBtn = (Button) findViewById(R.id.expenseAppendBtnView);
        expenseAppendBtn.setOnClickListener(this);
        Button retrieveUpdateDbBtn = (Button) findViewById(R.id.retrieveUpdateBtnView);
        retrieveUpdateDbBtn.setOnClickListener(this);
        //create DB
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
//Optional
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    12);
        }
    }


    @SuppressLint("WrongConstant")
    void createDbHelperMethod() {
        // Create Database if necessary or open it if exists

        ExpenseManagerDataBase = openOrCreateDatabase("ExpenseManagerDB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null); // in Mode  MODE_PRIVATE || SQLiteDatabase.CREATE_IF_NECESSARY
        boolean tableok = false;
        int backupFlag = 0;
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
            backupFlag = 1;
            // Income , savedIncome, item, expenditureAmount
        }
        if (backupFlag == 1) {
            backupDB();
        }

    }

    private void backupDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getPackageName() + "//databases//ExpenseManagerDB.db";
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


    public void callAlert() {
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.group_income, null);

        EditText incomeEditViewInf = alertLayout.findViewById(R.id.incomeViewET);
        incomeEditViewInf.setWidth(40);
        incomeEditViewInf.setVisibility(View.VISIBLE);
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
                EditText amountInflatedOnclickEditText = alertLayout.findViewById(R.id.incomeViewET);
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

            case R.id.incomeSaveBtnView:
                AlertDilg(v);
//                Toast tsave = Toast.makeText(getApplicationContext(), "SAVE BTN CLICKED", Toast.LENGTH_SHORT);
//                tsave.show();
//                break;

            case R.id.expenseAppendBtnView:
                EditText expenseET = (EditText) findViewById(R.id.expenseAddET);
                if (!expenseET.getText().toString().isEmpty()) {
                    createDbHelperMethod();

                    ContentValues cv = new ContentValues();
                    cv.put("savedIncome", 0);
                    cv.put("item", "e");
                    cv.put("ExpenseAmount", Double.parseDouble(expenseET.getText().toString()));
                    ExpenseManagerDataBase.insert("ExpenseManagerTable", null, cv);
                    Toast.makeText(getBaseContext(), "Expense: is saved in Database", Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(this, "Enter a Value in Field", Toast.LENGTH_SHORT).show();
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

    public void AlertDilg(View view) {

        callAlert();

    }

    public void AlertDilg(MenuItem item) {
        callAlert();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 12: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do something

//                    requestGranted=true;
                } else {
                    // not granted
                    Toast.makeText(this, "We require Storage permission to write on a Text File", Toast.LENGTH_SHORT).show();
                }
                return;
            }
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
        if (id == R.id.incomeMenu) {
//         AlertDilg();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }
}

