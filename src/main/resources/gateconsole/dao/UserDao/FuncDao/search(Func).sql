select
    Uzer.id,
    Uzer.userID as userID,
    Uzer.name as name
from
    Uzer
        join
    UzerFunc ON Uzer.id = UzerFunc.Uzer$id
where
    UzerFunc.Func$id = ?
order by Uzer.name