package com.kwezal.bearinmind.translation.model;

import static java.util.Objects.nonNull;

import java.math.BigInteger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

public class TranslationIdentifierGenerator implements ValueGenerator<Integer> {

    @Override
    public Integer generateValue(Session session, Object o) {
        if (o instanceof Translation translation && nonNull(translation.getIdentifier())) {
            return translation.getIdentifier();
        } else {
            final var value = (BigInteger) session
                .createNativeQuery("SELECT nextval('translations_identifier_seq')")
                .setFlushMode(FlushMode.COMMIT)
                .getSingleResult();
            return value.intValue();
        }
    }
}
