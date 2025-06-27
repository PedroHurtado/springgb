package com.example.demo.features.ingredients.Commands;

import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.infraestructure.IngredientRepository;

@Configuration
public class IngredientDelete {

    @RestController
    public class Controller{
        private final IngredientRepository repository;

        public Controller(IngredientRepository repository) {
            this.repository = repository;
        }
        @DeleteMapping("/ingredients/{id}")
        public ResponseEntity<?> handeler(
            @PathVariable UUID id
        ){
            var ingredient = repository.get(id);
            repository.delete(ingredient);
            return ResponseEntity.status(204).build();
        }
        
    }
}
