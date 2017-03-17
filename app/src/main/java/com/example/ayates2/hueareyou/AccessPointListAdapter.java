package com.example.ayates2.hueareyou;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.PHAccessPoint;



//this class provides an adapter view for a list of Found Bridges
public class AccessPointListAdapter extends AppCompatActivity {
    private LayoutInflater mInflater;
    private List<PHAccessPoint> accessPoints;

    //view holder class for access point list
    class BridgeListItem{
        private TextView bridgeIp;
        private TextView bridgeMac;
    }

    //creates instance of @link AccessPointListAdapter class
    public AccessPointListAdapter(Context context, List<PHAccessPoint> accessPoints){
        //Cache the LayoutInflate to avoid asking for a new one each time
        mInflater = LayoutInflater.from(context);
        this.accessPoints = accessPoints;
    }

    //get a view that displays the data at the specified position in the data set
    public View getView(final int position, View convertView, ViewGroup parent){
        BridgeListItem item;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.selectbridge_item, null);
            item = new BridgeListItem();
            item.bridgeMac = (TextView) convertView.findViewById(R.id.bridge_mac);
            item.bridgeIp = (TextView) convertView.findViewById(R.id.bridge_ip);
            convertView.setTag(item);
        }
        else{
            item = (BridgeListItem) convertView.getTag();
        }
        PHAccessPoint accessPoint = accessPoints.get(position);
        item.bridgeIp.setTextColor(Color.BLACK);
        item.bridgeIp.setText(accessPoint.getIpAddress());
        item.bridgeMac.setTextColor(Color.DKGRAY);
        item.bridgeMac.setText(accessPoint.getMacAddress());

        return convertView;
    }

    //get the row id associated with the specified position in the list
    public long getItemId(int position){
        return 0;
    }

    //how many items are in the data set are represented by this adapter
    public int getCount(){
        return accessPoints.size();
    }

    //get the data item associated with the specified position in the data set
    public Object getItem(int position){
        return accessPoints.get(position);
    }

    //update date of the list view and refresh listview
    public void updateData(List<PHAccessPoint> accessPoints){
        this.accessPoints = accessPoints;
        notify(); //notifyDataSetChanged();
    }
}