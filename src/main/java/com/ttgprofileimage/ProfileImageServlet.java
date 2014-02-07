package com.ttgprofileimage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
		
		init();
			
		if (handle.contains("linkedin")) {
			
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
	
			getTwitterProfileImage();		        
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
	
	@Override
	public void init() throws ServletException {
		
		if (handle != null) {
			
			try {
				
				handle = URLEncoder.encode(handle, "UTF-8");
				
			} catch (UnsupportedEncodingException error) {
				
				// TODO Autogenerated catch block
				error.printStackTrace();
			}

			String prefix = handle.contains("linkedin") ? "linkedIn" : "twitter";
				
			this.consumerKey = getInitParameter(prefix + "ConsumerKey");
			this.consumerSecret = getInitParameter(prefix + "ConsumerSecret");
			
			this.accessToken = getInitParameter(prefix + "AccessToken");
			this.accessTokenSecret = getInitParameter(prefix + "AccessTokenSecret");
			
			this.url = getInitParameter(prefix + "URL").replace("{ handle }", handle);
		}
	}
	
	private void getLinkedInProfileImage() throws  IOException, ParserConfigurationException, SAXException {
					
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
	
	private void getTwitterProfileImage() {
			
		oAuthService = new ServiceBuilder().provider(TwitterApi.class)
										   .apiKey(consumerKey)
										   .apiSecret(consumerSecret)
										   .build();
		
		response = getToken(url, oAuthService);
		
		JSONArray jsonArray = new JSONArray(response.getBody());		
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		
		this.profileImageURL = jsonObject.getString("profile_image_url")
										 .replace("_normal", "");		
	}
	
	private Response getToken(String url, OAuthService oAuthService) {
		
		oAuthRequest = new OAuthRequest(Verb.GET, url);
		oAuthService.signRequest(new Token(accessToken, accessTokenSecret), oAuthRequest);
		
		return oAuthRequest.send();
	}
}