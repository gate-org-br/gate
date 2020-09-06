SELECT 
    1 AS tipo, id, userID AS entityID, name
FROM
    Uzer
WHERE
    userID = ? OR email = ? OR name LIKE ?
UNION SELECT 
    2 AS tipo, id, roleID AS entityID, name
FROM
    Role
WHERE
    roleID = ? OR email = ? OR name LIKE ?