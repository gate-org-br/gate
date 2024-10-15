SELECT 
    Uzer.id AS id,
    Uzer.active AS active,
    Uzer.name AS name,
    Uzer.username,
    Uzer.password,
    Uzer.email AS email,
    Uzer.Role$id AS "role.id",
    NULL AS "auth.id",
    NULL AS "auth.module",
    NULL AS "auth.screen",
    NULL AS "auth.action",
    NULL AS "auth.access",
    NULL AS "auth.scope"
FROM
    gate.Uzer
WHERE
    Uzer.username = ? OR Uzer.email = ?
UNION SELECT 
    Uzer.id,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    Auth.id,
    Auth.module,
    Auth.screen,
    Auth.action,
    Auth.access,
    Auth.scope
FROM
    gate.Uzer
        LEFT JOIN
    Auth ON Uzer.id = Auth.Uzer$id
WHERE
   Uzer.username = ? OR Uzer.email = ?
UNION SELECT 
    Uzer.id AS id,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    Auth.id,
    Auth.module,
    Auth.screen,
    Auth.action,
    Auth.access,
    Auth.scope
FROM
    gate.Uzer
        JOIN
    UzerFunc ON Uzer.id = UzerFunc.Uzer$id
        JOIN
    Auth ON UzerFunc.Func$id = Auth.Func$id
WHERE
    Uzer.username = ? OR Uzer.email = ?