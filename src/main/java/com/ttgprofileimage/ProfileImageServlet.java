package com.ttgprofileimage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/* import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder; */

public class ProfileImageServlet extends HttpServlet {
	
	private static final String ACCESS_TOKEN = "2291204155-74hZw6tJATnOnGidwlLvMLMCuoI0hv1gHveRaab";
	private static final String ACCESS_TOKEN_SECRET = "4NL4979d3FQNTIzhsmsorkQ0MzaMhcUi4T9HeNTZhGFvS";
	
	private static final String CONSUMER_KEY = "hHKbEpoT86DcFwuvZ0B8JQ";
	private static final String CONSUMER_SECRET = "J2LhRTHWlVcVta7E7BFGvZNy0h6ZVcZGFf5d97R0ERI";
	
	private static final String REQUEST_PARAMETER = "handle";
	
	private String handle;	
	private String profileImageURL;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		this.handle = request.getParameter(REQUEST_PARAMETER);
		
		if (handle != null) {
		
			if (handle.contains("linkedin")) {
			
				try {
					getLinkedInUserDetails();
					
				} catch (ParserConfigurationException error) {
					
					// TODO Autogenerated catch block
					error.printStackTrace();
					
				} catch (SAXException error) {
					
					// TODO Autogenerated catch block
					error.printStackTrace();
				}
			} else {
			
		        try {
		        	getTwitterUserDetails();
		        	
		        } catch (TwitterException error) {
		        	
		        	// TODO Autogenerated catch block
		        	error.printStackTrace();
		        }
			}
	        
	        ServletOutputStream output = response.getOutputStream();
	        
	        output.write(profileImageURL.getBytes());
	        
	        /* URL url = new URL(profileImageURL);        
	        URLConnection urlConnection = url.openConnection();
	        
	        BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());        
	        
	        byte[] byteBuffer = new byte[1024];
	        int inputStreamLength;
	        
	        while ((inputStreamLength = bufferedInputStream.read(byteBuffer)) > 0) {
	        
	        	output.write(byteBuffer, 0, inputStreamLength);
	        } */
	        
	        output.flush();
	        output.close();
		}
    }
	
	private void getLinkedInUserDetails() throws  IOException, ParserConfigurationException, SAXException {		
		
		String apiKey = "7728b7mz21og75";
		String secretKey = "lsq6b6rSMQTX9ReG";
		
		String oAuthUserToken = "aaf71121-02b8-41b6-b049-d232e2222dd7";
		String oAuthUserSecret = "ab2bc414-5054-4f7d-a419-d1a1e82f0ff6";
		
		// Scribe implementation
		String url = "http://api.linkedin.com/v1/people/url=" + URLEncoder.encode(handle, "UTF8") + "/picture-urls::(original)";		
				
		OAuthService oAuthService = new ServiceBuilder().provider(LinkedInApi.class)
														.apiKey(apiKey)
														.apiSecret(secretKey)
														.build();
		
		OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, url);
		oAuthService.signRequest(new Token(oAuthUserToken, oAuthUserSecret), oAuthRequest);
		
		Response response = oAuthRequest.send();		
		String xml = response.getBody();
		
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance()
																.newDocumentBuilder();
		
		Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
		
		this.profileImageURL = document.getFirstChild()
									   .getTextContent();
	}
	
	private void getTwitterUserDetails() {
		
		// Scribe implementation		
		String url = "https://api.twitter.com/1.1/users/lookup.json?screen_name=" + handle;
		
		OAuthService oAuthService = new ServiceBuilder().provider(TwitterApi.class)
				.apiKey(CONSUMER_KEY)
				.apiSecret(CONSUMER_SECRET)
				.build();
		
		OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, url);
		oAuthService.signRequest(new Token(ACCESS_TOKEN, ACCESS_TOKEN_SECRET), oAuthRequest);
		
		Response response = oAuthRequest.send();
		
		this.profileImageURL = response.getBody();
		
		/*String xml = response.getBody();
		
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance()
																.newDocumentBuilder();
		
		Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
		
		this.profileImageURL = document.getFirstChild()
									   .getTextContent(); */
		
		// Twitter4J implementation
		
		/* ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
		
		Configuration configuration = configurationBuilder.build();
		
		TwitterFactory factory = new TwitterFactory(configuration);
		Twitter twitter = factory.getInstance();
		
		AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
		
		twitter.setOAuthAccessToken(accessToken);
		
		User user = twitter.showUser(handle);		
		this.profileImageURL = user.getOriginalProfileImageURL(); */		
	}
}