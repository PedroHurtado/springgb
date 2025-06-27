package com.example.demo.core;

import java.util.Optional;

public interface Get<T,ID> {
    T get(ID id);
    default T unwrap(Optional<T> entity){
        return entity.orElseThrow(()->
            new NotFoundException("La entidad no existe")
        );
    }
}
