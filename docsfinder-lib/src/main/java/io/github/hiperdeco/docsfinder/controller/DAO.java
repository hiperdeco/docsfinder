package io.github.hiperdeco.docsfinder.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

public interface DAO<T ,I extends Serializable> {
	
	public List<T> findAll();
	public T findById( I chave);
	public List<T> findByDesc(String description);
	public void insert(T objeto);
	public void update(T objeto);
	public void delete(T objeto);
	public List<T> findByMap(Map<String,Object> values);
	
	public EntityManager getEntityManager();
	public void close();
	
}
