ALTER TABLE `gate`.`Server` 
ADD COLUMN `useTLS` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER `password`,
ADD COLUMN `useSSL` TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER `useTLS`,
ADD COLUMN `timeout` INT UNSIGNED NULL AFTER `useSSL`;

CREATE DEFINER=`davins`@`%` PROCEDURE `auths`(user_id INT)
BEGIN
    declare role_id int;
    drop TEMPORARY TABLE if exists resultset;
    CREATE TEMPORARY TABLE resultset ENGINE=MEMORY as 
		select 
			Auth.id,
			Auth.Uzer$id as 'user.id', 
			Auth.Role$id as 'role.id', 
			Auth.Func$id as 'func.id', 
			Auth.module, 
			Auth.screen, 
			Auth.action, 
			Auth.scope, 
			Auth.access 
        from Auth 
        where Auth.Uzer$id = user_id;
    
	insert into resultset 
    select 
			Auth.id,
			Auth.Uzer$id as 'user.id', 
			Auth.Role$id as 'role.id', 
			Auth.Func$id as 'func.id', 
			Auth.module, 
			Auth.screen, 
			Auth.action, 
			Auth.scope, 
			Auth.access 
	from Auth join UzerFunc on Auth.Func$id = UzerFunc.Func$id 
    where UzerFunc.Uzer$id = user_id;
    
    select Role$id into role_id from Uzer where id = user_id limit 1;
      WHILE role_id is not null DO
			
            insert into resultset 
            select
				Auth.id,
				Auth.Uzer$id as 'user.id', 
				Auth.Role$id as 'role.id', 
				Auth.Func$id as 'func.id', 
				Auth.module, 
				Auth.screen, 
				Auth.action, 
				Auth.scope, 
				Auth.access
			from Auth where Role$id = role_id;	
            
            insert into resultset 
            select
				Auth.id,
				Auth.Uzer$id as 'user.id', 
				Auth.Role$id as 'role.id', 
				Auth.Func$id as 'func.id', 
				Auth.module, 
				Auth.screen, 
				Auth.action, 
				Auth.scope, 
				Auth.access
			from Auth join RoleFunc on Auth.Func$id = RoleFunc.Func$id 
            where RoleFunc.Role$id = role_id;
            
            select Role$id into role_id from Role where id = role_id limit 1;
	  END WHILE;

select * from resultset;  
END