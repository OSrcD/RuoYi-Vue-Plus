-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007814153535774721, '已评论', '2007771461162606594', '1', 'promptCommentComplete', 'business/promptCommentComplete/index', 1, 0, 'C', '0', '0', 'business:promptCommentComplete:list', '#', 103, 1, sysdate(), null, null, '已评论菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007814153535774722, '已评论查询', 2007814153535774721, '1',  '#', '', 1, 0, 'F', '0', '0', 'business:promptCommentComplete:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007814153535774723, '已评论新增', 2007814153535774721, '2',  '#', '', 1, 0, 'F', '0', '0', 'business:promptCommentComplete:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007814153535774724, '已评论修改', 2007814153535774721, '3',  '#', '', 1, 0, 'F', '0', '0', 'business:promptCommentComplete:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007814153535774725, '已评论删除', 2007814153535774721, '4',  '#', '', 1, 0, 'F', '0', '0', 'business:promptCommentComplete:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2007814153535774726, '已评论导出', 2007814153535774721, '5',  '#', '', 1, 0, 'F', '0', '0', 'business:promptCommentComplete:export',       '#', 103, 1, sysdate(), null, null, '');
