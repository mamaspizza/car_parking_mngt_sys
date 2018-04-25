package dslab.carparking;

public class LogicalCommand {
	
	public String[] _clocation;
	public String[] _tarea;
	public String[] _cmd;
	
	public LogicalCommand(int numrules){
		_clocation = new String[numrules];
		_tarea = new String[numrules];
		_cmd = new String[numrules];
	}
	public void AddLogicalCommand(int index, String clocation, String tarea, String cmd){
		_clocation[index] = clocation;
		_tarea[index] = tarea;
		_cmd[index] = cmd;
	}
	public String GetCommand(String clocation, String tarea){
		String command = null;
		int i = 0;
		boolean found = false;
		while(i<_cmd.length){
		
			if(_clocation[i].equals(clocation)){
				if(_tarea[i].equals(tarea)){
					found=true;
					command = _cmd[i];
					i=_cmd.length;
				}				
			}
			i++;			
		}
		return command;
	}
	public void Print(){
		int i = 0;
		while(i<_cmd.length){
			System.out.println("logical rule" +i+" current location="+ _clocation[i]+" target area="+_tarea[i]+" command="+_cmd[i]);
			i++;
		}
	}

}
