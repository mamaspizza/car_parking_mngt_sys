package dslab.carparking;

import java.rmi.Naming;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.AbstractButton;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Dimension;
//import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class RFIDServerRMI extends JPanel implements ActionListener{

	//static ReadRFIDPort rfid ;
	 private PMSServerInterface stub;
	 private JTable table;
	 private JButton button;
	 private JTextField inputmoteid;
	 private Container pane;
	 private JFrame frame;
	 private ReadRFIDPort rfid ;
	 private String _cardno =null;
	 private int _moteid =-1;
	 private boolean DEBUG = false;
	 
	public RFIDServerRMI()throws RemoteException{
		//super(new GridLayout(1,0));
		
		
		rfid = new ReadRFIDPort(this);
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
	    }catch(Exception e) {System.out.println("Lookup: " + e.getMessage());}
        
        // GUI
        
         frame = new JFrame("RFID Transaction Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane = frame.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
       
        String[] columnNames = {"Car number",
        		"Car owner",
                "Time in",
                "Time out",
                "Total amount",
                "Duration"};
       
        
     
        table = new JTable(getDataParkingTransaction(3), columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
 
        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }
      //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
 
        //Add the scroll pane to this panel.
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(scrollPane);
        
        inputmoteid = new JTextField();
        inputmoteid.setEnabled(false);
        inputmoteid.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.add(inputmoteid);
        
        // create button
        button = new JButton("Enter Mote ID");
        //button.setVerticalTextPosition(AbstractButton.CENTER);
        //button.setHorizontalTextPosition(AbstractButton.BOTTOM); //aka LEFT, for left-to-right locales
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setActionCommand("enter");
        button.addActionListener(this);
        button.setEnabled(false);
        pane.add(button);

        frame.pack();
        frame.setVisible(true);
	}
	 private void printDebugData(JTable table) {
	        int numRows = table.getRowCount();
	        int numCols = table.getColumnCount();
	        javax.swing.table.TableModel model = table.getModel();
	 
	        System.out.println("Value of data: ");
	        for (int i=0; i < numRows; i++) {
	            System.out.print("    row " + i + ":");
	            for (int j=0; j < numCols; j++) {
	                System.out.print("  " + model.getValueAt(i, j));
	            }
	            System.out.println();
	        }
	        System.out.println("--------------------------");
	    }
	
	public static void main(String[] args)throws RemoteException{		
		RFIDServerRMI rmi = new RFIDServerRMI();        
	}
	public void writeCardLog(String cardno, int moteid)throws RemoteException{		
		stub.CardLog(moteid, cardno );		
	}
	public void CardLogOut(String cardno){
		try{
			writeCardLog(cardno, -1);
		}catch(RemoteException err){
			System.out.println("Remote error "+err);      
		}
		
	}
	public void changeInput(){
		 button.setEnabled(true);
	     inputmoteid.setEnabled(true);
	}
	public Object[][] getDataParkingTransaction(int num)throws RemoteException{
		return stub.getDataParkingTransaction(num);
	}
	public void actionPerformed(ActionEvent e) {
	    if ("enter".equals(e.getActionCommand())) {
	    	_moteid = Integer.parseInt(inputmoteid.getText() );
	    	System.out.println("You entered "+_moteid);  
	    	try{
	        	if(stub.MoteVerify(_moteid)){
	        		button.setEnabled(false);
	        		inputmoteid.setText("");
	        		inputmoteid.setEnabled(false);
	    	        
	    	        writeCardLog(_cardno, _moteid);
	    	        _cardno=null;
	    			_moteid=-1;
		        }else{
		        	JOptionPane.showMessageDialog(frame, "The collision sensor ID was not valid. Please enter a valid ID.");
		        }
	        }catch(RemoteException err){
	        		System.out.println("Remote error "+err);        
	        }    	
	    } 
	}
	public void setVerification(String cardno){
		_cardno=cardno;
		_moteid=-1;
	}
}
