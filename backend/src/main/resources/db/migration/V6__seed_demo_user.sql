-- The demo account behind the "Try with Demo Account" button on the login page.
-- Credentials: demo@moneta.app / demo1234
--
-- The password column stores a BCrypt hash, never the raw password. This hash was
-- produced by the same BCryptPasswordEncoder the app uses (strength 10), so
-- passwordEncoder.matches("demo1234", password) succeeds at login.
INSERT INTO users (username, email, password, preferred_currency) VALUES
    ('Demo User',
     'demo@moneta.app',
     '$2a$10$2Loq9TD9KjyD5Cd/HCXCdeaHy0j0Cg6wiSdWJPjPH0j7GmHd5YFji',
     'AUD')
-- Skip quietly if someone already registered that email by hand.
ON CONFLICT (email) DO NOTHING;
