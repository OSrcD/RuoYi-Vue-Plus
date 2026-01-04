-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007807899920777218, '自媒体账号', '2007771461162606594', '1', 'mediaAccount', 'business/mediaAccount/index', 1, 0, 'C', '0', '0', 'business:mediaAccount:list', '#', 103, 1, sysdate(), null, null, '自媒体账号菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007807899920777219, '自媒体账号查询', 2007807899920777218, '1',  '#', '', 1, 0, 'F', '0', '0', 'business:mediaAccount:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007807899920777220, '自媒体账号新增', 2007807899920777218, '2',  '#', '', 1, 0, 'F', '0', '0', 'business:mediaAccount:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007807899920777221, '自媒体账号修改', 2007807899920777218, '3',  '#', '', 1, 0, 'F', '0', '0', 'business:mediaAccount:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007807899920777222, '自媒体账号删除', 2007807899920777218, '4',  '#', '', 1, 0, 'F', '0', '0', 'business:mediaAccount:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007807899920777223, '自媒体账号导出', 2007807899920777218, '5',  '#', '', 1, 0, 'F', '0', '0', 'business:mediaAccount:export',       '#', 103, 1, sysdate(), null, null, '');
