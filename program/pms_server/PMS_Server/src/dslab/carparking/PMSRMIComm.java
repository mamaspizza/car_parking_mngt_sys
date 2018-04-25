package dslab.carparking;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.io.*;
import java.text.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.MalformedURLException;

public class PMSRMIComm extends UnicastRemoteObject implements PMSServerInterface{

	private Vector ParkingAreas;
	private Vector CarParked;
	private DetectorSensor ds;
	private String host = "localhost";
	private String database = "pms_database";
	private String username = "user1";
	private String password = "lemontruck228";
	private Connection m_con;
	private int numrows = 100;
	private int wonperms = 1;
	private PMSAgent pmsagent;
	
	public PMSRMIComm(PMSAgent agentmanager)throws RemoteException{
		pmsagent =agentmanager;
		init();
	}
	
	/***** decide which area is the car in based on RSS ********/
	public void RSSIReading(int moteID, String Area, long RSS)throws RemoteException{
		boolean verify = false;
		int i =0;
		long tmpRSSI = 0;
		ParkingArea area;
		
		if(ds.verifyAssociation(moteID)){
			verify=true;
			tmpRSSI=ds.getRSSIParkingArea(moteID);
			if(RSS>tmpRSSI){
				
				ds.setParkingArea(moteID, Area, RSS);
				System.out.println("Car is in "+ds.getParkingArea(moteID) +" with mote id=" +moteID+" associated to "+ds.findCardNo(moteID));
				i = 0;
				while(i<ParkingAreas.size()){					
					area = (ParkingArea)ParkingAreas.elementAt(i);
					if(area.equals(Area)){
						if(!area.voidFindMoteID(moteID)){
							area.parkedMoteID(moteID);							
						}
					}else{
						if(area.voidFindMoteID(moteID)){
							area.unparkedMoteID(moteID);
						}
					}					
					i++;
				}
				
			}
		}		
	}
	
	/***** PIS agent request for the location of the car ********/
	public String getCurrentLocation(String cardno){
		String currentlocation = null;
		int moteid = ds.getMoteID(cardno);
		currentlocation = ds.getParkingArea(moteid);
		
		return currentlocation;
	}
			
	/***** PIS agent request for guide ********/
	public String getRecommendedParkingArea(){
		String recommendedarea = null;
		int i = 0;
		int pavailableslot = 0;
		int cavailableslot = 0;
		int areaindex = -1;
		
		while(i<ParkingAreas.size()){
			if(i==0){
				areaindex=0;
			}else{
				cavailableslot = ((ParkingArea)ParkingAreas.elementAt(i)).getNumSlot()-((ParkingArea)ParkingAreas.elementAt(i)).getCurrentParkedCars();
				if(pavailableslot>cavailableslot){
					areaindex=i;
				}
			}
			
			i++;
		}
		recommendedarea = ((ParkingArea)ParkingAreas.elementAt(areaindex)).getArea();
		return recommendedarea;
	}
	
	/***** collision detection ********/
	public void CollisionDetect(int moteid, String Area)throws RemoteException{

		/***** find the card on the associated mote id ********/
		String cardno = null;
		cardno = ds.findCardNo(moteid);
			
		if(!cardno.equals(null)){
			System.out.println("Checking collision method "+moteid+" "+cardno);
			if(ds.getStatusCollisionSense(moteid)){
				System.out.println("Collision in card "+cardno +" with mote id=" +moteid+".");			
				
				/***** send to the pisagent through pmsagent ********/		
				pmsagent.detectCollision(cardno, Area);
			}			
		}		
	}	
	public void ActivateCollisionSense(int moteid){
		System.out.println("Searching to activate... "+moteid);
		ds.ActivateCollisionSense(moteid);
	}
	public void DeactivateCollisionSense(int moteid){
		ds.DeactivateCollisionSense(moteid);
	}
	// recommended parking area
	
	// Card ID log
	public void CardLog(int moteID, String cardno)throws RemoteException{
		int i = 0;
		int tmpindex = -1;
		
		boolean card = false;
		String tmpcard;
		CardLog cardlog = null;
		System.out.println("check"+cardno);
		while(i<CarParked.size()){
			cardlog = (CardLog) CarParked.elementAt(i);
			tmpcard = cardlog.getCardNo();
			if(cardno.equals(tmpcard)){
				tmpindex = i;
				card = true;
				i = CarParked.size();
			}
			i++;
		}
		
		if(!card){		
			CardLog addcardlog = new CardLog(moteID, cardno, new Date());
			CarParked.addElement(addcardlog);
			ds.setAssociation(moteID, cardno);
			System.out.println("Card no "+ cardno +" has entered");
		}else{
			updateCarLog(cardlog.getCardNo(), cardlog.getTimeIn());
			CarParked.removeElementAt(tmpindex);
			ds.unsetAssociation(moteID);
			System.out.println("Card no "+ cardno +" is leaving");
		}
	}
	
	public boolean MoteVerify(int moteID)throws RemoteException{
		boolean verify = false;
		verify=ds.verifyMoteID(moteID);
		return verify;
	}
	
	public String[][] getDataCarOwnerTransaction(int carid)throws RemoteException{
		String[][] cor = null;
		try{
			Statement stmt;
			 stmt = m_con.createStatement(
		               ResultSet.TYPE_SCROLL_INSENSITIVE,
		                     ResultSet.CONCUR_READ_ONLY);
		      ResultSet rs1, rs2;
		      rs1 = stmt.executeQuery("SELECT * " +
	          "from parkingevents WHERE idCar ='" + carid + "'");
		      rs2 = stmt.executeQuery("SELECT car owner, car type " +
			          "from parkingevents WHERE idCar='" + carid + "'");
		      String carowner = rs2.getString("car owner");
		      String cartype = rs2.getString("car type");
		      //cor = new CarOwnerTransactions(carid, carowner, cartype);
		      int i =0;
		     
		      String timein = rs1.getDate("time in").toString();
		      String timeout = rs1.getDate("time out").toString();
		      long total = 0;
		      String logout = "";
		      //while(rs1.next()){
		    	//  cor.addData(rs1.getInt("idParkingEvent"),  
		    	//		  timein, timeout, rs1.getLong("charge"));
		      //}
		      
		}catch(Exception e){
			
		}
		return cor;
	}
	public String[][] getDataParkingTransaction(int num)throws RemoteException{
		String[][] pkt = new String[num][6];
		try{
			Statement stmt1, stmt2;
			 stmt1 = m_con.createStatement(
		               ResultSet.TYPE_SCROLL_INSENSITIVE,
		                     ResultSet.CONCUR_READ_ONLY);
		      ResultSet rs1, rs2;
		      
		      rs1 = stmt1.executeQuery("SELECT * " +
	          "FROM parkingevents ORDER BY timeout DESC");
		      String cardno = null;
		      int i =0;
		      String carowner = "";
		      String timein = "";
		      String timeout = "";
		      long total = 0;
		      String logout = "";
		      while(rs1.next()|| i>=num){
		    	  cardno = rs1.getString("cardno");
		    	  pkt[i][0] = String.valueOf(cardno);
		    	  
		    	  stmt2 = m_con.createStatement(
			               ResultSet.TYPE_SCROLL_INSENSITIVE,
			                     ResultSet.CONCUR_READ_ONLY);
			     rs2 = stmt2.executeQuery("SELECT carowner " +
		          "FROM carinformation WHERE cardno =" + cardno );
			     rs2.next(); 
			     pkt[i][1]= rs2.getString("carowner");
			     pkt[i][2] = rs1.getString("timein");
			     pkt[i][3] = rs1.getString("timeout");
			     pkt[i][4] = String.valueOf(rs1.getInt("charge"));
			     pkt[i][5] = String.valueOf(rs1.getInt("duration"));
			      
		    	  i++;
		      }
		      
		      
		}catch(Exception e){
			System.out.println("problem "+e.getMessage());
		}
		
		
		return pkt;
		
	}
	//car log in
	public void updateCarLog(String cardno, Date timein){
		
		try{
			Statement stmt1, stmt2;
			 stmt1 = m_con.createStatement(
		               ResultSet.TYPE_SCROLL_INSENSITIVE,
		                     ResultSet.CONCUR_READ_ONLY);
		      ResultSet rs1, rs2;
		      
		      rs1 = stmt1.executeQuery("SELECT * " +
	          "FROM carinformation WHERE cardno ='" + cardno+"'");
		      //String cardno = null;
		      int i=0;
		      int moteID = -1;
		      Date timeout = new Date();
		      long totaltime = timeout.getTime() - timein.getTime();
		      long price = wonperms * totaltime;
		      String plateno = "";
		      while(rs1.next()){
		    	  cardno = rs1.getString("cardno");
		    	  plateno = rs1.getString("platenumber");		      
		      }
		      stmt2 = m_con.createStatement();
		      SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		      while(i<CarParked.size()){
		    	  String tmpcardno = ((CardLog)CarParked.elementAt(i)).getCardNo();
		    	  if(cardno.equals(tmpcardno)  ){
		    		  moteID = ((CardLog)CarParked.elementAt(i)).getMoteID();
		    		  i=CarParked.size();
		    	  }
		    	  i++;
		      }
		      
		      String ftimein = sdf.format(timein);
		      String ftimeout = sdf.format(timeout);
		      int res = stmt2.executeUpdate("INSERT INTO parkingevents (cardno, plateno, timein, timeout, duration, charge, moteid) values ("+cardno+", '"+ plateno +"', '"+ ftimein+"', '"+  ftimeout + "', " + totaltime +", "+ price+", "+ moteID+ ")");
		    
		      
		      
		}catch(Exception e){
			System.out.println("problem "+e.getMessage());
		}
		
	}
	
	// Location estimation
	public void updateLocationEstimation(String cardno, int[] rss){
		
	}
	
	// Parking sensor update
	public void updateCarCollisionDetection(String cardno){
		
	}
	// initialize 
	
	public void init() {
	
		/**** Start the RMI server for the PMS ****/
		//System.setProperty("java.security.policy", "policy");
		 System.setSecurityManager (new RMISecurityManager() {
     	    public void checkConnect (String host, int port) {}
     	    public void checkConnect (String host, int port, Object context) {}
     	  });
		try {
			Naming.rebind("rmi://203.234.48.104:2001/Server", this);
			System.out.println("Car Interface Ready!");
			
		}
		catch(RemoteException ex) {
			System.out.println("Error 12" + ex.getMessage());
		}
		catch(MalformedURLException ex){
			System.out.println("Error 32" + ex.getMessage());
		}
		
		/**** Test the database  and connect ****/
		testconnect ();
		createconnection ( );
		
		/**** Default number for parking area and number of slot in an area ****/		
		setParkingArea();
		/**** Default associating motion sensor ****/
		setCollisionSensorAssoc();
		CarParked = new Vector();
		
		/*****************************************************/
		try{
			CardLog(101, "-11110429999999934");
			RSSIReading(101, "1", 1111);
			RSSIReading(101, "2", 111);
		}catch(RemoteException ex) {
			System.out.println("Error 12" + ex.getMessage());
		}
		/*****************************************************/
	}
	
	// database connection
	private void createconnection ( ) {
		String url = "";
		try {
		  url = "jdbc:mysql://" + host + "/" + database;
		  m_con = DriverManager.getConnection(url, username, password);

		} catch (Exception fe ) {
			System.out.println("Connection couldn't be established to " + url);
		}
  	}
  	private void testconnect () {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (Exception fe) {
			fe.printStackTrace();
			System.out.println("MySQL JDBC Driver not found...");
		}
	}
	public void connected(){
		System.out.println("connected");
	}
	public void setParkingArea(){
		ParkingAreas = new Vector();
		
		try{
			Statement stmt1;
			 stmt1 = m_con.createStatement(
		               ResultSet.TYPE_SCROLL_INSENSITIVE,
		                     ResultSet.CONCUR_READ_ONLY);
		      ResultSet rs1;
		      
		      rs1 = stmt1.executeQuery("SELECT * " +
	          "FROM parkingarea;");
		      int areaid = -1;
		      String location = null;
		      String hint = null;
		      String area = null;
		      int numslot = -1;
		      ParkingArea parkarea;
		      
		      while(rs1.next()){
		    	  parkarea = new ParkingArea(rs1.getInt("idParkingArea"), rs1.getString("Mainlocation"), rs1.getString("LocationHint"), 
		    			  rs1.getString("Area"), rs1.getInt("NumberofSlots"));
		    	  ParkingAreas.addElement(parkarea);	  
		    	 	      
		      }     
		      
		}catch(Exception e){
			System.out.println("problem "+e.getMessage());
		}
	}
	public void setCollisionSensorAssoc(){
		ds = new DetectorSensor(10);
		
		try{
			Statement stmt1;
			 stmt1 = m_con.createStatement(
		               ResultSet.TYPE_SCROLL_INSENSITIVE,
		                     ResultSet.CONCUR_READ_ONLY);
		      ResultSet rs1;
		      
		      rs1 = stmt1.executeQuery("SELECT * " +
	          "FROM collisionsensor;");
		      int i =0;
		      
		      
		      while(rs1.next()){
		    	  ds.registerCollisionSensor(i, rs1.getInt("moteid"));	  
		    	 i++;
		      }     
		      
		}catch(Exception e){
			System.out.println("problem "+e.getMessage());
		}
	}
}

class ParkingArea{
    private int _areaid = -1;
    private String _location = null;
    private String _hint = null;
    private String _area = null;
    private int _numslot = -1;
    private Vector parkedmote;
   
	public ParkingArea(int areaID, String location, String hint, String area, int numslot){
		_areaid = areaID;
		_location = location;
		_hint = hint;
		_area = area;
		_numslot = numslot;
		parkedmote = new Vector();
	}
	public void parkedMoteID(int moteID){
		parkedmote.addElement(new Integer(moteID));
	}
	public void unparkedMoteID(int moteID){
		int i = 0;
		int tmpmoteid = -1;
		while(i<parkedmote.size()){
			tmpmoteid = ((Integer)parkedmote.elementAt(i));
			if(tmpmoteid==moteID){
				parkedmote.removeElementAt(i);
				i=parkedmote.size();
			}
			i++;
		}		
	}
	public int getAreaID(){
		return _areaid;
	}
	public String getLocation(){
		return _location;
	}
	public String getHint(){
		return _hint;
	}
	public String getArea(){
		return _area;
	}
	public int getNumSlot(){
		return _numslot;
	}
	public int getCurrentParkedCars(){
		return parkedmote.size();
	}
	
	public boolean voidFindMoteID(int moteID){
		boolean found = false;
		int i = 0;
		int tmpmoteid = -1;
		while(i<parkedmote.size()){
			tmpmoteid = ((Integer)parkedmote.elementAt(i));
			if(tmpmoteid==moteID){
				found=true;
				i=parkedmote.size();
			}
			i++;
		}
		return found;
	}	
}
class CardLog{
	private String _cardno;
	private int _areaID;
	private int _moteID;
	private Date _timein;
	private boolean _entered;
	private boolean _active;
	public CardLog(int moteID, String cardno, Date timein){

		_moteID = moteID;
		_cardno = cardno;
		_timein = timein;
		
	}
	
	public int getMoteID(){
		return _moteID;
	}
	public String getCardNo(){
		return _cardno;
	}
	public Date getTimeIn(){
		return _timein;
	}
	public void Parked(boolean status){
		_entered = status;
	}
	public void CollisionActivate(boolean status){
		_active = status;
	}
	
}
class DetectorSensor{
	
	private int[] _moteID;
	private String[] _cardno;
	private String[] _ParkingArea;
	private long[] _RSSIArea;
	private boolean[] _activate;
	
	public DetectorSensor(int num){
		_moteID = new int[num];
		_cardno = new String[num];
		_ParkingArea = new String[num];
		_RSSIArea = new long[num];
		_activate = new boolean[num];
		
		int i =0;
		while(i<num){
			_moteID[i]=-1;
			_cardno[i] = null;
			_ParkingArea[i]=null;
			_RSSIArea[i]=0;
			_activate[i] = false;
			i++;
		}	
	}
	public void setAssociation(int moteID, String cardno ){
		int i = 0;
		while(i<_moteID.length){
			if(moteID==_moteID[i]){
				_cardno[i] = cardno;
			}
			i++;
		}
				
	}
	public void unsetAssociation(int moteID ){
		int i = 0;
		while(i<_moteID.length){
			if(moteID==_moteID[i]){
				_cardno[i] = null;
			}
			i++;
		}
				
	}
	public void registerCollisionSensor(int index, int moteID){
		_moteID[index]=moteID;
	}
	public String getCardNo(int index){
		return _cardno[index];
	}
	public String findCardNo(int moteid){
		int index = -1;
		int i = 0;
		while(i<_moteID.length){
			if(_moteID[i]==moteid){
				index = i;
				i = _moteID.length;
			}
			i++;
		}
		
		return _cardno[index];
	}
	public int getMoteID(String cardno){
		int i = 0;
		int index = -1;
		while(i<_cardno.length){
			if(cardno.equals(_cardno[i])){
				index = i;
				i = _cardno.length;
			}
			i++;
		}
		return _moteID[index];
	}
	public boolean verifyAssociation (int moteid){
		boolean verify = false;
		int i = 0;
		while(i<_moteID.length){
			if(moteid==_moteID[i]){
				if(_cardno[i]!=null){
					verify = true;
				}else{
					verify = false;
				}
				i=_moteID.length;
			}
			i++;
		}
		return verify;
	}
	public boolean verifyMoteID (int moteid){
		boolean verify = false;
		int i = 0;
		while(i<_moteID.length){
			if(moteid==_moteID[i]){
				verify=true;				
				i=_moteID.length;
			}
			i++;
		}
		return verify;
	}
	public String getParkingArea(int moteid){
		int index = -1;
		int i = 0;
		while(i<_moteID.length){
			if(_moteID[i]==moteid){
				index=i;
				i=_moteID.length;
			}
			i++;
		}
		return _ParkingArea[index];
	}
	public long getRSSIParkingArea(int moteid){
		int index = -1;
		int i = 0;
		while(i<_moteID.length){
			if(_moteID[i]==moteid){
				index=i;
				i=_moteID.length;
			}
			i++;
		}
		return _RSSIArea[index];
	}
	public void setParkingArea(int moteid, String Area, long RSSI){
		int i = 0;
		while(i<_moteID.length){
			if(_moteID[i]==moteid){
				_ParkingArea[i]=Area;
				_RSSIArea[i]=RSSI;
				i=_moteID.length;
			}
			i++;
		}
	}
	public void unsetParkingArea(int moteid){
		int i = 0;
		while(i<_moteID.length){
			if(_moteID[i]==moteid){
				_ParkingArea[i]=null;
				_RSSIArea[i]=0;
				i=_moteID.length;
			}
			i++;
		}
	}
	public void ActivateCollisionSense(int moteid){
		int i = 0;
		while(i<_moteID.length){
			System.out.println("Searching... "+_moteID[i]);
			if(_moteID[i]==moteid){
				System.out.println("Activated "+_moteID[i]);
				_activate[i]=true;
				i=_moteID.length;
			}
			i++;
		}
		
	}
	public void DeactivateCollisionSense(int moteid){
		int i = 0;
		while(i<_moteID.length){
			if(_moteID[i]==moteid){
				System.out.println("Deactivated "+_moteID[i]);
				_activate[i]=false;
				i=_moteID.length;
			}
			i++;
		}		
	}
	public boolean getStatusCollisionSense(int moteid){
		int i = 0;
		int index = -1;
		while(i<_moteID.length){
			if(_moteID[i]==moteid){
				index=i;
				i=_moteID.length;
			}
			i++;
		}	
		return _activate[index];
	}
}