-- DDL generado por Hibernate (MySQLDialect) a partir de las entidades JPA de com.bodegaweb.
-- Es la fuente de verdad del esquema; el resto de cambios de esquema van en migraciones nuevas.

create table carrito (created_at datetime(6), id bigint not null auto_increment, usuario_id bigint not null, primary key (id)) engine=InnoDB;
create table carrito_item (cantidad integer not null, precio_unitario decimal(38,2) not null, carrito_id bigint not null, id bigint not null auto_increment, producto_id bigint not null, primary key (id)) engine=InnoDB;
create table categorias (categoria_padre_id bigint, created_at datetime(6) not null, id bigint not null auto_increment, nombre varchar(100) not null, slug varchar(120) not null, primary key (id)) engine=InnoDB;
create table direcciones (id bigint not null auto_increment, usuario_id bigint not null, codigo_postal varchar(20) not null, numero varchar(20) not null, ciudad varchar(100) not null, pais varchar(100) not null, calle varchar(150) not null, primary key (id)) engine=InnoDB;
create table inventario (stock_disponible integer not null, stock_reservado integer not null, actualizado_en datetime(6) not null, id bigint not null auto_increment, producto_id bigint not null, version bigint not null, primary key (id)) engine=InnoDB;
create table pagos (monto decimal(12,2) not null, created_at datetime(6) not null, id bigint not null auto_increment, pedido_id bigint not null, updated_at datetime(6) not null, referencia_externa varchar(120), estado enum ('APROBADO','PENDIENTE','RECHAZADO','REEMBOLSADO') not null, metodo_pago enum ('EFECTIVO','PLIN','TARJETA','TRANSFERENCIA','YAPE') not null, primary key (id)) engine=InnoDB;
create table pedido (total decimal(38,2) not null, direccion_id bigint not null, fecha_pedido datetime(6), id bigint not null auto_increment, usuario_id bigint not null, estado enum ('CANCELADO','DESPACHADO','ENTREGADO','EN_RUTA','PAGADO','PENDIENTE') not null, primary key (id)) engine=InnoDB;
create table pedido_item (cantidad integer not null, precio_unitario decimal(38,2) not null, id bigint not null auto_increment, pedido_id bigint not null, producto_id bigint not null, primary key (id)) engine=InnoDB;
create table productos (activo bit not null, precio decimal(12,2) not null, categoria_id bigint, created_at datetime(6) not null, id bigint not null auto_increment, updated_at datetime(6) not null, sku varchar(50) not null, nombre varchar(200) not null, imagen_url varchar(500), descripcion TEXT, primary key (id)) engine=InnoDB;
create table resenas (calificacion integer not null, created_at datetime(6) not null, id bigint not null auto_increment, producto_id bigint not null, updated_at datetime(6) not null, usuario_id bigint not null, comentario TEXT, primary key (id)) engine=InnoDB;
create table usuarios (created_at datetime(6) not null, id bigint not null auto_increment, telefono varchar(20), apellido varchar(100) not null, nombre varchar(100) not null, email varchar(150) not null, password varchar(255) not null, rol enum ('ADMIN','USUARIO') not null, primary key (id)) engine=InnoDB;

alter table categorias add constraint UK7a8rqmhb8umdlquyud4j0j2xt unique (slug);
alter table inventario add constraint UKk6s7p1vvfeqoq5lc5f61ndoam unique (producto_id);
alter table pagos add constraint UK9ibmqk82q3wpbw2l1jcych5kv unique (pedido_id);
alter table productos add constraint UK8bwvjlh8b1xi4cc4ar819q61y unique (sku);
alter table resenas add constraint uk_resena_producto_usuario unique (producto_id, usuario_id);
alter table usuarios add constraint UKkfsp0s1tflm1cwlj8idhqsad0 unique (email);

alter table carrito_item add constraint FK9iygu3beausqdw3msi8n94t6l foreign key (carrito_id) references carrito (id);
alter table categorias add constraint FKodv3gb2ppjn0i5p44saqb67ky foreign key (categoria_padre_id) references categorias (id);
alter table direcciones add constraint FK54oy4k8b4ltgwmoq6kuocwhc7 foreign key (usuario_id) references usuarios (id);
alter table inventario add constraint FK8x46qgr3a08qk6y9e1vr6m26v foreign key (producto_id) references productos (id);
alter table pedido_item add constraint FKeyouxfvoi291lpo5168e6wpej foreign key (pedido_id) references pedido (id);
alter table productos add constraint FK2fwq10nwymfv7fumctxt9vpgb foreign key (categoria_id) references categorias (id);