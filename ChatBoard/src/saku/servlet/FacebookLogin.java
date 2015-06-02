package saku.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FacebookLogin")
public class FacebookLogin extends HttpServlet {
       
    public FacebookLogin() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer callbackURL = request.getRequestURL();
		int index = callbackURL.lastIndexOf("/");
        callbackURL.replace(index, callbackURL.length(), "").append("/FacebookCallback");

//        Properties prop = new Properties();
//        prop.load(this.getClass().getClassLoader().getResourceAsStream("facebook.properties"));
//        final String appId = prop.getProperty("appId");
        // ハードコードですが一時的に値抜いてます
        final String appId = "";

        final String url =
            "https://www.facebook.com/dialog/oauth?client_id="
                + appId
                + "&redirect_uri="
                + callbackURL
                + "&scope=publish_stream";
        response.sendRedirect(url);
	}
}
