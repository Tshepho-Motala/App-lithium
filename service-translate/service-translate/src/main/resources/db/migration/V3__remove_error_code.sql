DELETE FROM translation_value_v2
WHERE key_id = (SELECT id FROM translation_key_v2
                WHERE code = 'ERROR_DICTIONARY.REGISTRATION.SERVER_OAUTH2');

DELETE FROM translation_key_v2
WHERE code = 'ERROR_DICTIONARY.REGISTRATION.SERVER_OAUTH2';