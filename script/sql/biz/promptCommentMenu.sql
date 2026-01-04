-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007811402336653314, '提示词评论', '2007771461162606594', '1', 'promptComment', 'business/promptComment/index', 1, 0, 'C', '0', '0', 'business:promptComment:list', '#', 103, 1, sysdate(), null, null, '提示词评论菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007811402336653315, '提示词评论查询', 2007811402336653314, '1',  '#', '', 1, 0, 'F', '0', '0', 'business:promptComment:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007811402336653316, '提示词评论新增', 2007811402336653314, '2',  '#', '', 1, 0, 'F', '0', '0', 'business:promptComment:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007811402336653317, '提示词评论修改', 2007811402336653314, '3',  '#', '', 1, 0, 'F', '0', '0', 'business:promptComment:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007811402336653318, '提示词评论删除', 2007811402336653314, '4',  '#', '', 1, 0, 'F', '0', '0', 'business:promptComment:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007811402336653319, '提示词评论导出', 2007811402336653314, '5',  '#', '', 1, 0, 'F', '0', '0', 'business:promptComment:export',       '#', 103, 1, sysdate(), null, null, '');
