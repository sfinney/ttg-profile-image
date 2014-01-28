package com.profileimageservlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/* This class launches the web application in an embedded Jetty container and the Java command that is used for 
 launching should fire this main method */
public class Main {

    public static void main(String[] args) throws Exception {
    	
        String directory = "src/main/webapp/";
        
        // The port that should be used is set to an environment variable or defaults to 8080 if it's unavailable
        String port = System.getenv("PORT");
        
        if (port == null || port.isEmpty()) {
        	
            port = "8080";
        }

        Server server = new Server(Integer.valueOf(port));
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setDescriptor(directory + "/WEB-INF/web.xml");
        root.setResourceBase(directory);
        
        // Allows application to replace non-server libraries that are part of the container
        root.setParentLoaderPriority(true);
        
        server.setHandler(root);        
        server.start();
        server.join();   
    }
}