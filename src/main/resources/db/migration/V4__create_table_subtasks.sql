CREATE TABLE tb_subtasks (
                             id UUID PRIMARY KEY,
                             task_id UUID NOT NULL,
                             title VARCHAR(100) NOT NULL,
                             completed BOOLEAN DEFAULT FALSE,
                             created_at TIMESTAMP NOT NULL,
                             CONSTRAINT fk_task_subtask FOREIGN KEY (task_id) REFERENCES tb_tasks(id) ON DELETE CASCADE
);