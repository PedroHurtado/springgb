package com.example.demo.features.ingredients.Commands;

import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Ingredient;
import com.example.demo.infraestructure.IngredientRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Configuration
public class IngredientCreate {

    public record Request(String name,Double cost) {
    }
    public record Response(UUID id,String name, Double cost) {
    }
    @RestController
    public class Controller{
        private final Service service;
        public Controller(final Service service) {
            this.service = service;
        }
        @PostMapping("/ingredients")              
        public ResponseEntity<?> hanlder(@RequestBody Request request){
            return ResponseEntity.status(201).body(
                service.hanlder(request)
            );
        }

    }
    public interface Service {    
        Response hanlder(Request request);
    }
    @org.springframework.stereotype.Service
    public class ServiceImp implements Service{
        private final IngredientRepository repository;
        public ServiceImp(final IngredientRepository repository){
            this.repository = repository;
        }
        @Override
        public Response hanlder(Request request) {
            //transacciones,logger,validation,mapping,persistimos
            var ingredient = Ingredient.create(
                UUID.randomUUID(), 
                request.name(), 
                request.cost()
            );
            repository.save(ingredient);

            return new Response(
                ingredient.getId(), 
                ingredient.getName(), 
                ingredient.getCost()
            );
        }
    }
}
