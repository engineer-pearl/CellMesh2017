package com.cellmesh.peer_test.servicetest;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileDescriptor;
import java.util.HashMap;
import java.util.Map;

import android.net.wifi.p2p.*;

public class PeerService extends Service {
    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel p2pChannel;
    private IntentFilter p2pIntentFilter;
    private P2PBroadcastReceiver p2pReceiver;
    private WifiP2pDnsSdServiceInfo p2pElectionService;
    class P2PBroadcastReceiver extends BroadcastReceiver{
        private final WifiP2pManager p2pManager;
        private final WifiP2pManager.Channel p2pChannel;
        private final Service peerService;
        public P2PBroadcastReceiver(WifiP2pManager manager,WifiP2pManager.Channel channel,Service service) {
            super();
            p2pManager=manager;
            p2pChannel=channel;
            peerService=service;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                    Log.d("P2P","Connection Changed");
                    break;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    Log.d("P2P","Peers Changed");
                    break;
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                    Log.d("P2P","State Changed");
                    break;
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                    Log.d("P2P","Device Changed");
                    break;

                default:
                    Log.d("P2P","Unknown action.");
                    break;
            }
        }
    }
    public PeerService() {
        Log.d("P2P","Service Constructed");
    }

    @Override
    public void onCreate() {
        Log.d("P2P","Service Created");
        p2pIntentFilter=new IntentFilter();
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        p2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        p2pManager=(WifiP2pManager)getSystemService(WIFI_P2P_SERVICE);
        p2pChannel=p2pManager.initialize(getApplicationContext(),getMainLooper(),null);
        p2pReceiver=new P2PBroadcastReceiver(p2pManager,p2pChannel,this);
        HashMap<String,String> p2pElectionServiceData=new HashMap<>();
        p2pElectionServiceData.put("PORT","99821");
        p2pElectionService=WifiP2pDnsSdServiceInfo.newInstance("ElectionService","_p2p._election._tcp",p2pElectionServiceData);
        p2pManager.addLocalService(p2pChannel, p2pElectionService, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("P2P.Election","Registered");
            }

            @Override
            public void onFailure(int i) {

                Log.d("P2P.Election","Registration Failed");
            }
        });
        registerReceiver(p2pReceiver,p2pIntentFilter);
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener=new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String s, Map<String, String> map, WifiP2pDevice wifiP2pDevice) {
                Log.d("P2P.ServiceDiscovery","Discovered Service Record");
                Log.d("P2P.ServiceDiscovery","Peer: "+wifiP2pDevice.deviceName);
                Log.d("P2P.ServiceDiscovery","Service"+s);
                for(Map.Entry<String,String> entry:map.entrySet()){
                    Log.i("P2P.ServiceDiscovery","<"+entry.getKey()+">="+entry.getValue());
                }
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener sdServiceResponseListener=new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String s, String s1, WifiP2pDevice wifiP2pDevice) {
                Log.d("P2P.ServiceDiscovery","Service Response");
                Log.d("P2P.ServiceDiscovery","Peer: "+wifiP2pDevice.deviceName);
                Log.d("P2P.ServiceDiscovery","Service Instance: "+s);
                Log.d("P2P.ServiceDiscovery","Service Type: "+s1);

            }
        };
        p2pManager.setDnsSdResponseListeners(p2pChannel,sdServiceResponseListener,txtRecordListener);
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        p2pManager.addServiceRequest(p2pChannel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("P2P.ServiceDiscovery","Service Request Completed");
                    }

                    @Override
                    public void onFailure(int code) {

                        Log.d("P2P.ServiceDiscovery","Service Request Failed");
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    }
                });
        p2pManager.discoverServices(p2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("P2P.ServiceDiscovery","Service Discovery Successful");
            }

            @Override
            public void onFailure(int i) {

                Log.d("P2P.ServiceDiscovery","Service Discovery Failed");
            }
        });
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("P2P","Service Removed");
        unregisterReceiver(p2pReceiver);
        p2pManager.removeLocalService(p2pChannel, p2pElectionService, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("P2P.Election","Unregistered");
            }

            @Override
            public void onFailure(int i) {
                Log.d("P2P.Election","Unregistration Failed");
            }
        });
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
