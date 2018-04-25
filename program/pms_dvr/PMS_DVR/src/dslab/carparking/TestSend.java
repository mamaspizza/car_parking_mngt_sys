package dslab.carparking;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.IQ.Type;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;

public class TestSend implements PacketListener {

	XMPPConnection connection;
	public TestSend(){
		try{
	    	ConnectionConfiguration config = new ConnectionConfiguration("203.234.48.104",5222);
		    config.setSecurityMode(SecurityMode.required);
		    config.setSASLAuthenticationEnabled(false);
		    config.setCompressionEnabled(false);
		    
	        final String newUsername = newRandomUUID();
	        final String newPassword = newRandomUUID();  
		    
		    
		    connection = new XMPPConnection(config);
		 
		    connection.connect();
		   
		    
		   Registration registration = new Registration();
		    
		   //registration.setType(Type.ERROR)
	            registration.setType(Type.SET );

	            
	            Map<String, String> attributes = new HashMap<String, String>();
	             attributes.put("username", newUsername);
	             attributes.put("password", newPassword);
	             registration.setAttributes(attributes);
	            
	         
	             Date now = new Date();
	             long totaltime = now.getTime();
	             System.out.println("time "+totaltime);
	            connection.sendPacket(registration);
		   
		    
		  //Message msg =  new Message("Recepient", Message.Type.normal);
	       //     connection.addPacketListener(this, new MessageTypeFilter());
		
		    connection.addPacketListener(this, new PacketFilter(){
		    	
		    public boolean accept(Packet packet) {
		    	System.out.println("try to process message");
		    	return packet instanceof Message;
		    }
	       });
	 		    connection.login(newUsername, newPassword, "DVRServer");
	 		   //Message msg = new Message();
	 		   //msg.setBody("Message to your device");
	 		   
	 		  //  connection.sendPacket(msg);
		}catch(XMPPException  err){
			System.out.println("error in receiving message");
			
		}
	}
	
	public void processPacket(Packet packet){
		Message message = (Message) packet;
		
		String from = packet.getFrom();
		String body = message.getBody();

		String subject  = message.getSubject();
		//String[] tokens = body.split(">");
		//String cardno = tokens[0];
		//String moteid = tokens[1];
		System.out.println("The message is "+body+" "+from);
		
	}
	public void sendMessage(){
		
	}
	private IQ createNotificationIQ(String apiKey, String title,
            String message, String uri) {
        Random random = new Random();
        String id = Integer.toHexString(random.nextInt());
        // String id = String.valueOf(System.currentTimeMillis());

        Element notification = DocumentHelper.createElement(QName.get(
                "notification", NOTIFICATION_NAMESPACE));
        notification.addElement("id").setText(id);
        notification.addElement("apiKey").setText(apiKey);
        notification.addElement("title").setText(title);
        notification.addElement("message").setText(message);
        notification.addElement("uri").setText(uri);

        IQ iq = new IQ();
        iq.setType(IQ.Type.set);
        iq.setChildElement(notification);

        return iq;
    }
	private String newRandomUUID() {
	    String uuidRaw = UUID.randomUUID().toString();
	    return uuidRaw.replaceAll("-", "");
	}
	public static void main(String[] args){
		TestSend t = new TestSend();
	}
}
