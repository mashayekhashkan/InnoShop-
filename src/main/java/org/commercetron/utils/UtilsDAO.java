package org.commercetron.utils;

import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class UtilsDAO {
    public static <T, K> List<T> findItemsWithPropertyOrProperties(String jpql, Class<T> resultClass, EntityManager em, K... params) throws IllegalArgumentException {
        List<T> items = new ArrayList<>();
        if (params.length == 1) {
            items = em.createQuery(jpql, resultClass)
                    .setParameter("param", params[0]).getResultList();
        } else if (params.length == 2) {
            items = em.createQuery(jpql, resultClass)
                    .setParameter("param", params[0]).setParameter("param1", params[1]).getResultList();
        }
        else {
            throw new IllegalArgumentException("Only 2 parameters are allowed");
        }

        return items;
    }
}
