package com.example.demo.infraestructure;


import java.util.UUID;


import com.example.demo.core.CustomRepository;
import com.example.demo.domain.Ingredient;


public interface IngredientRepository extends CustomRepository<Ingredient,UUID> {
    
}
