package com.example.demo.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.example.demo.core.EntityBase;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;



@Entity
public class Pizza extends EntityBase {
    private static final double PROFIT= 1.2D;
    
    private String name;
    private String description;
    private String url;   
    @ManyToMany 
    private Set<Ingredient> ingredients;
    //Requerido por el orm
    protected Pizza(){
        super();
    }
    protected Pizza(UUID id, String name, String description, String url, Set<Ingredient> ingredients) {
        super(id);
        this.name = name;
        this.description = description;
        this.url = url;
        this.ingredients = new HashSet<>(ingredients);
    }
    public double getPrice(){
        return ingredients.stream()
            .map(i->i.getCost())
            .reduce(0D, Double::sum) * PROFIT;
    }
    public Set<Ingredient> getIngredients() {
        return new HashSet<>(ingredients);
    }
    
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getUrl() {
        return url;
    }
    public void addIngedient(Ingredient ingredient){
        //pizza.addingredient
        ingredients.add(ingredient);
    }
    public void removeIngredient(Ingredient ingredient){
        //pizza.removeingredient
        ingredients.remove(ingredient);
    }
    public void update(String name, String description, String url){
        //pizza.update
        this.name = name;
        this.description = description;
        this.url = url;
    }
    public static Pizza create(UUID id, String name, String description, String url, Set<Ingredient> ingredients){
        return new Pizza(id, name, description, url, ingredients);
    }
    
    
}
