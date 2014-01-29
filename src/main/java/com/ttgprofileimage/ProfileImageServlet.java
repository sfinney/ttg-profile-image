package com.ttgprofileimage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
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

// Twitter4J implementation

/* import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder; */

public class ProfileImageServlet extends HttpServlet {

	private static final String REQUEST_PARAMETER = "handle";
	
	private String consumerKey;
	private String consumerSecret;
	
	private String accessToken;
	private String accessTokenSecret;	
	
	private String handle;	
	private String profileImageURL;
	private String url;
	
	private OAuthService oAuthService;
	private OAuthRequest oAuthRequest;	
	
	private Response response;	

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		this.handle = request.getParameter(REQUEST_PARAMETER);
		
		if (handle != null) {
			
			Properties properties = new Properties();
			properties.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
			
			if (handle.contains("linkedin")) {
			
				this.consumerKey = properties.getProperty("linkedinconsumerkey");
				this.consumerSecret = properties.getProperty("linkedinconsumersecret");
				
				this.accessToken = properties.getProperty("linkedinaccesstoken");
				this.accessTokenSecret = properties.getProperty("linkedinaccesstokensecret");
				
				this.url = properties.getProperty("linkedinurl");
				
				/* this.consumerKey = "7728b7mz21og75";
				this.consumerSecret = "lsq6b6rSMQTX9ReG";
				
				this.accessToken = "aaf71121-02b8-41b6-b049-d232e2222dd7";
				this.accessTokenSecret = "ab2bc414-5054-4f7d-a419-d1a1e82f0ff6"; */				
				
				this.url = "http://api.linkedin.com/v1/people/url=" + URLEncoder.encode(handle, "UTF8") + "/picture-urls::(original)";
				
				try {
					
					getLinkedInProfileImage();
					
				} catch (ParserConfigurationException error) {
					
					// TODO Autogenerated catch block
					error.printStackTrace();
					
				} catch (SAXException error) {
					
					// TODO Autogenerated catch block
					error.printStackTrace();
				}
			} else {
				
				this.consumerKey = properties.getProperty("twitterconsumerkey");
				this.consumerSecret = properties.getProperty("twitterconsumersecret");
				
				this.accessToken = properties.getProperty("twitteraccesstoken");
				this.accessTokenSecret = properties.getProperty("twitteraccesstokensecret");
				
				this.url = properties.getProperty("twitterurl");
				
				/* this.consumerKey = "hHKbEpoT86DcFwuvZ0B8JQ";
				this.consumerSecret = "J2LhRTHWlVcVta7E7BFGvZNy0h6ZVcZGFf5d97R0ERI";
				
				this.accessToken = "2291204155-74hZw6tJATnOnGidwlLvMLMCuoI0hv1gHveRaab";
				this.accessTokenSecret = "4NL4979d3FQNTIzhsmsorkQ0MzaMhcUi4T9HeNTZhGFvS"; */				
				
				this.url = "https://api.twitter.com/1.1/users/lookup.json?screen_name=" + handle;
				
				// Scribe implementation
				getTwitterProfileImage();		        
				
				// Twitter4J implementation
				
				/* try {
				
		        	getTwitterProfileImage();
		        	
		        } catch (TwitterException error) {
		        	
		        	// TODO Autogenerated catch block
		        	error.printStackTrace();
		        } */
		        
			}	        
	        ServletOutputStream output = response.getOutputStream();
	        
	        URL url = new URL(profileImageURL);        
	        URLConnection urlConnection = url.openConnection();
	        
	        byte[] byteBuffer = new byte[1024];
	        int inputStreamLength;
	        
	        BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
	        
	        while ((inputStreamLength = bufferedInputStream.read(byteBuffer)) > 0) {
	        
	        	output.write(byteBuffer, 0, inputStreamLength);
	        }	        
	        output.flush();
	        output.close();
		}
    }
	
	private void getLinkedInProfileImage() throws  IOException, ParserConfigurationException, SAXException {
		
		// Scribe implementation				
		oAuthService = new ServiceBuilder().provider(LinkedInApi.class)
														.apiKey(consumerKey)
														.apiSecret(consumerSecret)
														.build();
		
		response = getToken(url, oAuthService);		
		String xml = response.getBody();	
		
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance()
																.newDocumentBuilder();
		
		Document document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
		
		this.profileImageURL = document.getFirstChild()
									   .getTextContent();
	}
	
	private void getTwitterProfileImage() /* throws TwitterException */ {
		
		// Scribe implementation		
		oAuthService = new ServiceBuilder().provider(TwitterApi.class)
										   .apiKey(consumerKey)
										   .apiSecret(consumerSecret)
										   .build();
		
		response = getToken(url, oAuthService);
		
		JSONArray jsonArray = new JSONArray(response.getBody());		
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		
		this.profileImageURL = jsonObject.getString("profile_image_url")
										 .replace("_normal", "");
		
		// Twitter4J implementation
		
		/* ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(consumerKey);
		configurationBuilder.setOAuthConsumerSecret(consumerSecret);
		
		Configuration configuration = configurationBuilder.build();
		
		TwitterFactory factory = new TwitterFactory(configuration);
		Twitter twitter = factory.getInstance();
		
		AccessToken token = new AccessToken(accessToken, accessTokenSecret);
		
		twitter.setOAuthAccessToken(token);
		
		User user = twitter.showUser(handle);		
		this.profileImageURL = user.getOriginalProfileImageURL(); */
		
	}
	
	private Response getToken(String url, OAuthService oAuthService) {
		
		oAuthRequest = new OAuthRequest(Verb.GET, url);
		oAuthService.signRequest(new Token(accessToken, accessTokenSecret), oAuthRequest);
		
		return oAuthRequest.send();
	}
}