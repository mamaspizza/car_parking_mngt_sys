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

#include "printf.h"
#include "Opt_App.h"
#include "Timer.h"
#include <stdio.h>
#include "oscilloscoperssi.h"

#define DEBUG_MODE

#define MOTION3X_DATA_LEN 3
#define MOTION3X_DATA_SIZE (MOTION3X_DATA_LEN * 2)
#define  MOTION3X_DATA_SAMPLING_TIME 500 //1sec

module OscopeMotion3AxisM{
	uses interface Boot;
	uses interface Leds;
    uses interface Interaction;

	uses{
		interface Receive as RadioRecv;
		interface AMSend as RadioSend;
		interface SplitControl as RadioCtrl;
		
		interface Packet;
	}
}
implementation{
	void checkReadings();
	void calc_EUC();
	
	norace App_struct_t AP_Frame;
	message_t packet;	
	uint16_t currentdata[1+MOTION3X_DATA_LEN]; //header append
	uint16_t prevdata[1+MOTION3X_DATA_LEN]; //header append
	  /* Current local state - interval, version and accumulated readings */
  oscilloscope_t local;

  uint8_t reading; /* 0 to NREADINGS */
  
	bool locked;
    bool suppress_count_change;

  // Use LEDs to report various status issues.
  void report_problem() { call Leds.led0Toggle(); }
  void report_sent() { call Leds.led1Toggle(); }
  void report_received() { call Leds.led2Toggle(); }
      
	event void Boot.booted() {
		local.interval = 1000;
		local.id = TOS_NODE_ID;
		local.count = 0;
		call Interaction.StartConfiguration (&AP_Frame);
	}

	event void Interaction.StartDoneConfiguration(uint8_t appType, uint8_t optType) {
#ifdef DEBUG_MODE
		printf("\nWellcom Motion 3X");
		printfflush();
#endif	

		if (optType != OPT_MOTION_3X){
#ifdef DEBUG_MODE
			printf("\nNot connect Motion 3X Module");
			printfflush();	
#endif
			return;		
		}
  	
    	/*
    	 * |<------       2byte    ------> |
    	 * |   1byte(H)   |   1byte(L)   |
    	 * |--------------+--------------|
    	 * |   optType    |          len        | 
    	 * 
    	 */
    						
    	   	currentdata[0] = (optType << 8) | MOTION3X_DATA_SIZE;
    	prevdata[0] = (optType << 8) | MOTION3X_DATA_SIZE; 
    	prevdata[1] = 0;
    	prevdata[2] = 0;
    	prevdata[3] = 0;
		
		call RadioCtrl.start();   		
		call Interaction.SetSamplingTime(MOTION3X_DATA_SAMPLING_TIME); 
	}

	event void RadioCtrl.startDone(error_t error){
    	if(error==SUCCESS){
#ifdef DEBUG_MODE
			printf("\nRadio Started");
			printfflush();
#endif	
    	}
    	else{
#ifdef DEBUG_MODE
			printf("\nRadio not start");
			printfflush();
#endif	
    	}
	}

	event void RadioCtrl.stopDone(error_t error){
		//do not
	}

	event void Interaction.Urgency_Data (uint8_t *Urgency_Payload, uint8_t len) {
		//do not
	}

	//ADC_data[0]: Motion X, ADC_data[1]: Motion Y, ADC_data[2]: Motion Z

	event void Interaction.getSensorDataDone(App_struct_t *App_Payload, uint8_t App_size){
		uint16_t *pData = &AP_Frame.AppData.sensor.Sdata.ADC_data[0];
		
		memcpy(currentdata+1, pData, MOTION3X_DATA_SIZE); //data + 0 is header

#ifdef DEBUG_MODE
		printf("\n[ModeID: 0X%02X, GroupID: 0X%02X]", TOS_NODE_ID, DEFINED_TOS_AM_GROUP);    	
    	printf(" X: %d, Y: %d, Z: %d", currentdata[1], currentdata[2], currentdata[3]);
		printfflush();
#endif

		//memcpy(call Packet.getPayload(&packet, 0), currentdata, sizeof(currentdata));
		
		//if(call RadioSend.send(AM_BROADCAST_ADDR, &packet, sizeof(data))){
		//	locked = TRUE;
		//}
		calc_EUC();					
    	checkReadings();		
    	report_received();
		
		
	}
	
	void checkReadings(){
      
	if (reading == NREADINGS)
	{
		if (!locked && sizeof local <= call RadioSend.maxPayloadLength())
		{
			memcpy(call RadioSend.getPayload(&packet,sizeof(local)), &local, sizeof local);
			if (call RadioSend.send(AM_BROADCAST_ADDR, &packet, sizeof local) == SUCCESS)
				locked = TRUE;
		}
		if (!locked)
			report_problem();

		reading = 0;
		/* Part 2 of cheap "time sync": increment our count if we didn't jump ahead. */
		if (!suppress_count_change)
			local.count++;
		suppress_count_change = FALSE;
	}	
  }
	
	
	event void RadioSend.sendDone(message_t* msg, error_t error) {
    	if(&packet == msg){
    			locked = FALSE;
    	}
    	 
    if (error == SUCCESS)
      report_sent();
    else
      report_problem();

  }
event message_t* RadioRecv.receive(message_t* msg, void* payload, uint8_t len) {

	oscilloscope_t *omsg = payload;
	report_received();

	if (omsg->version > local.version)
	{
		local.version = omsg->version;
		local.interval = omsg->interval;
		//call Timer.startPeriodic(local.interval);
		reading = 0;
	}

	if (omsg->count > local.count)
	{
		local.count = omsg->count;
		suppress_count_change = TRUE;
	}

	return msg;
  }
	
	void calc_EUC(){
			uint16_t data1 = abs(prevdata[1]-currentdata[1]);
			uint16_t data2 =abs(prevdata[2]-currentdata[2]);
			uint16_t data3 = abs(prevdata[3]-currentdata[3]);
			uint16_t total=data1+data2+data3;
			//uint16_t data1 = currentdata[1];
			//uint16_t data2 =currentdata[2];
			//uint16_t data3 =currentdata[3];
			//uint16_t total=data1*data2*data3;
			local.readings[reading++] = total;
			prevdata[1] = currentdata[1];
			prevdata[2] = currentdata[2];
			prevdata[3] = currentdata[3];
			
		}
}