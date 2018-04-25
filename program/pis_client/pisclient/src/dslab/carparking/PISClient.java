package dslab.carparking;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import org.androidpn.client.*;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.androidpn.client.*;

public class PISClient extends Activity implements OnClickListener, DialogInterface.OnClickListener {

	public static final String SERVICE_NAME = "dslab.carparking.PISClient";
    /** Called when the activity is first created. */
	// Buttons are declared
	
	Button btn_notify_settings;
	Button btn_reset_notify;
	Button btn_car_log_records;
	Button btn_find_park;
	Button btn_locate_car;
	Button btn_activate_collision;
	Button btn_view_col_images;
	Button btn_pis_close;
	Button btn_connect;
	
	boolean active_collision_sense;
	
	ServiceManager serviceManager;
	BroadcastReceiver mreceiver;
	// Text view is declared
	EditText txt_mote_id;
	EditText txt_notify;
	// Media player is declared
	MediaPlayer player;
	XMPPConnection connection;
	
	// My phone number is stored.
	String phone_Number = "0107777777";
	String cardno = "-11110429999999934";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        txt_notify = (EditText)findViewById(R.id.edit_text_notify);
        txt_mote_id = (EditText)findViewById(R.id.text_input_motion_no);
        
        btn_notify_settings  = (Button)findViewById(R.id.btn_settings);
        btn_reset_notify = (Button)findViewById(R.id.btn_reset_notify);
        btn_car_log_records = (Button)findViewById(R.id.btn_car_log_records);;
        btn_find_park = (Button)findViewById(R.id.btn_guide_parked_car);;
        btn_locate_car = (Button)findViewById(R.id.btn_car_locate);
        btn_activate_collision= (Button)findViewById(R.id.btn_activate_collision);
        btn_view_col_images = (Button)findViewById(R.id.btn_view_collision);
        btn_pis_close = (Button)findViewById(R.id.btn_pis_close);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        active_collision_sense=false;
        
        
        
        btn_locate_car.setOnClickListener(this);
        btn_find_park.setOnClickListener(this);
        btn_reset_notify.setOnClickListener(this);
        btn_car_log_records.setOnClickListener(this);
        btn_notify_settings.setOnClickListener(this);
        btn_activate_collision.setOnClickListener(this);
        btn_view_col_images.setOnClickListener(this);
        btn_pis_close.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        
        // Start the service
         serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
        mreceiver = new MessageReceiver(this, cardno);
        IntentFilter filter = new IntentFilter();
        
        filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
        
        
        registerReceiver(mreceiver, filter);
        buttonstatus(false);
       
               
    }
    public void onClick(View v) {
    	if(v == btn_find_park){ 
    		
    		Intent myIntent = new Intent(v.getContext(), ParkGuide.class);
    		buttonstatus(false);
            startActivityForResult(myIntent, 0);
            
    	
    	}else if(v==btn_reset_notify){
    		setText("");
    	
    		
    	}else if(v==btn_locate_car){
    	   		

    	}else if(v==btn_activate_collision){
    		if(!active_collision_sense){
    			txt_mote_id.setEnabled(false);
        		active_collision_sense=true;
        		btn_activate_collision.setText("Stop Sense");
        		
        		XMPPConnection connection = serviceManager.getXMPPManager().getConnection();
        		Message msg =  new Message("moteregistration", Message.Type.normal);
        		msg.setBody(cardno+">"+txt_mote_id.getText().toString());
        		connection.sendPacket(msg);
    		}else{
    			txt_mote_id.setEnabled(true);
        		active_collision_sense=false;
        		btn_activate_collision.setText("Collision Sense");
        		
        		XMPPConnection connection = serviceManager.getXMPPManager().getConnection();
        		setText(connection.getConnectionID());        		
        		Message msg =  new Message("moteunregistration", Message.Type.normal);
        		msg.setBody(cardno+">"+txt_mote_id.getText().toString());
        		connection.sendPacket(msg);
    			
    		}    		
    	}else if(v==btn_pis_close){
    		finish();
    	}else if(v==btn_view_col_images){
    		Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://203.234.48.105/carcollision/"));

    		startActivity(myIntent);
    	}else if(v==btn_connect){
    		if(serviceManager.getXMPPManager()!=null){
    			XMPPConnection connection = serviceManager.getXMPPManager().getConnection();
       	        Message msg =  new Message("pisregistration", Message.Type.normal);
       			msg.setBody(cardno+">"+"x");
       			connection.sendPacket(msg);
       			buttonstatus(true);
    		}
    		 
    	     
    	}    	
    }
    public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		switch(arg1){
		// Click the OK button when the siren sound stops
		case DialogInterface.BUTTON_NEUTRAL :
			soundStop();
			break;
		}
	}
	public void setText(String notification){
		txt_notify.setText(notification);
		
		
	}
	
	// Sound playing method
	public void soundPlay(int resid, boolean loop){
		// Initial sound stops
		soundStop();
		// Media Player Object Creation
		player = MediaPlayer.create(this, resid);
		// Repeat Settings
		player.setLooping(loop);
		// Playing sound
		player.start();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		soundStop();
	}

	//Sound stop methods
	public void soundStop(){
		if(player != null){
			player.stop();
			player.release();
			player = null;
		}	
	}
	
	 public void buttonstatus(boolean enabled){
		btn_notify_settings.setEnabled(enabled);
		btn_reset_notify.setEnabled(enabled);
		btn_car_log_records.setEnabled(enabled);
		btn_find_park.setEnabled(enabled);
		btn_locate_car.setEnabled(enabled);
		btn_activate_collision.setEnabled(enabled);
		btn_view_col_images.setEnabled(enabled);
	  }
	public void CarCollision(){
		// Create alert 
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		// AlertDialog Title Set
		alert.setTitle("Warning");
		// AlertDialog Message Set
		alert.setMessage("Accident");
		// AlertDialog Button Set
		alert.setNeutralButton("OK" , this);
		// Shows an alert.
		alert.show();
		//Play siren sound
		soundPlay(R.raw.a, true);
		// change the text view.
		txt_notify.setText(R.string.collision);
	}
	
	
}
