-- Eliminación de tablas si ya existen
DROP TABLE IF EXISTS Pagos;
DROP TABLE IF EXISTS Promociones;
DROP TABLE IF EXISTS Ventas;
DROP TABLE IF EXISTS Inventarios;
DROP TABLE IF EXISTS Servicios;
DROP TABLE IF EXISTS Productos;
DROP TABLE IF EXISTS Usuarios;

-- Creación de la tabla Usuarios
CREATE TABLE Usuarios (
                          id SERIAL PRIMARY KEY,
                          nombre VARCHAR(100),
                          email VARCHAR(100),
                          telefono VARCHAR(20),
                          direccion VARCHAR(255),
                          tipo VARCHAR(20) CHECK (tipo IN ('administrativo', 'cliente', 'proveedor'))
);

-- Creación de la tabla Productos
CREATE TABLE Productos (
                           id SERIAL PRIMARY KEY,
                           proveedor_id INT,
                           nombre VARCHAR(100),
                           descripcion TEXT,
                           precio NUMERIC(10, 2),
                           stock INT,
                           FOREIGN KEY (proveedor_id) REFERENCES Usuarios(id)
);

-- Creación de la tabla Servicios
CREATE TABLE Servicios (
                           id SERIAL PRIMARY KEY,
                           nombre VARCHAR(100),
                           descripcion TEXT,
                           precio NUMERIC(10, 2)
);

-- Creación de la tabla Inventarios
CREATE TABLE Inventarios (
                             id SERIAL PRIMARY KEY,
                             producto_id INT,
                             cantidad INT,
                             tipo_movimiento VARCHAR(20),
                             fecha_movimiento VARCHAR(10),
                             FOREIGN KEY (producto_id) REFERENCES Productos(id)
);

-- Creación de la tabla Ventas
CREATE TABLE Ventas (
                        id SERIAL PRIMARY KEY,
                        usuario_id INT,
                        fecha VARCHAR(10),
                        item_id INT,
                        tipo_item VARCHAR(10), -- 'producto' o 'servicio'
                        cantidad INT,
                        precio_unitario NUMERIC(10, 2),
                        total NUMERIC(10, 2),
                        FOREIGN KEY (usuario_id) REFERENCES Usuarios(id)
);

-- Creación de la tabla Promociones
CREATE TABLE Promociones (
                             id SERIAL PRIMARY KEY,
                             producto_id INT,
                             nombre VARCHAR(100),
                             descripcion TEXT,
                             descuento NUMERIC(5, 2),
                             FOREIGN KEY (producto_id) REFERENCES Productos(id)
);

-- Creación de la tabla Pagos
CREATE TABLE Pagos (
                       id SERIAL PRIMARY KEY,
                       venta_id INT,
                       monto NUMERIC(10, 2),
                       fecha VARCHAR(10),
                       metodo_pago VARCHAR(20) CHECK (metodo_pago IN ('qr', 'tarjeta', 'efectivo', 'transferencia')),
                       FOREIGN KEY (venta_id) REFERENCES Ventas(id)
);

-- Inserción de datos en la tabla Usuarios
INSERT INTO Usuarios (nombre, email, telefono, direccion, tipo) VALUES
                                                                    ('Juan Pérez', 'juan.perez@example.com', '1234567890', 'Calle Falsa 123, Ciudad', 'cliente'),
                                                                    ('María García', 'maria.garcia@example.com', '0987654321', 'Avenida Siempre Viva 456, Ciudad', 'cliente'),
                                                                    ('Carlos López', 'carlos.lopez@example.com', '1122334455', 'Boulevard de los Sueños 789, Ciudad', 'administrativo'),
                                                                    ('Proveedor A', 'proveedorA@example.com', '1112223333', 'Dirección Proveedor A', 'proveedor'),
                                                                    ('Proveedor B', 'proveedorB@example.com', '4445556666', 'Dirección Proveedor B', 'proveedor'),
                                                                    ('Ana Torres', 'ana.torres@example.com', '2223334445', 'Calle de las Flores 101, Ciudad', 'cliente'),
                                                                    ('Luis Martínez', 'luis.martinez@example.com', '3334445556', 'Calle de la Libertad 202, Ciudad', 'cliente'),
                                                                    ('Mariana López', 'mariana.lopez@example.com', '4445556667', 'Calle de la Esperanza 303, Ciudad', 'administrativo'),
                                                                    ('José Ramírez', 'jose.ramirez@example.com', '5556667778', 'Avenida de la Paz 404, Ciudad', 'cliente'),
                                                                    ('Verónica Ruiz', 'veronica.ruiz@example.com', '6667778889', 'Calle de la Amistad 505, Ciudad', 'proveedor');

-- Inserción de datos en la tabla Productos
INSERT INTO Productos (proveedor_id, nombre, descripcion, precio, stock) VALUES
                                                                             (4, 'Vacuna Canina', 'Vacuna para perros contra la rabia', 20.00, 50),
                                                                             (5, 'Alimento para Gatos', 'Alimento balanceado para gatos adultos', 30.00, 100),
                                                                             (4, 'Collar Antipulgas', 'Collar para protección contra pulgas y garrapatas', 15.00, 75),
                                                                             (5, 'Arena para Gatos', 'Arena absorbente para gatos', 10.00, 200),
                                                                             (4, 'Juguete para Perros', 'Juguete resistente para perros', 25.00, 60),
                                                                             (5, 'Snacks para Gatos', 'Snacks sabor pollo para gatos', 5.00, 150),
                                                                             (4, 'Cama para Perros', 'Cama cómoda para perros', 45.00, 30),
                                                                             (5, 'Comedero para Gatos', 'Comedero de acero inoxidable', 20.00, 80),
                                                                             (4, 'Correa para Perros', 'Correa ajustable para perros', 10.00, 90),
                                                                             (5, 'Tijeras para Pelo', 'Tijeras para corte de pelo de mascotas', 35.00, 40);

-- Inserción de datos en la tabla Servicios
INSERT INTO Servicios (nombre, descripcion, precio) VALUES
                                                        ('Consulta Veterinaria', 'Consulta médica para animales domésticos', 50.00),
                                                        ('Corte de Pelo', 'Servicio de corte de pelo para mascotas', 25.00),
                                                        ('Vacunación', 'Servicio de vacunación para mascotas', 40.00),
                                                        ('Baño y Despulgado', 'Baño y tratamiento antipulgas para mascotas', 30.00),
                                                        ('Revisión Médica', 'Revisión médica general para mascotas', 45.00),
                                                        ('Entrenamiento Básico', 'Entrenamiento básico para perros', 60.00),
                                                        ('Cuidado de Mascotas', 'Cuidado y alimentación de mascotas', 50.00),
                                                        ('Paseo de Mascotas', 'Servicio de paseo para perros', 20.00),
                                                        ('Consulta Nutricional', 'Consulta sobre nutrición para mascotas', 35.00),
                                                        ('Desparasitante', 'Servicio de desparasitante para mascotas', 20.00);

-- Inserción de datos en la tabla Inventarios
INSERT INTO Inventarios (producto_id, cantidad, tipo_movimiento, fecha_movimiento) VALUES
                                                                                       (1, 50, 'entrada', '2024-10-10'),
                                                                                       (2, 100, 'entrada', '2024-10-11'),
                                                                                       (3, 75, 'entrada', '2024-10-12'),
                                                                                       (4, 200, 'entrada', '2024-10-13'),
                                                                                       (5, 60, 'entrada', '2024-10-14'),
                                                                                       (6, 150, 'entrada', '2024-10-15'),
                                                                                       (7, 30, 'entrada', '2024-10-16'),
                                                                                       (8, 80, 'entrada', '2024-10-17'),
                                                                                       (9, 90, 'entrada', '2024-10-18'),
                                                                                       (10, 40, 'entrada', '2024-10-19');

-- Inserción de datos en la tabla Ventas
INSERT INTO Ventas (usuario_id, fecha, item_id, tipo_item, cantidad, precio_unitario, total) VALUES
                                                                                                 (1, '2024-10-12', 1, 'producto', 2, 20.00, 40.00),
                                                                                                 (2, '2024-10-13', 2, 'producto', 1, 30.00, 30.00),
                                                                                                 (3, '2024-10-14', 3, 'producto', 1, 15.00, 15.00),
                                                                                                 (1, '2024-10-15', 4, 'producto', 1, 25.00, 25.00),
                                                                                                 (2, '2024-10-16', 5, 'producto', 2, 10.00, 20.00),
                                                                                                 (3, '2024-10-17', 6, 'servicio', 1, 50.00, 50.00),
                                                                                                 (1, '2024-10-18', 7, 'servicio', 1, 25.00, 25.00),
                                                                                                 (2, '2024-10-19', 8, 'servicio', 1, 30.00, 30.00),
                                                                                                 (3, '2024-10-20', 9, 'producto', 1, 10.00, 10.00),
                                                                                                 (1, '2024-10-21', 10, 'servicio', 1, 60.00, 60.00);

-- Inserción de datos en la tabla Promociones
INSERT INTO Promociones (producto_id, nombre, descripcion, descuento) VALUES
                                                                          (1, 'Descuento de Vacuna', 'Descuento especial para vacunas', 10.00),
                                                                          (2, 'Descuento en Alimento', 'Oferta de 10% en alimentos para gatos', 5.00),
                                                                          (3, 'Promoción Collar', 'Compra un collar y obtén 10% de descuento en el siguiente', 10.00),
                                                                          (4, 'Arena Gratuita', 'Compra dos bolsas de arena y recibe una gratis', 0.00),
                                                                          (5, 'Juguete por Compras', 'Compra un juguete y recibe un 20% de descuento en el siguiente', 20.00),
                                                                          (6, 'Snacks de Regalo', 'Compra alimentos y recibe snacks para gatos de regalo', 0.00),
                                                                          (7, 'Cama con Descuento', 'Descuento del 15% en camas para perros', 15.00),
                                                                          (8, 'Comedero en Oferta', 'Compra un comedero y obtén un 10% de descuento en el siguiente', 10.00),
                                                                          (9, 'Correa Gratis', 'Compra dos correas y recibe una gratis', 0.00),
                                                                          (10, 'Descuento en Tijeras', '20% de descuento en tijeras para pelo de mascotas', 20.00);

-- Inserción de datos en la tabla Pagos
INSERT INTO Pagos (venta_id, monto, fecha, metodo_pago) VALUES
                                                            (1, 40.00, '2024-10-12', 'qr'),
                                                             (3, 15.00, '2024-10-14', 'efectivo'),
                                                         (2, 30.00, '2024-10-13', 'tarjeta'),
                                                                                            (4, 25.00, '2024-10-15', 'transferencia'),
                                                            (5, 20.00, '2024-10-16', 'qr'),
                                                            (6, 50.00, '2024-10-17', 'tarjeta'),
                                                            (7, 25.00, '2024-10-18', 'efectivo'),
                                                            (8, 30.00, '2024-10-19', 'transferencia'),
                                                            (9, 10.00, '2024-10-20', 'qr'),
                                                            (10, 60.00, '2024-10-21', 'tarjeta');

-- Creación de la tabla Help
DROP TABLE IF EXISTS Help;
CREATE TABLE Help (
                      id SERIAL PRIMARY KEY,
                      cu VARCHAR(100) NOT NULL,
                      accion VARCHAR(255) NOT NULL,
                      parametros VARCHAR(255) NOT NULL,
                      ejemplo TEXT
);

-- Inserción de datos en la tabla Help
INSERT INTO Help (cu, accion, parametros, ejemplo) VALUES
-- CU1: Gestionar Usuarios
('CU1: Gestionar Usuarios', 'Listar Usuarios', 'usuario mostrar', 'usuario mostrar'),
('CU1: Gestionar Usuarios', 'Registrar Usuario (administrativo, proveedor, cliente)', 'usuario agregar [nombre; email; telefono; direccion; tipo]', 'usuario agregar [Juan; juan.perez@example.com; 1234567890; Calle Falsa 123; admin]'),
('CU1: Gestionar Usuarios', 'Editar Usuario', 'usuario editar [usuario_id; nombre; email; telefono; direccion; tipo]', 'usuario editar [1; Juan; juan.perez@example.com; 0987654321; Calle Falsa 456; admin]'),
('CU1: Gestionar Usuarios', 'Eliminar Usuario', 'usuario eliminar [usuario_id]', 'usuario eliminar [1]'),
('CU1: Gestionar Usuarios', 'Ver Usuario', 'usuario ver [usuario_id]', 'usuario ver [1]'),
('CU1: Gestionar Usuarios', 'Reporte de Usuario', 'usuario reporte', 'usuario reporte'),

-- CU2: Gestionar Productos
('CU2: Gestionar Productos', 'Listar Productos', 'producto mostrar', 'producto mostrar'),
('CU2: Gestionar Productos', 'Registrar Producto', 'producto agregar [proveedor_id; nombre; descripción; precio; stock]', 'producto agregar [4; Producto1; Descripción del producto1; 100.00; 50]'),
('CU2: Gestionar Productos', 'Editar Producto', 'producto modificar [producto_id; proveedor_id; nombre; descripción; precio; stock]', 'producto modificar [1; 4; Producto1; Descripción modificada; 110.00; 60]'),
('CU2: Gestionar Productos', 'Eliminar Producto', 'producto eliminar [producto_id]', 'producto eliminar [1]'),
('CU2: Gestionar Productos', 'Ver Producto', 'producto ver [producto_id]', 'producto ver [1]'),
('CU2: Gestionar Productos', 'Reporte de Productos', 'producto reporte', 'producto reporte'),

-- CU3: Gestionar Servicios
('CU3: Gestionar Servicios', 'Listar Servicios', 'servicio mostrar', 'servicio mostrar'),
('CU3: Gestionar Servicios', 'Registrar Servicio', 'servicio agregar [nombre; descripcion; precio]', 'servicio agregar [Servicio1; Descripción del servicio1; 200.00]'),
('CU3: Gestionar Servicios', 'Editar Servicio', 'servicio modificar [servicio_id; nombre; descripcion; precio]', 'servicio modificar [1; Servicio1; Descripción modificada; 210.00]'),
('CU3: Gestionar Servicios', 'Eliminar Servicio', 'servicio eliminar [servicio_id]', 'servicio eliminar [1]'),
('CU3: Gestionar Servicios', 'Ver Servicio', 'servicio ver [servicio_id]', 'servicio ver [1]'),
('CU3: Gestionar Servicios', 'Reporte de Servicio', 'servicio reporte', 'servicio reporte'),

-- CU4: Gestionar Inventarios
('CU4: Gestionar Inventarios', 'Listar Inventarios', 'inventario mostrar', 'inventario mostrar'),
('CU4: Gestionar Inventarios', 'Registrar Inventario', 'inventario agregar [producto_id; cantidad; tipo_movimiento; fecha_movimiento]', 'inventario agregar [1; 10; entrada; 2023-01-01]'),
('CU4: Gestionar Inventarios', 'Editar Inventario', 'inventario modificar [inventario_id; producto_id; cantidad; tipo_movimiento; fecha_movimiento]', 'inventario modificar [1; 1; 15; salida; 2023-01-02]'),
('CU4: Gestionar Inventarios', 'Eliminar Inventario', 'inventario eliminar [inventario_id]', 'inventario eliminar [1]'),
('CU4: Gestionar Inventarios', 'Ver Inventario', 'inventario ver [inventario_id]', 'inventario ver [1]'),
('CU4: Gestionar Inventarios', 'Reporte de Inventario', 'inventario reporte', 'inventario reporte'),

-- CU5: Gestionar Ventas (con los parámetros corregidos)
('CU5: Gestionar Ventas', 'Listar Ventas', 'venta mostrar', 'venta mostrar'),
('CU5: Gestionar Ventas', 'Registrar Venta', 'venta agregar [usuario_id; fecha; item_id; tipo_item; cantidad; precio_unitario; total]', 'venta agregar [1; 2023-01-01; 1; producto; 5; 20.00; 100.00]'),
('CU5: Gestionar Ventas', 'Editar Venta', 'venta modificar [venta_id; usuario_id; fecha; item_id; tipo_item; cantidad; precio_unitario; total]', 'venta modificar [1; 1; 2023-01-02; 1; producto; 10; 18.00; 180.00]'),
('CU5: Gestionar Ventas', 'Eliminar Venta', 'venta eliminar [venta_id]', 'venta eliminar [1]'),
('CU5: Gestionar Ventas', 'Ver Venta', 'venta ver [venta_id]', 'venta ver [1]'),
('CU5: Gestionar Ventas', 'Reporte de Ventas', 'venta reporte', 'venta reporte'),

-- CU6: Gestionar Promociones
('CU6: Gestionar Promociones', 'Listar Promociones', 'promocion mostrar', 'promocion mostrar'),
('CU6: Gestionar Promociones', 'Registrar Promociones', 'promocion agregar [producto_id; nombre; descripcion; descuento]', 'promocion agregar [1; Promoción1; Descripción de la promoción1; 10%]'),
('CU6: Gestionar Promociones', 'Editar Promociones', 'promocion modificar [promocion_id; producto_id; nombre; descripcion; descuento]', 'promocion modificar [1; 1; Promoción1; Descripción modificada; 15%]'),
('CU6: Gestionar Promocion', 'Eliminar Promocion', 'promocion eliminar [promocion_id]', 'promocion eliminar [1]'),
('CU6: Gestionar Promocion', 'Ver Promocion', 'promocion ver [promocion_id]', 'promocion ver [1]'),
('CU6: Gestionar Promociones', 'Reporte de Promociones', 'promocion reporte', 'promocion reporte'),

-- CU7: Gestionar Pagos
('CU7: Gestionar Pagos', 'Listar Pagos', 'pago mostrar', 'pago mostrar'),
('CU7: Gestionar Pagos', 'Registrar Pago', 'pago agregar [venta_id; monto; fecha; metodo_pago]', 'pago agregar [1; 100.00; 2023-01-01; tarjeta]'),
('CU7: Gestionar Pagos', 'Editar Pago', 'pago modificar [pago_id; venta_id; monto; fecha; metodo_pago]', 'pago modificar [1; 1; 110.00; 2023-01-02; efectivo]'),
('CU7: Gestionar Pagos', 'Eliminar Pago', 'pago eliminar [pago_id]', 'pago eliminar [1]'),
('CU7: Gestionar Pagos', 'Ver Pago', 'pago ver [pago_id]', 'pago ver [1]'),
('CU7: Gestionar Pagos', 'Reporte de Pago', 'pago reporte', 'pago reporte'),

-- CU8: Reportes y Estadísticas
('CU8: Reportes y Estadísticas', 'Reporte de Usuario', 'usuario reporte', 'usuario reporte'),
('CU8: Reportes y Estadísticas', 'Reporte de Productos', 'producto reporte', 'producto reporte'),
('CU8: Reportes y Estadísticas', 'Reporte de Servicios', 'servicio reporte', 'servicio reporte'),
('CU8: Reportes y Estadísticas', 'Reporte de Inventario', 'inventario reporte', 'inventario reporte'),
('CU8: Reportes y Estadísticas', 'Reporte de Ventas', 'venta reporte', 'venta reporte'),
('CU8: Reportes y Estadísticas', 'Reporte de Promociones', 'promocion reporte', 'promocion reporte'),
('CU8: Reportes y Estadísticas', 'Reporte de Pago', 'pago reporte', 'pago reporte');