package dslab.carparking;

import jade.core.Agent;

import jade.core.behaviours.CyclicBehaviour;
//import jade.core.behaviours.ReceiverBehaviour;

//import org.jivesoftware.smack.Chat;
//import org.jivesoftware.smack.MessageListener;
//import org.jivesoftware.smack.packet.Message;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.Boot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.UUID;

import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
//import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
//import org.jivesoftware.smack.filter.PacketIDFilter;
//import org.jivesoftware.smack.filter.AndFilter;
//import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.xmpp.packet.IQ;


import dslab.carparking.server.starter.*;

public class PISAgent extends Agent implements PacketListener{
	private static final String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";
	private static Logger logger = Logger.getLogger("ServerStarter");
	
	private ServerStarter pisserver;
	private XMPPConnection connection;
	private SessionManager sessionManager;
	private Vector registeredpisclients;
	private LogicalCommand lc;
	
	public PISAgent(){
		try{
			ConnectionConfiguration config = new ConnectionConfiguration("203.234.48.104",5222);
		    config.setSecurityMode(SecurityMode.required);
		    config.setSASLAuthenticationEnabled(false);
		    config.setCompressionEnabled(false);
		    
            final String newUsername = newRandomUUID();
            final String newPassword = newRandomUUID();  
		    
		    
		    connection = new XMPPConnection(config);
		 
		    connection.connect();
		   // connection.login(newUsername, newPassword, "PISAgent");
		    
		   Registration registration = new Registration();
		    
		   //registration.setType(Type.ERROR)
	            registration.setType(Type.SET );
	
                
	            Map<String, String> attributes = new HashMap<String, String>();
	             attributes.put("username", newUsername);
	             attributes.put("password", newPassword);
	             registration.setAttributes(attributes);
	            
	         
	            connection.sendPacket(registration);
		   
		    
		  //Message msg =  new Message("Recepient", Message.Type.normal);
	       //     connection.addPacketListener(this, new MessageTypeFilter());
		
		    connection.addPacketListener(this, new PacketFilter(){
		    	
		    public boolean accept(Packet packet) {
		    	System.out.println("try to process message");
		    	return packet instanceof Message;
		    }
	       });
	
		    connection.login(newUsername, newPassword, "PISServer");	  
		}catch(XMPPException  err){
			System.out.println("error in receiving message");
			
			
		}
		registeredpisclients = new Vector();
		sessionManager = SessionManager.getInstance();
		//addBehaviour(new ReceiveMessage());
		XMLRuleReader xmlreader = new XMLRuleReader(); 
		lc = xmlreader.ReadRules();
		/*****************************************************/
		//RegisteredParkSense rparksensor = new RegisteredParkSense("-11110429999999934", 101 );
		//registeredpisclients.addElement(rparksensor);
		/*****************************************************/
	}
	public void setup(){
		//ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
		//msgSx.addReceiver(new AID("pmsagent", AID.ISLOCALNAME));
		//msgSx.setLanguage("English");
		//msgSx.setOntology("collision-sense-activate");
		//msgSx.setContent("101");
		//send(msgSx);
		addBehaviour(new ReceiveMessage());
	}
	public void processPacket(Packet packet){
		Message message = (Message) packet;
		
		String from = packet.getFrom();
		String body = message.getBody();

		String subject  = message.getSubject();
		String[] tokens = body.split(">");
		String cardno = tokens[0];
		String moteid = tokens[1];
		
		System.out.println("The message is "+cardno+" "+moteid+" "+from);
		if(from.equals("moteregistration")){	
			
			System.out.println("try moteregistration");
			int i = 0;
			while(i<registeredpisclients.size()){
				RegisteredPISClient rclient = (RegisteredPISClient)registeredpisclients.elementAt(i);
				if(rclient.getCardNo().equals(cardno)){
					rclient.registerParkSENSE( Integer.parseInt(moteid) );
					System.out.println("Activating collision sensor");
					ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
					msgSx.addReceiver(new AID("pmsagent", AID.ISLOCALNAME));
					msgSx.setLanguage("English");
					msgSx.setOntology("collision-sense-activate");
					msgSx.setContent(moteid);
					send(msgSx);
					i=registeredpisclients.size();
				}
				i++;
			}			
					
		}else if(from.equals("moteunregistration")){
			int i = 0;
			while(i<registeredpisclients.size()){
				RegisteredPISClient rclient = (RegisteredPISClient)registeredpisclients.elementAt(i);
				if(rclient.getCardNo().equals(cardno)){
					rclient.unregisterParkSENSE(  );
					System.out.println("Deactivating collision sensor");
					ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
					msgSx.addReceiver(new AID("pmsagent", AID.ISLOCALNAME));
					msgSx.setLanguage("English");
					msgSx.setOntology("collision-sense-deactivate");
					msgSx.setContent(moteid);
					send(msgSx);
					i=registeredpisclients.size();
				}
				i++;
			}			
		
		}else if(from.equals("pisregistration")){
			
			/** *verify same cardno */
			int i = 0;
			boolean duplicate = false;
			while(i<registeredpisclients.size()){
				if(cardno.equals(((RegisteredPISClient)registeredpisclients.elementAt(i)).getCardNo() )){
										
					ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
					msgSx.addReceiver(new AID("pmsagent", AID.ISLOCALNAME));
					msgSx.setLanguage("English");
					msgSx.setOntology("collision-sense-deactivate");
					msgSx.setContent(((RegisteredPISClient)registeredpisclients.elementAt(i)).getMoteID()+"");
					send(msgSx);
					((RegisteredPISClient)registeredpisclients.elementAt(i)).unregisterParkSENSE();
					duplicate=true;
				}
				i++;
			}
			if(!duplicate){
				System.out.println(cardno+" is not duplicate");
				RegisteredPISClient pisclient = new RegisteredPISClient(cardno);
				registeredpisclients.addElement(pisclient);
			}
			
		}else if(from.equals("recommendedarea")){
			/** *verify same cardno */
			int i = 0;
			while(i<registeredpisclients.size()){
				if(cardno.equals(((RegisteredPISClient)registeredpisclients.elementAt(i)).getCardNo() )){
										
					ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
					msgSx.addReceiver(new AID("pmsagent", AID.ISLOCALNAME));
					msgSx.setLanguage("English");
					msgSx.setOntology("request-area-recommended");
					msgSx.setContent(cardno);
					send(msgSx);
				}
				i++;
			}
		}else if(from.equals("currentlocation")){
			
			/** *verify same cardno */
			int i = 0;
			while(i<registeredpisclients.size()){
				if(cardno.equals(((RegisteredPISClient)registeredpisclients.elementAt(i)).getCardNo() )){
										
					ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
					msgSx.addReceiver(new AID("pmsagent", AID.ISLOCALNAME));
					msgSx.setLanguage("English");
					msgSx.setOntology("request-car-current-location");
					msgSx.setContent(cardno);
					send(msgSx);
					((RegisteredPISClient)registeredpisclients.elementAt(i)).unregisterParkSENSE();
				}
				i++;
			}
		}else if(from.equals("message")){
			sendMessage(body);
		}
	}
    private String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }
	public void RequestNumberCarsArea(){
		ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
		msgSx.addReceiver(new AID("pmsagent", AID.ISLOCALNAME));
		msgSx.setLanguage("English");
		msgSx.setOntology("request-area-status");
		msgSx.setContent("request");
		send(msgSx);
	}
	public void sendMessage(String msg){
		int i = 0;
		String tmp ="";		
		
		System.out.println("Trying to send notification...");
		String apiKey = "1234567890";
		IQ notificationIQ = createNotificationIQ(apiKey,  "message",msg, "TestMessageSent");
        for (ClientSession session : sessionManager.getSessions()) {
            if (session.getPresence().isAvailable()) {
                notificationIQ.setTo(session.getAddress());
              
                session.deliver(notificationIQ);
                System.out.println("send notification to..."+ session.toString() );
            }
        }
	}
	public void UpdateCurrentLocation(String cardno, String currentlocation){
		int i = 0;
		String tmp ="";
		//System.out.println("XXXXXXXXXXXXXXXXXXXXXXXn...");
		while(i<registeredpisclients.size()){
			RegisteredPISClient client = (RegisteredPISClient)registeredpisclients.elementAt(i);
			tmp = client.getCardNo();
			if(tmp.equals(cardno)){
				//if(client.getMoteID()!=-1){
					System.out.println("Trying to send location...");
					String apiKey = "1234567890";
					String command = lc.GetCommand(currentlocation, client.getRecommendedArea());
					cardno = cardno+">"+currentlocation+">"+command;
					IQ notificationIQ = createNotificationIQ(apiKey,  "currentlocation",cardno, "PISAgent");
			        for (ClientSession session : sessionManager.getSessions()) {
			            if (session.getPresence().isAvailable()) {
			                notificationIQ.setTo(session.getAddress());
			                session.deliver(notificationIQ);
			                System.out.println("send notification to..."+ session.toString() );
			            }
			        }
				//}
				
			}
			i++;
		}
	}
	
	public void RecommendationArea(String cardno, String recommendedarea){
		int i = 0;
		String tmp ="";
		
		while(i<registeredpisclients.size()){
			RegisteredPISClient client = (RegisteredPISClient)registeredpisclients.elementAt(i);
			tmp = client.getCardNo();
			if(tmp.equals(cardno)){
				if(client.getMoteID()!=-1){
					System.out.println("Trying to send recommendation area...");
					client.setRecommendedArea(recommendedarea);
					String apiKey = "1234567890";
					cardno = cardno+">"+recommendedarea+">"+"near school electronics and information building";
					IQ notificationIQ = createNotificationIQ(apiKey,  "recommendedarea",cardno, "PISAgent");
			        for (ClientSession session : sessionManager.getSessions()) {
			            if (session.getPresence().isAvailable()) {
			                notificationIQ.setTo(session.getAddress());
			                session.deliver(notificationIQ);
			                System.out.println("send notification to..."+ session.toString() );
			            }
			        }
				}
				
			}
			i++;
		}
	}
	public void CollissionAlert(String cardno){
		int i = 0;
		String tmp ="";
		
		while(i<registeredpisclients.size()){
			RegisteredPISClient client = (RegisteredPISClient)registeredpisclients.elementAt(i);
			tmp = client.getCardNo();
			if(tmp.equals(cardno)){
				if(client.getMoteID()!=-1){
					System.out.println("Trying to send notification...");
					String apiKey = "1234567890";
					IQ notificationIQ = createNotificationIQ(apiKey,  "collision",cardno, "PISAgent");
			        for (ClientSession session : sessionManager.getSessions()) {
			            if (session.getPresence().isAvailable()) {
			                notificationIQ.setTo(session.getAddress());
			                session.deliver(notificationIQ);
			                System.out.println("send notification to..."+ session.toString() );
			            }
			        }
				}
				
			}
			i++;
		}
		
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
	private class ReceiveMessage extends CyclicBehaviour {
		public void action() {
			ACLMessage msgRx = receive();
			
			if (msgRx != null) {
				
				String cont = msgRx.getContent();
				String onto = msgRx.getOntology();
				if(onto.equals("collision-car-id") ){
					CollissionAlert(cont);
				}else if(onto.equals("collision-area-id") ){
					Message msg =  new Message("collision-area-id", Message.Type.normal);
					msg.setBody(cont);
					 connection.sendPacket(msg);
				}else if(onto.equals("reply-area-recommended") ){
					String[] tokens = cont.split(">");
					String cardno = tokens[0];
					String areaid = tokens[1];
					RecommendationArea(cardno, areaid);
					//UpdateNumberCarsArea(cont);
				}else if(onto.equals("reply-car-current-location") ){
					System.out.println("aaa");
					String[] tokens = cont.split(">");
					String cardno = tokens[0];
					String areaid = tokens[1];
					UpdateCurrentLocation(cardno, areaid);
				}else if(onto.equals("reply-area-status") ){
					//UpdateNumberCarsArea(cont);
				}else if(onto.equals("send-duplicate-id") ){
					System.out.println("You have a duplicate ID");								
				}
				block();
			}
		}		
	}
	 public static void main(String[] args) {		
		 try {
	           
	        StreamHandler sh = new StreamHandler(System.out,
	                new SimpleFormatter());
	        logger.addHandler(sh);
	        logger.setLevel(Level.ALL);
	        new ServerStarter().start();
	        System.out.println("check if running");
	        
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
		Boot boot = new Boot();
		//PISAgent node = new PISAgent();
		
		String[] s = new String[3];
		//s[0] = "-gui";
		s[0] = "-container";
		//s[2] = "Main-Container";
		s[1] = "-agents";
		s[2] = "pisagent:dslab.carparking.PISAgent";		
		boot.main(s);   
	 }
	
}
