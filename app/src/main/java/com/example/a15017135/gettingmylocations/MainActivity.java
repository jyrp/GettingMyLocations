package com.example.a15017135.gettingmylocations;

import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import static com.example.a15017135.gettingmylocations.R.id.btnStop;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    Button btnstart,btnstop,btncheck;
    TextView tvlatlng;
    String latitude,longtitude,folderLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnstart = (Button)findViewById(R.id.btnstart);
        btnstop = (Button)findViewById(btnStop);
        btncheck = (Button)findViewById(R.id.btnCheck);
        tvlatlng = (TextView)findViewById(R.id.tvlatlng);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LatLng";

        File folder = new File(folderLocation);
        if (folder.exists() == false) {
            boolean result = folder.mkdir();
            if (result == true) {
                Log.d("File Read/Write", "Folder created");
            }
        }
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                startService(i);
            }
        });

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
            }
        });

        btncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File targetFile = new File(folderLocation, "data.txt");

                if (targetFile.exists() == true) {
                    String data = "";
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null) {
                            data += line + "\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to read!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    tvlatlng.setText(data);
                }
            }
        });
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest
                    .PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setSmallestDisplacement(100);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);

        } else {
            mLocation = null;
            Toast.makeText(MainActivity.this,
                    "Permission not granted to retrieve location info",
                    Toast.LENGTH_SHORT).show();
        }

        if (mLocation != null) {
            latitude = (mLocation.getLatitude() + "");
            longtitude = (mLocation.getLongitude() + "");

            File targetFile = new File(folderLocation, "data.txt");

            try {
                FileWriter writer = new FileWriter(targetFile, true);
                writer.write("Latitude: "+ latitude + "\n" + "Longtitude: " + longtitude);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Failed to write!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Toast.makeText(this, "Lat : " + mLocation.getLatitude() +
                            " Lng : " + mLocation.getLongitude(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location not Detected",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onLocationChanged(Location location) {

        try {

            File targetFile = new File(folderLocation, "data.txt");
            FileWriter writer = new FileWriter(targetFile, true);
            writer.write("Latitude: "+ latitude + "\n" + "Longtitude: " + longtitude);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to write!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        //the detected location is given by the variable location in the signature
        Toast.makeText(this, "Lat : " + location.getLatitude() + " Lng : " +
                location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

}
