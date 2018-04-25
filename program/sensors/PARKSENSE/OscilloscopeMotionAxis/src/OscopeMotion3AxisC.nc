/*
 ********************************************************************
 * 
 *                       Copyright (c) HANBACK ELECTRONICS 
 * 
 *  			                            All rights reserved. 
 *  
 *                              http://www.hanback.com
 * 
 *----------------------------------------------------------------------------------------------
 * Permission to use, copy, modify, and distribute this software and its
 * documentation are reserved by above authors and Hanback electronics.
 * The above copyright notice and authors must be described in this software.
 *----------------------------------------------------------------------------------------------
 * 
 * 2009.11.11 Motion 3X prototype by eacs  
 * *******************************************************************   
 */
 
//#define TOS_NODE_ID 0x14

//#ifdef DEFINED_TOS_AM_GROUP
//#undef DEFINED_TOS_AM_GROUP
//#endif

//#define DEFINED_TOS_AM_GROUP 0x60

#define AM_RADIO_MOTION3X 0x38

configuration OscopeMotion3AxisC {
}
implementation {
	components OscopeMotion3AxisM as App;
	components MainC; 
	components LedsC;
	components new AMSenderC(AM_OSCILLOSCOPE);
	components new AMReceiverC(AM_OSCILLOSCOPE);
	components ActiveMessageC;
	
	App.RadioRecv -> AMReceiverC;
	App.RadioSend -> AMSenderC;
	App.Packet -> AMSenderC;
	App.RadioCtrl -> ActiveMessageC;		
	App.Boot  -> MainC;
	App.Leds  -> LedsC;

	// Interaction Components
	components InteractionC;
	App.Interaction -> InteractionC;
	
	components PrintfC;
}
