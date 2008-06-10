package org.londonwicket.osiv.phonebook;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.wicket.protocol.http.WicketServlet;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class PhoneBookServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
	    EntityManagerFactory emFactory;
	    EntityManager em;
	    Connection connection;
        try {
            System.out.println("Starting in-memory HSQL database for unit tests");
            Class.forName("org.hsqldb.jdbcDriver");
            connection = DriverManager.getConnection("jdbc:hsqldb:mem:phonebook", "sa", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception during HSQL database startup.");
            throw ex;
        }
        try {
        	System.out.println("Building JPA EntityManager");
        	emFactory = Persistence.createEntityManagerFactory("phonebook");
            em = emFactory.createEntityManager();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception during JPA EntityManager instantiation.");
            throw ex;
        }
        
		
		
		Server server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });
		Context root = new Context(server, "/", Context.SESSIONS);
		ServletHolder holder = new ServletHolder();
		holder.setServlet(new WicketServlet());
		holder.setInitParameter("applicationClassName",
				"org.londonwicket.osiv.phonebook.PhoneBookApplication");
		root.addServlet(holder, "/*");

		server.start();
		server.join();

	}

}
