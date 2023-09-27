INSERT INTO translations(id, identifier, locale, text)
VALUES (1, 1, 'en', 'Monolingual text'),
       (2, 2, 'en', 'Multilingual text'),
       (3, 2, 'da', 'Flersproget tekst')
;

SELECT setval('translations_id_seq', 1000000);
SELECT setval('translations_identifier_seq', 1000000);