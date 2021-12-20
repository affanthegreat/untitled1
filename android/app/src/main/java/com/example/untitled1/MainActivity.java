package com.example.untitled1;
import java.util.List;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.*;
//import android.telephony.gsm.GsmCellLocation;
//import android.telephony.cdma.CdmaCellLocation;
//import android.telephony.CellInfo;
//import android.telephony.CellLocation;
//import android.telephony.NeighboringCellInfo;
//import android.telephony.PhoneStateListener;
//import android.telephony.SignalStrength;
//import android.telephony.SubscriptionInfo;
//import android.telephony.SubscriptionManager;
//import android.telephony.TelephonyManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {

    public static MethodChannel methodChannel;
    public final String channelName = "notification";
    NotificationManagerCompat notificationManager;
    public final String NOTIFICATION_ID = "Channel_ID";



    public void getIpBaseStation() {
        //permission check

        String list = "";  //I'm just adding everything to a string to display, but you can do whatever

        //get cell info
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> infos = tel.getAllCellInfo();
        for (int i = 0; i<infos.size(); ++i)
        {
            try {
                CellInfo info = infos.get(i);
                if (info instanceof CellInfoGsm) //if GSM connection
                {
                    list += "Site_"+i + "\r\n";
                    list += "Registered: " + info.isRegistered() + "\r\n";
                    CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                    CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                    list += "cellID: "+ identityGsm.getCid() + "\r\n";
                    list += "dBm: " + gsm.getDbm() + "\r\n\r\n";
                    //call whatever you want from gsm / identitydGsm
                }
                else if (info instanceof CellInfoLte)  //if LTE connection
                {
                    list += "Site_"+i + "\r\n";
                    list += "Registered: " + info.getCellIdentity() + "\r\n";
                    CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                    CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                    //call whatever you want from lte / identityLte
                }
                else if (info instanceof CellInfoWcdma)  //if wcdma connection
                {
                    CellSignalStrengthWcdma wcdmaS = ((CellInfoWcdma) info).getCellSignalStrength();
                    CellIdentityWcdma wcdmaid = ((CellInfoWcdma)info).getCellIdentity();
                    list += "Site_"+i + "\r\n";
                    list += "Cell Indentity: " + info.getCellIdentity() + "\r\n";
                    //call whatever you want from wcdmaS / wcdmaid

                }

            } catch (Exception ex) {
                System.out.println("Error aaya bsdk");
                System.out.println(ex.getMessage());
            }
        }
        System.out.println(list);  //display everything
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);




        methodChannel = new MethodChannel(
                flutterEngine.getDartExecutor().getBinaryMessenger(),
                channelName);
        methodChannel.setMethodCallHandler((call, result) -> {
            if (call.method.equals("create")) {

                TelephonyManager  tm=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                //Calling the methods of TelephonyManager the returns the information
                CellLocation gc = tm.getCellLocation();


                List<CellInfo> cellInfos=tm.getAllCellInfo();
//                for(CellInfo x:cellInfos){
//                    System.out.println(x.getCellIdentity());
//                }
                getIpBaseStation();

                result.success(cellInfos.toString());
            }

        });
    }
}