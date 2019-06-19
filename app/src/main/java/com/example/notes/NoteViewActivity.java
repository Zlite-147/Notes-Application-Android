package com.example.notes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.Common.Common;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class NoteViewActivity extends AppCompatActivity {

    EditText titleview;
    EditText textview;
    TextView txt_date;
    String sub_id;

    ImageView fabImage;

    ExampleDBHelper db;

    boolean isFavSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_view);

        db=new ExampleDBHelper(getApplicationContext());


        titleview =(EditText) findViewById(R.id.title_view);
        textview=(EditText) findViewById(R.id.text_view);
        txt_date=(TextView)findViewById(R.id.txt_date_);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        sub_id = getIntent().getStringExtra("id");
        final String title=getIntent().getStringExtra("title");
        final String text=getIntent().getStringExtra("text");
        final String date=getIntent().getStringExtra("date");
        titleview.setText(title);
        textview.setText(text);
        txt_date.setText(date);
        setupToolbar();

        titleview.setFocusable(false);
        titleview.setFocusableInTouchMode(true);
        titleview.setSelection(titleview.getText().length());
        imm.showSoftInput(titleview, InputMethodManager.SHOW_IMPLICIT);

        textview.setFocusable(true);
        imm.showSoftInput(textview, InputMethodManager.SHOW_IMPLICIT);
        textview.setFocusableInTouchMode(true);
        textview.setCustomSelectionActionModeCallback(new StyleCallback());

        fabImage=findViewById(R.id.fab_noteView);

        if (Common.POSITION_OF_MAinScreen==1001){
            fabImage.setImageResource(R.drawable.ic_star_red_full_24dp);
        }

        fabImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavSelect){
                    fabImage.setImageResource(R.drawable.ic_star_red_full_24dp);
                    isFavSelect=false;
                    db.insertFab(title,text,date);
                    Toast.makeText(NoteViewActivity.this, "Added To Favourite !!!", Toast.LENGTH_SHORT).show();
                }else {
                    isFavSelect=true;
                    Common.ADD_TO_FAB=false;
                   db.deleteSingleFab(sub_id);
                   fabImage.setImageResource(R.drawable.ic_star_border_white_24dp);
                    Toast.makeText(NoteViewActivity.this, "Removed From Favourite !!!", Toast.LENGTH_SHORT).show();
                }

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
        Drawable drawable;
        switch (item.getItemId()) {
            case R.id.new_activity_save:
                drawable= item.getIcon();
                drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                saved();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saved() {
        String id=sub_id;
        String title=titleview.getText().toString();
        String text=textview.getText().toString();
        Log.i("title",title);
        Log.i("Sub_Id",id);
        updateItem(getBaseContext(),id,title,text);
        Toast.makeText(this, "Changes Saved Successfully!!!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(NoteViewActivity.this,MainActivity.class));
    }

    public static boolean updateItem(Context context, String Id, String title, String text) {

        Date date = new Date();
        String date_text= DateFormat.getDateTimeInstance(). format(date);


        ExampleDBHelper dbHelper = new ExampleDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.i("ID_Values",Id);
        values.put(ExampleDBHelper.INPUT_COLUMN_Title, title);
        values.put(ExampleDBHelper.INPUT_COLUMN_Text, text);
        values.put("_textTEXT",date_text);
        try {

            db.update(ExampleDBHelper.INPUT_TABLE_NAME, values, "_id="+Id, null);
            Log.i("Values","Updated");
            db.close();

            return true;
        } catch (SQLiteException e) {
            Log.i("Exception", String.valueOf(e));
            db.close();

            return false;
        }
    }

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_brown_24dp);
        getSupportActionBar().setTitle("");

        toolbar.setTitleMarginStart(20);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setBackgroundColor(Color.parseColor("#FFF0F0"));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteViewActivity.this,MainActivity.class));
            }
        });
    }


    private class StyleCallback implements ActionMode.Callback {
        @SuppressLint("InlinedApi")

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.style, menu);
            menu.removeItem(android.R.id.shareText);
            //menu.removeItem(android.R.id.selectAll);
            menu.removeItem(android.R.id.copy);
            menu.removeItem(android.R.id.cut);
            menu.removeItem(android.R.id.paste);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            CharacterStyle cs;
            int start = textview.getSelectionStart();
            int end = textview.getSelectionEnd();
            SpannableStringBuilder ssb = new SpannableStringBuilder(textview.getText());
            final ForegroundColorSpan fcs;
            switch(menuItem.getItemId()) {

                case R.id.bold:
                    cs = new StyleSpan(Typeface.BOLD);
                    ssb.setSpan(cs, start, end, 1);
                    textview.setText(ssb);
                    return true;

                case R.id.italic_:
                    cs = new StyleSpan(Typeface.ITALIC);
                    ssb.setSpan(cs, start, end, 1);
                    textview.setText(ssb);
                    return true;

                case R.id.underline:
                    cs = new UnderlineSpan();
                    ssb.setSpan(cs, start, end, 1);
                    textview.setText(ssb);
                    return true;

                 case R.id.color_red:
                    fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
                    ssb.setSpan(fcs, start, end, 1);
                    //ssb.setSpan(cs, start, end, 1);
                    textview.setText(ssb);
                    return true;

                case R.id.color_green:
                    fcs = new ForegroundColorSpan(Color.rgb(0, 255, 0));
                    ssb.setSpan(fcs, start, end, 1);
                    textview.setText(ssb);
                    return true;

                case R.id.color_blue:
                    fcs = new ForegroundColorSpan(Color.rgb(0, 0, 255));
                    ssb.setSpan(fcs, start, end, 1);
                    textview.setText(ssb);
                    return true;

            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    }
}

