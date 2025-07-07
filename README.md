# 📱 Vida Sana – Coach Digital de Hábitos Saludables

**Vida Sana** es una aplicación móvil Android desarrollada con Kotlin que permite a los usuarios registrar, visualizar y sincronizar sus hábitos saludables como hidratación, sueño, alimentación, ejercicio y emociones.

El sistema cuenta con:

- Aplicación Android (interfaz amigable)
- Backend en PHP + MySQL (REST API)
- Base de datos con autenticación de usuarios
- Sincronización en la nube y generación de reportes en PDF
- Portal web (opcional) para administrar hábitos

---

## 🧠 Objetivos del proyecto

- Promover el autocuidado físico y emocional usando tecnología móvil.
- Permitir el **registro diario de hábitos** personalizados.
- Sincronizar los datos a una base de datos remota mediante PHP y MySQL.
- Mostrar estadísticas por usuario y permitir la generación de reportes.

---

## ⚙️ Tecnologías utilizadas

| Herramienta     | Uso principal                        |
|------------------|--------------------------------------|
| Android Studio   | Desarrollo de la app móvil          |
| Kotlin           | Lenguaje principal de programación  |
| SQLite           | Base de datos local (offline)       |
| PHP              | Backend para APIs REST              |
| MySQL            | Base de datos remota (XAMPP)        |
| Retrofit/OkHttp  | Librerías para conexión HTTP        |

---

## 🚀 Requisitos para ejecutar el proyecto

### 🖥️ PC (Backend - PHP y MySQL)

1. Instalar [XAMPP](https://www.apachefriends.org/)
2. Configurar puertos:
   - Apache: `81`
   - MySQL: `3307`

3. Editar `config.inc.php` en la carpeta `phpMyAdmin`:
   ```php
   $cfg['Servers'][$i]['host'] = '127.0.0.1';
   $cfg['Servers'][$i]['port'] = '3307';
   $cfg['Servers'][$i]['AllowNoPassword'] = true;

    Colocar los archivos .php en htdocs/vida_sana/

    Importar el archivo vida_sana.sql en phpMyAdmin

📱 Android Studio (Frontend)

    Clonar este repositorio o descargar ZIP

    Abrir el proyecto en Android Studio

    Verificar en registro_usuario.php y login_usuario.php que las rutas apunten a:

    http://10.0.2.2:81/vida_sana/registro_usuario.php

    Ejecutar el emulador o conectar tu dispositivo físico

🧪 Funcionalidades principales

✅ Registro de usuarios
✅ Login por nombre de usuario y contraseña
✅ Registro local de hábitos
✅ Sincronización con la base de datos en la nube
✅ Visualización de estadísticas
✅ Generación de archivo PDF con los hábitos
✅ Cierre de sesión
🖼️ Capturas de pantalla


![registro_usuario](screenshots/registro_usuario.png)  
![logeo_usuario](screenshots/logeo_usuario.png)  
![menu_principal](screenshots/menu_principal.png)  
![registro_habito](screenshots/registro_habito.png)  
![estadisticas_habito](screenshots/estadisticas_habito.png)  
![pdf_habito](screenshots/pdf_habito.png)  
![sincronizacion_habito](screenshots/sincronizacion_habito.png)  
![portal_web](screenshots/portal_web.png)


📝 Nota: Coloca las imágenes en la carpeta screenshots/ dentro del repositorio.
📂 Estructura del repositorio

vida-sana/
├── app/                        # Código fuente de la app
├── backend/                    # Archivos PHP para el servidor
│   ├── login_usuario.php
│   ├── registro_usuario.php
│   ├── sincronizar.php
│   └── vida_sana.sql
├── screenshots/                # Capturas de pantalla (para README)
│   ├── registro_usuario.png
│   └── ...
├── .gitignore
├── README.md


## Base de Datos

Puedes importar esta estructura en **phpMyAdmin** o ejecutar directamente desde el panel SQL:

```sql
CREATE DATABASE vida_sana;
USE vida_sana;

CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_usuario VARCHAR(100) NOT NULL,
  clave VARCHAR(255) NOT NULL
);

CREATE TABLE habitos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tipo VARCHAR(50),
  cantidad VARCHAR(50),
  fecha DATETIME,
  usuario_id INT,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT INTO usuarios (id, nombre_usuario, clave) VALUES
(1, 'luis', '$2y$10$5bWyTuiQDyoYhB4glsK/EuIhOAIscyEetQ4zsblLpWkUf3jPxXQHW'),
(2, 'sebastian', '$2y$10$gDJYEhA56f22rSmla41l/.h.jETowKrm.0p6ZsoQx3y2EpXnI8UyW');

INSERT INTO habitos (id, tipo, cantidad, fecha, usuario_id) VALUES
(1, 'correr', '2km', '2025-07-04 23:21:00', 1),
(2, 'sueno', '8h', '2025-07-04 23:21:00', 1),
(3, 'agua', '2l', '2025-07-04 02:53:00', 1),
(4, 'comida', '200kcal', '2025-07-04 23:23:00', 2),
(5, 'agua', '1L', '2025-07-04 23:23:00', 2),
(6, 'sueno', '7h', '2025-07-04 23:23:00', 2);


✅ Créditos

Proyecto desarrollado por estudiantes del curso:
Desarrollo de Aplicaciones Móviles I – Cibertec
Ciclo V – Computación e Informática


