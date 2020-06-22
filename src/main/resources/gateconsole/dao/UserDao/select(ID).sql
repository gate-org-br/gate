SELECT
    Uzer.id AS id,
    Uzer.active AS active,
    Role.id AS 'role.id',
    Role.name AS 'role.name',
    Uzer.name AS name,
    Uzer.userID AS userID,
    Uzer.email AS email,
    Uzer.details AS details,
    Uzer.phone AS phone,
    Uzer.cellPhone AS cellPhone,
    Uzer.photo AS photo,
    Uzer.CPF AS CPF,
    Uzer.sex AS sex,
    Uzer.birthdate AS birthdate,
    Uzer.registration AS registration
FROM
    Uzer
        JOIN
    Role ON Uzer.Role$id = Role.id
WHERE
    Uzer.id = ?
