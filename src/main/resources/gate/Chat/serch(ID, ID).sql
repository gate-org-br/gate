SELECT 
    Chat.id,
    Sender.id AS 'sender.id',
    Sender.name AS 'sender.name',
    Receiver.id AS 'receiver.id',
    Receiver.name AS 'receiver.name',
    date,
    text
FROM
    Chat
        JOIN
    Uzer AS Sender ON Chat.Sender$id = Sender.id
        JOIN
    Uzer AS Receiver ON Chat.Receiver$id = Receiver.id
WHERE
    (Sender$id = ? AND Receiver$id = ?)
        OR (Receiver$id = ? AND Sender$id = ?)
ORDER BY date