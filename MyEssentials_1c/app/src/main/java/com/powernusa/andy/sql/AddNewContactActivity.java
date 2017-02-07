package com.powernusa.andy.sql;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.powernusa.andy.sql.database.DatabaseConstants;
import com.powernusa.andy.sql.object.ContactModel;
import com.powernusa.andy.sql.provider.PersonalContactContract;

import java.io.ByteArrayOutputStream;

import static com.powernusa.andy.sql.R.id.contactEmail;
import static com.powernusa.andy.sql.R.id.contactName;
import static com.powernusa.andy.sql.R.id.contactPhone;
import static com.powernusa.andy.sql.database.DatabaseConstants.TABLE_ROW_EMAIL;
import static com.powernusa.andy.sql.database.DatabaseConstants.TABLE_ROW_NAME;
import static com.powernusa.andy.sql.database.DatabaseConstants.TABLE_ROW_PHONENUM;
import static com.powernusa.andy.sql.database.DatabaseConstants.TABLE_ROW_PHOTOID;

public class AddNewContactActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String LOG_TAG = AddNewContactActivity.class.getSimpleName();
    public static final int PICK_PHOTO_FROM_GALLERY = 1001;
    public static final int CAPTURE_PHOTO_FROM_CAMERA = 1002;

    private EditText mContactName, mContactPhone, mContactEmail;
    private TextView mContactPhoto;
    private Button mDoneButton, mPickPhotoBtn;
    private ImageView mCapturedImg;

    private Bitmap mImageBitmap;
    private byte[] mBlob;

    private boolean mPhotoPicked = false;
    private int mRequestType;
    private long mRowId;
    //private ContactModel mUpdatedContactModel;
    private Uri mUpdatedUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        bindViews();
        if(getIntent() != null){
            mRequestType = getIntent().getIntExtra(MainActivity.REQ_TYPE,0);
            if(mRequestType == MainActivity.CONTACT_UPDATE_REQ_CODE){
                mRowId = getIntent().getLongExtra(MainActivity.ITEM_CONTACT_ID,0);
                initializeFields(mRowId);
            }
        }

        setListeners();
        if(!checkPermission()){askPermission();}

    }

    private void initializeFields(long rowId){
        mUpdatedUri= ContentUris.withAppendedId(PersonalContactContract.CONTENT_URI,rowId);
        Log.d(LOG_TAG,">>>row id uri: " + mUpdatedUri.toString());
        Cursor c = getContentResolver().query(mUpdatedUri,PersonalContactContract.PROJECTION_ALL,null,null,null);
        if( c.moveToFirst()){
            mContactName.setText(c.getString(c.getColumnIndexOrThrow(DatabaseConstants.TABLE_ROW_NAME)));
            mContactPhone.setText(c.getString(c.getColumnIndexOrThrow(DatabaseConstants.TABLE_ROW_PHONENUM)));
            mContactEmail.setText(c.getString(c.getColumnIndexOrThrow(DatabaseConstants.TABLE_ROW_EMAIL)));
        }

    }
    private void bindViews() {

        mContactName = (EditText) findViewById(contactName);
        mContactPhone = (EditText) findViewById(contactPhone);
        mContactEmail = (EditText) findViewById(contactEmail);
        mContactPhoto = (TextView) findViewById(R.id.contactPhoto);
        mDoneButton = (Button) findViewById(R.id.doneBtn);
        mPickPhotoBtn = (Button) findViewById(R.id.pickPhotoBtn);
        mCapturedImg = (ImageView) findViewById(R.id.capturedImg);
    }

    private void setListeners() {
        mDoneButton.setOnClickListener(this);
        mPickPhotoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pickPhotoBtn:
                pickPhoto();
                break;
            case R.id.doneBtn:
                prepareSendData();
                break;
        }

    }
    private void prepareSendData(){


        if(TextUtils.isEmpty(mContactName.getText().toString())
                || TextUtils.isEmpty(mContactPhone.getText().toString()) ){
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Empty Fields");
            builder.setMessage("Please fill contact name and contact phone");
            builder.setCancelable(true);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
        else{
            ContactModel contact = new ContactModel();
            contact.setName(mContactName.getText().toString());
            contact.setContactNo(mContactPhone.getText().toString());
            contact.setEmail(mContactEmail.getText().toString());

            ContentValues cv = getContentValues(contact);
            ContentResolver contentResolver = getContentResolver();

            if(mRequestType == MainActivity.CONTACT_ADD_REQ_CODE) {
                contentResolver.insert(PersonalContactContract.CONTENT_URI, cv);
            }
            else if(mRequestType == MainActivity.CONTACT_UPDATE_REQ_CODE){
                // update database
                Log.d(LOG_TAG,">>>Processing update database");
                ContactModel UpdatedContactModel = new ContactModel(mContactName.getText().toString(),
                        mContactPhone.getText().toString(),
                        mContactEmail.getText().toString());
                contentResolver.update(mUpdatedUri,getContentValues(UpdatedContactModel),null,null);


            }
            setResult(RESULT_OK);
            finish();
        }
    }
    private ContentValues getContentValues(ContactModel contactObj) {

        ContentValues values = new ContentValues();
        values.put(TABLE_ROW_NAME, contactObj.getName());
        values.put(TABLE_ROW_PHONENUM, contactObj.getContactNo());
        values.put(TABLE_ROW_EMAIL, contactObj.getEmail());
        values.put(TABLE_ROW_PHOTOID, contactObj.getPhoto());
        return values;
    }

    private void pickPhoto() {

        final CharSequence[] items = { "Capture Photo", "Choose from Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAPTURE_PHOTO_FROM_CAMERA);
                } else if (items[item].equals("Choose from Gallery")) {

                    Intent intent = new Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //intent.setType("image/*");
                    startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);

                } else if (items[item].equals("Cancel")) {

                    dialog.dismiss();
                    mPhotoPicked = false;
                }
            }

        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path;
        Uri uri = data.getData();
        Log.d(LOG_TAG,">>>Uri: " + uri.toString());
        if (requestCode == PICK_PHOTO_FROM_GALLERY) {

            if (resultCode == RESULT_OK) {
                if (uri != null) {
                    path = getImagePath(data.getData());
                    Log.d(LOG_TAG,">>>Path to image: " + path );
                    mImageBitmap = getScaledBitmap(path, 256);
                    mCapturedImg.setImageBitmap(mImageBitmap);
                    mBlob = getBlob();
                    mPhotoPicked = true;
                } else {
                    Toast.makeText(this, "Could not load image",
                            Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {

                mPhotoPicked = false;
            }
        }
        else if (requestCode == CAPTURE_PHOTO_FROM_CAMERA) {

            if (resultCode == RESULT_OK) {

                if (uri != null) {
                    path = getImagePath(uri);
                    mImageBitmap = getScaledBitmap(path, 256);
                    mCapturedImg.setImageBitmap(mImageBitmap);
                    mBlob = getBlob();
                    mPhotoPicked = true;
                } else {
                    Toast.makeText(this, "Could not load image",
                            Toast.LENGTH_LONG).show();
                }

            } else if (resultCode == RESULT_CANCELED) {

                mPhotoPicked = false;
            }
        }
    }
    private byte[] getBlob() {

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);

        byte[] byteArray = boas.toByteArray();

        return byteArray;
    }
    private String getImagePath(Uri uri) {
        Log.d(LOG_TAG,">>> uri: " + uri);
        String[] projection = new String[] {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            if(checkPermission()){
                cursor = getContentResolver().query(uri, projection, null, null, null);
            }

        }
        catch (Exception e){
            Log.e("ERROR: ", " >>> ERROR: " + e.toString());
            e.printStackTrace();
        }
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);


        String path = cursor.getString(column_index);

        return path;
    }



    private Bitmap getScaledBitmap(String path, int maxSize) {

        Bitmap bmp = null;
        int width, height, inSampleSize;

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        op.inPurgeable = true;

        BitmapFactory.decodeFile(path, op);

        width = op.outWidth;
        height = op.outHeight;

        if (width == -1) {

            return null;
        }

        int max = Math.max(width, height);
        inSampleSize = 1;

        while (max > maxSize) {
            inSampleSize *= 2;
            max /= 2;
        }

        op.inJustDecodeBounds = false;
        op.inSampleSize = inSampleSize;

        bmp = BitmapFactory.decodeFile(path, op);

        return bmp;
    }

    /**
     *
     *
     */

    private boolean checkPermission(){
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    public static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();

            } else {
                // Permission denied
                //TODO close app when permission is denied
                //permissionsDenied();
            }
        }

    }
}
