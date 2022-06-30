SELECT 
    Chat.id,
    Sender.id AS 'sender.id',
    Sender.name AS 'sender.name',
    Receiver.id AS 'receiver.id',
    Receiver.name AS 'receiver.name',
    Chat.date,
    Chat.text,
    Chat.status
FROM
    Chat
        JOIN
    Uzer AS Sender ON Chat.Sender$id = Sender.id
        JOIN
    Uzer AS Receiver ON Chat.Receiver$id = Receiver.id
WHERE
    (Chat.Sender$id = ? AND Chat.Receiver$id = ?)
        OR (Chat.Receiver$id = ? AND Chat.Sender$id = ?)
ORDER BY date