package dslab.carparking;

import java.util.Scanner;
import java.io.*;
import java.util.*;
import gnu.io.*; // for rxtxSerial library
 
public class ReadRFIDPort implements SerialPortEventListener {
   static CommPortIdentifier portId;
   static CommPortIdentifier saveportId;
   static Enumeration        portList;
   InputStream           inputStream;
   OutputStream           outputStream;
   SerialPort           serialPort;
   RFIDServerRMI rmiserver;
   Vector cardlog;
   long timedelay = 20000;
   public void init(){
	  
	      // initalize serial port
	      try {
	         serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
	         //serialPort.close();
	      } catch (PortInUseException e) {}
	   
	      try {
	         inputStream = serialPort.getInputStream();
	         outputStream = serialPort.getOutputStream();
	         outputStream.flush();
	      } catch (IOException e) {}
	   
	      try {
	         serialPort.addEventListener(this);
	      } catch (TooManyListenersException e) {}
	      
	      // activate the DATA_AVAILABLE notifier
	      serialPort.notifyOnDataAvailable(true);
	      
	      try {
	         // set port parameters
	         serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, 
	                     SerialPort.STOPBITS_1, 
	                     SerialPort.PARITY_NONE);
	         
	        
	      } catch (UnsupportedCommOperationException e) {}
	     
	      //String msg = "hello" ;
	     //try {
	    //	 outputStream.write(msg.getBytes());
	     //} catch (IOException e) {}
	      
	      
   }
 
   public static void main(String[] args) {
       
      
   } 
 
   public ReadRFIDPort(RFIDServerRMI rmi) {
	   
	   
	   rmiserver = rmi;	
	   cardlog = new Vector();
	   boolean           portFound = false;
	      String           rfidPort;
	      //Scanner input = new Scanner (System.in);
	      System.out.println("Please enter the port here");
	      //rfidPort = input.next();
	      rfidPort = "COM1";
	      System.out.println("Set default port to "+ rfidPort);
	      
	                // parse ports and if the default port is found, initialized the reader
	      portList = CommPortIdentifier.getPortIdentifiers();
	      while (portList.hasMoreElements()) {
	         portId = (CommPortIdentifier) portList.nextElement();
	         if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	            if (portId.getName().equals(rfidPort)) {
	               System.out.println("Found port: "+rfidPort);
	               portFound = true;
	               
	               init();
	               //ReadRFIDPort reader = new ReadRFIDPort();
	            } 
	         } 
	         
	      }
	      
	      if (!portFound) {
	         System.out.println("port " + rfidPort + "not found.");
	      } else{
	    	  
	      }
 
      
   }
 
    public void serialEvent(SerialPortEvent event) {
      switch (event.getEventType()) {
      case SerialPortEvent.BI:
      case SerialPortEvent.OE:
      case SerialPortEvent.FE:
      case SerialPortEvent.PE:
      case SerialPortEvent.CD:
      case SerialPortEvent.CTS:
      case SerialPortEvent.DSR:
      case SerialPortEvent.RI:
      case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
    	  System.out.println("empty buffer");
    	  //String msg = "hello" ;
 	    // try {
 	   // 	 outputStream.write(msg.getBytes());
 	   //  } catch (IOException e) {}
       //  break;
      case SerialPortEvent.DATA_AVAILABLE:
         // we get here if data has been received
    	
         byte[] readBuffer = new byte[20];
         byte[] cmd_getID = {0x02, 0x01,0x00,0x70,0x73,0x03};
         int i = 0;
        
         try {
            // read data
        	// System.out.println("writing");
        	 while(i<cmd_getID.length){
        		 outputStream.write(cmd_getID[i]);
        		 i++;
        	 }
        	 //System.out.println("");
             
            while (inputStream.available() > 0) {
               int numBytes = inputStream.read(readBuffer);
            }            
           String cardno = "";
            try {
              	Thread.sleep(5000L);    // one second
              }
              catch (Exception e) {}  
            while(i<readBuffer.length){
            	//System.out.print("["+readBuffer[i]+"]");
            	cardno = cardno + readBuffer[i];
            	i++;
            }
           
            Date now = new Date();
            long totaltime = 0;
            boolean found = false;
            int moteid = -1;
           i=0;
           //System.out.println("cardlog size "+cardlog.size());
           if(cardlog.size()!=0){
        	   while(i<cardlog.size()){
        		   CardTimeLog tmplog = (CardTimeLog)cardlog.elementAt(i);        		  
        		   if(tmplog.getCardNo().equals(cardno)){
        			   found = true;
        			   totaltime = now.getTime() - tmplog.getTimeLog().getTime(); 
        			   if(tmplog.isLogged()&&totaltime>timedelay){
        				   CardLogOut(cardno);            			 
        			   }else if(!tmplog.isLogged()&&totaltime>timedelay){ 
            				CardLogIn(cardno, tmplog); 	
        			   }       			   
        		   }
            	   i++;
               }
        	   if(!found){
        		   CardLogIn(cardno, null);
        	   }
           }else{
        	   CardLogIn(cardno, null);
           }  
         } catch (IOException e) {}
   
         break;
      }
   }
   private void CardLogIn(String cardno, CardTimeLog log){
	   Date now = new Date();
	   if(log!=null){
		   log.setTimeLog(now);
		   log.setLogged(true);
	   }else{
		   CardTimeLog _log = new CardTimeLog(cardno, now);		   
		   cardlog.addElement(_log);	   
	   }
	   rmiserver.changeInput();
	   rmiserver.setVerification(cardno);
	 
   }
   public void CardLogOut(String cardno){
	   int i = 0;
	   CardTimeLog tmplog = null;
	   Date now = new Date();
	   
	   while(i<cardlog.size()){
		   tmplog = (CardTimeLog)cardlog.elementAt(i);
		   if(tmplog.getCardNo().equals(cardno)){
			   i=cardlog.size();
		   }
		   i++;
	   }
	   tmplog.setTimeLog(now);
	   tmplog.setLogged(false);
	   rmiserver.CardLogOut(cardno);
	  
	   
   }
 
}
class CardTimeLog{
	private String _cardno;
	private Date _timelog;
	private boolean logged;
	
	public CardTimeLog(String cardno, Date timelog){
		_cardno = cardno;
		_timelog = timelog;
		logged=true;
	}
	public String getCardNo(){
		return _cardno;
	}
	public Date getTimeLog(){
		return _timelog;
	}
	public void setTimeLog(Date time){
		_timelog = time;
	}
	public void setLogged(boolean status){
		logged = status;
	}
	public boolean isLogged(){
		return logged;
	}
	
}