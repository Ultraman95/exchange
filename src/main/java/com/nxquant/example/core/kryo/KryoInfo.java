package com.nxquant.example.core.kryo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public interface KryoInfo extends Serializable {
    @JsonIgnore
    default String getKryoName(){
        return getClass().getCanonicalName();
    }
}
