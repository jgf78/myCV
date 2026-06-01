🧑‍💻 MyCV - Personal Portfolio & Analytics Dashboard
====================================================

MyCV es una aplicación web desarrollada con Spring Boot + Java 17 que actúa como:

🌐 Portfolio personal profesional

📊 Sistema de analítica de visitas estilo Google Analytics light

🔐 Panel de administración con login JWT

🤖 Sistema de detección de bots y ataques automatizados

🌍 Geolocalización de visitantes en tiempo real

🚀 Características principales
==============================

⚡ Backend con Spring Boot 3 + Java 17

🎨 Frontend con Thymeleaf + Bootstrap 5

🔐 Login de administración con JWT + sesión

📊 Dashboard con métricas en tiempo real:
- Total de visitas
- Últimas rutas visitadas
- Visitas por país 🌍
- Tráfico humano vs bots 🤖

🧠 Detección de bots:
- /wp-admin
- /.env
- /phpinfo.php
- /.git/config
- /struts/webconsole.html
- /ping_isolation_*

🗺️ Geolocalización de IPs
📧 Notificaciones de acceso al admin
🐳 Preparado para Docker


📊 Dashboard (Admin Panel)
=========================

📈 Visitas por país con banderas
📉 Donut chart: humanos vs bots
📍 Tabla de últimas visitas:
   - Ruta
   - IP
   - País
   - Ciudad
   - Fecha

🔐 Acceso protegido con JWT


🧠 Sistema de analítica
=======================

El sistema registra:

- Visitas reales de usuarios
- IP + User-Agent
- Referer
- GeoIP (país, ciudad, región)
- Detección de bots por heurística

⚠️ Las rutas internas (/admin, /auth, /login) no se contabilizan


🤖 Detección de bots
====================

Ejemplos filtrados automáticamente:

- /wp-admin/install.php
- /.env
- /phpinfo.php
- /.git/config
- /struts/webconsole.html
- /ping_isolation_*


🧩 Tecnologías
==============

Java 17           -> Backend
Spring Boot 3.x   -> Framework
Spring Security   -> Seguridad
JWT               -> Auth
Thymeleaf         -> Frontend
Bootstrap 5       -> UI
Chart.js          -> Gráficas
Maven             -> Build tool
Docker            -> Deploy


🏗️ Arquitectura del proyecto
=============================

mycv/
├── controller/
│   ├── auth/              (Login + JWT)
│   ├── AdminDashboard     (Panel admin)
│
├── service/
│   ├── WebVisitService
│   ├── GeoIpService
│   ├── JwtService
│
├── config/
│   ├── SecurityConfig
│   ├── JwtFilter
│   ├── VisitFilter
│
├── entity/
│   ├── WebVisit
│   ├── WebVisitCounter
│
├── repository/
│   ├── WebVisitRepository
│
├── templates/
│   ├── login.html
│   ├── dashboard.html
│
└── resources/
    └── application.yml


⚙️ Configuración local
======================

git clone https://github.com/jgf78/mycv.git
cd mycv

mvn clean install -DskipTests
mvn spring-boot:run

👉 http://localhost:8080


👤 Autor
========

Julián Gómez Fernández
💻 Java Developer
⚙️ Spring Boot · Docker · CI/CD
📊 Backend + observabilidad


📄 Licencia
===========

MIT License — uso libre y modificación.


🧠 Frase final
==============

“Este no es solo un CV. Es un sistema vivo de análisis del tráfico que lo visita.”