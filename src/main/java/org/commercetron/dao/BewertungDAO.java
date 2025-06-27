package org.commercetron.dao;

import org.commercetron.beans.Bewertung;

import java.util.UUID;

public class BewertungDAO extends BaseDAO<Bewertung, UUID> {
    protected BewertungDAO() {
        super(Bewertung.class);
    }
}
