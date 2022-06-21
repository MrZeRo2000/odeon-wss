package com.romanpulov.odeonwss.builder;


public abstract class AbstractClassBuilder<T> {
    protected T instance;

    public AbstractClassBuilder(Class<T> clazz) {
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    public T build() {
        return instance;
    }
}
