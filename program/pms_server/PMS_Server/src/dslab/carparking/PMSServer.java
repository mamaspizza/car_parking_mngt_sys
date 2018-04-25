package dslab.carparking;

import java.rmi.RemoteException;
import jade.Boot;

public class PMSServer {

	private PMSAgent agentserver;
	
	public PMSServer()throws RemoteException{
		Boot boot = new Boot();		
		//agentserver = new PMSAgent();
		String s[] = new String[2];
		s[0] = "-agents";
		s[1] = "pmsagent:dslab.carparking.PMSAgent";
		
		//rmiserver = new PMSRMIComm(agentserver);		
		//agentserver.setRMIcommunication(rmiserver);
		
		boot.main(s);
	}
	public static void main(String[] args)throws RemoteException{
		PMSServer pms = new PMSServer();
		
	}
}
