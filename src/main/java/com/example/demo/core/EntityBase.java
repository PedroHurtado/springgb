package com.example.demo.core;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class EntityBase{

    
    @Id
    private final UUID id;
    //Requerido por el ORM
    protected EntityBase(){
        this(null);
    }
    protected EntityBase(UUID id){
        this.id = id;
    } 
    UUID getId(){
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public boolean equals(Object obj) {
        //JAVA 16 pattern matching instance of
        //https://openjdk.org/jeps/394
        if(obj instanceof EntityBase e){
            return e.getId().equals(id);
        }
        return false;
    }
}
