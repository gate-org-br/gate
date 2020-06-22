select
    Role.id,
    Role.roleID as roleID,
    Role.name as name
from
    Role
        join
    RoleFunc ON Role.id = RoleFunc.Role$id
where
    RoleFunc.Func$id = ?