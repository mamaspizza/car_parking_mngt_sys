package dslab.carparking;


import it.sauronsoftware.ftp4j.FTPClient;


import java.util.*;
import java.io.*;


import org.jivesoftware.smack.PacketListener;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;


;

public class DVRAgentXMPP implements PacketListener{
	
	XMPPConnection connection;
	private int  _areaID=1;
	private int _firstdelay;
	private long _timedelay = 20000;
	private Date previous;
	
    public DVRAgentXMPP(){
    	
    	_firstdelay=0;
    	previous = new Date();
    	try{
    		
			//SaveImage(10);
			//grab.stopWebcam();
			
			ConnectionConfiguration config = new ConnectionConfiguration("203.234.48.104", 5222);
			config.setSecurityMode(SecurityMode.required);
			config.setSASLAuthenticationEnabled(false);
			config.setCompressionEnabled(false);
			final String newUsername = newRandomUUID();
			final String newPassword = newRandomUUID();
			connection = new XMPPConnection(config);
			connection.connect();
			
			Registration registration = new Registration();
			registration.setType(Type.SET);
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("username", newUsername);
			attributes.put("password", newPassword);
			
			registration.setAttributes(attributes);
			connection.sendPacket(registration);
			connection.addPacketListener(this, new PacketFilter(){
				public boolean accept(Packet packet){
					System.out.println("try process message here");
					return packet instanceof Message;
				}
			});
			
			connection.login(newUsername, newPassword, "DVRServer");
			
    	
		}catch(XMPPException xe){
			System.out.println("Error "+xe);
		} 
      	try{
        	
    		FrameGrab grab = new FrameGrab();
    		grab.run();
        }catch(Exception e){
    		System.out.println("Error "+e);
        }
    }
	 
	private String newRandomUUID() {
	    String uuidRaw = UUID.randomUUID().toString();
	    return uuidRaw.replaceAll("-", "");
	}
	   
	public void processPacket(Packet packet){
		Message message = (Message) packet;
		
		String from = packet.getFrom();
		String body = message.getBody();
		String subject  = message.getSubject();
		System.out.println("The message is "+body+" "+from);
		if(from.equals("collision-area-id")){	
			
			int areaid = Integer.parseInt(body);
			if(areaid==_areaID){
				
				Date now = new Date();
				long totaltime = now.getTime()-previous.getTime();
				System.out.println("processing");
				if(_firstdelay==0){
					System.out.println("previous is null");
					SaveImage(10);
					_firstdelay=1;
				}else{
					if(totaltime>_timedelay){
						System.out.println("previous is not null");
						SaveImage(10);
						previous= now;
					}
				}				
			}
		}	
		
	}
	
	public void SaveImage(int numimage) {
    	File[] inputFile = new File[numimage];	    	
    	int i = 0;   	
    	try {
    		FTPClient ftp = new FTPClient();
    	    System.out.println("Try saving..");
    	    // Connect to an FTP server on port 21.
    	    ftp.setType(FTPClient.TYPE_BINARY);
    	    ftp.connect("203.234.48.104", 22);
    	    ftp.login("root", "lemontruck228");
    	    
    	    
    	    // Upload some files.
    	    i = 0;
    	    
    	    while(i<numimage){
    	    	inputFile[i] = new File("C:\\webcampics\\"+i+".png");
    	    	ftp.upload(inputFile[i], new MyFTPListener());
    	    	i++;
    	    }   	    
    	    // Quit from the FTP server.
    	    System.out.println("Saved!");
    	    ftp.disconnect(true);
    	}
    	catch(Exception e){
    		System.out.println("Error "+e);
    	}   
    }	   
	 
    public static void main(String args[])   {
    // declare variables
    	DVRAgentXMPP c = new DVRAgentXMPP();    	
    

    }
	 
	 

}
