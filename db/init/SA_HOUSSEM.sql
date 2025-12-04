
-- les tables staging 

CREATE SCHEMA IF NOT EXISTS "SA";

CREATE TABLE IF NOT EXISTS "SA".stg_products (
  rec_type        text,
  product_type    text,
  product_name    text,
  product_code    text,
  ean13           text,
  variant_ref     text,
  description     text,
  department      text,
  note            text,
  status_wf       text,
  brand           text,
  family          text,
  stock_min       integer,
  stock_qty       integer,
  valid_from      date,
  valid_to        date,
  taxonomy        text,
  is_new          boolean,
  is_top_seller   boolean,
  is_promo        boolean,
  owner_name      text,
  manager_name    text,
  price           numeric(18,2),
  category_main   text,
  category_sub    text,
  is_visible      boolean,
  image_url       text,
  supplier        text,
  is_discontinued boolean,
  flag1           boolean,
  flag2           boolean,
  flag3           boolean,
  flag4           boolean,
  src_filename    text,
  load_ts         timestamp default now()
);

CREATE TABLE IF NOT EXISTS "SA".etl_product_rejects (
  file_name       text,
  file_path       text,
  line_number     integer,
  rec_type_raw    text,
  product_code_raw text,
  product_name_raw text,
  price_raw       text,
  valid_from_raw  text,
  valid_to_raw    text,
  reason          text,
  raw_line        text,
  load_ts         timestamp default now()
);


CREATE TABLE IF NOT EXISTS "SA".etl_stock_HL62110 (
  seq_no          text,
  site_code       text,
  sign            text,
  mov_family      text,
  customer        text,
  doc_no          text,
  label           text,
  qty_str         text,
  qty             decimal,
  file_name       text,
  load_ts         timestamp default now()
);

CREATE TABLE IF NOT EXISTS "SA".etl_stock_HL62111 (
  seq_no          text,
  partner_code    text,
  origin_site     text,
  dest_site       text,
  reason_flag     text,
  file_name       text,
  load_ts         timestamp default now()
);
CREATE TABLE IF NOT EXISTS "SA".etl_stock_HL62112 (
  seq_no          text,
  doc_date        date,
  mvt_date        date,
  route_code      text,
  route_label     text,
  product_id      text,
  lot             text,
  file_name       text,
  load_ts         timestamp default now()
);


CREATE TABLE IF NOT EXISTS "SA".etl_status (
  seq_no          text,
  site_code       text,
  partner_code    text,
  product_id      text,
  customer        text,
  address         text,
  zipcode         text,
  city            text,
  country         text,
  status_code     text,
  datetime        timestamp,
  file_name       text,
  load_ts         timestamp default now()
);

