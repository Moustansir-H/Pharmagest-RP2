--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2
-- Dumped by pg_dump version 16.2

-- Started on 2025-04-17 20:15:33

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 4971 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 927 (class 1247 OID 25194)
-- Name: role; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.role AS ENUM (
    'PHARMACIEN',
    'VENDEUR'
);


ALTER TYPE public.role OWNER TO postgres;

--
-- TOC entry 876 (class 1247 OID 24928)
-- Name: statutpaiement; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.statutpaiement AS ENUM (
    'EN_ATTENTE',
    'VALIDE',
    'REJETE'
);


ALTER TYPE public.statutpaiement OWNER TO postgres;

--
-- TOC entry 873 (class 1247 OID 24922)
-- Name: typevente; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.typevente AS ENUM (
    'LIBRE',
    'PRESCRITE'
);


ALTER TYPE public.typevente OWNER TO postgres;

--
-- TOC entry 259 (class 1255 OID 50512)
-- Name: creer_commande_automatique(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.creer_commande_automatique() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
v_fournisseur_id INTEGER;
v_commande_id INTEGER;
v_quantite_a_commander INTEGER;
v_prix_unitaire NUMERIC(10,2);
BEGIN
-- Ne déclencher que si le stock est passé sous le seuil de 10
IF NEW.stock < 10 AND (OLD.stock IS NULL OR OLD.stock >= 10) THEN
    -- Récupérer l'ID du fournisseur associé au médicament
    SELECT fournisseur_id INTO v_fournisseur_id FROM medicament WHERE id = NEW.id;
    
    -- Si pas de fournisseur assigné, utiliser un fournisseur par défaut
    IF v_fournisseur_id IS NULL THEN
        SELECT id INTO v_fournisseur_id FROM fournisseur LIMIT 1;
        
        IF v_fournisseur_id IS NULL THEN
            RAISE EXCEPTION 'Aucun fournisseur disponible pour créer une commande automatique';
            RETURN NEW;
        END IF;
        
        -- Mettre à jour le médicament avec ce fournisseur
        UPDATE medicament SET fournisseur_id = v_fournisseur_id WHERE id = NEW.id;
    END IF;
    
    -- Calculer la quantité à commander (pour atteindre 100)
    v_quantite_a_commander := 100 - NEW.stock;
    
    -- Récupérer le prix d'achat du médicament
    v_prix_unitaire := NEW.prixachat;
    
    -- Vérifier si une commande en attente existe déjà pour ce fournisseur
    SELECT id INTO v_commande_id 
    FROM commande 
    WHERE fournisseur_id = v_fournisseur_id 
      AND statut = 'En attente de confirmation'
    LIMIT 1;
    
    -- Si aucune commande en attente n'existe, en créer une nouvelle
    IF v_commande_id IS NULL THEN
        INSERT INTO commande (montant, fournisseur_id, date_creation, statut)
        VALUES (0, v_fournisseur_id, CURRENT_TIMESTAMP, 'En attente de confirmation')
        RETURNING id INTO v_commande_id;
        
        -- Créer une livraison associée à cette commande avec statut "En cours"
        INSERT INTO livraison (datelivraison, status, commande_id, fournisseur_id)
        VALUES (CURRENT_TIMESTAMP, 'En cours', v_commande_id, v_fournisseur_id);
        
        RAISE NOTICE 'Nouvelle commande créée (ID: %) pour le fournisseur % avec livraison associée', v_commande_id, v_fournisseur_id;
    ELSE
        RAISE NOTICE 'Ajout à une commande existante (ID: %) pour le fournisseur %', v_commande_id, v_fournisseur_id;
    END IF;
    
    -- Vérifier si ce médicament est déjà dans la commande
    IF EXISTS (SELECT 1 FROM lignedecommande WHERE commande_id = v_commande_id AND medicament_id = NEW.id) THEN
        -- Mettre à jour la ligne de commande existante
        UPDATE lignedecommande 
        SET quantitevendu = quantitevendu + v_quantite_a_commander
        WHERE commande_id = v_commande_id AND medicament_id = NEW.id;
        
        RAISE NOTICE 'Mise à jour de la quantité pour le médicament % dans la commande %', NEW.id, v_commande_id;
    ELSE
        -- Ajouter une nouvelle ligne de commande
        INSERT INTO lignedecommande (quantitevendu, prixunitaire, commande_id, medicament_id, quantiterecue, prixachatreel, prixventereel)
        VALUES (v_quantite_a_commander, v_prix_unitaire, v_commande_id, NEW.id, 0, 0, 0);
        
        RAISE NOTICE 'Ajout du médicament % à la commande %', NEW.id, v_commande_id;
    END IF;
    
    -- Mettre à jour le montant total de la commande
    UPDATE commande
    SET montant = (
        SELECT SUM(quantitevendu * prixunitaire)
        FROM lignedecommande
        WHERE commande_id = v_commande_id
    )
    WHERE id = v_commande_id;
    
    RAISE NOTICE 'Commande automatique créée/mise à jour pour le médicament % (stock: %)', NEW.nom, NEW.stock;
END IF;

RETURN NEW;
END;
$$;


ALTER FUNCTION public.creer_commande_automatique() OWNER TO postgres;

--
-- TOC entry 258 (class 1255 OID 50514)
-- Name: valider_commande(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.valider_commande(p_commande_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
r_ligne RECORD;
v_livraison_id INTEGER;
BEGIN
-- Vérifier que la commande existe et est en attente
IF NOT EXISTS (SELECT 1 FROM commande WHERE id = p_commande_id AND statut = 'En attente de confirmation') THEN
    RAISE EXCEPTION 'Commande % inexistante ou déjà validée', p_commande_id;
END IF;

-- Pour chaque ligne de commande, mettre à jour le stock du médicament
FOR r_ligne IN (SELECT medicament_id, quantitevendu FROM lignedecommande WHERE commande_id = p_commande_id) LOOP
    UPDATE medicament
    SET stock = stock + r_ligne.quantitevendu
    WHERE id = r_ligne.medicament_id;
    
    RAISE NOTICE 'Stock du médicament % mis à jour', r_ligne.medicament_id;
END LOOP;

-- Marquer la commande comme validée
UPDATE commande
SET statut = 'Validée'
WHERE id = p_commande_id;

-- Mettre à jour le statut de la livraison associée à "Livrée"
SELECT id INTO v_livraison_id FROM livraison WHERE commande_id = p_commande_id;
IF v_livraison_id IS NOT NULL THEN
    UPDATE livraison
    SET status = 'Livrée'
    WHERE id = v_livraison_id;
    
    RAISE NOTICE 'Livraison % associée à la commande % marquée comme livrée', v_livraison_id, p_commande_id;
END IF;

RAISE NOTICE 'Commande % validée avec succès', p_commande_id;
END;
$$;


ALTER FUNCTION public.valider_commande(p_commande_id integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 230 (class 1259 OID 25011)
-- Name: commande; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.commande (
    id integer NOT NULL,
    montant numeric(10,2) NOT NULL,
    date_creation timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    statut character varying(50) DEFAULT 'En attente de confirmation'::character varying,
    fournisseur_id integer
);


ALTER TABLE public.commande OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 25010)
-- Name: commande_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.commande_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.commande_id_seq OWNER TO postgres;

--
-- TOC entry 4972 (class 0 OID 0)
-- Dependencies: 229
-- Name: commande_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.commande_id_seq OWNED BY public.commande.id;


--
-- TOC entry 242 (class 1259 OID 25095)
-- Name: facture; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.facture (
    id integer NOT NULL,
    dateemission timestamp without time zone NOT NULL,
    montanttotal numeric(10,2) NOT NULL,
    numerofacture character varying(255) NOT NULL,
    vente_id integer
);


ALTER TABLE public.facture OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 25094)
-- Name: facture_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.facture_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.facture_id_seq OWNER TO postgres;

--
-- TOC entry 4973 (class 0 OID 0)
-- Dependencies: 241
-- Name: facture_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.facture_id_seq OWNED BY public.facture.id;


--
-- TOC entry 216 (class 1259 OID 24936)
-- Name: famille; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.famille (
    id integer NOT NULL,
    nom character varying(255) NOT NULL
);


ALTER TABLE public.famille OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 24935)
-- Name: famille_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.famille_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.famille_id_seq OWNER TO postgres;

--
-- TOC entry 4974 (class 0 OID 0)
-- Dependencies: 215
-- Name: famille_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.famille_id_seq OWNED BY public.famille.id;


--
-- TOC entry 220 (class 1259 OID 24950)
-- Name: fournisseur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.fournisseur (
    id integer NOT NULL,
    nom character varying(255) NOT NULL,
    adresse character varying(255),
    contact character varying(255),
    email character varying
);


ALTER TABLE public.fournisseur OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24949)
-- Name: fournisseur_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.fournisseur_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.fournisseur_id_seq OWNER TO postgres;

--
-- TOC entry 4975 (class 0 OID 0)
-- Dependencies: 219
-- Name: fournisseur_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.fournisseur_id_seq OWNED BY public.fournisseur.id;


--
-- TOC entry 232 (class 1259 OID 25023)
-- Name: lignedecommande; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lignedecommande (
    id integer NOT NULL,
    quantitevendu integer NOT NULL,
    prixunitaire numeric(10,2) NOT NULL,
    commande_id integer,
    medicament_id integer,
    quantiterecue integer DEFAULT 0,
    prixachatreel numeric(10,2) DEFAULT 0,
    prixventereel numeric(10,2) DEFAULT 0
);


ALTER TABLE public.lignedecommande OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 25022)
-- Name: lignedecommande_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.lignedecommande_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.lignedecommande_id_seq OWNER TO postgres;

--
-- TOC entry 4976 (class 0 OID 0)
-- Dependencies: 231
-- Name: lignedecommande_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.lignedecommande_id_seq OWNED BY public.lignedecommande.id;


--
-- TOC entry 240 (class 1259 OID 25078)
-- Name: lignevente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lignevente (
    id integer NOT NULL,
    quantitevendu integer NOT NULL,
    prixunitaire numeric(10,2) NOT NULL,
    vente_id integer,
    medicament_id integer
);


ALTER TABLE public.lignevente OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 25077)
-- Name: lignevente_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.lignevente_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.lignevente_id_seq OWNER TO postgres;

--
-- TOC entry 4977 (class 0 OID 0)
-- Dependencies: 239
-- Name: lignevente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.lignevente_id_seq OWNED BY public.lignevente.id;


--
-- TOC entry 246 (class 1259 OID 25119)
-- Name: livraison; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.livraison (
    id integer NOT NULL,
    datelivraison timestamp without time zone NOT NULL,
    status character varying(255),
    commande_id integer,
    fournisseur_id integer
);


ALTER TABLE public.livraison OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 25118)
-- Name: livraison_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.livraison_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.livraison_id_seq OWNER TO postgres;

--
-- TOC entry 4978 (class 0 OID 0)
-- Dependencies: 245
-- Name: livraison_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.livraison_id_seq OWNED BY public.livraison.id;


--
-- TOC entry 222 (class 1259 OID 24959)
-- Name: medicament; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.medicament (
    id integer NOT NULL,
    nom character varying(255) NOT NULL,
    forme character varying(255),
    prixachat numeric(10,2) NOT NULL,
    prixvente numeric(10,2) NOT NULL,
    stock integer NOT NULL,
    seuilcommande integer NOT NULL,
    qtemax integer NOT NULL,
    famille_id integer,
    fournisseur_id integer NOT NULL,
    ordonnance boolean
);


ALTER TABLE public.medicament OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 24958)
-- Name: medicament_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.medicament_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.medicament_id_seq OWNER TO postgres;

--
-- TOC entry 4979 (class 0 OID 0)
-- Dependencies: 221
-- Name: medicament_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.medicament_id_seq OWNED BY public.medicament.id;


--
-- TOC entry 244 (class 1259 OID 25107)
-- Name: paiement; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.paiement (
    id integer NOT NULL,
    montant numeric(10,2) NOT NULL,
    modepaiement character varying(255),
    statut public.statutpaiement NOT NULL,
    vente_id integer
);


ALTER TABLE public.paiement OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 25106)
-- Name: paiement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.paiement_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.paiement_id_seq OWNER TO postgres;

--
-- TOC entry 4980 (class 0 OID 0)
-- Dependencies: 243
-- Name: paiement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.paiement_id_seq OWNED BY public.paiement.id;


--
-- TOC entry 234 (class 1259 OID 25040)
-- Name: patient; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.patient (
    id integer NOT NULL,
    nom character varying(255) NOT NULL,
    prenom character varying(255) NOT NULL,
    datenaissance date NOT NULL,
    adresse character varying(255),
    contact character varying(255)
);


ALTER TABLE public.patient OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 25039)
-- Name: patient_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.patient_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.patient_id_seq OWNER TO postgres;

--
-- TOC entry 4981 (class 0 OID 0)
-- Dependencies: 233
-- Name: patient_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.patient_id_seq OWNED BY public.patient.id;


--
-- TOC entry 226 (class 1259 OID 24987)
-- Name: pharmacien; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pharmacien (
    id integer NOT NULL,
    utilisateur_id integer
);


ALTER TABLE public.pharmacien OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24986)
-- Name: pharmacien_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pharmacien_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pharmacien_id_seq OWNER TO postgres;

--
-- TOC entry 4982 (class 0 OID 0)
-- Dependencies: 225
-- Name: pharmacien_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pharmacien_id_seq OWNED BY public.pharmacien.id;


--
-- TOC entry 236 (class 1259 OID 25049)
-- Name: prescription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.prescription (
    id integer NOT NULL,
    nommedecin character varying(255) NOT NULL,
    dateprescription timestamp without time zone NOT NULL,
    patient_id integer
);


ALTER TABLE public.prescription OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 25048)
-- Name: prescription_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.prescription_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.prescription_id_seq OWNER TO postgres;

--
-- TOC entry 4983 (class 0 OID 0)
-- Dependencies: 235
-- Name: prescription_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.prescription_id_seq OWNED BY public.prescription.id;


--
-- TOC entry 218 (class 1259 OID 24943)
-- Name: unite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.unite (
    id integer NOT NULL,
    nomunite character varying(255) NOT NULL
);


ALTER TABLE public.unite OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 24942)
-- Name: unite_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.unite_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.unite_id_seq OWNER TO postgres;

--
-- TOC entry 4984 (class 0 OID 0)
-- Dependencies: 217
-- Name: unite_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.unite_id_seq OWNED BY public.unite.id;


--
-- TOC entry 224 (class 1259 OID 24978)
-- Name: utilisateur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.utilisateur (
    id integer NOT NULL,
    identifiant character varying(255) NOT NULL,
    motdepasse character varying(255) NOT NULL,
    role public.role DEFAULT 'PHARMACIEN'::public.role
);


ALTER TABLE public.utilisateur OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 24977)
-- Name: utilisateur_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.utilisateur_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.utilisateur_id_seq OWNER TO postgres;

--
-- TOC entry 4985 (class 0 OID 0)
-- Dependencies: 223
-- Name: utilisateur_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.utilisateur_id_seq OWNED BY public.utilisateur.id;


--
-- TOC entry 228 (class 1259 OID 24999)
-- Name: vendeur; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vendeur (
    id integer NOT NULL,
    utilisateur_id integer
);


ALTER TABLE public.vendeur OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 24998)
-- Name: vendeur_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.vendeur_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.vendeur_id_seq OWNER TO postgres;

--
-- TOC entry 4986 (class 0 OID 0)
-- Dependencies: 227
-- Name: vendeur_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.vendeur_id_seq OWNED BY public.vendeur.id;


--
-- TOC entry 238 (class 1259 OID 25061)
-- Name: vente; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vente (
    id integer NOT NULL,
    datevente timestamp without time zone NOT NULL,
    montanttotal numeric(10,2) NOT NULL,
    typevente public.typevente NOT NULL,
    vendeur_id integer,
    prescription_id integer
);


ALTER TABLE public.vente OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 25060)
-- Name: vente_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.vente_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.vente_id_seq OWNER TO postgres;

--
-- TOC entry 4987 (class 0 OID 0)
-- Dependencies: 237
-- Name: vente_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.vente_id_seq OWNED BY public.vente.id;


--
-- TOC entry 4728 (class 2604 OID 25014)
-- Name: commande id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commande ALTER COLUMN id SET DEFAULT nextval('public.commande_id_seq'::regclass);


--
-- TOC entry 4739 (class 2604 OID 25098)
-- Name: facture id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.facture ALTER COLUMN id SET DEFAULT nextval('public.facture_id_seq'::regclass);


--
-- TOC entry 4720 (class 2604 OID 24939)
-- Name: famille id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.famille ALTER COLUMN id SET DEFAULT nextval('public.famille_id_seq'::regclass);


--
-- TOC entry 4722 (class 2604 OID 24953)
-- Name: fournisseur id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fournisseur ALTER COLUMN id SET DEFAULT nextval('public.fournisseur_id_seq'::regclass);


--
-- TOC entry 4731 (class 2604 OID 25026)
-- Name: lignedecommande id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignedecommande ALTER COLUMN id SET DEFAULT nextval('public.lignedecommande_id_seq'::regclass);


--
-- TOC entry 4738 (class 2604 OID 25081)
-- Name: lignevente id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignevente ALTER COLUMN id SET DEFAULT nextval('public.lignevente_id_seq'::regclass);


--
-- TOC entry 4741 (class 2604 OID 25122)
-- Name: livraison id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livraison ALTER COLUMN id SET DEFAULT nextval('public.livraison_id_seq'::regclass);


--
-- TOC entry 4723 (class 2604 OID 24962)
-- Name: medicament id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.medicament ALTER COLUMN id SET DEFAULT nextval('public.medicament_id_seq'::regclass);


--
-- TOC entry 4740 (class 2604 OID 25110)
-- Name: paiement id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.paiement ALTER COLUMN id SET DEFAULT nextval('public.paiement_id_seq'::regclass);


--
-- TOC entry 4735 (class 2604 OID 25043)
-- Name: patient id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient ALTER COLUMN id SET DEFAULT nextval('public.patient_id_seq'::regclass);


--
-- TOC entry 4726 (class 2604 OID 24990)
-- Name: pharmacien id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pharmacien ALTER COLUMN id SET DEFAULT nextval('public.pharmacien_id_seq'::regclass);


--
-- TOC entry 4736 (class 2604 OID 25052)
-- Name: prescription id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prescription ALTER COLUMN id SET DEFAULT nextval('public.prescription_id_seq'::regclass);


--
-- TOC entry 4721 (class 2604 OID 24946)
-- Name: unite id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unite ALTER COLUMN id SET DEFAULT nextval('public.unite_id_seq'::regclass);


--
-- TOC entry 4724 (class 2604 OID 24981)
-- Name: utilisateur id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur ALTER COLUMN id SET DEFAULT nextval('public.utilisateur_id_seq'::regclass);


--
-- TOC entry 4727 (class 2604 OID 25002)
-- Name: vendeur id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendeur ALTER COLUMN id SET DEFAULT nextval('public.vendeur_id_seq'::regclass);


--
-- TOC entry 4737 (class 2604 OID 25064)
-- Name: vente id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vente ALTER COLUMN id SET DEFAULT nextval('public.vente_id_seq'::regclass);


--
-- TOC entry 4949 (class 0 OID 25011)
-- Dependencies: 230
-- Data for Name: commande; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.commande (id, montant, date_creation, statut, fournisseur_id) FROM stdin;
1	2730.00	2025-04-01 14:29:05.1748	Validée	1
2	2880.00	2025-04-17 18:20:51.987488	Validée	1
\.


--
-- TOC entry 4961 (class 0 OID 25095)
-- Dependencies: 242
-- Data for Name: facture; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.facture (id, dateemission, montanttotal, numerofacture, vente_id) FROM stdin;
1	2025-04-01 13:41:53.906	440.00	FAC-1-1743500513896	1
2	2025-04-01 13:42:35.025	800.00	FAC-2-1743500555025	2
3	2025-04-01 13:43:24.433	440.00	FAC-3-1743500604433	3
4	2025-04-01 14:28:41.907	440.00	FAC-4-1743503321896	4
5	2025-04-15 19:13:08.442	3880.00	FAC-5-1744729988418	5
6	2025-04-15 19:16:04.731	3640.00	FAC-6-1744730164722	6
7	2025-04-16 11:33:43.076	800.00	FAC-7-1744788823071	7
8	2025-04-16 11:40:40.556	1200.00	FAC-8-1744789240549	8
9	2025-04-16 11:48:24.833	200.00	FAC-9-1744789704827	9
10	2025-04-16 11:51:07.986	200.00	FAC-10-1744789867986	10
11	2025-04-17 18:20:10.904	400.00	FAC-11-1744899610895	11
12	2025-04-17 18:34:04.073	400.00	FAC-12-1744900444066	12
\.


--
-- TOC entry 4935 (class 0 OID 24936)
-- Dependencies: 216
-- Data for Name: famille; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.famille (id, nom) FROM stdin;
1	Paracetamol
\.


--
-- TOC entry 4939 (class 0 OID 24950)
-- Dependencies: 220
-- Data for Name: fournisseur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.fournisseur (id, nom, adresse, contact, email) FROM stdin;
1	Chan	louis	5554470	sensiro456@gmail.com
3	ff	ff	7777777	kkkk@gmail.com
\.


--
-- TOC entry 4951 (class 0 OID 25023)
-- Dependencies: 232
-- Data for Name: lignedecommande; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lignedecommande (id, quantitevendu, prixunitaire, commande_id, medicament_id, quantiterecue, prixachatreel, prixventereel) FROM stdin;
1	91	30.00	1	1	0	0.00	0.00
2	96	30.00	2	1	0	0.00	0.00
\.


--
-- TOC entry 4959 (class 0 OID 25078)
-- Dependencies: 240
-- Data for Name: lignevente; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lignevente (id, quantitevendu, prixunitaire, vente_id, medicament_id) FROM stdin;
1	11	40.00	1	1
2	20	40.00	2	1
3	11	40.00	3	1
4	11	40.00	4	1
5	97	40.00	5	1
6	91	40.00	6	1
7	20	40.00	7	1
8	30	40.00	8	1
9	5	40.00	9	1
10	5	40.00	10	1
11	10	40.00	11	1
12	10	40.00	12	1
\.


--
-- TOC entry 4965 (class 0 OID 25119)
-- Dependencies: 246
-- Data for Name: livraison; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.livraison (id, datelivraison, status, commande_id, fournisseur_id) FROM stdin;
1	2025-04-17 18:20:51.987488	Livrée	2	1
\.


--
-- TOC entry 4941 (class 0 OID 24959)
-- Dependencies: 222
-- Data for Name: medicament; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.medicament (id, nom, forme, prixachat, prixvente, stock, seuilcommande, qtemax, famille_id, fournisseur_id, ordonnance) FROM stdin;
2	Paracetamol 500mg	Comprimé	15.00	20.00	50	10	100	1	1	\N
1	Doliprane 1000 mg	Comprimé	30.00	40.00	90	10	100	1	1	\N
\.


--
-- TOC entry 4963 (class 0 OID 25107)
-- Dependencies: 244
-- Data for Name: paiement; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.paiement (id, montant, modepaiement, statut, vente_id) FROM stdin;
1	440.00	ESPECES	VALIDE	1
2	440.00	ESPECES	VALIDE	4
3	3880.00	ESPECES	VALIDE	5
4	3640.00	ESPECES	VALIDE	6
5	800.00	ESPECES	VALIDE	2
6	440.00	ESPECES	VALIDE	3
7	800.00	ESPECES	VALIDE	7
8	1200.00	ESPECES	VALIDE	8
9	200.00	ESPECES	VALIDE	9
10	200.00	ESPECES	VALIDE	10
11	400.00	ESPECES	VALIDE	11
12	400.00	ESPECES	VALIDE	12
\.


--
-- TOC entry 4953 (class 0 OID 25040)
-- Dependencies: 234
-- Data for Name: patient; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.patient (id, nom, prenom, datenaissance, adresse, contact) FROM stdin;
1	jackie	chan	2004-03-12	dessese	55522200
\.


--
-- TOC entry 4945 (class 0 OID 24987)
-- Dependencies: 226
-- Data for Name: pharmacien; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pharmacien (id, utilisateur_id) FROM stdin;
\.


--
-- TOC entry 4955 (class 0 OID 25049)
-- Dependencies: 236
-- Data for Name: prescription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.prescription (id, nommedecin, dateprescription, patient_id) FROM stdin;
1	grand	2025-04-11 00:00:00	1
\.


--
-- TOC entry 4937 (class 0 OID 24943)
-- Dependencies: 218
-- Data for Name: unite; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.unite (id, nomunite) FROM stdin;
\.


--
-- TOC entry 4943 (class 0 OID 24978)
-- Dependencies: 224
-- Data for Name: utilisateur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.utilisateur (id, identifiant, motdepasse, role) FROM stdin;
1	admin	admin123	PHARMACIEN
2	user1	user123	PHARMACIEN
3	user2	user234	PHARMACIEN
\.


--
-- TOC entry 4947 (class 0 OID 24999)
-- Dependencies: 228
-- Data for Name: vendeur; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vendeur (id, utilisateur_id) FROM stdin;
\.


--
-- TOC entry 4957 (class 0 OID 25061)
-- Dependencies: 238
-- Data for Name: vente; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vente (id, datevente, montanttotal, typevente, vendeur_id, prescription_id) FROM stdin;
1	2025-04-01 00:00:00	440.00	LIBRE	\N	\N
2	2025-04-01 00:00:00	800.00	LIBRE	\N	\N
3	2025-04-01 00:00:00	440.00	LIBRE	\N	\N
4	2025-04-01 00:00:00	440.00	LIBRE	\N	\N
5	2025-04-15 00:00:00	3880.00	LIBRE	\N	\N
6	2025-04-15 00:00:00	3640.00	LIBRE	\N	\N
7	2025-04-16 00:00:00	800.00	LIBRE	\N	\N
8	2025-04-16 00:00:00	1200.00	LIBRE	\N	\N
9	2025-04-16 00:00:00	200.00	PRESCRITE	\N	1
10	2025-04-16 00:00:00	200.00	LIBRE	\N	\N
11	2025-04-17 00:00:00	400.00	LIBRE	\N	\N
12	2025-04-17 00:00:00	400.00	LIBRE	\N	\N
\.


--
-- TOC entry 4988 (class 0 OID 0)
-- Dependencies: 229
-- Name: commande_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.commande_id_seq', 2, true);


--
-- TOC entry 4989 (class 0 OID 0)
-- Dependencies: 241
-- Name: facture_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.facture_id_seq', 12, true);


--
-- TOC entry 4990 (class 0 OID 0)
-- Dependencies: 215
-- Name: famille_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.famille_id_seq', 1, true);


--
-- TOC entry 4991 (class 0 OID 0)
-- Dependencies: 219
-- Name: fournisseur_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.fournisseur_id_seq', 3, true);


--
-- TOC entry 4992 (class 0 OID 0)
-- Dependencies: 231
-- Name: lignedecommande_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lignedecommande_id_seq', 2, true);


--
-- TOC entry 4993 (class 0 OID 0)
-- Dependencies: 239
-- Name: lignevente_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lignevente_id_seq', 12, true);


--
-- TOC entry 4994 (class 0 OID 0)
-- Dependencies: 245
-- Name: livraison_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.livraison_id_seq', 1, true);


--
-- TOC entry 4995 (class 0 OID 0)
-- Dependencies: 221
-- Name: medicament_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.medicament_id_seq', 2, true);


--
-- TOC entry 4996 (class 0 OID 0)
-- Dependencies: 243
-- Name: paiement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.paiement_id_seq', 12, true);


--
-- TOC entry 4997 (class 0 OID 0)
-- Dependencies: 233
-- Name: patient_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.patient_id_seq', 1, true);


--
-- TOC entry 4998 (class 0 OID 0)
-- Dependencies: 225
-- Name: pharmacien_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pharmacien_id_seq', 2, true);


--
-- TOC entry 4999 (class 0 OID 0)
-- Dependencies: 235
-- Name: prescription_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.prescription_id_seq', 1, true);


--
-- TOC entry 5000 (class 0 OID 0)
-- Dependencies: 217
-- Name: unite_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.unite_id_seq', 1, false);


--
-- TOC entry 5001 (class 0 OID 0)
-- Dependencies: 223
-- Name: utilisateur_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.utilisateur_id_seq', 14, true);


--
-- TOC entry 5002 (class 0 OID 0)
-- Dependencies: 227
-- Name: vendeur_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.vendeur_id_seq', 1, true);


--
-- TOC entry 5003 (class 0 OID 0)
-- Dependencies: 237
-- Name: vente_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.vente_id_seq', 12, true);


--
-- TOC entry 4757 (class 2606 OID 25016)
-- Name: commande commande_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commande
    ADD CONSTRAINT commande_pkey PRIMARY KEY (id);


--
-- TOC entry 4769 (class 2606 OID 25100)
-- Name: facture facture_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.facture
    ADD CONSTRAINT facture_pkey PRIMARY KEY (id);


--
-- TOC entry 4743 (class 2606 OID 24941)
-- Name: famille famille_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.famille
    ADD CONSTRAINT famille_pkey PRIMARY KEY (id);


--
-- TOC entry 4747 (class 2606 OID 24957)
-- Name: fournisseur fournisseur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.fournisseur
    ADD CONSTRAINT fournisseur_pkey PRIMARY KEY (id);


--
-- TOC entry 4759 (class 2606 OID 25028)
-- Name: lignedecommande lignedecommande_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignedecommande
    ADD CONSTRAINT lignedecommande_pkey PRIMARY KEY (id);


--
-- TOC entry 4767 (class 2606 OID 25083)
-- Name: lignevente lignevente_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignevente
    ADD CONSTRAINT lignevente_pkey PRIMARY KEY (id);


--
-- TOC entry 4773 (class 2606 OID 25124)
-- Name: livraison livraison_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livraison
    ADD CONSTRAINT livraison_pkey PRIMARY KEY (id);


--
-- TOC entry 4749 (class 2606 OID 24966)
-- Name: medicament medicament_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.medicament
    ADD CONSTRAINT medicament_pkey PRIMARY KEY (id);


--
-- TOC entry 4771 (class 2606 OID 25112)
-- Name: paiement paiement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.paiement
    ADD CONSTRAINT paiement_pkey PRIMARY KEY (id);


--
-- TOC entry 4761 (class 2606 OID 25047)
-- Name: patient patient_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_pkey PRIMARY KEY (id);


--
-- TOC entry 4753 (class 2606 OID 24992)
-- Name: pharmacien pharmacien_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pharmacien
    ADD CONSTRAINT pharmacien_pkey PRIMARY KEY (id);


--
-- TOC entry 4763 (class 2606 OID 25054)
-- Name: prescription prescription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prescription
    ADD CONSTRAINT prescription_pkey PRIMARY KEY (id);


--
-- TOC entry 4745 (class 2606 OID 24948)
-- Name: unite unite_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unite
    ADD CONSTRAINT unite_pkey PRIMARY KEY (id);


--
-- TOC entry 4751 (class 2606 OID 24985)
-- Name: utilisateur utilisateur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.utilisateur
    ADD CONSTRAINT utilisateur_pkey PRIMARY KEY (id);


--
-- TOC entry 4755 (class 2606 OID 25004)
-- Name: vendeur vendeur_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendeur
    ADD CONSTRAINT vendeur_pkey PRIMARY KEY (id);


--
-- TOC entry 4765 (class 2606 OID 25066)
-- Name: vente vente_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vente
    ADD CONSTRAINT vente_pkey PRIMARY KEY (id);


--
-- TOC entry 4790 (class 2620 OID 50529)
-- Name: medicament trigger_commande_automatique; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_commande_automatique AFTER UPDATE OF stock ON public.medicament FOR EACH ROW EXECUTE FUNCTION public.creer_commande_automatique();


--
-- TOC entry 4778 (class 2606 OID 50515)
-- Name: commande commande_fournisseur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.commande
    ADD CONSTRAINT commande_fournisseur_id_fkey FOREIGN KEY (fournisseur_id) REFERENCES public.fournisseur(id);


--
-- TOC entry 4786 (class 2606 OID 25101)
-- Name: facture facture_vente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.facture
    ADD CONSTRAINT facture_vente_id_fkey FOREIGN KEY (vente_id) REFERENCES public.vente(id);


--
-- TOC entry 4779 (class 2606 OID 25029)
-- Name: lignedecommande lignedecommande_commande_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignedecommande
    ADD CONSTRAINT lignedecommande_commande_id_fkey FOREIGN KEY (commande_id) REFERENCES public.commande(id);


--
-- TOC entry 4780 (class 2606 OID 25034)
-- Name: lignedecommande lignedecommande_medicament_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignedecommande
    ADD CONSTRAINT lignedecommande_medicament_id_fkey FOREIGN KEY (medicament_id) REFERENCES public.medicament(id);


--
-- TOC entry 4784 (class 2606 OID 25089)
-- Name: lignevente lignevente_medicament_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignevente
    ADD CONSTRAINT lignevente_medicament_id_fkey FOREIGN KEY (medicament_id) REFERENCES public.medicament(id);


--
-- TOC entry 4785 (class 2606 OID 25084)
-- Name: lignevente lignevente_vente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignevente
    ADD CONSTRAINT lignevente_vente_id_fkey FOREIGN KEY (vente_id) REFERENCES public.vente(id);


--
-- TOC entry 4788 (class 2606 OID 25125)
-- Name: livraison livraison_commande_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livraison
    ADD CONSTRAINT livraison_commande_id_fkey FOREIGN KEY (commande_id) REFERENCES public.commande(id);


--
-- TOC entry 4789 (class 2606 OID 25130)
-- Name: livraison livraison_fournisseur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.livraison
    ADD CONSTRAINT livraison_fournisseur_id_fkey FOREIGN KEY (fournisseur_id) REFERENCES public.fournisseur(id);


--
-- TOC entry 4774 (class 2606 OID 24967)
-- Name: medicament medicament_famille_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.medicament
    ADD CONSTRAINT medicament_famille_id_fkey FOREIGN KEY (famille_id) REFERENCES public.famille(id);


--
-- TOC entry 4775 (class 2606 OID 24972)
-- Name: medicament medicament_fournisseur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.medicament
    ADD CONSTRAINT medicament_fournisseur_id_fkey FOREIGN KEY (fournisseur_id) REFERENCES public.fournisseur(id);


--
-- TOC entry 4787 (class 2606 OID 25113)
-- Name: paiement paiement_vente_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.paiement
    ADD CONSTRAINT paiement_vente_id_fkey FOREIGN KEY (vente_id) REFERENCES public.vente(id);


--
-- TOC entry 4776 (class 2606 OID 24993)
-- Name: pharmacien pharmacien_utilisateur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pharmacien
    ADD CONSTRAINT pharmacien_utilisateur_id_fkey FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateur(id);


--
-- TOC entry 4781 (class 2606 OID 25055)
-- Name: prescription prescription_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.prescription
    ADD CONSTRAINT prescription_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patient(id);


--
-- TOC entry 4777 (class 2606 OID 25005)
-- Name: vendeur vendeur_utilisateur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vendeur
    ADD CONSTRAINT vendeur_utilisateur_id_fkey FOREIGN KEY (utilisateur_id) REFERENCES public.utilisateur(id);


--
-- TOC entry 4782 (class 2606 OID 25072)
-- Name: vente vente_prescription_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vente
    ADD CONSTRAINT vente_prescription_id_fkey FOREIGN KEY (prescription_id) REFERENCES public.prescription(id);


--
-- TOC entry 4783 (class 2606 OID 25067)
-- Name: vente vente_vendeur_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vente
    ADD CONSTRAINT vente_vendeur_id_fkey FOREIGN KEY (vendeur_id) REFERENCES public.vendeur(id);


-- Completed on 2025-04-17 20:15:34

--
-- PostgreSQL database dump complete
--

