package com.example.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.app.AlertDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.notes.Common.Common;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.support.v7.widget.SearchView;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    ExampleDBHelper mydb;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

    ArrayList<HashMap<String, String>> fab_dataList = new ArrayList<HashMap<String, String>>();

    public static final String INPUT_COLUMN_ID="_id";
    public static final String INPUT_COLUMN_Title="title";
    public static final String INPUT_COLUMN_Text="text";
    public static final String INPUT_COLUMN_DATE="_text";

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    RecyclerView recyclerView;
    NoticeAdapter adapter;
    RelativeLayout relativeLayout;
    android.support.design.widget.FloatingActionButton floatingActionButton;

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback;


    ArrayList<Integer> favourite_list = new ArrayList<>();

    AlertDialog.Builder builder;
    Spinner mySpinner;

    LinearLayout share,preview,email,copy;
    AlertDialog alert;

    MenuItem search;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        builder = new AlertDialog.Builder(this);


        mydb = new ExampleDBHelper(getApplicationContext());
        relativeLayout=findViewById(R.id.relative_Layout_main);
        floatingActionButton=(FloatingActionButton) findViewById(R.id.normal_plus);

        mySpinner = (Spinner) findViewById(R.id.spinner);

        recyclerView = (RecyclerView) findViewById(R.id.recylerView_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, (RecyclerItemTouchHelper.RecyclerItemTouchHelperListener) this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.custom_spinner_layout,
                getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               switch (mySpinner.getSelectedItem().toString()){
                    case "Recents":
                        adapter.recents();
                        floatingActionButton.setVisibility(View.VISIBLE);
                        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addNew(view);
                            }
                        });
                        Common.POSITION_OF_MAinScreen=1000;
                        Log.i("Recents","Clicked");
                        break;
                    case "Favourite":
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        Common.POSITION_OF_MAinScreen=1001;
                        Log.i("addFavourite","Clicked");
                        try {
                            see();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            adapter.addFavourite();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                adapter.recents();
            }
        });

        recyclerView.setLongClickable(true);

        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(MainActivity.this, NoteViewActivity.class);
                Common.POSITION=+position;
                Log.i("position", String.valueOf(Common.POSITION));
                i.putExtra("id", dataList.get(+position).get(INPUT_COLUMN_ID));
                i.putExtra("title", dataList.get(+position).get(INPUT_COLUMN_Title));
                i.putExtra("text", dataList.get(+position).get(INPUT_COLUMN_Text));
                i.putExtra("date", dataList.get(+position).get("_textTEXT"));
                startActivity(i);

                Animation animation = AnimationUtils.loadAnimation(getBaseContext(),
                        R.anim.animation_recycler);
                view.startAnimation(animation);
            }

            @Override
            public void onLongClick(View view, int position) {
                // dialogAlerter();
                Common.CONTENT_TO_COPY=dataList.get(+position).get(INPUT_COLUMN_Text);
                Common.CONTENT_TO_SHARE_TITLE=dataList.get(+position).get(INPUT_COLUMN_Title);
                Common.CONTENT_TO_SHARE_TEXT=dataList.get(+position).get(INPUT_COLUMN_Text);
                try {
                    showBottomFragment();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }));


        try {
            setupToolbar();
         //  see();
         //    loadData();
           //  Common.POSITION_OF_MAinScreen=1000;
        } catch (IOException e) {
            e.printStackTrace();
        }


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionButton.setBackgroundColor(Color.WHITE);
                addNew(view);
            }
        });

    }

    private void showBottomFragment() throws IOException {
        View view = getLayoutInflater().inflate(R.layout.custom_bottom_layout, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);

        share=view.findViewById(R.id.share_layout);
        preview=view.findViewById(R.id.preview_layout);
        email=view.findViewById(R.id.email_layout);
        copy=view.findViewById(R.id.copy_layout);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, Common.CONTENT_TO_SHARE_TITLE);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,Common.CONTENT_TO_SHARE_TEXT);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
                Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
            }
        });
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Typeface typeface = (Typeface) Typeface.createFromAsset(getAssets(),"greatvibes_regular.otf");
                builder.setIcon(R.drawable.note);
                builder.setMessage("\n"+Common.CONTENT_TO_SHARE_TEXT) ;
                builder.setTitle(Html.fromHtml("<font color='#B447C2' face='serif'>"+Common.CONTENT_TO_SHARE_TITLE+"</font>"));
                // builder.setTitle("\t\n"+Common.CONTENT_TO_SHARE_TITLE);
                alert= builder.create();
                alert.show();
                Toast.makeText(MainActivity.this, "preview", Toast.LENGTH_SHORT).show();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Email", Toast.LENGTH_SHORT).show();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", Common.CONTENT_TO_COPY);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Copied !!! ", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setContentView(view);
        dialog.show();

    }


    private void dialogAlerter()
    {
        Alerter.create(MainActivity.this)
                .setTitle("Options")
                .setText("Share")
                .setIcon(R.drawable.ic_share_black_24dp)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Share", Toast.LENGTH_SHORT).show();
                    }
                })
                .setText("Make a Copy!!")
                .setIcon(R.drawable.ic_content_copy_black_24dp)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Copy", Toast.LENGTH_SHORT).show();
                    }
                })
                .setText("Preview!!")
                .setIcon(R.drawable.ic_remove_red_eye_black_24dp)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Preview", Toast.LENGTH_SHORT).show();
                    }
                })
                .enableSwipeToDismiss()
                .show();

    }

    private void see()throws IOException {

        SQLiteDatabase mDataBase;
        //(some code here...)
        mDataBase = mydb.getReadableDatabase();
        Cursor dbCursor = mDataBase.query("input_", null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        for (String value:columnNames){
            Log.i("1", value);
        }
    }

    private void setupToolbar()throws IOException {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");

        toolbar.setTitleMarginStart(20);

        toolbar.setTitleTextColor(Color.parseColor("#E000E0"));
        toolbar.setBackgroundColor(Color.parseColor("#FFF0F0"));
        toolbar.setLogoDescription("Notes Application");

    }


    @Override
    public void onPositiveClick(int from) {

    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    @Override
    public void setActive(View rootView, boolean state) {

    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof NoticeViewHolder) {

            if (direction==ItemTouchHelper.LEFT){
                adapter.removeItem(viewHolder.getAdapterPosition());
                //adapter.removeItem(position);
                Toast.makeText(this, "Deleted !!!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void addNew(View view) {
        Intent intent = new Intent(this, Input_Note_DataActivity.class);
        startActivity(intent);
    }

    public void loadData() throws IOException {
       dataList.clear();
        Cursor cursor = mydb.getAllPersons();
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {


                HashMap<String, String> map = new HashMap<String, String>();
                map.put(INPUT_COLUMN_ID, cursor.getString(cursor.getColumnIndex(INPUT_COLUMN_ID)));
                map.put(INPUT_COLUMN_Title, cursor.getString(cursor.getColumnIndex(INPUT_COLUMN_Title)));
                map.put(INPUT_COLUMN_Text, cursor.getString(cursor.getColumnIndex(INPUT_COLUMN_Text)));
                map.put("_textTEXT", cursor.getString(cursor.getColumnIndex("_textTEXT")));

                dataList.add(map);

                cursor.moveToNext();
            }
        }
        Collections.reverse(dataList);
        adapter= new NoticeAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(adapter);
        //mobile_list.setAdapter(adapter);

//        mobile_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//                Intent i = new Intent(MainActivity.this, NoteViewActivity.class);
//                i.putExtra("id", dataList.get(+position).get(INPUT_COLUMN_ID));
//                i.putExtra("title", dataList.get(+position).get(INPUT_COLUMN_Title));
//                i.putExtra("text", dataList.get(+position).get(INPUT_COLUMN_Text));
//                //i.putExtra("date", dataList.get(+position).get(INPUT_COLUMN_DATE));
//                startActivity(i);
//
//            }
//        });


        //  list item long press to delete -------------start-----------

//        mobile_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
//                return onLongListItemClick(v,pos,id);
//            }
//            protected boolean onLongListItemClick(View v, final int pos, long id) {
//
//                /////Display Dialog Here.......................
//
//                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
//                alertDialog.setTitle("Delete...");
//                alertDialog.setMessage("Are you sure?");
//                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        String a=dataList.get(+pos).get(INPUT_COLUMN_ID);
//                        mydb.deleteSingleContact(a);
//                        loadData();
//                    }
//                });
//                alertDialog.show();
//                return true;
//            }});
//
//        //--------finish------------
//    }
    }



    class NoticeAdapter extends RecyclerView.Adapter<NoticeViewHolder> implements View.OnLongClickListener, Filterable {

        private Activity activity;
        private ArrayList<HashMap<String, String>> data;

        public NoticeAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
            this.activity = activity;
            this.data = data;
        }

        @NonNull
        @Override
        public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.note_data, viewGroup, false);
            return new NoticeViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(@NonNull final NoticeViewHolder noticeViewHolder, final int position) {

            favourite_list.clear();
            noticeViewHolder.title.setId(position);
            noticeViewHolder.text.setId(position);
            noticeViewHolder.date.setId(position);

            HashMap<String, String> song = new HashMap<String, String>();
            song = data.get(position);

            noticeViewHolder.title.setText(Html.fromHtml(song.get(MainActivity.INPUT_COLUMN_Title)));
            noticeViewHolder.text.setText(Html.fromHtml(song.get(MainActivity.INPUT_COLUMN_Text)));
            Log.i("Notice ", String.valueOf(Html.fromHtml(song.get(MainActivity.INPUT_COLUMN_Text))));
            noticeViewHolder.date.setText(Html.fromHtml(song.get("_textTEXT")));

//            noticeViewHolder.img_favourite.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (isFavSelect){
//                        noticeViewHolder.img_favourite.setImageResource(R.drawable.ic_star_red_full_24dp);
//                        isFavSelect=false;
//                        favourite_list.add(noticeViewHolder.getAdapterPosition()+1);
//                        Toast.makeText(activity, "Added To Favourite !!!", Toast.LENGTH_SHORT).show();
//                    }else {
//                        isFavSelect=true;
//                        noticeViewHolder.img_favourite.setImageResource(R.drawable.ic_star_border_white_24dp);
//                        if (favourite_list != null && favourite_list.isEmpty()){
//
//                        }
//                      else {
//                          favourite_list.remove(noticeViewHolder.getAdapterPosition()+1);
//                        }
//                        Toast.makeText(activity, "Removed From Favourite !!!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });

        }

         private void addFavourite() throws IOException{
             fab_dataList.clear();
             Cursor cursor = mydb.getFabList();
             if (cursor.moveToFirst()) {
                 while (cursor.isAfterLast() == false) {


                     HashMap<String, String> map = new HashMap<String, String>();
                     map.put(INPUT_COLUMN_ID, cursor.getString(cursor.getColumnIndex(INPUT_COLUMN_ID)));
                     map.put(INPUT_COLUMN_Title, cursor.getString(cursor.getColumnIndex(INPUT_COLUMN_Title)));
                     map.put(INPUT_COLUMN_Text, cursor.getString(cursor.getColumnIndex(INPUT_COLUMN_Text)));
                     map.put("_textTEXT", cursor.getString(cursor.getColumnIndex("_textTEXT")));

                     fab_dataList.add(map);

                     cursor.moveToNext();
                 }
             }
             Collections.reverse(fab_dataList);
             adapter= new NoticeAdapter(MainActivity.this, fab_dataList);
             recyclerView.setAdapter(adapter);

        }


        private void recents(){
            try {
                loadData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void removeItem(int position) {
            if(Common.POSITION_OF_MAinScreen==1000){
                String a=dataList.get(+position).get(INPUT_COLUMN_ID);
                Log.i("Item",""+a);
                mydb.deleteSingleContact(a);
                try {
                    loadData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (Common.POSITION_OF_MAinScreen==1001)
            {
                String a=fab_dataList.get(+position).get(INPUT_COLUMN_ID);
                Log.i("Item",""+a);
                mydb.deleteSingleFab(a);
                try {
                    addFavourite();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

         @Override
         public boolean onLongClick(View view) {
             return false;
         }


        @Override
        public Filter getFilter() {
            return  new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    return null;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                }
            };
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        search= menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(search);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.new_activity_about:
                startActivity(new Intent(MainActivity.this,About.class));
                Toast.makeText(this, "About US", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, "Tap To Exit", Toast.LENGTH_SHORT).show();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}
