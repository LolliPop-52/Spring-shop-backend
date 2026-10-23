--
-- PostgreSQL database dump
--

\restrict in1h8CDSefLLb8dZWSbnLjvSCHqoYDykx3HfLLKHPjqvPVapzhMlBK3FJM2pdAm

-- Dumped from database version 18.6 (Ubuntu 18.6-0ubuntu0.26.04.1)
-- Dumped by pg_dump version 18.6 (Ubuntu 18.6-0ubuntu0.26.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: bucket_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bucket_items (
    id bigint NOT NULL,
    product_id bigint NOT NULL,
    bucket_id bigint,
    amount numeric(38,2) NOT NULL
);


ALTER TABLE public.bucket_items OWNER TO postgres;

--
-- Name: bucket_items_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bucket_items_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bucket_items_seq OWNER TO postgres;

--
-- Name: bucket_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bucket_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bucket_seq OWNER TO postgres;

--
-- Name: buckets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.buckets (
    id bigint NOT NULL,
    user_id bigint
);


ALTER TABLE public.buckets OWNER TO postgres;

--
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    id bigint NOT NULL,
    title character varying(255) NOT NULL
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- Name: category_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.category_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.category_seq OWNER TO postgres;

--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- Name: order_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_details (
    id bigint NOT NULL,
    order_id bigint,
    product_id bigint,
    amount numeric(38,2) NOT NULL,
    total_price numeric(38,2),
    delivery_status character varying(255),
    payment_type character varying(255),
    created_time timestamp(6) without time zone,
    payment_status character varying(255),
    CONSTRAINT order_details_payment_status_check CHECK (((payment_status)::text = ANY ((ARRAY['PAID'::character varying, 'UNPAID'::character varying])::text[])))
);


ALTER TABLE public.order_details OWNER TO postgres;

--
-- Name: order_details_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.order_details_seq OWNER TO postgres;

--
-- Name: order_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.order_seq OWNER TO postgres;

--
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orders (
    id bigint NOT NULL,
    user_id bigint,
    pickup_point_id bigint NOT NULL,
    created_time timestamp without time zone,
    updated_time timestamp without time zone NOT NULL,
    total_price numeric(38,2),
    total_amount numeric(38,2),
    delivery_status character varying(255),
    payment_status character varying(255),
    CONSTRAINT orders_delivery_status_check CHECK (((delivery_status)::text = ANY ((ARRAY['PROCESSING'::character varying, 'SHIPPED'::character varying, 'ARRIVED'::character varying, 'RECEIVED'::character varying, 'CANCELLED'::character varying])::text[]))),
    CONSTRAINT orders_payment_status_check CHECK (((payment_status)::text = ANY ((ARRAY['PAID'::character varying, 'UNPAID'::character varying])::text[])))
);


ALTER TABLE public.orders OWNER TO postgres;

--
-- Name: pickup_point_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pickup_point_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pickup_point_seq OWNER TO postgres;

--
-- Name: pickup_points; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pickup_points (
    id bigint NOT NULL,
    address character varying(255) NOT NULL,
    opening_time time without time zone,
    closing_time time without time zone
);


ALTER TABLE public.pickup_points OWNER TO postgres;

--
-- Name: product_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_seq OWNER TO postgres;

--
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products (
    id bigint NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(3000),
    price numeric(38,2) NOT NULL,
    image_url character varying(255) NOT NULL
);


ALTER TABLE public.products OWNER TO postgres;

--
-- Name: products_categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products_categories (
    category_id bigint NOT NULL,
    product_id bigint NOT NULL
);


ALTER TABLE public.products_categories OWNER TO postgres;

--
-- Name: user_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_seq OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    role character varying(255),
    enabled boolean NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: verification_token; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.verification_token (
    id bigint NOT NULL,
    expiry_date timestamp(6) without time zone,
    token character varying(255),
    user_id bigint NOT NULL
);


ALTER TABLE public.verification_token OWNER TO postgres;

--
-- Name: verification_token_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.verification_token ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.verification_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Data for Name: bucket_items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bucket_items (id, product_id, bucket_id, amount) FROM stdin;
\.


--
-- Data for Name: buckets; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.buckets (id, user_id) FROM stdin;
\.


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categories (id, title) FROM stdin;
1	гитара
2	ibanez
3	yamaha
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	init	SQL	V1__init.sql	1303394684	postgres	2026-09-28 18:04:12.547714	13	t
2	2	add user	SQL	V2__add_user.sql	0	postgres	2026-09-28 18:04:12.577522	0	t
3	3	add products	SQL	V3__add_products.sql	0	postgres	2026-09-28 18:04:12.583344	0	t
\.


--
-- Data for Name: order_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_details (id, order_id, product_id, amount, total_price, delivery_status, payment_type, created_time, payment_status) FROM stdin;
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.orders (id, user_id, pickup_point_id, created_time, updated_time, total_price, total_amount, delivery_status, payment_status) FROM stdin;
\.


--
-- Data for Name: pickup_points; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pickup_points (id, address, opening_time, closing_time) FROM stdin;
1	ул. Пролетарская 150	10:00:00	19:00:00
2	ул. Московская 73	10:00:00	20:00:00
3	ул. Ленинградская 13	10:00:00	19:00:00
\.


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.products (id, title, description, price, image_url) FROM stdin;
1	IBANEZ GIO GRG140-WH электрогитара	Электрогитара Ibanez GRG140 была беспощадно протестирована лучшими металлистами-эндорсерами Ibanez, чтобы создать уникальный и эргономичный инструмент. В результате на свет появилась гитара, на которой можно исполнять самые сложные и быстрые соло-партии, а следовательно, достичь с ее помощью вершин музыкального мастерства.\n	30774.00	https://i.ibb.co/hxHX0b7z/ibanez1.webp
2	IBANEZ GRX40-BKN электрогитара 	BANEZ GRX40-BKN – шестиструнная электрогитара из самой узнаваемой линейки Ibanez – RG. Это инструмент с мощным звуком, идеальный для скоростной игры. Характерные черты гитары сформировались за три десятилетия развития металла. Корпус инструмента выполнен из тополя.	21532.00	https://i.ibb.co/h1Wm904g/ibanez2.webp
3	Yamaha PAC611 HFM TBL	Yamaha PAC611 HFM TBL	522258.00	https://i.ibb.co/8g8w2MSH/yamaha1.webp
\.


--
-- Data for Name: products_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.products_categories (category_id, product_id) FROM stdin;
1	1
1	2
1	3
2	1
2	2
3	3
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, name, password, email, role, enabled) FROM stdin;
\.


--
-- Data for Name: verification_token; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.verification_token (id, expiry_date, token, user_id) FROM stdin;
\.


--
-- Name: bucket_items_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bucket_items_seq', 1, false);


--
-- Name: bucket_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bucket_seq', 1, false);


--
-- Name: category_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.category_seq', 1, false);


--
-- Name: order_details_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.order_details_seq', 1, false);


--
-- Name: order_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.order_seq', 1, false);


--
-- Name: pickup_point_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pickup_point_seq', 1, false);


--
-- Name: product_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.product_seq', 1, false);


--
-- Name: user_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_seq', 1, false);


--
-- Name: verification_token_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.verification_token_id_seq', 1, false);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: bucket_items pk_bucket_items; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bucket_items
    ADD CONSTRAINT pk_bucket_items PRIMARY KEY (id);


--
-- Name: buckets pk_buckets; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.buckets
    ADD CONSTRAINT pk_buckets PRIMARY KEY (id);


--
-- Name: categories pk_categories; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT pk_categories PRIMARY KEY (id);


--
-- Name: order_details pk_order_details; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT pk_order_details PRIMARY KEY (id);


--
-- Name: orders pk_orders; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT pk_orders PRIMARY KEY (id);


--
-- Name: pickup_points pk_pickup_points; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pickup_points
    ADD CONSTRAINT pk_pickup_points PRIMARY KEY (id);


--
-- Name: products pk_products; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT pk_products PRIMARY KEY (id);


--
-- Name: products_categories pk_products_categories; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_categories
    ADD CONSTRAINT pk_products_categories PRIMARY KEY (category_id, product_id);


--
-- Name: users pk_users; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);


--
-- Name: buckets uc_buckets_user; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.buckets
    ADD CONSTRAINT uc_buckets_user UNIQUE (user_id);


--
-- Name: categories uc_categories_title; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT uc_categories_title UNIQUE (title);


--
-- Name: users uc_users_email; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uc_users_email UNIQUE (email);


--
-- Name: verification_token ukq6jibbenp7o9v6tq178xg88hg; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT ukq6jibbenp7o9v6tq178xg88hg UNIQUE (user_id);


--
-- Name: verification_token verification_token_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT verification_token_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: verification_token fk3asw9wnv76uxu3kr1ekq4i1ld; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT fk3asw9wnv76uxu3kr1ekq4i1ld FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: bucket_items fk_bucket_items_on_bucket; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bucket_items
    ADD CONSTRAINT fk_bucket_items_on_bucket FOREIGN KEY (bucket_id) REFERENCES public.buckets(id);


--
-- Name: bucket_items fk_bucket_items_on_product; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bucket_items
    ADD CONSTRAINT fk_bucket_items_on_product FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: buckets fk_buckets_on_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.buckets
    ADD CONSTRAINT fk_buckets_on_user FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: order_details fk_order_details_on_order; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT fk_order_details_on_order FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: order_details fk_order_details_on_product; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT fk_order_details_on_product FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: orders fk_orders_on_pickup_point; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk_orders_on_pickup_point FOREIGN KEY (pickup_point_id) REFERENCES public.pickup_points(id);


--
-- Name: orders fk_orders_on_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk_orders_on_user FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: products_categories fk_procat_on_category; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_categories
    ADD CONSTRAINT fk_procat_on_category FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: products_categories fk_procat_on_product; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_categories
    ADD CONSTRAINT fk_procat_on_product FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- PostgreSQL database dump complete
--

\unrestrict in1h8CDSefLLb8dZWSbnLjvSCHqoYDykx3HfLLKHPjqvPVapzhMlBK3FJM2pdAm

