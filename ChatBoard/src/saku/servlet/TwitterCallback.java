package saku.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import twitter4j.*;
import twitter4j.auth.*;

@WebServlet("/TwitterCallback")
public class TwitterCallback extends HttpServlet {
       
    public TwitterCallback() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
        RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
        String verifier = request.getParameter("oauth_verifier");
        try {
            twitter.getOAuthAccessToken(requestToken, verifier);
            request.getSession().removeAttribute("requestToken");
        } catch (TwitterException e) {
            throw new ServletException(e);
        }
        
		Cookie cookie = new Cookie("usr_name", "twitterlogin");
		cookie.setMaxAge(60 * 60 * 24 * 3);
		response.addCookie(cookie);

		HttpSession session = request.getSession(true);

	    session.setMaxInactiveInterval(60 * 10);
	    session.setAttribute("login","true");

        response.sendRedirect("./index.jsp");
	}
}
