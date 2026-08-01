-- 1. Cria a tabela de usuários
CREATE TABLE tb_users (
                          id UUID PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP NOT NULL
);

-- 2. Adiciona a coluna user_id na tabela de tarefas
ALTER TABLE tb_tasks ADD COLUMN user_id UUID;

-- 3. Em um cenário real com dados em produção, precisaríamos vincular as tarefas
-- existentes a um usuário padrão aqui. Como estamos em dev, podemos apagar as antigas
-- (descomente a linha abaixo se necessário) ou apenas adicionar a constraint.
DELETE FROM tb_tasks;

-- 4. Torna a coluna obrigatória e cria a chave estrangeira (Foreign Key)
ALTER TABLE tb_tasks ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE tb_tasks ADD CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES tb_users (id);