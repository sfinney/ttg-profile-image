package com.example;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class HelloServlet extends HttpServlet {
	
	private static final String ACCESS_TOKEN = "2291204155-74hZw6tJATnOnGidwlLvMLMCuoI0hv1gHveRaab";
	private static final String ACCESS_TOKEN_SECRET = "4NL4979d3FQNTIzhsmsorkQ0MzaMhcUi4T9HeNTZhGFvS";
	
	private static final String CONSUMER_KEY = "hHKbEpoT86DcFwuvZ0B8JQ";
	private static final String CONSUMER_SECRET = "J2LhRTHWlVcVta7E7BFGvZNy0h6ZVcZGFf5d97R0ERI";
	
	private static final String REQUEST_PARAMETER = "twitterHandle";
	
	private String twitterHandle;	
	private String profileImageURL;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        
        out.write("Hello Heroku".getBytes());
        out.flush();
        out.close();
        
        /* setTwitterHandle(request.getParameter(REQUEST_PARAMETER));
    	
    	try {    		
    		twitterImplementation();
    		
		} catch (TwitterException error) {
			
			// TODO Auto-generated catch block
			error.printStackTrace();
		}
    	
    	ServletOutputStream output = response.getOutputStream();
        
    	output.write(getProfileImageURL().getBytes());
        
        output.flush();    	
        output.close(); */
    }
	
private void twitterImplementation() throws TwitterException {
		
		Twitter twitter = TwitterFactory.getSingleton();
		
		AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);		
		twitter.setOAuthAccessToken(accessToken);
		
		User user = twitter.showUser(twitterHandle);
		
		this.profileImageURL = user.getOriginalProfileImageURL();
	}
	
	public String getProfileImageURL() { return this.profileImageURL; }
	
	public void setTwitterHandle(String twitterHandle) { this.twitterHandle = twitterHandle; } 
    
}
