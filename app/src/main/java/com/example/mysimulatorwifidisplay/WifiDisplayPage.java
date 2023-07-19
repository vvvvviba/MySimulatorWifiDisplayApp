package com.example.mysimulatorwifidisplay;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysimulatorwifidisplay.adapter.DisplayAdapter;
import com.example.mysimulatorwifidisplay.mojo.Display;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @ClassName: WifiDisplayPage
 * @Description: A preference screen for show wifi display device. Including implement.
 * @Author: shuailin.wang
 * @CreateDate: 2023/7/18
 */
public class WifiDisplayPage extends AppCompatActivity
        implements WifiP2pManager.DeviceInfoListener, WifiP2pManager.PeerListListener {

    private WifiP2pManager mWifiP2pManager;

    private WifiP2pManager.Channel mChannel;

    private IntentFilter mIntentFilter;

    private boolean mIsSearching;

    private WifiP2pDeviceList mPeers;

    private RecyclerView mRecyclerView;

    private DisplayAdapter mDisplayAdapter;

    public static final String TAG = "WifiDisplayPage";

    public static final int REQUEST_CODE_ADDRESS = 100;

    /**
     * Execute all p2p signal by BroadcastReceiver.
     *      WIFI_P2P_PEERS_CHANGED_ACTION: get all discover result.
     *      WIFI_P2P_DISCOVERY_CHANGED_ACTION: upload the search state of WifiDisplayPage.
     */
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    checkPermission();
                    mWifiP2pManager.requestPeers(mChannel, WifiDisplayPage.this);
                    break;
                case WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION:
                    int discoverState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
                    Log.d(TAG, "onReceive: " + discoverState);
                    if (discoverState == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                        uploadSearchingButtonState(true);
                    } else {
                        uploadSearchingButtonState(false);
                    }
                default:
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_display_settings);

        mIntentFilter = new IntentFilter();

        if (mWifiP2pManager == null) {
            mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        }
        if (mWifiP2pManager != null) {
            initChannel();
        } else {
            Log.e(TAG, "onCreate: init WifiP2pManager failed!");
        }

        // get recycle view and set empty.
        mRecyclerView = (RecyclerView) findViewById(R.id.displayView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Button button = (Button) findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
    }

    private void uploadSearchingButtonState(boolean searching) {
        mIsSearching = searching;
        Button button = (Button) findViewById(R.id.search);
        if (searching) {
            button.setEnabled(false);
            button.setText("正在搜索...");
        } else {
            button.setEnabled(true);
            button.setText("搜索");
        }
    }

    /**
     * This is the first step to search p2p device.
     * Process should be like below:
     *  discoverPeers()
     *      ---> once success, send WIFI_P2P_PEERS_CHANGED_ACTION
     *          ---> BroadcastReceiver receive action
     *              ---> requestPeers()
     *                  ---> PeersListListener.onPeersAvailable(WifiP2pDeviceList)
     */
    private void startSearch() {
        if (mWifiP2pManager != null && mChannel != null) {
            checkPermission();
            mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i) {
                    Log.e(TAG, "discover peers error. error=" + i);
                }
            });
        }
    }

    /**
     * Check permission if the context don't have them yet.
     * If we don't have location permission, we need to apply it to use P2P function.
     */
    private void checkPermission() {
        int haveNearbyWifiDevice = ContextCompat.checkSelfPermission(WifiDisplayPage.this, Manifest.permission.NEARBY_WIFI_DEVICES);
        int haveAccessFineLocation = ContextCompat.checkSelfPermission(WifiDisplayPage.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (haveNearbyWifiDevice == PackageManager.PERMISSION_GRANTED && haveAccessFineLocation == PackageManager.PERMISSION_GRANTED) {
            // nothing need to do here.
        } else {
            // apply the permission.
            ActivityCompat.requestPermissions(WifiDisplayPage.this,
                    new String[] {Manifest.permission.NEARBY_WIFI_DEVICES, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ADDRESS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ADDRESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "apply the permission success!");
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // add some action here, execute them by BroadcastReceiver.
        // see more in @mReceiver
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);  // discover peers and show them.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION); // discover end or not.
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    /**
     * init Wifi P2p Channel to execute some function like connect and search or something else.
     * @return
     */
    private boolean initChannel() {
        if (mChannel != null) {
            Log.d(TAG, "initChannel: Channel has been already init.");
            return true;
        }
        if (mWifiP2pManager != null) {
            mChannel = mWifiP2pManager.initialize(getApplicationContext(), getMainLooper(), new WifiP2pManager.ChannelListener() {
                @Override
                public void onChannelDisconnected() {
                    Log.d(TAG, "onChannelDisconnected: disconnect channel.");
                }
            });
        }
        if (mChannel == null) {
            Log.e(TAG, "initChannel: init channel failed.");
            mWifiP2pManager = null;
            return false;
        }
        Log.d(TAG, "initChannel: init channel success.");
        return true;
    }

    @Override
    public void onDeviceInfoAvailable(@Nullable WifiP2pDevice wifiP2pDevice) {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        Log.d(TAG, "Requested peers are available." + wifiP2pDeviceList.getDeviceList());
        mPeers = wifiP2pDeviceList;
        // we need to convert Collections to ArrayList
        ArrayList<Display> displayList = new ArrayList<>();
        for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
            Display display = new Display(device.deviceName, device.deviceAddress, device.primaryDeviceType);
            displayList.add(display);
        }
        mDisplayAdapter = new DisplayAdapter(displayList);
        mRecyclerView.setAdapter(mDisplayAdapter);
    }
}
