-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007805356482248706, '提示词模板', '2007771461162606594', '1', 'promptTemplate', 'business/promptTemplate/index', 1, 0, 'C', '0', '0', 'business:promptTemplate:list', '#', 103, 1, sysdate(), null, null, '提示词模板菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007805356482248707, '提示词模板查询', 2007805356482248706, '1',  '#', '', 1, 0, 'F', '0', '0', 'business:promptTemplate:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007805356482248708, '提示词模板新增', 2007805356482248706, '2',  '#', '', 1, 0, 'F', '0', '0', 'business:promptTemplate:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007805356482248709, '提示词模板修改', 2007805356482248706, '3',  '#', '', 1, 0, 'F', '0', '0', 'business:promptTemplate:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007805356482248710, '提示词模板删除', 2007805356482248706, '4',  '#', '', 1, 0, 'F', '0', '0', 'business:promptTemplate:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007805356482248711, '提示词模板导出', 2007805356482248706, '5',  '#', '', 1, 0, 'F', '0', '0', 'business:promptTemplate:export',       '#', 103, 1, sysdate(), null, null, '');
