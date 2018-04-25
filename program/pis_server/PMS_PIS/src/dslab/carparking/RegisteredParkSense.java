package dslab.carparking;

public class RegisteredParkSense {

	private String _cardno;
	private int _moteid;
	public RegisteredParkSense(String cardno, int moteid){
		_cardno = cardno;
		_moteid = moteid;
	}
	public String getCardNo(){
		return _cardno;
	}
	public int getMoteID(){
		return _moteid;
	}
}
