package com.example.demo.infraestructure;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Ingredient;


public interface IngredientRepository extends JpaRepository<Ingredient,UUID> {
    
}
