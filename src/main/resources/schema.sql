

-- public.users definition

-- Drop table

-- DROP TABLE public.users;

-- DROP SCHEMA public;

CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION postgres;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE IF NOT EXISTS public.users (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	first_name text NOT NULL,
	last_name text NOT NULL,
	"document" text NOT NULL,
	user_type text NOT NULL,
	email text NOT NULL,
	balance numeric(15, 2) NULL,
	"password" text NOT NULL,
	created_at timestamptz NOT NULL,
	updated_at timestamptz NULL,
	CONSTRAINT users_document_key UNIQUE (document),
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_users_document ON public.users USING btree (document);
CREATE INDEX IF NOT EXISTS idx_users_email ON public.users USING btree (email);

-- public.transactions definition

-- Drop table

-- DROP TABLE public.transactions;

CREATE TABLE IF NOT EXISTS public.transactions (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	sender_id uuid NOT NULL,
	receiver_id uuid NOT NULL,
	amount numeric(15, 2) NULL,
	created_at timestamptz NOT NULL,
	updated_at timestamptz NULL,
	CONSTRAINT transactions_pkey PRIMARY KEY (id)
);