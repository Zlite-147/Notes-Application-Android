package com.example.notes;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Input_Note_DataActivity extends AppCompatActivity {
    ExampleDBHelper db;

    EditText n_title;
    EditText n_text;
    String title,text;
    String date_text;

    public static SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input__note__data);
        db=new ExampleDBHelper(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);


        n_title=(EditText) findViewById(R.id.title);
        n_text=(EditText) findViewById(R.id.text);

        setupToolbar();

    }

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_brown_24dp);
        getSupportActionBar().setTitle("Compose");
        toolbar.setTitleMarginStart(20);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setBackgroundColor(Color.parseColor("#FFF0F0"));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Input_Note_DataActivity.this,MainActivity.class));
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_activity_save:
                    saved();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saved() {

        title = n_title.getText().toString();
        text = n_text.getText().toString();
        Date date = new Date();
        date_text= DateFormat.getDateTimeInstance(). format(date);


        if(title.length() == 0){
            SharedPreferences.Editor editor = pref.edit();

            int idName = pref.getInt("name", 0);
            idName++;
            title="new document "+idName ;
            editor.putInt("name",idName);
            editor.commit();

        }
        if( text.length() == 0){
            Toast.makeText(getApplicationContext(), "title or text box is empty !!!",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            db.insertPerson(title,text,date_text);
            Log.i("date",""+date_text);
            Log.i("Text",""+text);
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
            finish();
        }
    }


}