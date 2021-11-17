SELECT
    id,
    active,
    Role$id as 'role.id',
    username,
    password,
    name,
    code,
    email,
    description,
    phone,
    cellPhone,
    photo,
    CPF,
    sex,
    birthdate,
    registration
FROM
    gate.Uzer
where username = ? or email = ?