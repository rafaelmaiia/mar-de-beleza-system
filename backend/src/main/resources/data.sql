-- =================================================================
--  SCRIPT DE INICIALIZAÇÃO DE DADOS PARA O SISTEMA MAR DE BELEZA
--  (VERSÃO CORRIGIDA COM NOMES DE TABELA 'tb_*')
-- =================================================================

-- Opcional: Limpa as tabelas na ordem inversa de dependência para evitar erros de FK
DELETE FROM tb_appointment;
DELETE FROM tb_professional;
DELETE FROM tb_client;
DELETE FROM tb_contact;
DELETE FROM tb_salon_service;
DELETE FROM tb_users;


-- =============================================
--  Usuário Administrador
-- =============================================
INSERT INTO tb_users (name, email, password, role) VALUES
('Rafael Maia', 'rafaelmaia.developer@gmail.com', '$2a$10$jiyG0jSrKO6sOE.WdZWlZejNtJT6Ym.vh2jupPPO0dLNXfsBNQI8a', 'ADMIN');


-- =============================================
--  Contatos (Telefones)
-- =============================================
-- Contatos para Profissionais (IDs 1-3)
INSERT INTO tb_contact (phone, phone_is_whatsapp) VALUES
('85999887766', true),
('85987654321', true),
('85991234567', false);

-- Contatos para Clientes (IDs 4-6)
INSERT INTO tb_contact (phone, phone_is_whatsapp) VALUES
('85988776655', true),
('85996543210', true),
('85992345678', true);


-- =============================================
--  Profissionais (Professionals)
-- =============================================
INSERT INTO tb_professional (name, contact_id) VALUES
('Ana Silva', 1),
('Beatriz Costa', 2),
('Carla Dias', 3);


-- =============================================
--  Especialidades dos Profissionais
-- =============================================
INSERT INTO tb_professional_specialties (professional_id, specialty) VALUES
(1, 'HAIR'),      -- Ana Silva faz Cabelo
(1, 'EYEBROW'),   -- Ana Silva também faz Sobrancelha
(2, 'LASH'),      -- Beatriz Costa faz Cílios
(3, 'MANICURE'),  -- Carla Dias faz Manicure
(3, 'OTHER');     -- e também Outros serviços


-- =============================================
--  Serviços do Salão (SalonService)
-- =============================================
INSERT INTO tb_salon_service (name, service_type, duration_in_minutes, price) VALUES
('Design de Sobrancelha com Henna', 'EYEBROW', 45, 50.00),
('Extensão de Cílios Volume Brasileiro', 'LASH', 120, 220.00),
('Corte Feminino e Escova', 'HAIR', 90, 150.00),
('Manicure e Pedicure Simples', 'OTHER', 60, 75.00),
('Hidratação Capilar Profunda', 'HAIR', 75, 130.00);


-- =============================================
--  Clientes (Clients)
-- =============================================
INSERT INTO tb_client (name, birth_date, gender, contact_id) VALUES
('Fernanda Lima', '1995-08-20', 'FEMALE', 4),
('Gabriela Melo', '2001-04-10', 'FEMALE', 5),
('Heloísa Borges', '1989-12-01', 'OTHER', 6);


-- =============================================
--  Agendamentos (Appointments) - VERSÃO REFATORADA
-- =============================================
-- Cada linha agora representa UM serviço com UM profissional.

-- Agendamento PASSADO para o histórico da cliente 1
INSERT INTO tb_appointment (client_id, professional_id, service_id, price, appointment_date, status, observations, created_at) VALUES
(1, 1, 1, 50.00, '2025-06-15 14:00:00', 'DONE', 'Cliente adorou o resultado da sobrancelha.', '2025-06-10 11:30:00');

-- O "combo" da cliente 2 agora são DOIS agendamentos separados e sequenciais.
-- Primeiro, os cílios com a Beatriz.
INSERT INTO tb_appointment (client_id, professional_id, service_id, price, appointment_date, status, observations, created_at) VALUES
(2, 2, 2, 220.00, '2025-07-21 10:00:00', 'SCHEDULED', 'Aplicação completa de cílios.', '2025-07-14 10:00:00');

-- Em seguida, a manicure com a Carla, começando depois que os cílios terminam.
INSERT INTO tb_appointment (client_id, professional_id, service_id, price, appointment_date, status, observations, created_at) VALUES
(2, 3, 4, 75.00, '2025-07-21 12:30:00', 'SCHEDULED', 'Manicure e pedicure após os cílios.', '2025-07-14 10:05:00');

-- Agendamento para HOJE (ajustei a data para o dia de hoje, para facilitar seus testes no dashboard)
INSERT INTO tb_appointment (client_id, professional_id, service_id, price, appointment_date, status, observations, created_at) VALUES
(3, 1, 3, 150.00, '2025-07-16 16:30:00', 'CONFIRMED', 'Tolerância de 10 minutos de atraso.', '2025-07-14 11:00:00');

-- Mais um agendamento para HOJE, para a lista ficar mais interessante
INSERT INTO tb_appointment (client_id, professional_id, service_id, price, appointment_date, status, observations, created_at) VALUES
(1, 2, 1, 50.00, '2025-07-17 11:00:00', 'SCHEDULED', 'Apenas design.', '2025-07-14 12:00:00');