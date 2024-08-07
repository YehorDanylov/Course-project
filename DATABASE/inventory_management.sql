PGDMP  ;    0                |           inventory_management    16.3    16.3     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    16581    inventory_management    DATABASE     �   CREATE DATABASE inventory_management WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Ukraine.1251';
 $   DROP DATABASE inventory_management;
                postgres    false            �            1259    16592    product_groups    TABLE     �   CREATE TABLE public.product_groups (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    description text
);
 "   DROP TABLE public.product_groups;
       public         heap    postgres    false            �            1259    16591    product_groups_id_seq    SEQUENCE     �   CREATE SEQUENCE public.product_groups_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 ,   DROP SEQUENCE public.product_groups_id_seq;
       public          postgres    false    217            �           0    0    product_groups_id_seq    SEQUENCE OWNED BY     O   ALTER SEQUENCE public.product_groups_id_seq OWNED BY public.product_groups.id;
          public          postgres    false    216            �            1259    16583    products    TABLE     �   CREATE TABLE public.products (
    name character varying(255) NOT NULL,
    description text,
    manufacturer character varying(100),
    quantity integer,
    price_per_unit numeric(10,2),
    id integer NOT NULL,
    group_id integer
);
    DROP TABLE public.products;
       public         heap    postgres    false            �            1259    16600    products_id_seq    SEQUENCE     �   CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.products_id_seq;
       public          postgres    false    215            �           0    0    products_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;
          public          postgres    false    218                        2604    16595    product_groups id    DEFAULT     v   ALTER TABLE ONLY public.product_groups ALTER COLUMN id SET DEFAULT nextval('public.product_groups_id_seq'::regclass);
 @   ALTER TABLE public.product_groups ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    217    216    217                       2604    16601    products id    DEFAULT     j   ALTER TABLE ONLY public.products ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);
 :   ALTER TABLE public.products ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    218    215            �          0    16592    product_groups 
   TABLE DATA           ?   COPY public.product_groups (id, name, description) FROM stdin;
    public          postgres    false    217   u       �          0    16583    products 
   TABLE DATA           k   COPY public.products (name, description, manufacturer, quantity, price_per_unit, id, group_id) FROM stdin;
    public          postgres    false    215   �       �           0    0    product_groups_id_seq    SEQUENCE SET     D   SELECT pg_catalog.setval('public.product_groups_id_seq', 17, true);
          public          postgres    false    216            �           0    0    products_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.products_id_seq', 28, true);
          public          postgres    false    218            $           2606    16599 "   product_groups product_groups_pkey 
   CONSTRAINT     `   ALTER TABLE ONLY public.product_groups
    ADD CONSTRAINT product_groups_pkey PRIMARY KEY (id);
 L   ALTER TABLE ONLY public.product_groups DROP CONSTRAINT product_groups_pkey;
       public            postgres    false    217            "           2606    16603    products products_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.products DROP CONSTRAINT products_pkey;
       public            postgres    false    215            %           2606    16610    products fk_group    FK CONSTRAINT     z   ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES public.product_groups(id);
 ;   ALTER TABLE ONLY public.products DROP CONSTRAINT fk_group;
       public          postgres    false    217    4644    215            �   /   x�34�t��/�H-��24�t�IM.)���L��9���S8�b����  u      �   �   x�-�1�0 g��؈�Ш]� 	��� �Q�$j�a�ng��~���
�Tk��n�-L�0k"0��sS���Y�x�y�N��R����?�5z��Fr��������b-p�.8���1���O��?�4*�     