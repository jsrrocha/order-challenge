-- ==================================================================================
-- 1. PRODUTOS FÍSICOS (PHYSICAL)
-- ==================================================================================
INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('BOOK-CC-001', 'Clean Code', 'PHYSICAL', 89.90, 150, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('LAPTOP-PRO-2024', 'Laptop Pro', 'PHYSICAL', 5499.00, 8, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('LAPTOP-MBP-M3-001', 'MacBook Pro M3', 'PHYSICAL', 12999.00, 25, true, NULL)
ON CONFLICT (product_id) DO NOTHING;


-- ==================================================================================
-- 2. ASSINATURAS (SUBSCRIPTION)
-- ==================================================================================
INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('SUB-PREMIUM-001', 'Premium Monthly', 'SUBSCRIPTION', 49.90, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('SUB-BASIC-001', 'Basic Monthly', 'SUBSCRIPTION', 19.90, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('SUB-ENTERPRISE-001', 'Enterprise Plan', 'SUBSCRIPTION', 299.00, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('SUB-ADOBE-CC-001', 'Adobe Creative Cloud', 'SUBSCRIPTION', 159.00, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;


-- ==================================================================================
-- 3. PRODUTOS DIGITAIS (DIGITAL)
-- ==================================================================================
INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('EBOOK-JAVA-001', 'Effective Java', 'DIGITAL', 39.90, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('EBOOK-DDD-001', 'Domain-Driven Design', 'DIGITAL', 59.90, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('EBOOK-SWIFT-001', 'Swift Programming', 'DIGITAL', 49.90, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('COURSE-KAFKA-001', 'Kafka Mastery', 'DIGITAL', 299.00, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;


-- ==================================================================================
-- 4. PRÉ-VENDA (PRE_ORDER)
-- ==================================================================================
INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('GAME-2025-001', 'Epic Game 2025', 'PRE_ORDER', 249.90, 1000, true, '{"releaseDate": "2025-06-01"}')
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('PRE-PS6-001', 'PlayStation 6', 'PRE_ORDER', 4999.00, 500, true, '{"releaseDate": "2025-11-15"}')
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('PRE-IPHONE16-001', 'iPhone 16 Pro', 'PRE_ORDER', 7999.00, 2000, true, '{"releaseDate": "2025-09-20"}')
ON CONFLICT (product_id) DO NOTHING;


-- ==================================================================================
-- 5. CORPORATIVO (CORPORATE)
-- ==================================================================================
INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('CORP-LICENSE-ENT', 'Enterprise License', 'CORPORATE', 15000.00, NULL, true, NULL)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, name, product_type, price, stock_quantity, active, metadata)
VALUES ('CORP-CHAIR-ERG-001', 'Ergonomic Chair Bulk', 'CORPORATE', 899.00, 500, true, NULL)
ON CONFLICT (product_id) DO NOTHING;