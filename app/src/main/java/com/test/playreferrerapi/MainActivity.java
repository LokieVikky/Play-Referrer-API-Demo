package com.test.playreferrerapi;

import android.os.Bundle;
import android.os.RemoteException;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

public class MainActivity extends AppCompatActivity {
    InstallReferrerClient referrerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        referrerClient = InstallReferrerClient.newBuilder(this).build();
        startConnection();
    }

    private void startConnection() {
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                        loadReferrerDetails();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        Toast.makeText(MainActivity.this, "API not available", Toast.LENGTH_SHORT).show();
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        Toast.makeText(MainActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    private void loadReferrerDetails() {
        ReferrerDetails response = null;
        try {
            response = referrerClient.getInstallReferrer();
            String referrerUrl = response.getInstallReferrer();
            long referrerClickTime = response.getReferrerClickTimestampSeconds();
            long appInstallTime = response.getInstallBeginTimestampSeconds();
            boolean instantExperienceLaunched = response.getGooglePlayInstantParam();

            ((TextView) findViewById(R.id.txtRefferalParams)).setText(
                    "ReferrerURL: " + referrerUrl + "\n" + "\n" +
                            "Referrer Click Time: " + referrerClickTime + "\n" + "\n" +
                            "App Install Time: " + appInstallTime
            );
            closeConnection();
        } catch (RemoteException e) {
            Toast.makeText(this, "Error occurred, please reopen app", Toast.LENGTH_SHORT).show();
            closeConnection();
            e.printStackTrace();
        }

    }

    private void closeConnection() {
        referrerClient.endConnection();
    }


}