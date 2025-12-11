package com.kwezal.bearinmind.translation.model;

import static java.util.Objects.nonNull;

import jakarta.persistence.FlushModeType;
import java.util.EnumSet;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

class TranslationIdentifierGenerator implements BeforeExecutionGenerator {

    private static final EnumSet<EventType> EVENT_TYPES = EnumSet.of(EventType.INSERT);

    @Override
    public Object generate(SharedSessionContractImplementor session, Object o, Object o1, EventType eventType) {
        if (o instanceof Translation translation && nonNull(translation.getIdentifier())) {
            return translation.getIdentifier();
        } else {
            final var value = (Long) session
                .createNativeQuery("SELECT nextval('translations_identifier_seq')")
                .setFlushMode(FlushModeType.COMMIT)
                .getSingleResult();
            return value.intValue();
        }
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EVENT_TYPES;
    }
}
