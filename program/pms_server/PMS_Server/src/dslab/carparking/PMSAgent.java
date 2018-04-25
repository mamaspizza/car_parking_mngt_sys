package dslab.carparking;


//import dslab.carparking.PISAgent.ReceiveMessage;
import java.rmi.RemoteException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.Boot;


public class PMSAgent extends Agent {
	
	public PMSRMIComm pmsrmi;
	public PMSAgent()throws RemoteException{
		pmsrmi = new PMSRMIComm(this);		
		//agentserver.setRMIcommunication(rmiserver);
	}
	public void setup() {
		System.out.println("ok agent pms");
		addBehaviour(new ReceiveMessage(this));
	}
	public void registerDVR(String areaid){
		
	}
	public void registerMonitorView(String areaid){
		
	}
	public void registerPIS(String pisid){
		
	}
	public int getNumberCars(String AreaID){
		int numcars=0;
		
		return numcars;
	}
	public boolean checkDuplicate(String id){
		boolean duplicate = false;
		return duplicate;
	}
	public String checkCurrentLocation(String carid){
		String areaid = "";
		areaid=pmsrmi.getCurrentLocation(carid);
		return areaid;
	}
	public void detectCollision(String cardno, String Area){
		/* Then send to PIS and PMS*/
		/** SEND THE URL OF IMAGES**/
		ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
		
		String onto = "collision-area-id";
		String cont = Area;
		msgSx.addReceiver(new AID("pisagent", AID.ISLOCALNAME));
		msgSx.setLanguage("English");
		msgSx.setOntology(onto);
		msgSx.setContent(cont);
		send(msgSx);
		System.out.println("sending collision information");
		try
		  {
		  Thread.sleep(1000);  
		 
		  }catch (InterruptedException ie)
		  {
		  System.out.println(ie.getMessage());
		  }
		
		msgSx = new ACLMessage(ACLMessage.INFORM);
		 onto = "collision-car-id";
		 cont = cardno;
		//String onto = msgSx.getOntology();
		msgSx.addReceiver(new AID("pisagent", AID.ISLOCALNAME));
		msgSx.setLanguage("English");
		msgSx.setOntology(onto);
		msgSx.setContent(cont);
		send(msgSx);
		
	}
	public void ActivateCollisionSense(int moteid){
		System.out.println("activate sensor");
		if(pmsrmi!=null){
			pmsrmi.ActivateCollisionSense(moteid);
		}
		
	}
	public void DeactivateCollisionSense(int moteid){
		if(pmsrmi!=null){
			pmsrmi.DeactivateCollisionSense(moteid);
		}
		
	}
	private class ReceiveMessage extends CyclicBehaviour {
		//private PMSRMIComm pmsrmi;
		private PMSAgent _pmsagent;
		public ReceiveMessage(PMSAgent pmsagent){
			_pmsagent = pmsagent;
		}
		public void action() {
			ACLMessage msgRx = receive();
			
			if(msgRx!=null){
				String cont = msgRx.getContent();
				String onto = msgRx.getOntology();
				AID senderid = msgRx.getSender();
				System.out.println("check message");
				boolean duplicate = false;
				if (onto.equals(null)) {
					System.out.println("There are no content of message");
				} else {
					System.out.println("PMS Agent received message. Ontology is "+onto);
					if(onto.equals("send-dvr-info") ){
						
						duplicate = checkDuplicate(cont);
						if(!duplicate){
							registerDVR(cont);
							try{					
								cont = "confirm";
								/* Then send to PIS */
								ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
								msgSx.addReceiver(senderid);
								msgSx.setLanguage("English");
								msgSx.setOntology(onto);
								msgSx.setContent(cont);
								
								send(msgSx);
							}catch(Exception e){
								System.out.println("Error");
							}
						}else{
							System.out.println("ID duplicated");
						}
						
						
					}else if(onto.equals("send-monitorview-info")){
						
						duplicate = checkDuplicate(cont);
						if(!duplicate){
							registerMonitorView(cont);
							try{					
								cont = "confirm";
								/* Then send to PIS */
								ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
								msgSx.addReceiver(senderid);
								msgSx.setLanguage("English");
								msgSx.setOntology(onto);
								msgSx.setContent(cont);
								
								send(msgSx);
							}catch(Exception e){
								System.out.println("Error");
							}
						}else{
							System.out.println("ID duplicated");
						}
						
					}else if(onto.equals("send-pis-info")){
						duplicate = checkDuplicate(cont);
						registerPIS(cont);
						if(!duplicate){
							try{					
								cont = "confirm";
								/* Then send to PIS */
								ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
								msgSx.addReceiver(senderid);
								msgSx.setLanguage("English");
								msgSx.setOntology(onto);
								msgSx.setContent(cont);
								
								send(msgSx);
							}catch(Exception e){
								System.out.println("Error");
							}

						}else{
							System.out.println("ID duplicated");
						}
					}else if(onto.equals("request-area-recommended")){
						int numcars = getNumberCars(cont);
						try{
							onto ="reply-area-recommended";
							String cardno = cont;
							String areaid = pmsrmi.getRecommendedParkingArea();
							
							cont = cardno+">"+areaid;
							/* Then send to PIS */
							ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
							msgSx.addReceiver(senderid);
							msgSx.setLanguage("English");
							msgSx.setOntology(onto);
							msgSx.setContent(cont);
							System.out.println("Sending recommended area");
							send(msgSx);
						}catch(Exception e){
							System.out.println("Error");
						}
					}else if(onto.equals("request-car-current-location")){
						String cardno = cont;
						String areaid = checkCurrentLocation(cardno);
						try{
							onto ="reply-car-current-location";
							cont = cardno+">"+areaid;
							/* Then send to PIS */
							ACLMessage msgSx = new ACLMessage(ACLMessage.INFORM);
							msgSx.addReceiver(senderid);
							msgSx.setLanguage("English");
							msgSx.setOntology(onto);
							msgSx.setContent(cont);
							System.out.println("Sending location area");
							send(msgSx);
						}catch(Exception e){
							System.out.println("Error");
						}
					}else if(onto.equals("collision-sense-activate")){
						System.out.println("content "+cont);
						_pmsagent.ActivateCollisionSense(Integer.parseInt(cont));
						
					}else if(onto.equals("collsion-sense-deactivate")){
						_pmsagent.DeactivateCollisionSense(Integer.parseInt(cont));
					}
					block();
				}
			}
			
		}
	}
	
	
}
