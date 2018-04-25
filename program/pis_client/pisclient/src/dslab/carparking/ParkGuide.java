package dslab.carparking;

import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;
import org.androidpn.client.*;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ParkGuide extends Activity implements OnClickListener {
	
	Button btn_exit;
	Button btn_guide;
	ImageView direction;
	EditText txt_desc;
	TextView txt_recom_area;
	TextView txt_cur_location;
	
	// My phone number is stored.
	String phone_Number = "0107777777";
	String cardno = "-11110429999999934";
	ServiceManager serviceManager;
	BroadcastReceiver mreceiver;
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.parkingguidance);
	        direction = (ImageView)findViewById(R.id.img_direction);
	        txt_desc= (EditText)findViewById(R.id.text_area_desc);
	        //txt_desc = (TextView)findViewById(R.id.txt_details);
	        txt_recom_area = (TextView)findViewById(R.id.lbl_recomended_parkarea);
	        txt_cur_location = (TextView)findViewById(R.id.lbl_current_location);
	        btn_exit  = (Button)findViewById(R.id.btn_close_guide);
	        btn_guide  = (Button)findViewById(R.id.btn_direction);
	        
	        btn_exit.setOnClickListener(this);
	        btn_guide.setOnClickListener(this);
	        
	        serviceManager = new ServiceManager(this);
	        serviceManager.setNotificationIcon(R.drawable.notification);
	        serviceManager.startService();
	        mreceiver = new LocationInformationReceiver(this, cardno);
	        IntentFilter filter = new IntentFilter();
	        
	        filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
	        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
	        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
	        
	        XMPPConnection connection = serviceManager.getXMPPManager().getConnection();
    		Message msg =  new Message("recommendedarea", Message.Type.normal);
    		msg.setBody(cardno+">"+"x");
    		connection.sendPacket(msg);
	        
    		registerReceiver(mreceiver, filter);
	  }
	  public void onClick(View v) {
	    	if(v == btn_exit){ 
	    		finish();
	    		//Intent myIntent = new Intent(v.getContext(), PISClient.class);
	            //startActivityForResult(myIntent, 0);
	    	}if(v == btn_guide){ 
	    		 XMPPConnection connection = serviceManager.getXMPPManager().getConnection();
	     		Message msg =  new Message("currentlocation", Message.Type.normal);
	     		msg.setBody(cardno+">"+"x");
	     		connection.sendPacket(msg);
	    	}
	  }
	 
	  public void changeRecommendedArea(String area, String desc){
		  txt_recom_area.setText("Recommended area: "+area);
		  txt_desc.setText(desc);		  
	  }
	  public void updateLocation(String area, String cmd){
		  txt_cur_location.setText("Current location: "+area);
		  
		  if(cmd.equals("go_straight")){
			  direction.setImageResource(R.drawable.straight);
		  }else if(cmd.equals("go_right")){
			  direction.setImageResource(R.drawable.right);
		  }else if(cmd.equals("go_left")){
			  direction.setImageResource(R.drawable.left);
		  }
	  }
	 
}
