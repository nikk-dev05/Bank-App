package in.sp.main.exception;

public class Myexception extends RuntimeException {
	public static  final long  serialVersionUID=1L;
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Myexception(String msg) {
	
		this.msg = msg;
	}
	

}
