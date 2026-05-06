Guia de ejecucion y prueba - SERVICARGO

1) Requisitos
- Java 17 instalado
- Maven en PATH
- Acceso a Internet (para servidor de correo y BD)

2) Crear tablas (solo una vez)
Desde la carpeta "primer parcial":

  mvn -q -DskipTests compile exec:java "-Dexec.mainClass=servicargo.App"

Esto crea todas las tablas del sistema en la BD configurada.

3) Ejecutar el daemon de correo
Desde la carpeta "primer parcial":

  mvn -q -DskipTests compile exec:java "-Dexec.mainClass=servicargo.mail.MailDaemon"

Como funciona el daemon:
- Se conecta por POP3 al servidor configurado.
- Lee los correos entrantes.
- Procesa el Subject como comando (por ejemplo INSUSU[]).
- Lee el Body con formato clave=valor.
- Ejecuta el comando en la BD.
- Responde por SMTP al remitente del correo.
- Elimina el mensaje para no reprocesarlo.
- Espera 10 segundos y repite.

4) Como probar el envio de correo (cualquier persona)
Cualquier usuario puede enviar un correo a:

  grupo12sa@tecnoweb.org.bo

Ejemplo 1 - Crear usuario
Subject:
  INSUSU[]

Body:
  ci=12345678
  nombre=Carlos
  apellido=Lopez
  rol=vendedor
  telefono=59170000000
  email=carlos@mail.com
  password=secure123

Respuesta esperada:
  Usuario creado. ID: <numero>

Ejemplo 2 - Listar usuarios
Subject:
  LISUSU[]

Body (opcional):
  rol=vendedor

Respuesta esperada:
  Lista de usuarios.

Ejemplo 3 - Crear almacen
Subject:
  INSALM[]

Body:
  nombre=Almacen Central
  direccion=Av. Principal 123
  capacidad=1000
  responsable=Juan Perez
  telefono=59170000001

Respuesta esperada:
  Almacen creado. ID: <numero>

Ejemplo 4 - Crear producto
Subject:
  INSPRO[]

Body:
  codigo=P-001
  nombre=Caja de carton
  descripcion=Paquete ligero
  precio=25.50

Respuesta esperada:
  Producto creado. ID: <numero>

Ejemplo 5 - Registrar inventario
Subject:
  INSINV[]

Body:
  producto_id=1
  almacen_id=1
  cantidad=50

Respuesta esperada:
  Inventario creado. ID: <numero>

Ejemplo 6 - Crear encomienda
Subject:
  INSENC[]

Body:
  cliente_id=1
  producto_id=1
  almacen_id=1
  descripcion=Paquete a Santa Cruz
  peso=2.5
  estado=RECIBIDO

Respuesta esperada:
  Encomienda creada. ID: <numero>

Ejemplo 7 - Crear cotizacion
Subject:
  INSCOT[]

Body:
  cliente_id=1
  vendedor_id=2
  producto_id=1
  cantidad=3
  precio_unit=25.50
  estado=PENDIENTE

Respuesta esperada:
  Cotizacion creada. ID: <numero> Total: <monto>

Ejemplo 8 - Crear venta
Subject:
  INSVEN[]

Body:
  cliente_id=1
  vendedor_id=2
  cotizacion_id=1
  total=76.50
  estado=PENDIENTE

Respuesta esperada:
  Venta creada. ID: <numero>

Ejemplo 9 - Registrar pago
Subject:
  INSPAG[]

Body:
  venta_id=1
  monto=76.50
  metodo=EFECTIVO

Respuesta esperada:
  Pago registrado. ID: <numero>

Ejemplo 10 - Crear factura
Subject:
  INSFAC[]

Body:
  venta_id=1
  nit=1234567
  razon_social=Empresa Demo SRL
  total=76.50

Respuesta esperada:
  Factura creada. ID: <numero>

Ejemplo 11 - Reporte de ventas
Subject:
  REPVEN[]

Respuesta esperada:
  Total ventas: <numero>
  Monto total: <monto>

Ejemplo 12 - Reporte de encomiendas
Subject:
  REPENC[]

Respuesta esperada:
  RECIBIDO: <numero>
  ...

5) Notas
- Los comandos son insensibles a mayusculas/minusculas en el Subject.
- El daemon responde al remitente original.
- Si el correo no tiene Subject valido, responde con "Comando no reconocido".
- Si falta un campo obligatorio, el sistema devuelve error indicando el campo.
