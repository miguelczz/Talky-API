create extension if not exists "pgcrypto"; -- para gen_random_uuid()

create table if not exists users (
  id uuid primary key default gen_random_uuid(),   -- id local
  cognito_sub varchar(100) not null unique,        -- ID único de Cognito
  email varchar(255) not null unique,              -- correo asociado en Cognito
  name varchar(120),                               -- nombre del usuario
  role varchar(30) default 'student',              -- rol dentro de la app (student, teacher, admin)
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);
