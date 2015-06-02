package saku.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckString {

//	public static void main(String[] args) {
//		CheckString cs = new CheckString();
//		System.out.println(cs.isThreadID("th99999"));
//		System.out.println(cs.isResponseNumber("001"));
//	}
	
	
	public String isThreadID(String id) {
		String match = "";
		if (id != null) {
			Pattern pattern = Pattern.compile("^th\\d{6}$");
			Matcher matcher = pattern.matcher(id);
			while (matcher.find()) {
				match = matcher.group();
			}
		}
		if (match.length() == 0) {
			return "th000001";
		}
		return match;
	}
	
	public int isResponseNumber(String res) {
		String match = "";
		if (res != null) {
			Pattern pattern = Pattern.compile("^\\d{3}$");
			Matcher matcher = pattern.matcher(res);
			while (matcher.find()) {
				match = matcher.group();
			}
		}
		if (match.length() == 0) {
			int ans = Integer.parseInt(res);
			return ans <= 0 ? ans * -1 : ans;
		}
		return 1;
	}
}
