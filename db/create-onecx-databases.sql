/*
Create users and databases for OneCX ai

IMPORTANT!
  This script cannot be executed at once!
  Pick up the section you need in a SQL session and execute it manually.
  The DROP/CREATE database statements must be executed separately.
*/

DROP DATABASE IF EXISTS "onecx-ai";
  DROP ROLE IF EXISTS onecx_ai;
  CREATE USER onecx_ai WITH PASSWORD 'onecx_ai';
CREATE DATABASE "onecx-ai" WITH OWNER = onecx_ai;

