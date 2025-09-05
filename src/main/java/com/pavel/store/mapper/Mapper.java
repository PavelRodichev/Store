package com.pavel.store.mapper;

public interface Maper <F,T> {

    T mapTo(F f);

    default T map(F fromObject, T toObject) {
        return toObject;
    }

}
