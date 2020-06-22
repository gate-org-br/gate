select
    Func.id, Func.name as name
from
    Func
        join
    UzerFunc ON Func.id = UzerFunc.Func$id
where
    UzerFunc.Uzer$id = ?