package com.example.core;

import java.util.Objects;
import java.util.UUID;


public abstract class EntityBase{

    //Requerido por el ORM
    protected EntityBase(){
        this(null);
    }

    private final UUID id;
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
