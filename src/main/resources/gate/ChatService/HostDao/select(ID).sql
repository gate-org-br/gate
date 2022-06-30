SELECT 
    Uzer.id AS id,
    Uzer.name AS name,
    COALESCE(SUM(Chat.status = 'POSTED'), 0) AS unread
FROM
    gate.Uzer
        LEFT JOIN
    gate.Chat ON Chat.Receiver$id = Uzer.id
WHERE
    Uzer.id = ?