package com.example.demo.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomRepository<T,ID> extends JpaRepository<T,ID>, Get<T,ID> {
 @Override
 default T get(ID id){
    return Get.super.unwrap(findById(id));
 }
    
} 
