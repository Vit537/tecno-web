Guia de ejecucion y prueba - SERVICARGO

1) Requisitos
- Java 17 instalado
- Maven en PATH
- Acceso a Internet (para servidor de correo y BD)

2) Crear tablas (solo una vez)
Desde la carpeta "primer parcial":

  mvn -q -DskipTests compile exec:java "-Dexec.mainClass=servicargo.App"

Esto crea las tablas usuarios y almacenes en la BD configurada.

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

5) Notas
- Los comandos son insensibles a mayusculas/minusculas en el Subject.
- El daemon responde al remitente original.
- Si el correo no tiene Subject valido, responde con "Comando no reconocido".
- Si falta un campo obligatorio, el sistema devuelve error indicando el campo.
