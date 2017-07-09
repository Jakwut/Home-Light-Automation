package com.example.ju.homelightautomation;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.ju.homelightautomation.models.Report;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static java.security.AccessController.getContext;

public class ReportActivity extends AppCompatActivity {

    private ListView list_data;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private List<Report> list_report = new ArrayList<>();
    private Button mDelete,mSearchBtn;
    private SearchView mSearch;
    private Report selectedReport;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String TAG = "ReportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("ประวัติการใช้งาน");

        mSearchBtn = (Button) findViewById(R.id.btnSearch);
        mSearch = (SearchView) findViewById(R.id.SearchView);
        mDelete = (Button) findViewById(R.id.bDelete);
        list_data = (ListView) findViewById(R.id.list_data);

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ReportActivity.this)
                        .setTitle("ลบประวัติ")
                        .setMessage("ต้องการลบประวัติการใช้งานทั้งหมดใช่ไหม?")
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference mReport = FirebaseDatabase.getInstance().getReference().child("Record");
                                mReport.removeValue();
                            }
                        })
                        .setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });

        list_data.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                Report report = (Report)adapterView.getItemAtPosition(position);
                selectedReport = report;
                new AlertDialog.Builder(ReportActivity.this)
                        .setTitle("ลบประวัติ")
                        .setMessage("ต้องการลบประวัติการใช้งานนี้ใช่ไหม?")
                        .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteReport(selectedReport);
                            }
                        })
                        .setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                return false;
            }
        });

 /*       mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ReportActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" + year;
                mSearch.setText(date);
            }
        };
*/

        initFirebase();
        addEventFirebaseListener();

    }

    private void addEventFirebaseListener() {

        list_data.setVisibility(View.INVISIBLE);

        mDatabaseReference.child("Record").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (list_report.size() > 0)
                    list_report.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Report report = postSnapshot.getValue(Report.class);
                    list_report.add(report);

                }
                final ListViewAdapterReport adapter = new ListViewAdapterReport(ReportActivity.this, list_report);
                list_data.setAdapter(adapter);
                list_data.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deleteReport(Report selectedReport) {
        mDatabaseReference.child("Record").child(selectedReport.getUid()).removeValue();
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(ReportActivity.this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
