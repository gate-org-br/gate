SELECT 
    Uzer.id AS id,
    Uzer.active AS active,
    Uzer.name AS name,
    Uzer.username,
    Uzer.password,
    Uzer.email AS email,
    Uzer.Role$id AS 'role.id',
    NULL AS 'auth.id',
    NULL AS 'auth.module',
    NULL AS 'auth.screen',
    NULL AS 'auth.action',
    NULL AS 'auth.access',
    NULL AS 'auth.scope'
FROM
    gate.Uzer
WHERE
    Uzer.id = ? 
UNION SELECT 
    Auth.Uzer$id,
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
    gate.Auth
WHERE
    Auth.Uzer$id = ? 
UNION SELECT 
    UzerFunc.Uzer$id,
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
    Auth
        JOIN
    UzerFunc ON UzerFunc.Func$id = Auth.Func$id
WHERE
    UzerFunc.Uzer$id = ?