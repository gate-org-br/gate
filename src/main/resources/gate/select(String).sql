SELECT
    id,
    active,
    Role$id as 'role.id',
    userID,
    passwd,
    name,
    code,
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
where userID = ? or email = ?