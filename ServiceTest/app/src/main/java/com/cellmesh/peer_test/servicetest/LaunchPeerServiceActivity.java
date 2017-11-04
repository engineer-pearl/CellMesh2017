package com.cellmesh.peer_test.servicetest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchPeerServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent=new Intent(getApplicationContext(),PeerService.class);
        this.startService(serviceIntent);
        this.finish();
        //setContentView(R.layout.activity_launch_peer_service);
    }
}
