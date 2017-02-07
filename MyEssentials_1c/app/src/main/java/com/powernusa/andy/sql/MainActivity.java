package com.powernusa.andy.sql;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.powernusa.andy.sql.provider.PersonalContactContract;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    final static String LOG_TAG = "MainActivity";
    public final static int CONTACT_ADD_REQ_CODE = 100;
    public final static int CONTACT_UPDATE_REQ_CODE = 101;
    public final static String REQ_TYPE = "req_type";
    public final static String ITEM_CONTACT_ID = "item_contact_id";
    public static final int CUR_LOADER = 0;

    private Button mAddNewButton;
    private ListView mListView;
    //private DatabaseManager mDb;
    private CustomListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        setListeners();

        //mDb = new DatabaseManager(this);
        mListAdapter = new CustomListAdapter(this,null,0);
        mListView.setAdapter(mListAdapter);
        registerForContextMenu(mListView);

        getSupportLoaderManager().initLoader(CUR_LOADER,null,this);

    }
    private void bindViews() {
        mAddNewButton = (Button) findViewById(R.id.addNew);
        mListView = (ListView) findViewById(R.id.list_view);

    }

    private void setListeners() {
        mAddNewButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addNew:
                Intent intent = new Intent(this,AddNewContactActivity.class);
                //startActivity(intent);
                intent.putExtra(REQ_TYPE,CONTACT_ADD_REQ_CODE);
                startActivityForResult(intent,CONTACT_ADD_REQ_CODE);

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(CUR_LOADER,null,this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSupportLoaderManager().destroyLoader(CUR_LOADER);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.del_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.del_item:
                //Toast.makeText(this,"Position clicked: " + info.position
                //        + " ID clicked: " + info.id,Toast.LENGTH_SHORT).show();
                mListAdapter.deleteRow(info.position);
                break;

            case R.id.update_item:
                Toast.makeText(this,"Update Item",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,AddNewContactActivity.class);
                intent.putExtra(REQ_TYPE,CONTACT_UPDATE_REQ_CODE);
                long rowContactId = mListAdapter.getItemId(info.position);
                Log.d(LOG_TAG,">>> row contact id: " + rowContactId);
                intent.putExtra(ITEM_CONTACT_ID,rowContactId);
                startActivityForResult(intent,CONTACT_UPDATE_REQ_CODE);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_ADD_REQ_CODE){
            if(resultCode == RESULT_OK){
                Log.d(LOG_TAG,">>>Add New Contact RESULT OK");
            }

        }
        else if(requestCode == CONTACT_UPDATE_REQ_CODE){
            if(resultCode == RESULT_OK){
                Log.d(LOG_TAG,">>>Update Contact RESULT OK");
            }

        }
    }

    /**
     *
     *
     *
     *
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CUR_LOADER:
                return new CursorLoader(this,
                        PersonalContactContract.CONTENT_URI,
                        PersonalContactContract.PROJECTION_ALL,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.changeCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.changeCursor(null);

    }
}
