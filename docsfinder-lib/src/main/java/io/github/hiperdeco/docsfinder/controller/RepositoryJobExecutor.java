package io.github.hiperdeco.docsfinder.controller;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.entity.RepositoryStatus;

public class RepositoryJobExecutor  implements Job, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8644020445522297152L;
	
	private static Logger log = Logger.getLogger(RepositoryJobExecutor.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
	    JobKey jobKey = context.getJobDetail().getKey();
	    String repoName = jobKey.getName();
	    log.info("Executing scheduled index for : " + repoName);
	    //rename prevents for unique job "_un"
	    if (repoName.endsWith("_un")) {
	    	repoName = repoName.replaceAll("_un$", "");
	    }
	    
	    EntityManager em = null;
	    Repository repo = null;
	    try {
		    em = JPAUtil.getEntityManager();
		    repo = em.createNamedQuery("Repository.findByName",Repository.class).setParameter(1, repoName).getSingleResult();
	    }catch(Exception e) {
	    	log.error("Failed to find Repository: "+ repoName, e);
	    	throw new JobExecutionException("Failed to find repository");
	    }finally {
	    	JPAUtil.closeEntityManager(em);
	    }

	    try {
		    RepositoryFetcher fetcher = new RepositoryFetcher(repo);
		    fetcher.execute();
		    
		    RepositoryIndexer indexer = new RepositoryIndexer(repo);
		    indexer.execute();
		    log.info("End of scheduled execution for: " + repoName);
	    }catch(Exception e) {
	    	log.error("Error to schedule execution for: "+ repoName, e);
	    }finally {
	    	repo = (Repository) JPAUtil.findById(Repository.class, repo.getId());
	    	repo.setStatus(RepositoryStatus.ERROR);
	    	JPAUtil.update(repo);
	    }
	    
	}
	
	

}
