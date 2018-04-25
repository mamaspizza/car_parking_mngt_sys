package dslab.carparking;

import java.util.Date;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LocationInformationReceiver extends BroadcastReceiver  {

	public ParkGuide parkguide;
	private String _cardno;
	public LocationInformationReceiver(ParkGuide pg, String cardno){
		_cardno = cardno;
		parkguide= pg;
		//pisclient.setText("");
	}
    @Override
    public void onReceive(Context aContext, Intent intent) {
        
        Bundle myIntent = intent.getExtras();
        Date now = new Date();
        long time = now.getTime();
        String strtime = Long.toString(time);
        String title = myIntent.getString("NOTIFICATION_TITLE");
       
        if(title!=null){
        	 if(title.equals("currentlocation") ){
             	String body = myIntent.getString("NOTIFICATION_MESSAGE");
             	
             	String[] tokens = body.split(">");
        		String cardno = tokens[0];
        		String clocation = tokens[1];
        		String command = tokens[2];
             	
             	if(cardno.equals(_cardno)){
             		parkguide.updateLocation(clocation,command);           		
             	}
                 
             }else if(title.equals("recommendedarea")){
            	String body = myIntent.getString("NOTIFICATION_MESSAGE");
            	String[] tokens = body.split(">");
            	String cardno = tokens[0];
            	String area = tokens[1];
         		String desc = tokens[2];
         		
         		if(cardno.equals(_cardno)){
         			parkguide.changeRecommendedArea(area, desc);
         		}
         		
             }
        }      
        return;
    }
}
