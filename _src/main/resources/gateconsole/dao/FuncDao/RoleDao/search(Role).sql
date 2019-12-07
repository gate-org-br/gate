select
    Func.id, Func.name as name
from
    Func
        join
    RoleFunc ON Func.id = RoleFunc.Func$id
where
    RoleFunc.Role$id = ?