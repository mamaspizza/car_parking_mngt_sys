Instructions

I. INSTALLATIONS

1. Make sure you have jdk and eclipse installed to edit the configurations and run the program. If you do not want to use eclipse, you can use batch files configuration to run the programs but make sure that java virtual machine is installed properly and the declared path directories of libraries are correctly spacified.

2. In compiling the sensors, ZigbeXStudio is preferred. You can use its utilities to upload the compiled codes to the sensors.

3. In running the DVR, make sure that you installed the Java Media Framework correctly.

4. Install a webserver like Apache Server to your PIS server so that you can copy the "carcollision" to the htdocs directory of Apache and the PIS client should be dirrected to this URL.

5. Install the FTP server to the PIS server to transfer the images from DVR server.


II. CONFIGURATIONS

1. Make sure the path directory of libraries from each program are set correctly. You can change this in eclipse project properties in its Libraries tab and browsing the correct directory for each library. The libraries are found in the SDKs folder of the compilation.

2. The network should be configured correctly and the PMS server should be set to a fixed IP address. You  need to change the RMI server address on the name server of PMS_Server and also to its clients (PMS_WSNServer and PMS_RFIDReader).


3. Your PIS server should provide a live IP address in which it needs to accessed on the Internet by the PIS clients and DVR server.
	* Set the IP address of the XMPP connection and FTP server in the DVR server. Also, make a folder named "webcampics" in the C directory
	* Set the IP address of the XMPP connection in the PIS client.


III. RUNNING

A. Running the PMS
	1. Start the PMS server first, before starting run the rmi registry in the batch file found in the bin folder of the PMS_server project
	2. Run the WSN sensor
	3. Run the RFID reader server

B. Running the PIS
	1. Start the PIS server
	2. Run the DVR server
	3. Run the PIS client