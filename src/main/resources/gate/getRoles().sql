SELECT 
    Role.id AS id,
    Role.active AS active,
    Role.master AS master,
    Role.Role$id AS 'role.id',
    Role.rolename AS rolename,
    Role.name AS name,
    Role.email AS email,
    Role.description AS description,
    Manager.id AS 'manager.id',
    Manager.name AS 'manager.name',
    RoleFunc.Func$id as 'func.id'
FROM
    gate.Role
        LEFT JOIN
    gate.Uzer AS Manager ON Role.Manager$id = Manager.id
        LEFT JOIN
    RoleFunc ON Role.id = RoleFunc.Role$id
ORDER BY Role.id