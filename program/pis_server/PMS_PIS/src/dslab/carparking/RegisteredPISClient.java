package dslab.carparking;

public class RegisteredPISClient {

	private String _cardno;
	private int _moteid;
	private String _recommendedarea;
	
	public RegisteredPISClient(String cardno){
		_cardno = cardno;
		_moteid = -1;
		_recommendedarea = null;
	}
	public String getCardNo(){
		return _cardno;
	}

	public int getMoteID(){
		return _moteid;
	}
	public void registerParkSENSE(int moteid){
		_moteid = moteid;
	}
	public void unregisterParkSENSE(){
		_moteid = -1;
	}
	public void setRecommendedArea(String area){
		_recommendedarea=area;
	}
	public String getRecommendedArea(){
		return _recommendedarea;
	}
}
