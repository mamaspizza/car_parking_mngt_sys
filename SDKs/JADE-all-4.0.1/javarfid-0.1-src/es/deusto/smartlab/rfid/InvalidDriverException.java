package es.deusto.smartlab.rfid;

/**
*An InvalidDriverClassException is thrown if an exception occurs while loading a RFID kit.
*@author Xabier Etxebarría Espinosa
*/
public class InvalidDriverException extends Exception
{
    /**
    *Generates the InvalidDriverClassException.
    *@param message
    *The exception message.
    */    
    public InvalidDriverException(String message)
    {
        super(message);
    }
}
