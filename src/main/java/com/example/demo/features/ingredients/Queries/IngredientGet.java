package com.example.demo.features.ingredients.Queries;

import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.infraestructure.IngredientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Configuration
public class IngredientGet {
    public record Response(UUID id,String nane,Double cost) {
    }

    @RestController
    public class Controller{
        private final Service service;

        public Controller(Service service) {
            this.service = service;
        }
        @GetMapping("/ingredients/{id}")        
        public ResponseEntity<?> handler(@PathVariable UUID id){           
            return ResponseEntity.ok().body(service.handler(id));           
        }
    }
    
    public interface Service {    
        Response handler(UUID id);
    }

    @org.springframework.stereotype.Service
    public class ServiceImp implements Service {
        private final IngredientRepository repository;
        public ServiceImp(IngredientRepository repository) {
            this.repository = repository;
        }
        @Override
        public Response handler(UUID id) {            
            var ingredient = repository.get(id);
            return new Response(
                ingredient.getId(), 
                ingredient.getName(),
                ingredient.getCost()
            );
                

        }
    
        
    }
}
