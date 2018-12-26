package io.github.hiperdeco.docsfinder.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;



//TODO: documentar 
public class JPAUtil {
	
	private static Logger log = Logger.getLogger(JPAUtil.class);

	public static EntityManager getEntityManager() {
		return DAOManager.getInstance().getFactory().createEntityManager();

	}

	public static void closeEntityManager(EntityManager em) {
		if (em != null) {
			em.close();
		}

	}

	@SuppressWarnings("unchecked")
	public static List findAll(Class clazz) {
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query q = em.createNamedQuery(clazz.getSimpleName() + ".findAll");
			return q.getResultList();
		} catch (Exception e) {
			log.debug("Error in find. Trying traditional...");
			String queryStr = "select t from " + clazz.getSimpleName() + " t ";
			em = getEntityManager();
			return em.createQuery(queryStr).getResultList();
		} finally {
			closeEntityManager(em);
		}

	}

	@SuppressWarnings("unchecked")
	public static Object findById(Class clazz, Object chave) {
		EntityManager em = null;
		Object retorno = null;
		try {
			em = getEntityManager();
			retorno = em.find(clazz, chave);
		} catch (Exception e) {
			log.error("Find error.",e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
		return retorno;
	}

	public static void insert(Object objeto) {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			em.persist(objeto);
			em.getTransaction().commit();
		} catch (Exception e) {
			log.error("Insert error.",e);
			try {
				em.getTransaction().rollback();
			}catch(Exception e1) {}
			if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e .getCause();
				throw new RuntimeException("Constraint Violation: " + cve.getConstraintName());
			}
			throw e;
		} finally {
			closeEntityManager(em);
		}

	}

	public static void update(Object objeto) {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			em.merge(objeto);
			em.getTransaction().commit();
		} catch (Exception e) {
			log.error("Update error.",e);
			try {
				em.getTransaction().rollback();
			}catch(Exception e1) {}
			if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e .getCause();
				throw new RuntimeException("Constraint Violation: " + cve.getConstraintName());
			}
			throw e;
		} finally {
			closeEntityManager(em);
		}

	}

	public static void delete(Object objeto) {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Object objMerge = em.merge(objeto);
			em.remove(objMerge);
			em.getTransaction().commit();
		} catch (Exception e) {
			log.error("Delete error.",e);
			try {
				em.getTransaction().rollback();
			}catch(Exception e1) {}
			if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e .getCause();
				throw new RuntimeException("Constraint Violation: " + cve.getConstraintName());
			}
			throw e;
		} finally {
			closeEntityManager(em);
		}

	}
	
	public static List<Object[]> getListIdAndDescription(Class clazz){
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query q = em.createNamedQuery(clazz.getSimpleName() + ".findAllIdAndDescription");
			return q.getResultList();
		} catch (Exception e) {
			log.debug("getListIdAndDescription NamedQuery error. Trying tradictional ");
			String queryStr = "select t.id, t.id from " + clazz.getSimpleName() + " t ";
			em = getEntityManager();
			return em.createQuery(queryStr).getResultList();
		} finally {
			closeEntityManager(em);
		}
	}
	
	public static void executeUpdate(String hql) {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Query q = em.createQuery(hql);
			q.executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			log.error("Error executing query: " + hql);
			em.getTransaction().rollback();
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}
}
