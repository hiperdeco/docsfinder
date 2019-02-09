package io.github.hiperdeco.docsfinder.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import io.github.hiperdeco.docsfinder.controller.RepositoryJobManager;

public class IndexerJobServlet implements ServletContextListener {

	private static Logger log = Logger.getLogger(IndexerJobServlet.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
		RepositoryJobManager.getInstance().shutdownScheduler();
		} catch (Exception e) {
			log.error("Stopping scheduler error: ", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			RepositoryJobManager.getInstance().startScheduler();
			log.info("Schedule started");
		} catch (Exception e) {
			log.error("Starting scheduler error: ", e);
		}
	}
}