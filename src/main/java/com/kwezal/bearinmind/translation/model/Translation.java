package com.kwezal.bearinmind.translation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedTranslationIdentifier
    Integer identifier;

    @Column(nullable = false)
    String locale;

    @Column(nullable = false)
    String text;
}
