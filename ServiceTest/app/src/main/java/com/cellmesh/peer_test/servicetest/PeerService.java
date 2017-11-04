package com.cellmesh.peer_test.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;

public class PeerService extends Service {
    public PeerService() {
        Log.d("P2P","Service Constructed");
    }

    @Override
    public void onCreate() {
        Log.d("P2P","Service Created");
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("P2P","Service Removed");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("P2P","Service Starting");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("P2P","Service Destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("P2P","Service Bound");
        return null;
    }
}
