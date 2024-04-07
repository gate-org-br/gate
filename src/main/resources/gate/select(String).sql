SELECT 
    Uzer.id,
    Uzer.active,
    Uzer.Role$id AS 'role.id',
    Uzer.username,
    Uzer.password,
    Uzer.name,
    Uzer.code,
    Uzer.email,
    Uzer.phone,
    Uzer.cellPhone,
    Uzer.CPF,
    Uzer.sex,
    Uzer.birthdate,
    Uzer.registration,
    UzerFunc.Func$id AS 'func.id'
FROM
    gate.Uzer
        LEFT JOIN
    UzerFunc ON Uzer.id = UzerFunc.Uzer$id
WHERE
    username = ? OR email = ?