package model.tutorial;

public enum Size {
	L(100), 
	M(95), 
	S(90);
	
	private int code;
	
	private Size(int code) {
		this.code=code;
	}
}
