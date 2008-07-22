
public class DTRObject {

	private String start;
	private String end;
	private String time;
	private String path;
	
	DTRObject(String start, String end, String path, String time){
		this.start = start;
		this.end = end;
		this.time = time;
		this.path = path;
	}
	
	String getStart(){
		return start;
	}
	String getEnd(){
		return end;
	}
	String getTime(){
		return time;
	}
	String getPath(){
		return path;
	}
	
}
