package saku.util;

public class ChatResponse {
	
	public int number;
	public String name;
	public String datetime;
	public String article;
	
	public ChatResponse(int number, String name, String datetime, String article) {
		this.number = number;
		this.name = name;
		this.datetime = datetime;
		this.article = article;
	}

	public String getHeader() {
		return number + ":" + name + ":" + datetime;
	}
}
