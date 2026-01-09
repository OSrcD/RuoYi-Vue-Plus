create table biz_prompt_template
(
    prompt_id                bigint           not null comment '提示词ID'
        primary key,
    template   varchar(2048)    null comment '提示词模板',
    template_type int(2)        default 0        null comment '提示词分类（0棋牌 1对象）',
    remark            varchar(200)     null comment '备注',
    tenant_id         varchar(20)      not null comment '租户编号',
    version     int         default 0        null comment '版本',
    create_dept       bigint           null comment '创建部门',
    create_by         bigint           null comment '创建者',
    create_time       datetime         null comment '创建时间',
    update_by         bigint           null comment '更新者',
    update_time       datetime         null comment '更新时间',
    del_flag          char default '0' null comment '删除标志（0代表存在 1代表删除）'
)
    comment '提示词模板表';


create table biz_prompt_comment
(
    comment_id                bigint           not null comment '提示词评论ID'
        primary key,
    prompt_id                bigint           not null comment '提示词模板ID',
    comment_content   varchar(500)    not null unique comment '提示词评论内容',
    remark            varchar(200)     null comment '备注',
    tenant_id         varchar(20)      not null comment '租户编号',
    version     int         default 0        null comment '版本',
    create_dept       bigint           null comment '创建部门',
    create_by         bigint           null comment '创建者',
    create_time       datetime         null comment '创建时间',
    update_by         bigint           null comment '更新者',
    update_time       datetime         null comment '更新时间',
    del_flag          char default '0' null comment '删除标志（0代表存在 1代表删除）'
)
    comment '提示词评论表';


create table biz_prompt_comment_complete
(
    comment_complete_id                bigint           not null comment '已评论ID'
        primary key,
    comment_id                bigint           not null comment '评论ID',
    media_account_id bigint           not null comment '自媒体账号ID',
    remark            varchar(200)     null comment '备注',
    tenant_id         varchar(20)      not null comment '租户编号',
    version     int         default 0        null comment '版本',
    create_dept       bigint           null comment '创建部门',
    create_by         bigint           null comment '创建者',
    create_time       datetime         null comment '创建时间',
    update_by         bigint           null comment '更新者',
    update_time       datetime         null comment '更新时间',
    del_flag          char default '0' null comment '删除标志（0代表存在 1代表删除）'
)
    comment '已评论表';





create table biz_media_account
(
    id  bigint         not null comment '自媒体账号ID'        primary key,
    account_id       varchar(40)         not null comment '账号ID',
    account_name     varchar(64)    not null comment '账号名称',
    account_platform int(2)         default 0  not null comment '平台（0小红书 1抖音 2快手 3闲鱼 4视频号 5B站 6其他）',
    account_type     int(2)         default 0  null comment '账号类型（0个人 1企业 2机构 3其他）',
    account_url      varchar(500)   null comment '账号主页链接',
    follower_count   int            default 0  null comment '粉丝数',
    status           int(2)         default 1  null comment '状态（0停用 1启用 2封禁）',
    remark           varchar(500)   null comment '账号描述',
    tenant_id        varchar(20)    not null comment '租户编号',
    version          int            default 0  null comment '版本',
    create_dept      bigint         null comment '创建部门',
    create_by        bigint         null comment '创建者',
    create_time      datetime       null comment '创建时间',
    update_by        bigint         null comment '更新者',
    update_time      datetime       null comment '更新时间',
    del_flag         char default '0' null comment '删除标志（0代表存在 1代表删除）'
)
    comment '自媒体账号表';

alter table biz_media_account
    add phone_number varchar(11) default '' null comment '手机号码' after account_url;


INSERT INTO `ry-vue`.biz_media_account (id, account_id, account_name, account_platform, account_type, account_url, phone_number, follower_count, status, remark, tenant_id, version, create_dept, create_by, create_time, update_by, update_time, del_flag) VALUES (2007793238983045121, '4130194161', '王小帅', 0, 0, null, '', 0, 0, null, '000000', 0, 103, 1, '2026-01-04 20:36:26', 1, '2026-01-04 20:51:19', '0');
INSERT INTO `ry-vue`.biz_media_account (id, account_id, account_name, account_platform, account_type, account_url, phone_number, follower_count, status, remark, tenant_id, version, create_dept, create_by, create_time, update_by, update_time, del_flag) VALUES (2008123477344796674, '8943177125', '全栈程序员阿志', 0, 0, null, '', 0, 0, null, '000000', 0, 103, 1, '2026-01-05 18:28:41', 1, '2026-01-05 18:28:41', '0');


INSERT INTO `ry-vue`.biz_prompt_template (prompt_id, template, template_type, remark, tenant_id, version, create_dept, create_by, create_time, update_by, update_time, del_flag) VALUES (2007841375655260161, '# Role 你是一名深谙小红书风控机制的 NLP 对抗工程师。你的任务是生成 100 条关于“提供无人棋牌室系统搭建服务”的评论，通过破坏文本的统计特征，绕过平台的语义聚类审查。

# Task 生成 100 条内容。核心意图是表达“我有技术/源码/方案，能帮人搭建无人自助棋牌室系统”。

# 第一步：核心长词“动态混淆”（防聚类关键） 虽然必须表达“无人自助棋牌室系统搭建”，但严禁 100 条文案都使用完全相同的字符排列。请通过以下方式破坏长词特征：

位置平移：将“搭建”放在句首、句中、句末循环切换。

词组拆解：在长词中间随机插入语气词（如：无人自助的棋牌室系统、搭建一套棋牌室系统（无人自助版））。

# 第二步：对抗性结构约束（风控红线）

句法结构扰动：

30% 碎片化：字数控制在 8 字以内，像真人随手打字（如：这套系统，无人搭建，稳）。

40% 结果导向：强调“弄好了”、“跑通了”、“已出”。

30% 倒装/特殊句式：将宾语前置（如：无人棋牌室系统搭建，我这有现成的）。

Jaccard 相似度控制：任意相邻 5 条文案的词频重合度必须低于 20%。

禁止项：严禁数字、拼音、微信号、问号、以及任何“官方感”的词汇（如“竭诚为您服务”）。

# 第三步：相似度自我审计

计算 100 条文案的平均余弦相似度（Cosine Similarity）预测值。

评估文案的“真人感”打分。

# Output Format (JSON Only)
要求：
1. 只输出一个 JSON 对象，不要数组。
2. 不要使用 Markdown 代码块。
3. 不要有任何额外文字或解释。
4. 格式必须为：{"highSimilarityPrediction": "xx%", "commentList": ["文案1", "文案2", ...]}', 0, null, '000000', 0, 103, 1, '2026-01-04 23:47:43', 1, '2026-01-05 02:47:30', '0');
INSERT INTO `ry-vue`.biz_prompt_template (prompt_id, template, template_type, remark, tenant_id, version, create_dept, create_by, create_time, update_by, update_time, del_flag) VALUES (2008142051283189762, '# Role 你是一名深谙小红书风控机制的 NLP 对抗工程师。你的任务是生成 100 条关于“广州找个对象”的评论，通过破坏文本的统计特征，绕过平台的语义聚类审查。

# Task 生成 100 条内容。核心意图是表达“广州找个对象”。

# 第一步：核心长词“动态混淆”（防聚类关键） 必须包含“广州找个对象”这几个文字，但严禁 100 条文案都使用完全相同的字符排列。请通过以下方式破坏长词特征：

位置平移： 句末循环切换。

词组拆解：在长词中间随机插入语气词

# 第二步：对抗性结构约束（风控红线）

句法结构扰动：

30% 碎片化：字数控制在 8 字以内，像真人随手打字（如：找个广州的对象）。

40% 结果导向

30% 倒装/特殊句式

Jaccard 相似度控制：任意相邻 5 条文案的词频重合度必须低于 20%。

禁止项：严禁数字、拼音、微信号、问号、约、以及任何“官方感”的词汇（如“竭诚为您服务”）。

# 第三步：相似度自我审计

计算 100 条文案的平均余弦相似度（Cosine Similarity）预测值。

评估文案的“真人感”打分。

# Output Format (JSON Only)
要求：
1. 只输出一个 JSON 对象，不要数组。
2. 不要使用 Markdown 代码块。
3. 不要有任何额外文字或解释。
4. 格式必须为：{"highSimilarityPrediction": "xx%", "commentList": ["文案1", "文案2", ...]}', 1, null, '000000', 0, 103, 1, '2026-01-05 19:42:29', 1, '2026-01-05 20:33:10', '0');

INSERT INTO `ry-vue`.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007781825916936193, '000000', '自媒体平台类型', 'biz_account_platform', 103, 1, '2026-01-04 19:51:05', 1, '2026-01-04 19:51:18', '自媒体平台类型');
INSERT INTO `ry-vue`.sys_dict_type (dict_id, tenant_id, dict_name, dict_type, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007802740067024897, '000000', '提示词模板分类', 'biz_prompt_template_type', 103, 1, '2026-01-04 21:14:11', 1, '2026-01-04 21:14:27', '提示词模板分类');


INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007782763633287170, '000000', 0, '小红书', '0', 'biz_account_platform', '', 'primary', 'N', 103, 1, '2026-01-04 19:54:48', 1, '2026-01-04 19:54:48', '小红书');
INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007782858093207553, '000000', 0, '抖音', '1', 'biz_account_platform', '', 'primary', 'N', 103, 1, '2026-01-04 19:55:11', 1, '2026-01-04 19:55:17', '抖音');
INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007782965215731714, '000000', 0, '快手', '2', 'biz_account_platform', '', 'primary', 'N', 103, 1, '2026-01-04 19:55:36', 1, '2026-01-04 19:55:36', '快手');
INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007783009692131330, '000000', 0, '闲鱼', '3', 'biz_account_platform', '', 'primary', 'N', 103, 1, '2026-01-04 19:55:47', 1, '2026-01-04 19:56:26', '闲鱼');
INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007783106521833473, '000000', 0, '视频号', '4', 'biz_account_platform', '', 'primary', 'N', 103, 1, '2026-01-04 19:56:10', 1, '2026-01-04 19:56:10', '视频号');
INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007783355030151169, '000000', 0, 'B站', '5', 'biz_account_platform', '', 'primary', 'N', 103, 1, '2026-01-04 19:57:09', 1, '2026-01-04 19:57:09', 'B站');
INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007783434273136641, '000000', 0, '其他', '6', 'biz_account_platform', '', 'primary', 'N', 103, 1, '2026-01-04 19:57:28', 1, '2026-01-04 19:57:38', '其他');
INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007803012877139969, '000000', 0, '棋牌', '0', 'biz_prompt_template_type', '', 'primary', 'N', 103, 1, '2026-01-04 21:15:16', 1, '2026-01-04 21:15:16', '');
INSERT INTO `ry-vue`.sys_dict_data (dict_code, tenant_id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (2007803073652604929, '000000', 0, '对象', '1', 'biz_prompt_template_type', '', 'primary', 'N', 103, 1, '2026-01-04 21:15:31', 1, '2026-01-04 21:15:31', '');



alter table biz_prompt_comment
    add title varchar(128) null comment '标题' after prompt_id;

alter table biz_prompt_template
    add status int default 1 null comment '状态（0停用 1启用 2封禁）' after template_type;

alter table biz_prompt_comment
    add constraint biz_prompt_comment_pk
        unique (title);



alter table biz_prompt_comment
    add operate_group_id bigint null comment '操作分组ID' after prompt_id;
