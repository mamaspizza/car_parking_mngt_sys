package dslab.carparking;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MessageReceiver extends BroadcastReceiver {

	public PISClient pisclient;
	private String _cardno;
	public MessageReceiver(PISClient pis, String cardno){
		_cardno = cardno;
		pisclient= pis;
		//pisclient.setText("");
	}
    @Override
    public void onReceive(Context aContext, Intent intent) {
        
        Bundle myIntent = intent.getExtras();
        Date now = new Date();
        long time = now.getTime();
        String strtime = Long.toString(time);
        //pisclient.setText(strtime);
        String title = myIntent.getString("NOTIFICATION_TITLE");
       
        if(title!=null){
        	 if(title.equals("collision") ){
             	String cardno = myIntent.getString("NOTIFICATION_MESSAGE");
             	if(cardno.equals(_cardno)){
             		pisclient.setText("Your car had a collision!");
             		pisclient.CarCollision();
             	}else{
             		String notification = myIntent.getString("message");
             		pisclient.setText(notification);
             	}
                 
             }else if(title.equals("message")){
            	 Log.d("C2DM", "Received message at time "+strtime);
             }
        }      
        //pisclient.setText("");
        //pisclient.setText(notification);
        return;
    }
	
}
