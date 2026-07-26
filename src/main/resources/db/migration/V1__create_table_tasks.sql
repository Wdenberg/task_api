CREATE TABLE tb_tasks (
                          id UUID PRIMARY KEY,
                          title VARCHAR(100) NOT NULL,
                          description VARCHAR(500),
                          status VARCHAR(50) NOT NULL,
                          due_date TIMESTAMP,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP
);