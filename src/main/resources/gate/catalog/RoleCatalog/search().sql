SELECT *
FROM (SELECT gate.Role.id        AS id,
			 gate.Role.name      AS name,
			 gate.Role.rolename  AS rolename,
			 gate.Role.email     AS email,
			 gate.Role.active    AS active,
			 gate.Role.master    AS master,
			 gate.Role.`Role$id` AS `role.id`,
			 Manager.id          AS `manager.id`,
			 Manager.name        AS `manager.name`,
			 NULL                AS `auth.id`,
			 NULL                AS `auth.module`,
			 NULL                AS `auth.screen`,
			 NULL                AS `auth.action`,
			 NULL                AS `auth.access`,
			 NULL                AS `auth.scope`,
			 0                   AS ord
	  FROM gate.Role
			   LEFT JOIN gate.Uzer AS Manager ON gate.Role.`Manager$id` = Manager.id

	  UNION ALL

	  SELECT gate.Role.id     AS id,
			 NULL             AS name,
			 NULL             AS rolename,
			 NULL             AS email,
			 NULL             AS active,
			 NULL             AS master,
			 NULL             AS `role.id`,
			 NULL             AS `manager.id`,
			 NULL             AS `manager.name`,
			 gate.Auth.id     AS `auth.id`,
			 gate.Auth.module AS `auth.module`,
			 gate.Auth.screen AS `auth.screen`,
			 gate.Auth.action AS `auth.action`,
			 gate.Auth.access AS `auth.access`,
			 gate.Auth.scope  AS `auth.scope`,
			 1                AS ord
	  FROM gate.Role
			   JOIN gate.Auth ON gate.Role.id = gate.Auth.`Role$id`

	  UNION ALL

	  SELECT gate.Role.id     AS id,
			 NULL             AS name,
			 NULL             AS rolename,
			 NULL             AS email,
			 NULL             AS active,
			 NULL             AS master,
			 NULL             AS `role.id`,
			 NULL             AS `manager.id`,
			 NULL             AS `manager.name`,
			 gate.Auth.id     AS `auth.id`,
			 gate.Auth.module AS `auth.module`,
			 gate.Auth.screen AS `auth.screen`,
			 gate.Auth.action AS `auth.action`,
			 gate.Auth.access AS `auth.access`,
			 gate.Auth.scope  AS `auth.scope`,
			 2                AS ord
	  FROM gate.Role
			   JOIN gate.RoleFunc ON gate.Role.id = gate.RoleFunc.`Role$id`
			   JOIN gate.Auth ON gate.RoleFunc.`Func$id` = gate.Auth.`Func$id`) AS Roles
ORDER BY Roles.id,
		 Roles.ord