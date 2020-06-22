select
    RoleFunc.Role$id as 'role.id',
    null as 'user.id',
    Func.id as 'func.id',
    Func.name as 'func.name'
from
    RoleFunc
        join
    Func ON RoleFunc.Func$id = Func.id
union select
    null as 'role.id',
    UzerFunc.Uzer$id as 'user.id',
    Func.id as 'func.id',
    Func.name as 'func.name'
from
    UzerFunc
        join
    Func ON UzerFunc.Func$id = Func.id