SELECT
    id,
    Role$id as 'role.id',
    Uzer$id as 'user.id',
    Func$id as 'func.id',
    module,
    screen,
    action,
    type,
    mode
FROM
    gate.Auth
