MERGE INTO TB_VITALIA_SPECIES S
    USING (
        SELECT 'Cachorro' AS NAME FROM DUAL
        UNION ALL
        SELECT 'Gato' FROM DUAL
    ) SRC
    ON (UPPER(S.NAME) = UPPER(SRC.NAME))
    WHEN NOT MATCHED THEN
        INSERT (NAME)
            VALUES (SRC.NAME);


MERGE INTO TB_VITALIA_BREED B
    USING (
        SELECT
            S.SPECIES_ID,
            'Yorkshire' AS NAME
        FROM TB_VITALIA_SPECIES S
        WHERE UPPER(S.NAME) = 'CACHORRO'

        UNION ALL

        SELECT
            S.SPECIES_ID,
            'Labrador'
        FROM TB_VITALIA_SPECIES S
        WHERE UPPER(S.NAME) = 'CACHORRO'

        UNION ALL

        SELECT
            S.SPECIES_ID,
            'Golden Retriever'
        FROM TB_VITALIA_SPECIES S
        WHERE UPPER(S.NAME) = 'CACHORRO'

        UNION ALL

        SELECT
            S.SPECIES_ID,
            'Poodle'
        FROM TB_VITALIA_SPECIES S
        WHERE UPPER(S.NAME) = 'CACHORRO'

        UNION ALL

        SELECT
            S.SPECIES_ID,
            'Persa'
        FROM TB_VITALIA_SPECIES S
        WHERE UPPER(S.NAME) = 'GATO'

        UNION ALL

        SELECT
            S.SPECIES_ID,
            'Siamês'
        FROM TB_VITALIA_SPECIES S
        WHERE UPPER(S.NAME) = 'GATO'

        UNION ALL

        SELECT
            S.SPECIES_ID,
            'Maine Coon'
        FROM TB_VITALIA_SPECIES S
        WHERE UPPER(S.NAME) = 'GATO'
    ) SRC
    ON (
        B.SPECIES_ID = SRC.SPECIES_ID
            AND UPPER(B.NAME) = UPPER(SRC.NAME)
        )
    WHEN NOT MATCHED THEN
        INSERT (
                SPECIES_ID,
                NAME
            )
            VALUES (
                       SRC.SPECIES_ID,
                       SRC.NAME
                   );