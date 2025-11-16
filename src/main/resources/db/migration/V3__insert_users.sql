--Insert few dafult users into users table
INSERT INTO public.users (name, email, password, role, is_active) VALUES
  ('Tarun', 'tarun@example.com', '$2a$10$1V.uMQdk69bvcBzuvP3/eusY5qq39QYGcaKIeh98/N05TAvVPY2cK', 'VENDOR', true),
  ('Ruby', 'ruby@example.com', '$2a$10$AeDjle88f/voYrw2Ah6EwOU/E9AP5R2ZeCH7xz2//S7NI.w3Vz5TC', 'ADMIN', true),
  ('Ishu', 'ishu@example.com', '$2a$10$bgdCgzrh5k6Gg/uzss42fu.YlIbgayWJJBOsspKTIMmqURJT7jYGC', 'BUYER', true);
 Update the sequence to the max id in the users table
 SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
INSERT INTO public.users (name, email, password, role, is_active) VALUES
  ('Roshan', 'roshan@example.com', '$2a$10$1V.uMQdk69bvcBzuvP3/eusY5qq39QYGcaKIeh98/N05TAvVPY2cK', 'VENDOR', true);

