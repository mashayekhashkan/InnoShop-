package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Kategorie;

import java.util.List;
import java.util.UUID;

/**
 * DAO-Klasse zur Verwaltung von Kategorie-Entitäten.
 * Erweitert BaseDAO, um spezifische Datenzugriffsmethoden für Kategorien bereitzustellen.
 */
public class KategorieDAO extends BaseDAO<Kategorie, UUID> {

    /**
     * Konstruktor zur Initialisierung von KategorieDAO mit der Kategorie-Entitätsklasse.
     */
    public KategorieDAO() {
        super(Kategorie.class);
    }

    /**
     * Findet alle Kategorien, deren Name dem angegebenen Suchbegriff entspricht.
     * Die Suche erfolgt mithilfe eines LIKE-Operators, um Teilübereinstimmungen zu ermöglichen.
     *
     * @param name Name der Kategorie, nach der gesucht werden soll
     * @return Liste der Kategorien, die dem angegebenen Namen entsprechen
     */
    public List<Kategorie> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT k FROM Kategorie k WHERE k.kategorie LIKE :kategorie";
            return em.createQuery(jpql, Kategorie.class)
                    .setParameter("kategorie", "%" + name + "%") // Parametername muss mit der Abfrage übereinstimmen
                    .getResultList();
        } finally {
            em.close(); // EntityManager schließen, um Ressourcen freizugeben
        }
    }
}
