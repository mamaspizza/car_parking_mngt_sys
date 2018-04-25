package dslab.carparking;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLRuleReader {

	public XMLRuleReader(){
		
	}
	public LogicalCommand ReadRules(){
		LogicalCommand lc = null;
		try{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (new File("./db/rules.xml"));
			
            // normalize text representation
            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the rule is " + 
                 doc.getDocumentElement().getNodeName());
            NodeList nodeLst = doc.getElementsByTagName("logical");
            lc = new LogicalCommand(nodeLst.getLength());
            String clocation = null;
            String tarea = null;
            String command = null;
            for (int s = 0; s < nodeLst.getLength(); s++) {
            	 Node fstNode = nodeLst.item(s);
            	    
            	    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
            	    	
            	    	org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) fstNode;
            	    	
            	    	NodeList elmntclocation = fstElmnt.getElementsByTagName("clocation");
            	    	org.w3c.dom.Element fstNmElmnt = (org.w3c.dom.Element) elmntclocation.item(0);
            	    	NodeList fstNm = fstNmElmnt.getChildNodes();
            	    	clocation = (fstNm.item(0)).getNodeValue() ;            	    	
            	    	
            	    	NodeList elmnttarea = fstElmnt.getElementsByTagName("tarea");
            	    	org.w3c.dom.Element lstNmElmnt = (org.w3c.dom.Element) elmnttarea.item(0);
            	    	NodeList lstNm = lstNmElmnt.getChildNodes();
            	    	tarea = (lstNm.item(0)).getNodeValue() ;  
            	    	
            	    	NodeList elmntcommand = fstElmnt.getElementsByTagName("command");
            	    	org.w3c.dom.Element vNmElmnt = (org.w3c.dom.Element) elmntcommand.item(0);
            	    	NodeList vNm = vNmElmnt.getChildNodes();
            	    	command = (vNm.item(0)).getNodeValue() ;  
            	    
            	    }
            	    lc.AddLogicalCommand(s, clocation, tarea, command);
            }

			lc.Print();
		  }catch (SAXParseException err) {
		        System.out.println ("** Parsing error" + ", line " 
		             + err.getLineNumber () + ", uri " + err.getSystemId ());
		        System.out.println(" " + err.getMessage ());

		  }catch (SAXException e) {
		        Exception x = e.getException ();
		        ((x == null) ? e : x).printStackTrace ();

		  }catch (Throwable t) {
		        t.printStackTrace ();
		  }
		return lc;
	}
}
