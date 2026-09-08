MERGE INTO T_ROLE r
    USING (
        SELECT 'TUTOR' AS role_name FROM DUAL UNION ALL
        SELECT 'VETERINARIAN' AS role_name FROM DUAL UNION ALL
        SELECT 'ADMIN' AS role_name FROM DUAL
    ) src
    ON (r.name = src.role_name)
    WHEN NOT MATCHED THEN
        INSERT (id, name) VALUES (seq_role.NEXTVAL, src.role_name);