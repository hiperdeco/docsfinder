package io.github.hiperdeco.docsfinder.main;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import io.github.hiperdeco.docsfinder.entity.User;

public class TesteJPA01 {

	public static void main(String[] args) {
		
		EntityManagerFactory emf = 
				Persistence.createEntityManagerFactory("docsfinder");

		EntityManager em =  emf.createEntityManager();
	  
		User user = em.find(User.class, 0L);
		System.exit(0);
	}

}
