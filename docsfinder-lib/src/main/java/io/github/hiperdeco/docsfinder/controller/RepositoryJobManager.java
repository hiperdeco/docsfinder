package io.github.hiperdeco.docsfinder.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import io.github.hiperdeco.docsfinder.entity.Repository;

import static org.quartz.JobBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.DateBuilder.*;

/**
 * TODO: documentar classe
 * @author andre
 *
 */
public class RepositoryJobManager implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(RepositoryJobManager.class);
	
	private Scheduler sched = null;
	
	private static RepositoryJobManager _instance = null;
	
	private RepositoryJobManager() {
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			this.sched = sf.getScheduler();
		} catch (SchedulerException e) {
			log.error("Scheduler error", e);
		}
	}
	
	public static RepositoryJobManager getInstance() {
		if (_instance == null) {
			_instance = new RepositoryJobManager();
		}
		return _instance;
	}
	
	public void addJob(Repository repo) throws SchedulerException {
		try {
			addJobOffLine(repo);
			this.sched.start();
		} catch (SchedulerException e) {
			log.error("Error scheduling " + repo.getName(),e);
			throw e;
		}
	}
	
	public void addJobOffLine(Repository repo) throws SchedulerException {
		removeJob(repo);
		JobKey jobKey = new JobKey(repo.getName());
		TriggerKey triggerKey = new TriggerKey(repo.getName());
		log.info("Scheduling RepoIndexer: " + repo.getName());
		
		JobDetail job = newJob(RepositoryJobExecutor.class)
			    .withIdentity(jobKey)
			    .build();

		CronTrigger trigger = newTrigger()
			    .withIdentity(triggerKey)
			    .withSchedule(cronSchedule(repo.getCronSchedule()))
			    .build();

		try {
			this.sched.scheduleJob(job, trigger);
			log.info("Scheduled RepoIndexer: " + repo.getName());
		} catch (SchedulerException e) {
			log.error("Error scheduling " + repo.getName(),e);
			throw e;
		}
				
	}
	
	public void executeJob(Repository repo) throws SchedulerException {
		
		JobKey jobKey = new JobKey(repo.getName() + "_un");
		TriggerKey triggerKey = new TriggerKey(repo.getName()+"_un");
		log.info("Scheduling immediatelly RepoIndexer: " + repo.getName());
		
		JobDetail job = newJob(RepositoryJobExecutor.class)
			    .withIdentity(jobKey)
			    .build();

		// compute a time that is on the next round minute
		Date runTime = evenMinuteDate(new Date());

		// Trigger the job to run on the next round minute
		Trigger trigger = newTrigger()
		    .withIdentity(triggerKey)
		    .startAt(runTime)
		    .build();

		try {
			this.sched.scheduleJob(job, trigger);
			this.sched.start();
			log.info("Scheduled immediatelly RepoIndexer: " + repo.getName());
		} catch (SchedulerException e) {
			log.error("Error scheduling " + repo.getName(),e);
			throw e;
		}
				
	}
	
	public void removeJob(String repositoryName) throws SchedulerException {
		JobKey jobKey = new JobKey(repositoryName);
		try {
			sched.deleteJob(jobKey);
		} catch (SchedulerException e) {
			log.error("Error removing schedule: " + jobKey);
			throw e;
		}
		
	}
	public  void removeJob(Repository repo) throws SchedulerException {
		removeJob(repo.getName());
	}
	
	public void startScheduler() throws SchedulerException {
		log.info("Starting RepositoryIndexer scheduler");
		try {
			@SuppressWarnings("unchecked")
			List<Repository> repositories = JPAUtil.findAll(Repository.class);
			for(Repository repo: repositories) {
				addJobOffLine(repo);
			}		
			this.sched.start();
			log.info("Started RepositoryIndexer scheduler");
		} catch (SchedulerException e) {
			log.error("Starting Schedule Error", e);
			throw e;
		}
	}
	
	public void shutdownScheduler() throws SchedulerException {
		try {
			this.sched.shutdown();
		} catch (SchedulerException e) {
			log.error("Shutdown Schedule Error", e);
			throw e;
		}
	}
	
	public static Repository getRepository(String name) {
		Repository repo = null;    
		EntityManager em = null;
	    try {
	    	em = JPAUtil.getEntityManager();
	    	repo = em.createNamedQuery("Repository.findByName",Repository.class).setParameter(1, name).getSingleResult();
	    }catch(Exception e) {
	    	log.error(e.getMessage(), e);
	    }finally {
	    	JPAUtil.closeEntityManager(em);
	    }
		return repo;
	}

	
}
