-- 高校学生请假管理系统 数据库初始化脚本
CREATE DATABASE IF NOT EXISTS leave_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE leave_management;

-- 1. 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1启用 0禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    perm_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    perm_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    description VARCHAR(200) COMMENT '权限描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 3. 角色-权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 4. 院系表
CREATE TABLE IF NOT EXISTS department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL COMMENT '院系名称',
    dept_code VARCHAR(50) NOT NULL UNIQUE COMMENT '院系编码',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院系表';

-- 5. 班级表
CREATE TABLE IF NOT EXISTS class_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    class_code VARCHAR(50) NOT NULL UNIQUE COMMENT '班级编码',
    department_id BIGINT NOT NULL COMMENT '所属院系',
    grade VARCHAR(10) COMMENT '年级',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 6. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '学号/工号',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(500) COMMENT '头像路径',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    department_id BIGINT COMMENT '所属院系',
    class_id BIGINT COMMENT '所属班级(学生)',
    status TINYINT DEFAULT 1 COMMENT '状态 1正常 0冻结',
    last_login DATETIME COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    FOREIGN KEY (department_id) REFERENCES department(id),
    FOREIGN KEY (class_id) REFERENCES class_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 7. 辅导员-班级关联表
CREATE TABLE IF NOT EXISTS counselor_class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    counselor_id BIGINT NOT NULL COMMENT '辅导员用户ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    UNIQUE KEY uk_counselor_class (counselor_id, class_id),
    FOREIGN KEY (counselor_id) REFERENCES sys_user(id),
    FOREIGN KEY (class_id) REFERENCES class_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='辅导员班级关联表';

-- 8. 请假类型表
CREATE TABLE IF NOT EXISTS leave_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL COMMENT '假别名称',
    type_code VARCHAR(50) NOT NULL UNIQUE COMMENT '假别编码',
    max_days INT DEFAULT 0 COMMENT '最大请假天数 0为不限',
    need_proof TINYINT DEFAULT 0 COMMENT '是否需要证明材料',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假类型表';

-- 9. 审批流程配置表
CREATE TABLE IF NOT EXISTS approval_flow_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    flow_name VARCHAR(100) NOT NULL COMMENT '流程名称',
    leave_type_id BIGINT COMMENT '关联假别 NULL表示通用',
    min_days INT DEFAULT 0 COMMENT '适用最小天数',
    max_days INT DEFAULT 9999 COMMENT '适用最大天数',
    department_id BIGINT COMMENT '适用院系 NULL表示全校',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (leave_type_id) REFERENCES leave_type(id),
    FOREIGN KEY (department_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程配置表';

-- 10. 审批流程节点表
CREATE TABLE IF NOT EXISTS approval_flow_node (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    flow_id BIGINT NOT NULL COMMENT '流程ID',
    node_order INT NOT NULL COMMENT '节点顺序',
    approver_role_id BIGINT NOT NULL COMMENT '审批人角色ID',
    node_name VARCHAR(100) NOT NULL COMMENT '节点名称',
    FOREIGN KEY (flow_id) REFERENCES approval_flow_config(id),
    FOREIGN KEY (approver_role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程节点表';

-- 11. 请假申请表
CREATE TABLE IF NOT EXISTS leave_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    leave_no VARCHAR(50) NOT NULL UNIQUE COMMENT '请假单号',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    leave_type_id BIGINT NOT NULL COMMENT '请假类型',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    duration DECIMAL(5,1) NOT NULL COMMENT '请假时长(天)',
    reason TEXT NOT NULL COMMENT '请假事由',
    status TINYINT DEFAULT 0 COMMENT '0待审批 1审批中 2已通过 3已驳回 4已撤销 5已销假',
    current_node_id BIGINT COMMENT '当前审批节点',
    flow_id BIGINT COMMENT '审批流程ID',
    cancel_time DATETIME COMMENT '撤销时间',
    cancel_reason VARCHAR(500) COMMENT '撤销原因',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (applicant_id) REFERENCES sys_user(id),
    FOREIGN KEY (leave_type_id) REFERENCES leave_type(id),
    FOREIGN KEY (flow_id) REFERENCES approval_flow_config(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- 12. 请假附件表
CREATE TABLE IF NOT EXISTS leave_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    leave_id BIGINT NOT NULL COMMENT '请假申请ID',
    file_name VARCHAR(200) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小',
    file_type VARCHAR(50) COMMENT '文件类型',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (leave_id) REFERENCES leave_application(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假附件表';

-- 13. 审批记录表
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    leave_id BIGINT NOT NULL COMMENT '请假申请ID',
    node_id BIGINT COMMENT '审批节点ID',
    approver_id BIGINT NOT NULL COMMENT '审批人ID',
    action TINYINT NOT NULL COMMENT '1通过 2驳回',
    comment VARCHAR(500) COMMENT '审批意见',
    approved_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    FOREIGN KEY (leave_id) REFERENCES leave_application(id),
    FOREIGN KEY (approver_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- 14. 销假申请表
CREATE TABLE IF NOT EXISTS leave_cancellation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    leave_id BIGINT NOT NULL COMMENT '请假申请ID',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    actual_return_time DATETIME NOT NULL COMMENT '实际返校时间',
    is_overdue TINYINT DEFAULT 0 COMMENT '是否超期',
    status TINYINT DEFAULT 0 COMMENT '0待审核 1已通过 2已驳回',
    reviewer_id BIGINT COMMENT '审核人',
    review_comment VARCHAR(500) COMMENT '审核意见',
    reviewed_at DATETIME COMMENT '审核时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (leave_id) REFERENCES leave_application(id),
    FOREIGN KEY (applicant_id) REFERENCES sys_user(id),
    FOREIGN KEY (reviewer_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销假申请表';

-- 15. 系统消息表
CREATE TABLE IF NOT EXISTS sys_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    receiver_id BIGINT NOT NULL COMMENT '接收人ID',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    msg_type TINYINT DEFAULT 0 COMMENT '0系统通知 1审批通知 2销假提醒 3超时提醒',
    related_id BIGINT COMMENT '关联业务ID',
    is_read TINYINT DEFAULT 0 COMMENT '0未读 1已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receiver_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统消息表';

-- 16. 销假规则配置表
CREATE TABLE IF NOT EXISTS cancellation_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    remind_hours INT DEFAULT 24 COMMENT '销假提醒(假期结束前N小时)',
    overdue_hours INT DEFAULT 48 COMMENT '超期阈值(假期结束后N小时)',
    penalty_desc VARCHAR(500) COMMENT '超期处罚说明',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销假规则配置表';

-- 初始数据
INSERT INTO sys_role (role_name, role_code, description) VALUES
('学生', 'STUDENT', '学生角色'),
('辅导员', 'COUNSELOR', '辅导员/教师角色'),
('院系管理员', 'DEPT_ADMIN', '院系管理员角色'),
('教务管理员', 'ADMIN', '系统管理员角色');

INSERT INTO leave_type (type_name, type_code, max_days, need_proof, description) VALUES
('事假', 'PERSONAL', 7, 0, '因个人事务请假'),
('病假', 'SICK', 30, 1, '因疾病请假，需提供病历证明'),
('婚假', 'MARRIAGE', 15, 1, '因结婚请假，需提供结婚证明'),
('实习假', 'INTERNSHIP', 180, 1, '因实习请假，需提供实习证明'),
('丧假', 'FUNERAL', 7, 1, '因丧事请假'),
('其他', 'OTHER', 3, 0, '其他类型请假');

INSERT INTO department (dept_name, dept_code) VALUES
('计算机科学与技术学院', 'CS'),
('电子信息工程学院', 'EE'),
('机械工程学院', 'ME'),
('文学院', 'LA');

INSERT INTO class_info (class_name, class_code, department_id, grade) VALUES
('计科2201', 'CS2201', 1, '2022'),
('计科2202', 'CS2202', 1, '2022'),
('计科2301', 'CS2301', 1, '2023'),
('计科2302', 'CS2302', 1, '2023'),
('电信2201', 'EE2201', 2, '2022'),
('电信2202', 'EE2202', 2, '2022'),
('电信2301', 'EE2301', 2, '2023'),
('电信2302', 'EE2302', 2, '2023'),
('机械2201', 'ME2201', 3, '2022'),
('机械2202', 'ME2202', 3, '2022'),
('机械2301', 'ME2301', 3, '2023'),
('文学2201', 'LA2201', 4, '2022'),
('文学2202', 'LA2202', 4, '2022'),
('文学2301', 'LA2301', 4, '2023');

-- 默认管理员账号 密码: admin123
INSERT INTO sys_user (username, password, real_name, gender, role_id, status) VALUES
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PQm1z0lMwGmSIEgNS1yWe', '系统管理员', 1, 4, 1);

-- 默认审批流程 (7天内辅导员审批，7天以上需教务审批)
INSERT INTO approval_flow_config (flow_name, leave_type_id, min_days, max_days) VALUES
('短期请假审批(7天内)', NULL, 0, 7),
('长期请假审批(7天以上)', NULL, 8, 9999);

INSERT INTO approval_flow_node (flow_id, node_order, approver_role_id, node_name) VALUES
(1, 1, 2, '辅导员审批'),
(2, 1, 2, '辅导员审批'),
(2, 2, 4, '教务管理员审批');

-- 默认销假规则
INSERT INTO cancellation_rule (remind_hours, overdue_hours, penalty_desc) VALUES
(24, 48, '超期未销假将记入考勤异常记录');
