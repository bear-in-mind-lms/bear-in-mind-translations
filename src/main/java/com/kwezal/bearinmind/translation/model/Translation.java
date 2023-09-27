package com.kwezal.bearinmind.translation.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

@Entity
@Table(name = "translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    @GeneratorType(type = TranslationIdentifierGenerator.class, when = GenerationTime.INSERT)
    Integer identifier;

    @Column(nullable = false)
    String locale;

    @Column(nullable = false)
    String text;
}
