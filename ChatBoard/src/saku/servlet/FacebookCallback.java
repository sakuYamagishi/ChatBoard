package saku.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONValue;

@WebServlet("/FacebookCallback")
public class FacebookCallback extends HttpServlet {
       
    public FacebookCallback() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String callbackURL = request.getRequestURL().toString();
        final String code = request.getParameter("code");

//        Properties prop = new Properties();
//        prop.load(this.getClass().getClassLoader().getResourceAsStream("facebook.properties"));
//        final String appId = prop.getProperty("appId");
//        final String appSecret = prop.getProperty("appSecret");
        
        // 一時的にハードコーディング
        final String appId = "";
        final String appSecret = "";

        final String accessTokenURL =
            "https://graph.facebook.com/oauth/access_token?client_id="
                + appId
                + "&redirect_uri="
                + URLEncoder.encode(callbackURL, "UTF-8")
                + "&client_secret="
                + appSecret
                + "&code="
                + URLEncoder.encode(code, "UTF-8");
        final String accessTokenResult = httpRequest(new URL(accessTokenURL));

        // 結果のパース
        String accessToken = null;
        String[] pairs = accessTokenResult.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length != 2) {
                throw new RuntimeException("Unexpected auth response");
            } else {
                if (kv[0].equals("access_token")) {
                    accessToken = kv[1];
                }
            }
        }

        // APIの実行
        final String apiURL =
            "https://graph.facebook.com/me?access_token=" + URLEncoder.encode(accessToken, "UTF-8");
        final String apiResult = httpRequest(new URL(apiURL));
        Map me = (Map) JSONValue.parse(apiResult);

        // ログイン処理
        Map map = new HashMap();
        map.put("service", "Facebook");
        map.put("userId", String.valueOf(me.get("id")));
        map.put("name", String.valueOf(me.get("name")));
        map.put("accessToken", accessToken);
        request.getSession().setAttribute("loginUser", map);

		HttpSession session = request.getSession(true);

	    session.setMaxInactiveInterval(60 * 10);
	    session.setAttribute("login","true");

        response.sendRedirect("./index.jsp");
    }

    String httpRequest(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = null;
        String response = "";
        while ((line = reader.readLine()) != null) {
            response += line;
        }
        reader.close();
        conn.disconnect();
        return response;
    }}
