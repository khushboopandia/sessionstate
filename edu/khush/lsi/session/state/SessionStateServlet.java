package edu.khush.lsi.session.state;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionStateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String hostId="server1";
	private static HashMap<String, SessionData> sessionTable = new HashMap<String, SessionData>();
	private static String cookieName = "CS5300PROJ1SESSIONKP365";
	private static final String HTML_STRING = 
			" <html> " + " <head> "
				+ " <title>Session State Management</title> " + " </head> "
					+ " <body> " + " <center><h2> %s </h2></center> " + " <br></br> "
						+ " <center><h2> Session expires at   %s </h2></center> "
						+ " <form action=\"session\" method=\"POST\"> "
							+ " <input type=\"text\" name=\"display_msg\" /> "
							+ " <input type=\"submit\" name=\"replace\" value=\"Replace\" /> "
							+ " <br></br>"
							+ " <input type=\"submit\" name=\"refresh\" value=\"Refresh\" /> "
							+ " <br></br>"
							+ " <input type=\"submit\" name=\"logout\" value=\"Logout\" /> "
						+ " </form> " 
					+ " </body> " 
			+ " </html> ";

	private static final String LOGOUT_MESSAGE = "You have been logged out or your seesion has expired";
	private static final int SESSION_TIMEOUT = 60;

	
	
	@Override
	public void init() throws ServletException {
		SessionStateCleaner cleanupThread = new SessionStateCleaner();
		cleanupThread.setSessionTable(sessionTable);
		cleanupThread.start();
	}
	
	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		doPost(request, response);

	}
	
	

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {


		PrintWriter writer = response.getWriter();
		response.setContentType("text/html");

		// If the session is not present in the Session table its a new session
		String sessionId = null;
		String hostName = hostId;
		int versionNo = 1;

		// Check if the session cookie is present
		Cookie[] cookies = null;
		cookies = request.getCookies();
		Cookie sessionCookie = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookieName.equals(cookies[i].getName())) {
					sessionCookie = cookies[i];
					sessionId = cookies[i].getValue().split("_")[0];
					versionNo = Integer.parseInt(cookies[i].getValue().split(
							"_")[1]);
					hostName = cookies[i].getValue().split("_")[2];
				}
			}
		}

		if (sessionId == null) {

			System.out.println("A new session is being generated");
			Calendar cal = GregorianCalendar.getInstance();

			// Generate the session id
			sessionId = hostId +"%"+cal.getTimeInMillis();
			System.out.println("Generated session id is: "+sessionId);

			// Expiration Time
			cal.add(Calendar.SECOND, SESSION_TIMEOUT);

			// Prepare Session Data
			SessionData sessionData = new SessionData();
			sessionData.setSessionId(sessionId);
			sessionData.setVersionNo(versionNo);
			sessionData.setExpirationTime(cal.getTime());
			sessionData.setHostName(hostName);


			// Set session table entry
			sessionTable.put(sessionId, sessionData);

			// Send back the session management cookie
			System.out.println("Generated cookie value id is: "+sessionData.toString());
			sessionCookie = new Cookie(cookieName, sessionData.toString());
			sessionCookie.setMaxAge(SESSION_TIMEOUT);
			response.addCookie(sessionCookie);
			writer.println(String.format(HTML_STRING, sessionData.getMessage(),
					cal.getTime()));

		} else {

			SessionData sessionData = sessionTable.get(sessionId);

			if (sessionData == null) {
				
				System.out.println("No session data found in table for session id: "+sessionId);
				
				// Remove the cookie
				sessionCookie.setMaxAge(0);
				response.addCookie(sessionCookie);

				// Send the response
				writer.println(LOGOUT_MESSAGE);
				return;

			} else {
				
				try {
					// Synchronize on the session
					synchronized (sessionData) {
						
						//double-checked locking
						sessionData = sessionTable.get(sessionId);

						if (sessionData == null) {
							
							System.out.println("No session data found in table for session id: "+sessionId);

							// Remove the cookie
							sessionCookie.setMaxAge(0);
							response.addCookie(sessionCookie);

							// Send the response
							writer.println(LOGOUT_MESSAGE);
						}


						// If the user want to logout
						if (request.getParameter("logout") != null) {
							
							System.out.println("Logging out the user for session id: "+sessionId);

							// Remove the session from the session table
							sessionTable.remove(sessionId);

							// Remove the cookie
							sessionCookie.setMaxAge(0);
							response.addCookie(sessionCookie);

							// Send the response
							writer.println(LOGOUT_MESSAGE);

						}

						// In case of replace,refresh or browser refresh
						// requests
						else {
							/*
							 * if (request.getParameter("replace") != null ||
							 * request.getParameter("refresh") != null)
							 */

							if (request.getParameter("display_msg") != null
									&& request.getParameter("replace") != null){
								
								System.out.println("Updating display message for session id: "+sessionId);

								String displayMsg=request
								.getParameter("display_msg");
								if(displayMsg.length()>=450)
									sessionData.setMessage( displayMsg.substring(0,450));
								else
									sessionData.setMessage( displayMsg);
							}

							Calendar cal = GregorianCalendar.getInstance();
							cal.add(Calendar.SECOND, SESSION_TIMEOUT);

							// Get the session data
							System.out.println("Updating expiration time for session id: "+sessionId);

							sessionData.setExpirationTime(cal.getTime());
							sessionData.setHostName(hostName);
							
							int updatedVersionNo=sessionData.getVersionNo() + 1;
							System.out.println("Updating version number to: "+updatedVersionNo+" for session id: "+sessionId);

							sessionData
									.setVersionNo(updatedVersionNo);

							// Putting it back helps certain
							// multithreading/concurrency conditions
							sessionTable.put(sessionId, sessionData);

							// Set the cookie
							sessionCookie.setValue(sessionData.toString());
							sessionCookie.setMaxAge(SESSION_TIMEOUT);
							response.addCookie(sessionCookie);

							// Send the response
							writer.println(String.format(HTML_STRING,
									sessionData.getMessage(), cal.getTime()));

						}

					}

				} catch (NullPointerException npe) {
					// Remove the cookie
					sessionCookie.setMaxAge(0);
					response.addCookie(sessionCookie);

					// Send the response
					writer.println(LOGOUT_MESSAGE);
				}

			}
		}

	}

}
