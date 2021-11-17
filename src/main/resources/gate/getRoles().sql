select
    Role.id as id,
    Role.active as active,
    Role.master as master,
    Role.Role$id as 'role.id',
    Role.rolename as rolename,
    Role.name as name,
    Role.email as email,
    Role.description as description,
    Manager.id as 'manager.id',
    Manager.name as 'manager.name'
from
    gate.Role
        left join
    gate.Uzer as Manager ON Role.Manager$id = Manager.id
order by Role.name