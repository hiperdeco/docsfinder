package io.github.hiperdeco.docsfinder.main;

import java.util.List;

import io.github.hiperdeco.docsfinder.controller.JPAUtil;
import io.github.hiperdeco.docsfinder.entity.Configuration;
import io.github.hiperdeco.docsfinder.entity.Repository;

public class TesteJPA02 {
	
	public static void main(String[] args) {
		
		
		Configuration conf1 = new Configuration();
		conf1.setKey("JPAKEYTESTE");
		conf1.setValue("JPAVALUE");
		Repository repo = new Repository();
		repo.setId(1);
		conf1.setRepository(repo);
		
		JPAUtil.insert(conf1);
		
		List<Configuration> conf = (List<Configuration>) JPAUtil.findAll(Configuration.class);
		
		for (Configuration item: conf) {
			System.out.println(item.getKey());
		}
		
		System.exit(0);
	}

}
