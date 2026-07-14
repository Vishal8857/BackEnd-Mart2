package com.product.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.product.Entity.Category;
import com.product.Repository.CategoryRepo;

@Service
public class CategoryService {
	private static final Logger logger=LoggerFactory.getLogger(CategoryService.class);
	@Autowired
	private CategoryRepo repo;
	
	//Create Category
		public Category createCategory(Category category) {
			return repo.save(category);
		}
		
		//get all Category
		public List<Category> allCategory(){
			List<Category> categoryList=repo.findAll();
			
			return categoryList;
		}
}
