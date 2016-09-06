package com.example.kyle.reminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends AppCompatActivity {

    private noteDatabase database;
    private SimpleCursorAdapter cursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = new noteDatabase(this);
        final Cursor cursor = database.getAllNotes();
        String[] columns = new String[]{
                noteDatabase.DB_COLUMN_CONTENT
        };
        int[] widgets = new int[]{
                R.id.noteName
        };

        cursorAdapter = new SimpleCursorAdapter(this, R.layout.note_layout,
                cursor, columns, widgets, 0);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(cursorAdapter);
        requery();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor item = (Cursor) adapterView.getItemAtPosition(i);
                int id = item.getInt(item.getColumnIndex(noteDatabase.DB_COLUMN_ID));
                String note = item.getString(item.getColumnIndex(noteDatabase.DB_COLUMN_CONTENT));
                Log.i("tag", ""+id);
                Intent intent = new Intent(MainActivity.this, seeNote.class);
                intent.putExtra("noteID", id);
                startActivity(intent);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor item = (Cursor) adapterView.getItemAtPosition(i);
                int id = item.getInt(item.getColumnIndex(noteDatabase.DB_COLUMN_ID));
                AlertDialog confirm = AskOption(id);
                confirm.show();
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_note:
                Intent intent = new Intent(this, addNote.class);
                startActivity(intent);
                finish();
                break;
            case R.id.action_add_alert:
                break;
            case R.id.action_settings:
                break;
            default:
                break;
        }

        return true;
    }

    private AlertDialog AskOption(int id) {
        final int deleteId = id;
        AlertDialog deleteConfirm = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Confirm")
                .setMessage("Do you want to delete?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        database.deleteNote(deleteId);
                        requery();
                        dialog.dismiss();
                    }

                })


                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return deleteConfirm;

    }

    private void requery(){
        Cursor cursor = database.getAllNotes();
        cursorAdapter.changeCursor(cursor);
    }
}

