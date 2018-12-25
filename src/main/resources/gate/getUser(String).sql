SELECT
    id,
    active,
    Role$id as 'role.id',
    userID,
    passwd,
    name,
    email,
    details,
    phone,
    cellPhone,
    photo,
    CPF,
    sex,
    birthdate,
    registration
FROM
    gate.Uzer
where userID = ?