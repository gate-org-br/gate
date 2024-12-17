DELIMITER $$
CREATE DEFINER=`davins`@`%` PROCEDURE `get_authorized_users`(
    IN p_module VARCHAR(255),
    IN p_screen VARCHAR(255),
    IN p_action VARCHAR(255)
)
BEGIN
    WITH auth_users AS (
        -- Autorização direta do usuário
        SELECT 
            Uzer.id, Uzer.username, Uzer.name, Uzer.email, 
            Auth.module, Auth.screen, Auth.action
        FROM
            gate.Auth
                JOIN
            gate.Uzer ON Auth.`Uzer$id` = Uzer.id
        
        UNION
        
        -- Autorização baseada em função
        SELECT 
            Uzer.id, Uzer.username, Uzer.name, Uzer.email,
            Auth.module, Auth.screen, Auth.action
        FROM
            gate.Auth
                JOIN
            gate.UzerFunc ON UzerFunc.`Func$id` = Auth.`Func$id`
                JOIN
            gate.Uzer ON UzerFunc.`Uzer$id` = Uzer.id
        
        UNION
        
        -- Autorização direta baseada em papel
        SELECT 
            Uzer.id, Uzer.username, Uzer.name, Uzer.email,
            Auth.module, Auth.screen, Auth.action
        FROM
            gate.Auth
                JOIN
            gate.Uzer ON Auth.`Role$id` = Uzer.`Role$id`
        
        UNION
        
        -- Autorização hierárquica baseada em papel
        SELECT 
            Uzer.id, Uzer.username, Uzer.name, Uzer.email,
            Auth.module, Auth.screen, Auth.action
        FROM
            gate.Auth
                JOIN
            gate.Uzer ON gate.isparent(Auth.`Role$id`, Uzer.`Role$id`)
        WHERE
            Auth.scope = 'PUBLIC'
        
        UNION
        
        -- Autorização baseada em papel-função
        SELECT 
            Uzer.id, Uzer.username, Uzer.name, Uzer.email,
            Auth.module, Auth.screen, Auth.action
        FROM
            gate.Auth
                JOIN
            gate.RoleFunc ON RoleFunc.`Func$id` = Auth.`Func$id`
                JOIN
            gate.Uzer ON RoleFunc.`Role$id` = Uzer.`Role$id`
        
        UNION
        
        -- Autorização hierárquica baseada em papel-função
        SELECT 
            Uzer.id, Uzer.username, Uzer.name, Uzer.email,
            Auth.module, Auth.screen, Auth.action
        FROM
            gate.Auth
                JOIN
            gate.RoleFunc ON RoleFunc.`Func$id` = Auth.`Func$id`
                JOIN
            gate.Uzer ON gate.isparent(RoleFunc.`Role$id`, Uzer.`Role$id`)
        WHERE
            Auth.scope = 'PUBLIC'
    )
    SELECT distinct id, username, name, email
    FROM auth_users
    WHERE
        (p_module IS NULL OR module IS NULL OR module = p_module)
        AND (p_screen IS NULL OR screen IS NULL OR screen = p_screen)
        AND (p_action IS NULL OR action IS NULL OR action = p_action);
END$$
DELIMITER ;

drop function if exists gate.secure;

DELIMITER $$

CREATE FUNCTION `check_access`(
    user_id INTEGER,
    p_module VARCHAR(64),
    p_screen VARCHAR(32),
    p_action VARCHAR(32)
) RETURNS BOOLEAN
    READS SQL DATA
BEGIN
    -- Check direct user authorization
    IF EXISTS (
        SELECT 1 FROM Auth 
        WHERE Auth.Uzer$id = user_id
        AND (Auth.module IS NULL OR Auth.module = p_module)
        AND (p_screen IS NULL OR Auth.screen IS NULL OR Auth.screen = p_screen)
        AND (p_action IS NULL OR Auth.action IS NULL OR Auth.action = p_action)
    ) THEN
        RETURN TRUE;
    END IF;

    -- Check user's function-based authorization
    IF EXISTS (
        SELECT 1 
        FROM Auth 
        JOIN UzerFunc ON Auth.Func$id = UzerFunc.Func$id 
        WHERE UzerFunc.Uzer$id = user_id
        AND (Auth.module IS NULL OR Auth.module = p_module)
        AND (p_screen IS NULL OR Auth.screen IS NULL OR Auth.screen = p_screen)
        AND (p_action IS NULL OR Auth.action IS NULL OR Auth.action = p_action)
    ) THEN
        RETURN TRUE;
    END IF;

    -- Check authorization through role hierarchy
    SET @current_role = (SELECT Role$id FROM Uzer WHERE id = user_id);
    
    WHILE @current_role IS NOT NULL DO
        -- Check direct role authorization
        IF EXISTS (
            SELECT 1 
            FROM Auth 
            WHERE Auth.Role$id = @current_role
            AND (Auth.module IS NULL OR Auth.module = p_module)
            AND (p_screen IS NULL OR Auth.screen IS NULL OR Auth.screen = p_screen)
            AND (p_action IS NULL OR Auth.action IS NULL OR Auth.action = p_action)
        ) THEN
            RETURN TRUE;
        END IF;

        -- Check role's function-based authorization
        IF EXISTS (
            SELECT 1 
            FROM Auth 
            JOIN RoleFunc ON Auth.Func$id = RoleFunc.Func$id 
            WHERE RoleFunc.Role$id = @current_role
            AND (Auth.module IS NULL OR Auth.module = p_module)
            AND (p_screen IS NULL OR Auth.screen IS NULL OR Auth.screen = p_screen)
            AND (p_action IS NULL OR Auth.action IS NULL OR Auth.action = p_action)
        ) THEN
            RETURN TRUE;
        END IF;

        -- Move up in the role hierarchy
        SELECT Role.Role$id 
        INTO @current_role
        FROM Role 
        WHERE Role.id = @current_role;
    END WHILE;

    RETURN FALSE;
END$$

DELIMITER ;