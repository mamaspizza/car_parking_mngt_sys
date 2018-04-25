package dslab.carparking;
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.Vector;

//import javax.swing.*;

//import java.awt.*;
//import java.awt.event.*;
import java.util.Random;
import net.tinyos.message.*;
import net.tinyos.util.*;
import net.tinyos.packet.*;
import java.rmi.*;



public class ServerMotionListener implements MessageListener, Messenger {

	MoteIF mote;
	boolean clientconnected = false;
	double motionval = 5;
	double lightval= 100;
	PMSServerInterface stub;
	String Area = "1";
	public ServerMotionListener(){
		try{
    		System.out.println("Connecting...");
    		mote = new MoteIF(this);
    		mote.registerListener(new OscilloscopeMsg(), this);
    	}catch(Exception e){
    		System.out.println("Error in gateway");
    	}
		if(System.getSecurityManager() == null){
 			System.setSecurityManager (new RMISecurityManager() {
 			    public void checkConnect (String host, int port) {}
 			    public void checkConnect (String host, int port, Object context) {}
 			  });
        } else System.out.println("Already has a security manager, so cant set RMI SM");
	    
		
	   
		
	
		try{
        	stub  = (PMSServerInterface) Naming.lookup("//203.234.48.104:2001/Server");
 			System.out.println("Found server");	    
		}catch(RemoteException e) {System.out.println("error: " + e.getMessage());
		}catch(Exception e) {
		    	System.out.println("Lookup: " + e.getMessage());
		}
		
		/*****************************************************/
		//try{
			//stub.CollisionDetect(101, Area);
			//System.out.println("Trying to send collision");
		//}catch(RemoteException e) {System.out.println("error: " + e.getMessage());
		//}catch(Exception e) {
		//   	System.out.println("Lookup: " + e.getMessage());
		//}
		/*****************************************************/
	}

	public synchronized void  messageReceived(int dest_addr, Message msg) {
		if (msg instanceof OscilloscopeMsg) {
			
			OscilloscopeMsg mmsg = (OscilloscopeMsg)msg;
	        /* get data */	        
	        getReadings(mmsg.get_id(), mmsg.get_readings(), mmsg.get_version(), mmsg.get_count() );        
	    }
	} 
	
	public boolean analyzeReadings(int moteID, double val){
		boolean ret = false;
		
		if (val>motionval){ // >5, moved
			ret = true;
		}
		return ret;
		
	}
	
	public void getReadings(int moteID, int[] motiondata, int RSSI, int parkingareaid){
		int i = 0;
		int total = 0;
		double ave = 0;
		
		//moteID= randomize(50);
		//System.out.print("Mote ID:"+ moteID);
		while(i<motiondata.length){
			//System.out.print(" "+motiondata[i]);
			total = total + motiondata[i];
			i++;
		}
		ave=total/motiondata.length;
		Area = ""+parkingareaid;
		System.out.println(" ="+ave + " " + analyzeReadings(moteID,ave) + " " + clientconnected+" RSSI="+RSSI+" area="+parkingareaid);
		if(analyzeReadings(moteID,ave)){
			System.out.println("Car with ID " + moteID + " bumped. Sending collision data to server...");
			
			try{
				stub.CollisionDetect(moteID, Area);
			}catch(RemoteException e) {System.out.println("error: " + e.getMessage());
			}catch(Exception e) {
			   	System.out.println("Lookup: " + e.getMessage());
			}
		}
		if(RSSI!=0){
			try{
				stub.RSSIReading(moteID, Area, RSSI);
			}catch(RemoteException e) {System.out.println("error: " + e.getMessage());
			}catch(Exception e) {
			   	System.out.println("Lookup: " + e.getMessage());
			}
		}
				
	}	
    

	public void message(String arg0) {
		// TODO Auto-generated method stub
		
	}	

	public static void main(String[] args){
		 ServerMotionListener me = new ServerMotionListener();
		 
	 }
}