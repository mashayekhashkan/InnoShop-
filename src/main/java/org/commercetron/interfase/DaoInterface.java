package org.commercetron.interfase;

import java.util.List;
import java.util.UUID;

public interface DaoInterface<T, ID> {
    /**
     * Speichert die angegebene Entität in der Datenbank.
     * <p>
     * Wenn die Entität bereits existiert (z. B. mit einer vorhandenen ID),
     * wird sie aktualisiert. Andernfalls wird ein neuer Eintrag erstellt.
     *
     * @param entity die zu speichernde Entität (darf nicht null sein)
     * @return die gespeicherte Entität mit eventuell aktualisierten Feldern (z. B. generierte ID)
     */
    T save(T entity);

    /**
     * Aktualisiert die angegebene Entität in der Datenbank.
     * <p>
     * Die Entität muss bereits existieren (z. B. eine gültige ID besitzen),
     * andernfalls kann es zu einer Ausnahme oder keinem Effekt kommen – abhängig von der Implementierung.
     *
     * @param entity die zu aktualisierende Entität (darf nicht null sein)
     * @return die aktualisierte Entität
     */
    T update(T entity);

    /**
     * Löscht die angegebene Entität aus der Datenbank.
     * <p>
     * Die Entität sollte bereits existieren, andernfalls hat der Aufruf
     * möglicherweise keine Wirkung oder führt zu einer Ausnahme – je nach Implementierung.
     *
     * @param entity die zu löschende Entität (darf nicht null sein)
     * @return die gelöschte Entität oder null, falls sie nicht gefunden wurde
     */
    T delete(T entity);
    /**
     * Sucht eine Entität anhand ihrer eindeutigen UUID.
     *
     * @param id die eindeutige ID der Entität (darf nicht null sein)
     * @return die gefundene Entität oder {@code null}, wenn keine entsprechende Entität vorhanden ist
     */
    T findById(UUID id);
    /**
     * Sucht Entitäten in der Datenbank, die mit den Attributen der übergebenen Entität übereinstimmen.
     * <p>
     * Diese Methode entspricht einer Suche nach einem Beispielobjekt (Query by Example).
     *
     * @return eine Liste aller passenden Entitäten
     */
    List<T> findAll();
    /**
     * Schließt alle Ressourcen, die mit diesem DAO verbunden sind. Sollte am
     * Ende der Anwendung aufgerufen werden.
     */
    void close();
}
