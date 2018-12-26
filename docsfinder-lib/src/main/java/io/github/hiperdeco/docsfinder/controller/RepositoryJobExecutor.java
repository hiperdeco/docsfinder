package io.github.hiperdeco.docsfinder.controller;

import java.io.Serializable;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import io.github.hiperdeco.docsfinder.entity.Repository;

public class RepositoryJobExecutor  implements Job, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8644020445522297152L;
	
	private static Logger log = Logger.getLogger(RepositoryJobExecutor.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
	    JobKey jobKey = context.getJobDetail().getKey();
	    log.info("Executing scheduled index for : " + jobKey);
	    
	    Repository repo = JPAUtil.getEntityManager().createNamedQuery("Repository.findByName",Repository.class).setParameter(1, jobKey.getName()).getSingleResult();
	    
	    RepositoryIndexer indexer = new RepositoryIndexer(repo);
	    indexer.execute();
	    
	    log.info("End of scheduled execution for: " + jobKey);
	}
	
	

}
