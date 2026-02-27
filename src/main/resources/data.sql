INSERT INTO users (id, name, email, senha)
VALUES ('11111111-1111-1111-1111-111111111111', 'William Abreu', 'william@email.com', 'senha123');


INSERT INTO account (id, name, type, balance, users_id)
VALUES ('22222222-2222-2222-2222-222222222222', 'Conta Corrente Santander', 'CURRENT', 5000.00, '11111111-1111-1111-1111-111111111111');

INSERT INTO account (id, name, type, balance, users_id)
VALUES ('33333333-3333-3333-3333-333333333333', 'Reserva de Emergência Inter', 'INVESTMENT', 100.00, '11111111-1111-1111-1111-111111111111');

INSERT INTO credit_card (id, name, limit_amount, available_limit, users_id)
VALUES ('44444444-4444-4444-4444-444444444444', 'Nubank Ultravioleta', 10000.00, 8000.00, '11111111-1111-1111-1111-111111111111');

INSERT INTO invoice (id, reference_month, total_amount, paid, credit_card_id)
VALUES ('55555555-5555-5555-5555-555555555555', '2026-02-01', 2000.00, false, '44444444-4444-4444-4444-444444444444');

INSERT INTO transaction (id, description, amount, type, category , date, source_account_id, destination_account_id, credit_card_id)
VALUES (random_uuid(), 'Transferência para Investimento', 500.00, 'TRANSFER','ALIMENTACAO', CURRENT_DATE,
        '22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', NULL);

INSERT INTO transaction (id, description, amount, type, category , date, source_account_id, destination_account_id, credit_card_id)
VALUES (random_uuid(), 'Pagamento Fatura Nubank', 2000.00, 'INVOICE_PAYMENT','INTERNET' , CURRENT_DATE,
        '22222222-2222-2222-2222-222222222222', NULL, '44444444-4444-4444-4444-444444444444');

INSERT INTO transaction (id, description, amount, type, category , date, source_account_id, destination_account_id, credit_card_id)
VALUES (random_uuid(), 'Jantar Restaurante Japonês', 150.00, 'CREDIT_CARD_PURCHASE','ENERGIA', CURRENT_DATE,
        NULL, NULL, '44444444-4444-4444-4444-444444444444');