CREATE USER demo WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  encrypted password 'demo';

CREATE SCHEMA demo
    AUTHORIZATION demo;


