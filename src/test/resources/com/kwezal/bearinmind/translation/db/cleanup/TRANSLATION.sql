DELETE FROM translations t WHERE t.id >= 1000000;
SELECT setval('translations_id_seq', 1000000);
SELECT setval('translations_identifier_seq', 1000000);