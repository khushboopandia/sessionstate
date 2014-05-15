package edu.khush.lsi.session.state;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SessionStateCleaner extends Thread {

	private static HashMap<String, SessionData> sessionTable = null;
	private static final int SLEEP_INTERVAL=60;

	public static void setSessionTable(HashMap<String, SessionData> sessionTable) {
		SessionStateCleaner.sessionTable = sessionTable;
	}

	public SessionStateCleaner() {
		setDaemon(true);

	}

	public void run() {

		while (true) {

			System.out.println("Running cleanup...");
			Iterator it = sessionTable.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				SessionData sessionEntry = (SessionData) pairs.getValue();
				
				
					synchronized (sessionEntry) {
						
						Calendar cal = Calendar.getInstance();
						if (sessionEntry.getExpirationTime().before(
								cal.getTime())) {
							System.out.println("Removing expired session: "+pairs.getKey());
							it.remove();
						}

					}
				
			}

			try {

				System.out.println("Clean up thread sleeping...");
				Thread.sleep(SLEEP_INTERVAL * 1000);

			} catch (InterruptedException e) {

				System.out
						.println("Cleanup Thread interrupted! Cleanup is no longer running!");
				e.printStackTrace();

			}
		}

	}

}
