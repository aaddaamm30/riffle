package com.team6.rifflegroup.streamsavvy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;



public class dataLayout extends AppCompatActivity {

    //support for gps
    private static final String TAG = dataLayout.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private FusedLocationProviderClient mFusedLocationClient;

    //string holders for lat long
    private String mLatitudeLabel;
    private String mLongitudeLabel;

    //get instances of all elements
    private EditText inField;
    private TextView textLocation;
    private EditText inDate;
    private Location location;

    //data file path and file and name
    private static final String FILE_NAME = "RiffleData.csv";

    //email specs
    private static final String EMAIL_ADDRESS = "a.daman.loo@gmail.com";

    //on create set way to access UI and get LocationServices getLocation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_layout);


        //strings
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);

        //get objects to use
        inField = (EditText)findViewById(R.id.someString);
        textLocation = (TextView)findViewById(R.id.Location);
        inDate = (EditText)findViewById(R.id.inDate);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    //onstart
    @Override
    public void onStart(){
        super.onStart();

        //get user permissions
        if(!checkPermissions()){
            requestPermissions();
        }
    }

    //get and set location data
    @SuppressWarnings("MissingPermission")
    private void getsetLocation(){
        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {

            //setting callback function
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    location=task.getResult();
                    String input="";
                    input=input.concat(String.format(Locale.ENGLISH,"%s: %f",mLatitudeLabel,location.getLatitude()));
                    input=input.concat(String.format(Locale.ENGLISH,"%s: %f",mLongitudeLabel,location.getLongitude()));
                    textLocation.setText(input);
                }else{
                    Log.w(TAG, "getLastLocation:exception", task.getException());
                    showSnackbar(getString(R.string.no_location_detected));
                }
            }
        });
    }

    //shows snackDar with just string argument
    private void showSnackbar(final String text){
        View container = findViewById(R.id.data_layout_container);
        if(container != null){
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }


    //multi argument showSnackbar
    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener){
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE).setAction(getString(actionStringId), listener).show();
    }

    //return the state of permissions needed
    private boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    //requests permissions
    private void startLocationPermissionRequest(){
        ActivityCompat.requestPermissions(dataLayout.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    //uses internal processes to request user permissions
    private void requestPermissions(){
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        //provide info to user choice
        if(shouldProvideRationale){
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok, new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    startLocationPermissionRequest();
                }
            });

        }else{
            Log.i(TAG, "Requesting permission");
            startLocationPermissionRequest();
        }
    }

    //request permission result action
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        Log.i(TAG, "onRequestPermissionResult");
        if(requestCode == REQUEST_PERMISSIONS_REQUEST_CODE){
            if(grantResults.length <= 0){
                Log.i(TAG, "User interaction was cancelled.");
            }else{
                showSnackbar(R.string.permission_denied_explanation, R.string.settings, new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });
            }
        }
    }

    //function to update date
    public void getsetDate(){

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        inDate.setText(currentDateTimeString);
    }

    //function that's called when update is pressed
    public void updateFields(View view){
        getsetLocation();
        getsetDate();
    }

    //function that's called by the update button which saves the file then sends it via email to me
    public void sendEmailData(View view) {

        String csv = "date,";
        csv = csv.concat(inDate.getText().toString());
        csv = csv.concat(",lat,");
        csv = csv.concat(Double.toString(location.getLatitude()));
        csv = csv.concat(",long,");
        csv = csv.concat(Double.toString(location.getLongitude()));
        csv = csv.concat(",value,");
        csv = csv.concat(inField.getText().toString());
        showSnackbar(csv);
        //FileOperations fO = new FileOperations();
        //fO.write(FILE_NAME,csv);

        File dataPath = new File(this.getFilesDir(), "xml");
        File newData = new File(dataPath, FILE_NAME);
        newData.mkdirs();

        try {
            FileWriter writer = new FileWriter(newData);
            writer.append(csv);
            writer.flush();
            writer.close();
            Toast.makeText(this, "SAVED", Toast.LENGTH_SHORT).show();

        }catch (IOException e){
            e.printStackTrace();
        }

        String subject = "[RIFFLE DATA] date: ";
        subject = subject.concat(inDate.getText().toString());

        Uri contentUri = FileProvider.getUriForFile(this, "com.team6.rifflegroup.streamsavvy.FileProvider", newData);

        this.grantUriPermission(getPackageName(), contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {EMAIL_ADDRESS});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "File Attached");
        emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

        startActivity(Intent.createChooser(emailIntent, "Send email..."));

    }

}
