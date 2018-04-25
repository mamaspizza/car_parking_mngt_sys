package dslab.carparking;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.util.*;


/**
 * Grabs a frame from a Webcam, overlays the current date and time, and saves the frame as a PNG to c:\webcam.png
 *
 * @author David
 * @version 1.0, 16/01/2004
 */
public class FrameGrab implements Runnable
{

	private Player player;
	
    public FrameGrab()throws Exception{
    }
    public void stopWebcam(){
    	try{
    		CaptureDeviceInfo deviceInfo = CaptureDeviceManager.getDevice("vfw:Microsoft WDM Image Capture (Win32):0");
    		player = Manager.createRealizedPlayer(deviceInfo.getLocator());
        	player.stop();
        	player.close();
            player.deallocate();
            System.exit(0);
    	}catch(Exception e){
    		System.out.println("Error in grabing video "+e);
    	}
        
    }
    public void run() {
    	
    	try{
    		CaptureDeviceInfo deviceInfo = CaptureDeviceManager.getDevice("vfw:Microsoft WDM Image Capture (Win32):0");
            player = Manager.createRealizedPlayer(deviceInfo.getLocator());
            //player.stop();
         
            player.start();
           
            System.out.println("check");
            
            // Wait a few seconds for camera to initialise (otherwise img==null)
            Thread.sleep(2000);
    		GrabLoop(player, 10);
    	}catch(Exception e){
    		System.out.println("Error in grabing video "+e);
    	}
    	
    }
    public void GrabLoop(Player player, int numloop)throws Exception{
    	int i = 0;    	
    	while(true){
    		if(i==numloop){
    			i=0;
    		}
    		// Grab a frame from the capture device
    		//System.out.println("grabbing "+i);
    		Thread.sleep(500);
    		FrameGrabbingControl frameGrabber = (FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");
    		
            Buffer buf = frameGrabber.grabFrame();
            
            // Convert frame to an buffered image so it can be processed and saved
            Image img = (new BufferToImage((VideoFormat)buf.getFormat()).createImage(buf));
           
            BufferedImage buffImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g = buffImg.createGraphics();
            g.drawImage(img, null, null);
            //System.out.println("problem here");
            // Overlay curent time on image
            g.setColor(Color.RED);
            g.setFont(new Font("Verdana", Font.BOLD, 16));
            g.drawString((new Date()).toString(), 10, 25);
            
            // Save image to disk as PNG
            ImageIO.write(buffImg, "png", new File("C:\\webcampics\\"+i+".png"));            
            
            i++;
    	}
    	
    }
   
}