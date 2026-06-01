# 🧑‍💻 MyCV - Personal Portfolio & Analytics Dashboard

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-TemplateEngine-005F0F?logo=thymeleaf)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue?logo=docker)
![CI/CD](https://img.shields.io/badge/CI%2FCD-Jenkins-blueviolet?logo=jenkins)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

**MyCV** es una aplicación web desarrollada con **Spring Boot + Java 17** que actúa como:

- 🌐 Portfolio personal profesional
- 📊 Sistema de analítica de visitas estilo Google Analytics light
- 🔐 Panel de administración con login JWT
- 🤖 Sistema de detección de bots y ataques automatizados
- 🌍 Geolocalización de visitantes en tiempo real

---

## 🚀 Características principales

- ⚡ Backend con **Spring Boot 3 + Java 17**
- 🎨 Frontend con **Thymeleaf + Bootstrap 5**
- 🔐 Login de administración con **JWT + sesión**
- 📊 Dashboard con métricas en tiempo real:
  - Total de visitas
  - Últimas rutas visitadas
  - Visitas por país 🌍
  - Tráfico humano vs bots 🤖
- 🧠 Detección de bots y escáneres automáticos:
  - `/wp-admin`, `/.env`, `/phpinfo.php`, `/git/config`, etc.
- 🗺️ Geolocalización de IPs (país, ciudad, región)
- 📧 Sistema de notificaciones de acceso al panel admin
- 🐳 Preparado para Docker
- 🔄 Arquitectura extensible tipo “mini Google Analytics”

---

## 📊 Dashboard (Admin Panel)

El panel de administración incluye:

- 📈 Gráfico de visitas por país con banderas
- 📉 Gráfico donut de:
  - 👤 Usuarios reales
  - 🤖 Bots filtrados
- 📍 Tabla de últimas visitas con:
  - Ruta
  - IP
  - País
  - Ciudad
  - Fecha/hora
- 🔐 Acceso protegido mediante JWT

---

## 🧠 Sistema de analítica

El sistema registra:

- Visitas reales de usuarios
- IP de origen
- User-Agent simplificado
- Referer
- Geolocalización aproximada
- Detección de bots mediante heurística

⚠️ Las visitas a rutas internas (`/admin`, `/auth`, `/login`) NO se contabilizan.

---

## 🤖 Detección de bots

Se filtran automáticamente:

- Escáneres de vulnerabilidades
- Bots de indexación agresiva
- Intentos de acceso a rutas sensibles

Ejemplos bloqueados:
/wp-admin/install.php
/.env
/phpinfo.php
/.git/config
/struts/webconsole.html
/robots.txt (opcional análisis)
/ping_isolation_*


---

## 🧩 Tecnologías

| Tecnología | Uso |
|-------------|-----|
| Java 17 | Lenguaje principal |
| Spring Boot 3.x | Backend |
| Spring Security | Seguridad y filtros |
| JWT (jjwt) | Autenticación |
| Thymeleaf | Renderizado HTML |
| Bootstrap 5 | UI |
| Chart.js | Gráficas |
| Maven | Build tool |
| Docker | Contenedorización |

---

## 🏗️ Arquitectura del proyecto

mycv/
├── controller/
│ ├── auth/ # Login + JWT
│ ├── AdminDashboard # Panel admin
│
├── service/
│ ├── WebVisitService
│ ├── GeoIpService
│ ├── JwtService
│
├── config/
│ ├── SecurityConfig
│ ├── JwtFilter
│ ├── VisitFilter
│
├── entity/
│ ├── WebVisit
│ ├── WebVisitCounter
│
├── repository/
│ ├── WebVisitRepository
│
├── templates/
│ ├── login.html
│ ├── dashboard.html
│
└── resources/
└── application.yml


---

## ⚙️ Configuración local

### 1️⃣ Clonar repositorio

```bash
git clone https://github.com/jgf78/mycv.git
cd mycv

mvn clean install -DskipTests

mvn spring-boot:run

http://localhost:8080

👤 Autor

Julián Gómez Fernández
💻 Java Developer
⚙️ Spring Boot · Docker · CI/CD
📊 Backend + sistemas de observabilidad
🐧 Proyectos personales DevOps & Analytics

📄 Licencia

MIT License — libre uso y modificación.

🧠 “Este no es solo un CV. Es un sistema vivo de análisis del tráfico que lo visita.”