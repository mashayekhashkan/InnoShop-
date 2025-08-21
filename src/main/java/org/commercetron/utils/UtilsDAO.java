package org.commercetron.utils;

import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse zur Vereinfachung von Datenbankabfragen mit JPQL.
 * Ermöglicht die Ausführung generischer Abfragen mit einem oder zwei Parametern.
 */
public class UtilsDAO {

    /**
     * Führt eine JPQL-Abfrage aus und gibt eine Liste von Ergebnissen zurück.
     * Unterstützt dynamisch ein oder zwei Parameter. Bei mehr als zwei Parametern wird eine Exception geworfen.
     *
     * @param jpql        Die JPQL-Abfrage mit benannten Parametern (z. B. :param, :param1)
     * @param resultClass Die Klasse der erwarteten Ergebnistypen
     * @param em          Der EntityManager zur Ausführung der Abfrage
     * @param params      Ein oder zwei Parameter, die in der Abfrage verwendet werden
     * @return Eine Liste mit den gefundenen Entitäten
     * @throws IllegalArgumentException Wenn mehr als zwei Parameter übergeben wurden
     */
    public static <T, K> List<T> findItemsWithPropertyOrProperties(String jpql,
                                                                   Class<T> resultClass,
                                                                   EntityManager em,
                                                                   K... params) throws IllegalArgumentException {
        List<T> items = new ArrayList<>();

        if (params.length == 1) {
            // Abfrage mit einem Parameter (:param)
            items = em.createQuery(jpql, resultClass)
                    .setParameter("param", params[0])
                    .getResultList();

        } else if (params.length == 2) {
            // Abfrage mit zwei Parametern (:param, :param1)
            items = em.createQuery(jpql, resultClass)
                    .setParameter("param", params[0])
                    .setParameter("param1", params[1])
                    .getResultList();

        } else {
            // Fehler: Nur 1 oder 2 Parameter werden unterstützt
            throw new IllegalArgumentException("Nur 1 oder 2 Parameter sind erlaubt.");
        }

        return items;
    }
}