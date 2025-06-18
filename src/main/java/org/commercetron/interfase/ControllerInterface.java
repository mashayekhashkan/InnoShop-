package org.commercetron.interfase;

import java.util.List;
import java.util.UUID;

public interface ControllerInterface<T, entity> {

    /**
     * Erstellt eine neue Entität und speichert sie in der Datenbank.
     * <p>
     * Diese Methode wird in der Regel vom Controller oder Service aufgerufen,
     * um einen neuen Datensatz anzulegen – z. B. bei einer Benutzerregistrierung oder einer neuen Bestellung.
     *
     * @param entity die zu speichernde Entität (darf nicht null sein)
     * @return die gespeicherte Entität, ggf. mit generierter ID oder aktualisierten Feldern
     */
    T create(T entity);

    /**
     * Ruft eine Entität anhand ihrer eindeutigen UUID ab.
     * <p>
     * Im Gegensatz zu {@code findById}, die {@code null} oder {@code Optional.empty()} zurückgeben kann,
     * wird bei {@code getById} häufig erwartet, dass eine Entität existiert – andernfalls sollte eine Ausnahme
     * geworfen werden (z. B. {@code EntityNotFoundException}).
     *
     * @param id die eindeutige ID der Entität (darf nicht null sein)
     * @return die gefundene Entität
     * @throws RuntimeException wenn keine Entität mit der angegebenen ID gefunden wird
     */
    T getById(UUID id);

    /**
     * Gibt eine Liste aller gespeicherten Entitäten zurück.
     * <p>
     * Diese Methode wird typischerweise verwendet, um eine Übersicht oder eine
     * vollständige Auflistung aller Datensätze bereitzustellen – z. B. für Tabellen oder Listenansichten.
     *
     * @return eine Liste aller vorhandenen Entitäten; eine leere Liste, wenn keine Entitäten gefunden wurden
     */
    List<T> getAll();

    /**
     * Aktualisiert eine bestehende Entität in der Datenbank.
     * <p>
     * Diese Methode wird verwendet, um geänderte Daten eines bereits vorhandenen Objekts zu speichern,
     * z. B. bei der Bearbeitung eines Benutzerprofils oder einer Bestellung.
     *
     * @param entity die zu aktualisierende Entität mit gültiger ID und neuen Werten (darf nicht null sein)
     * @return die aktualisierte Entität nach dem Speichern
     * @throws RuntimeException wenn die Entität nicht existiert oder nicht aktualisiert werden kann
     */
    T update(T entity);
    /**
     * Löscht die übergebene Entität aus der Datenquelle.
     *
     * @param entity Die zu löschende Entität vom Typ T.
     * @return true, wenn die Entität erfolgreich gelöscht wurde, sonst false.
     */
    boolean delete(T entity);
    /**
     * Löscht die Entität mit der angegebenen ID aus der Datenquelle.
     *
     * @param id Die eindeutige ID (UUID) der zu löschenden Entität.
     * @return true, wenn die Entität mit der angegebenen ID erfolgreich gelöscht wurde, sonst false.
     */
    boolean deleteById(UUID id);
}
