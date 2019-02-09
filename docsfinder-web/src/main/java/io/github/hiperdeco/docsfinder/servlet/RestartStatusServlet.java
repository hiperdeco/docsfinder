package io.github.hiperdeco.docsfinder.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import io.github.hiperdeco.docsfinder.controller.RepositoryIndexer;
import io.github.hiperdeco.docsfinder.controller.RepositoryJobManager;
import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.entity.RepositoryStatus;

/**
 * Servlet implementation class RestartStatus
 */
@WebServlet("/admin/RestartStatus")
public class RestartStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static Logger log = Logger.getLogger(RestartStatusServlet.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RestartStatusServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jobKey = request.getParameter("name");
		if (jobKey != null && !jobKey.isEmpty()) {
		log.info("Restarting status for Repository : " + jobKey);
	    
	    Repository repo = RepositoryJobManager.getRepository(jobKey);
	    
	    RepositoryIndexer indexer = new RepositoryIndexer(repo);
	    indexer.changeIndexStatus(RepositoryStatus.EMPTY);
	    
	    response.getWriter().println("<html><h1> Repository " + jobKey + " status restarted</h1></html>");
	    
	    log.info("Status Repository restarted: " + jobKey);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
