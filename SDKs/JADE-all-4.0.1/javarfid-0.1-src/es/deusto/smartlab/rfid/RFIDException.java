package es.deusto.smartlab.rfid;

/**
*An RFIDException is thrown if an exception occurs while making an operation to the reader/interrogator for a remote function call.
*@author Xabier Etxebarría Espinosa
*/
public class RFIDException extends Exception
{
    /**
    *Generates the RFIDException.
    *@param message
    *The exception message.
    */
    public RFIDException(String message)
    {
        super(message);
    }
}
