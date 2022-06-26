SELECT 
    Uzer.id AS id,
    Uzer.status AS status,
    Uzer.name AS name,
    COUNT(Chat.id) AS unread
FROM
    Uzer
        LEFT JOIN
    Chat ON Chat.Sender$id = Uzer.id
        AND Chat.Receiver$id = ?
        AND Chat.status = 'POSTED'
WHERE
    Uzer.id <> ?
        AND Uzer.status <> 'INACTIVE'
GROUP BY Uzer.id
ORDER BY Uzer.name
