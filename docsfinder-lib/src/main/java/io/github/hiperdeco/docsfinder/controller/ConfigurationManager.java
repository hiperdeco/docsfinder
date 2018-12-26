package io.github.hiperdeco.docsfinder.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import io.github.hiperdeco.docsfinder.entity.Configuration;

public class ConfigurationManager {
	
	
	public static String getValue(long repositoryId, String keyName) {
		String hql = "select conf.value from Configuration conf where conf.repositoryId = :repo and conf.key = :key";
		EntityManager em = null;
		try {
			em = JPAUtil.getEntityManager();
			Query qry = em.createQuery(hql,String.class);
			qry.setParameter("repo", repositoryId);
			qry.setParameter("key", keyName);
			String ret = (String) qry.getSingleResult();
			return ret;
		}catch(Exception e) {
			throw e;
		}finally {
			JPAUtil.closeEntityManager(em);
		}
	}
	
	public static List<String> getListValue(long repositoryId, String keyName) {
		String hql = "select conf.value from Configuration conf where conf.repositoryId = :repo and conf.key = :key";
		EntityManager em = null;
		try {
			em = JPAUtil.getEntityManager();
			Query qry = em.createQuery(hql,String.class);
			qry.setParameter("repo", repositoryId);
			qry.setParameter("key", keyName);
			@SuppressWarnings("unchecked")
			List<String> ret =  (List<String>) qry.getResultList();
			return ret;
		}catch(Exception e) {
			throw e;
		}finally {
			JPAUtil.closeEntityManager(em);
		}
	}
	
	public static List<Configuration> getConfiguration(long repositoryId) {
		String hql = "select conf from Configuration conf where conf.repositoryId = :repo";
		EntityManager em = null;
		try {
			em = JPAUtil.getEntityManager();
			Query qry = em.createQuery(hql,Configuration.class);
			qry.setParameter("repo", repositoryId);
			@SuppressWarnings("unchecked")
			List<Configuration> ret =  (List<Configuration>) qry.getResultList();
			return ret;
		}catch(Exception e) {
			throw e;
		}finally {
			JPAUtil.closeEntityManager(em);
		}
	}

}
