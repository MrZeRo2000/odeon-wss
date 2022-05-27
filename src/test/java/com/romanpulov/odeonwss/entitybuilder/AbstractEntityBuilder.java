package com.romanpulov.odeonwss.entitybuilder;


public abstract class AbstractEntityBuilder<T> {
    protected T entity;

    public AbstractEntityBuilder(Class<T> entityClass) {
        try {
            entity = entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    public T build() {
        return entity;
    }
}
