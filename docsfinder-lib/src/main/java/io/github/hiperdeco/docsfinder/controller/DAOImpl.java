package io.github.hiperdeco.docsfinder.controller;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.Map;

//TODO: colocar "closes" no finally 
public class DAOImpl<T,I extends Serializable> implements DAO<T, I> {

	private EntityManager em = null;
	private Class<T> classe;
	
	public DAOImpl(Class<T> classe) {
		this.classe = classe;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		try{
			Query q = getEntityManager()
					.createNamedQuery(classe.getSimpleName()+".findAll");
			return q.getResultList();
		}catch(Exception e){
			String queryStr = "select t from " + classe.getSimpleName() + " t ";
			return getEntityManager().createQuery(queryStr)
					.getResultList();
		}finally{
			close();
		}
		
	}

	public T findById(I chave) {
		T retorno = getEntityManager().find(classe,chave);
		close();
		return retorno;
	}

	
	public void insert(T objeto) {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(objeto);
		getEntityManager().getTransaction().commit();
		close();
		
	}

	
	public void update(T objeto) {
		getEntityManager().getTransaction().begin();
		getEntityManager().merge(objeto);
		getEntityManager().getTransaction().commit();
		close();
		
	}

	
	public void delete(T objeto) {
		getEntityManager().getTransaction().begin();
		getEntityManager().remove(objeto);
		getEntityManager().getTransaction().commit();
		close();
		
	}

	public EntityManager getEntityManager() {
	     if (this.em == null)
	    	 this.em = DAOManager.getInstance().getFactory()
	    	 	.createEntityManager();
	     
		return em;
	}

	
	public void close() {
		if (this.em != null){
			em.close();
			em = null;
		}
		
	}
	
	public List<T> findByDesc(String description) {
		Query qry = getEntityManager()
				.createNamedQuery(classe.getSimpleName() + ".findByDesc");
		qry.setParameter("desc", "%" + description.toUpperCase() + "%");
		
		List<T> resultado = qry.getResultList();
		close();
		return resultado;
	}
	
	@Deprecated
	public List<T> findByObject(T object){
		List<T> resultado= null;
		StringBuffer query = 
				new StringBuffer("select o from "+ object.getClass().getSimpleName() + " o ");
		
		try{
		//navegar entre os atributos
		 Class<?> p = object.getClass().getSuperclass();
		 Class<?> c = object.getClass();
		 List<Field> fields = new ArrayList<Field>();
		 fields.addAll(Arrays.asList(p.getDeclaredFields()));
		 fields.addAll(Arrays.asList(c.getDeclaredFields()));
		 Map<String,Object> criterios = new HashMap<String, Object>();
		 // montar a where baseado no banco
		 StringBuffer where = new StringBuffer();
		 for (Field f: fields){
			 	f.setAccessible(true);
			 	Object value = f.get(object);
			 	if (value == null) continue;
			 	
				criterios.put(f.getName(), f.get(object));
				
				if (where.length() > 0) where.append(" AND " );
				where.append("o."+f.getName()).append(" = :"+f.getName());	
		 }
		 
		 if (where.length() > 0) query.append("where " + where);
		 System.out.println(query.toString());
		 Query q = getEntityManager().createQuery(query.toString());
		 for(String chave: criterios.keySet()){
			 q.setParameter(chave, criterios.get(chave));
		 }
		 
		 resultado = q.getResultList();
		 
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultado;
	}
	
	public List<T> findByMap(Map<String, Object> values) {
		StringBuffer query = new StringBuffer(); 
		try{
		    query.append( 
		    		 getEntityManager()
		    		.createNamedQuery(classe.getSimpleName() + ".findAll")
		    		.unwrap(org.hibernate.Query.class)
		    		.getQueryString()
		    		);
		}catch(Exception e){
			query = new StringBuffer("select o from "
							+ classe.getSimpleName() + " o ");
		}
		StringBuffer where = new StringBuffer();
		for (String chave : values.keySet()) {
			if (where.length() > 0)	where.append(" AND ");
			
			if (values.get(chave) instanceof String){
				where.append("upper(o." + chave + ")")
				.append(" like upper(:" + chave + ") ");
			}else{
				where.append("o." + chave).append(" = :" + chave);
			}
		}
		if (where.length() > 0) query.append(" where " + where);

		System.out.println(query.toString());
		Query q = getEntityManager().createQuery(query.toString());

		for (String chave : values.keySet()) {
			q.setParameter(chave, values.get(chave));
		}
		return q.getResultList();
	}

}