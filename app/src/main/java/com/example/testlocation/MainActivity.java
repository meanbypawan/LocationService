package com.example.testlocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.testlocation.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    double latitude,longitude;
    LocationManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PermissionChecker.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},22);
        }

        manager = (LocationManager)getSystemService(LOCATION_SERVICE);

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,100,new GetLocation());
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               binding.textView.setText(latitude+" : "+longitude);
               try {
                   Geocoder geocoder;
                   List<Address> addresses;
                   geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                   addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                   String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                   String city = addresses.get(0).getLocality();
                   String state = addresses.get(0).getAdminArea();
                   String country = addresses.get(0).getCountryName();
                   String postalCode = addresses.get(0).getPostalCode();
                   String knownName = addresses.get(0).getFeatureName();
//                   Log.e("Address","Address : "+address+
//                           "\nCity : "+city+
//                           "\nState : "+state+
//                           "\nCountry : "+country+
//                           "\nPostalCode : "+postalCode+
//                           "\nKnownName : "+knownName);
                   String channelId = "Location";
                   String channeName = "CurrentLocation";

                   NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                   NotificationCompat.Builder nb = new NotificationCompat.Builder(MainActivity.this,channelId);

                   if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                       NotificationChannel channel = new NotificationChannel(channelId,channeName, NotificationManager.IMPORTANCE_HIGH);
                       manager.createNotificationChannel(channel);
                   }
                   nb.setChannelId(channelId);
                   nb.setSmallIcon(R.mipmap.ic_launcher);
                   nb.setContentTitle("Current Location");
                   nb.setContentText(address);
                   manager.notify(1,nb.build());
               }
               catch (Exception e){
                   Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
               }
             }
        });
    }
    // GPS
    class GetLocation implements LocationListener{
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
           latitude = location.getLatitude();
           longitude = location.getLongitude();
        }
    }
}