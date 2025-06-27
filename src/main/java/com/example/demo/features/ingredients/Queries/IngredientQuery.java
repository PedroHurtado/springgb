package com.example.demo.features.ingredients.Queries;

import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.infraestructure.IngredientRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Configuration
public class IngredientQuery {
    public record Response(UUID id, String name, Double cost ) {
    }
    @RestController
    public class Controller {
    
        private final IngredientRepository respository;
        
        public Controller(IngredientRepository respository) {
            this.respository = respository;
        }

        @GetMapping("/ingredients")       
        public ResponseEntity<?> handler(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
        ){
            var ingrediensts = respository.query(
                name, 
                PageRequest.of(page, size) 
            )
            .stream()
            .map(i->new Response(i.getId(), i.getName(), i.getCost()))
            .toList();

            return ResponseEntity.ok().body(ingrediensts);

        }
    }
}
