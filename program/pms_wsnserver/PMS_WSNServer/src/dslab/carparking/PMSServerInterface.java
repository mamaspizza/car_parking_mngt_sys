package dslab.carparking;

import java.rmi.RemoteException;;

public interface PMSServerInterface extends java.rmi.Remote {

	
	public void CardLog(int moteID, String cardno)throws RemoteException;
	public void CollisionDetect(int moteID, String Area)throws RemoteException;
	public void RSSIReading(int moteID, String Area, long RSS)throws RemoteException;
	
	
	public String[][] getDataParkingTransaction(int num)throws RemoteException;
	public String[][]  getDataCarOwnerTransaction(int carid)throws RemoteException;

}
