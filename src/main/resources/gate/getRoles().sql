SELECT 
    *
FROM
    (SELECT 
        Role.id AS id,
            Role.name,
            Role.rolename,
            Role.email,
            Role.active,
            Role.master,
            Role.Role$id AS 'role.id',
            Manager.id AS 'manager.id',
            Manager.name AS 'manager.name',
            NULL AS 'auth.id',
            NULL AS 'auth.module',
            NULL AS 'auth.screen',
            NULL AS 'auth.action',
            NULL AS 'auth.access',
            NULL AS 'auth.scope'
    FROM
        gate.Role
    LEFT JOIN gate.Uzer AS Manager ON Role.Manager$id = Manager.id UNION SELECT 
        Role.id AS id,
            NULL,
            NULL,
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
        gate.Role
    JOIN Auth ON Role.id = Auth.Role$id UNION SELECT 
        Role.id AS id,
            NULL,
            NULL,
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
        gate.Role
    JOIN RoleFunc ON Role.id = RoleFunc.Role$id
    JOIN Auth ON RoleFunc.Func$id = Auth.Func$id) AS Roles
ORDER BY id , rolename IS NULL