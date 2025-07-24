-- =================================================================
--  SCRIPT DE INICIALIZAÇÃO DE DADOS
-- =================================================================

-- Limpa as tabelas na ordem correta para evitar erros de chave estrangeira
DELETE FROM tb_appointment;
DELETE FROM tb_user_specialties;
DELETE FROM tb_users;
DELETE FROM tb_client;
DELETE FROM tb_contact;
DELETE FROM tb_salon_service;


-- =============================================
--  Contatos (Telefones)
-- =============================================
-- IDs 1, 2, 3 serão para as funcionárias. IDs 4, 5, 6 para as clientes.
INSERT INTO tb_contact (phone, phone_is_whatsapp) VALUES
('85999887766', true), ('85987654321', true), ('85991234567', false),
('85988776655', true), ('85996543210', true), ('85992345678', true);


-- =============================================
--  Usuários do Sistema (tb_users) - Funcionárias e Admins
-- =============================================
-- IDs gerados: 1 (Rafael), 2 (Ana), 3 (Beatriz), 4 (Carla)
INSERT INTO tb_users (name, email, password, role, contact_id) VALUES
('Rafael Maia', 'rafaelmaia.developer@gmail.com', '$2a$10$jiyG0jSrKO6sOE.WdZWlZejNtJT6Ym.vh2jupPPO0dLNXfsBNQI8a', 'ADMIN', NULL),
('Ana Silva', 'ana.silva@salao.com', '$2a$10$MFnIzsjNN9oi/NTwGP5y/.48bE./O/toxbWirWy32PKsa9hvSHMOa', 'STAFF', 1),
('Beatriz Costa', 'beatriz.costa@salao.com', '$2a$10$vNUmX9XkFOt82MGFE/1ooeB/MqW70.CzTQYww3.PDQkPBARqvJMZ.', 'STAFF', 2),
('Carla Dias', 'carla.dias@salao.com', '$2a$10$BTWjS34K.4UHpqBg5BnNVOqomMWlXm3KKQqGL46c8x4DJI4/0dL0i', 'STAFF', 3);


-- =============================================
--  Especialidades dos Usuários (Funcionárias)
-- =============================================
INSERT INTO tb_user_specialties (user_id, specialty) VALUES
(2, 'HAIR'),      -- Ana Silva (ID 2)
(2, 'EYEBROW'),
(3, 'LASH'),      -- Beatriz Costa (ID 3)
(4, 'MANICURE');  -- Carla Dias (ID 4)


-- =============================================
--  Serviços do Salão (SalonService)
-- =============================================
INSERT INTO tb_salon_service (name, service_type, duration_in_minutes, price) VALUES
('Design de Sobrancelha com Henna', 'EYEBROW', 45, 50.00),
('Extensão de Cílios Volume Brasileiro', 'LASH', 120, 220.00),
('Corte Feminino e Escova', 'HAIR', 90, 150.00),
('Manicure e Pedicure Simples', 'MANICURE', 60, 75.00),
('Hidratação Capilar Profunda', 'HAIR', 75, 130.00);


-- =============================================
--  Clientes (Clients)
-- =============================================
-- IDs gerados: 1 (Fernanda), 2 (Gabriela), 3 (Heloísa)
INSERT INTO tb_client (name, birth_date, gender, contact_id) VALUES
('Fernanda Lima', '1995-08-20', 'FEMALE', 4),
('Gabriela Melo', '2001-04-10', 'FEMALE', 5),
('Heloísa Borges', '1989-12-01', 'OTHER', 6);


-- =============================================
--  Agendamentos (Appointments) - VERSÃO FINAL
-- =============================================
-- Agendamentos para HOJE (23/07/2025)
INSERT INTO tb_appointment (client_id, professional_id, service_id, price, appointment_date, status, observations, created_at) VALUES
(1, 2, 1, 50.00, '2025-07-23 10:00:00', 'CONFIRMED', 'Cliente pediu para chegar 10 min antes.', '2025-07-20 11:30:00'),
(2, 3, 2, 220.00, '2025-07-23 14:30:00', 'SCHEDULED', 'Aplicação completa de cílios.', '2025-07-21 18:00:00');

-- Agendamentos para AMANHÃ (24/07/2025)
INSERT INTO tb_appointment (client_id, professional_id, service_id, price, appointment_date, status, observations, created_at) VALUES
(3, 2, 3, 150.00, '2025-07-24 11:00:00', 'SCHEDULED', 'Tolerância de 10 minutos de atraso.', '2025-07-22 09:12:00'),
(1, 4, 4, 75.00, '2025-07-24 16:00:00', 'SCHEDULED', 'Manicure e pedicure.', '2025-07-22 15:00:00');