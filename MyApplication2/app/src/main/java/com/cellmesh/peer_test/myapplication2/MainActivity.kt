package com.cellmesh.peer_test.myapplication2

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.IntentFilter
import android.os.Looper
import android.net.wifi.p2p.WifiP2pDevice
import android.content.Intent
import android.R.attr.start
import android.R.raw
import android.media.MediaPlayer
import android.content.BroadcastReceiver
import android.net.wifi.p2p.WifiP2pDeviceList
import android.os.Parcelable
import android.util.Log


class MainActivity : AppCompatActivity() {
    inner class P2pStateReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    this@MainActivity.setIsWifiP2pEnabled(true)
                } else {
                    this@MainActivity.setIsWifiP2pEnabled(false)
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {

                Log.d("P2P", "Peers Changed")
                val obj = object : WifiP2pManager.PeerListListener {
                    override fun onPeersAvailable(p0: WifiP2pDeviceList?) {

                        for (device in p0?.deviceList.orEmpty()) {
                            Log.d("P2P", device.deviceName)
                        }
                    }
                }

                p2pManager?.requestPeers(p2pChannel, obj)

                // The peer list has changed!  We should probably do something about
                // that.

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
                Log.d("P2P", "Connection Changed")
                // Connection state changed!  We should probably do something about
                // that.

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
                Log.d("P2P", "Device Changed")

                // val fragment = activity.getFragmentManager()
                //         .findFragmentById(R.id.frag_list) as DeviceListFragment
                // fragment.updateThisDevice(intent.getParcelableExtra<Parcelable>(
                //         WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice)

            }
        }
    }

    private val intentFilter = IntentFilter()
    private var p2pManager: WifiP2pManager? = null
    private var p2pChannel: WifiP2pManager.Channel? = null
    private var p2pReceiver: P2pStateReceiver? = null

    fun setIsWifiP2pEnabled(isWifiP2pEnabled: Boolean) {
        // Do something in response to the boolean you are supplied
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        p2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        p2pChannel = p2pManager?.initialize(applicationContext, Looper.getMainLooper(), null)


    }

    public override fun onResume() {
        super.onResume()
        p2pReceiver = P2pStateReceiver()
        registerReceiver(p2pReceiver, intentFilter)
        p2pManager?.discoverPeers(p2pChannel, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                Log.d("P2P", "Peers Discovered, Enumerating...")
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            override fun onFailure(reasonCode: Int) {

                Log.d("P2P", "Peers Discovery Failed: " + reasonCode)
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        })
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(p2pReceiver)
    }

}
