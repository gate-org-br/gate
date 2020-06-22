SELECT 
    1 AS tipo, id, userID AS entityID, name
FROM
    Uzer
WHERE
    userID = ? OR name LIKE ?
UNION SELECT 
    2 AS tipo, id, roleID AS entityID, name
FROM
    Role
WHERE
    roleID = ? OR name LIKE ?