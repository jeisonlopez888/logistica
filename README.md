# 🚚 Sistema de Logística y Entregas Same-Day

> Plataforma integral para gestión de envíos urbanos con seguimiento en tiempo real

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java](https://img.shields.io/badge/Java-17-orange)]()
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue)]()
[![Maven](https://img.shields.io/badge/Maven-3.8+-red)]()

---

## 📋 Descripción

Sistema de logística desarrollado en **JavaFX** que permite gestionar envíos, repartidores, usuarios y generar métricas operativas. Implementa una **arquitectura en capas** con separación clara de responsabilidades siguiendo los principios **SOLID** y múltiples **patrones de diseño**.

### Características Principales

- ✅ **Gestión de Usuarios:** Registro, login, perfiles, direcciones (múltiples por usuario)
- ✅ **Gestión de Envíos:** Cotización, creación, edición, seguimiento, estados
- ✅ **Gestión de Repartidores:** Asignación automática por zonas
- ✅ **Sistema de Pagos:** Múltiples métodos, confirmación automática, facturas
- ✅ **Rastreo Público:** Consulta de estado sin login
- ✅ **Panel de Métricas:** Visualización con JavaFX Charts, tablas y gráficos
- ✅ **Reportes:** Exportación Excel/PDF con filtros de fecha
- ✅ **Servicios Adicionales:** Prioridad, seguro, frágil, firma requerida
- ✅ **Sistema de Notificaciones:** Observer pattern con WhatsApp, Email, SMS
- ✅ **Tarifas Dinámicas:** Express y Normal, configurables por administrador
- ✅ **Gestión de Incidencias:** Registro y seguimiento de problemas

---

## 🏗️ Arquitectura

### Capas del Sistema

```
┌─────────────────────────────────────────┐
│   PRESENTACIÓN (Controllers)             │  ← Solo validación, usa DTOs
├─────────────────────────────────────────┤
│   FACHADA (LogisticaFacade)             │  ← Convierte DTOs ↔ Entities
├─────────────────────────────────────────┤
│   NEGOCIO (Services)                    │  ← Lógica de negocio
├─────────────────────────────────────────┤
│   PERSISTENCIA (DataStore)              │  ← Datos en memoria
└─────────────────────────────────────────┘
```

### Componentes Principales

**Controladores (25):**
- `LoginController`, `RegistroController`, `RastreoController`
- `UserController`, `UserAdminController`, `AdminController`
- `CrearEnvioController`, `CrearEnvioUserController`, `CrearEnvioAdminController`
- `EditarEnvioController`, `EditarUsuarioUserController`, `EditarUsuarioAdminController`
- `HistorialEnviosController`, `HistorialEnviosUserController`, `HistorialEnviosAdminController`
- `UsuariosController`, `CrearUsuarioController`, `CrearUsuarioAdminController`
- `RepartidoresController`, `CrearRepartidorController`
- `PagosController`, `TarifasController`, `ReportesController`
- `MetricasController`, `DetalleTarifaController`, `AdminsController`

**Servicios (7):**
- `UsuarioService` - Gestión de usuarios y autenticación
- `EnvioService` - Gestión de envíos y estados
- `PagoService` - Gestión de pagos y confirmaciones
- `TarifaService` - Cálculo de tarifas y desgloses
- `RepartidorService` - Gestión de repartidores y asignación
- `MetricasService` - Cálculo de métricas operativas
- `NotificationService` - Sistema de notificaciones (Observer)

**DTOs (6):**
- `UsuarioDTO`, `EnvioDTO`, `PagoDTO`
- `RepartidorDTO`, `TarifaDTO`, `DireccionDTO`

**Mappers (6):**
- `UsuarioMapper`, `EnvioMapper`, `PagoMapper`
- `RepartidorMapper`, `TarifaMapper`, `DireccionMapper`
- Conversión bidireccional DTO ↔ Entity

**Observer Pattern:**
- `Subject`, `Observer` (interfaces)
- `EnvioSubject` - Sujeto observable
- `TipoEvento` - Enum de tipos de eventos
- `NotificationService` - Observador que envía notificaciones

---

## 🚀 Instalación y Ejecución

### Requisitos Previos

- **Java JDK 17** o superior
- **Maven 3.8+**
- **JavaFX 17+**

### Clonar y Compilar

```bash
# Navegar al directorio
cd C:\Users\User\IdeaProjects\logistica

# Compilar el proyecto
mvn clean compile

# Empaquetar (genera JAR)
mvn package -DskipTests
```

### Ejecutar la Aplicación

```bash
# Opción 1: Con Maven
mvn javafx:run

# Opción 2: Desde el JAR (si tienes JavaFX configurado)
java -jar target/Logistica-1.0-SNAPSHOT.jar
```

---

## 🔐 Credenciales de Prueba

### Usuarios Regulares

| Email | Password | Descripción |
|-------|----------|-------------|
| carlos@uniquindio.edu | 12345 | Usuario con 2 direcciones |
| maria@uniquindio.edu | maria123 | Usuario con envíos activos |
| lucia@uniquindio.edu | lucia789 | Usuario con historial |
| pepito@uniquindio.edu | pepito123 | Usuario con incidencias |
| sofia@uniquindio.edu | sofia456 | Usuario nuevo |

### Administradores

| Email | Password | Descripción |
|-------|----------|-------------|
| ana@uniquindio.edu | admin123 | Administrador principal |
| pedro@uniquindio.edu | admin456 | Administrador secundario |

---

## 📊 Funcionalidades por Perfil

### 👤 Usuario Regular

- **Crear envíos** con cotización en tiempo real
- **Editar/Cancelar** envíos en estado SOLICITADO
- **Consultar historial** de todos sus envíos con filtros
- **Rastrear estado** de envíos (Solicitado, Asignado, En Ruta, Entregado, Incidencia)
- **Editar perfil** y gestionar direcciones (múltiples)
- **Ver detalle de tarifa** antes de confirmar
- **Imprimir factura** de envíos confirmados
- **Descargar reportes** personales
- **Elegir canal de notificación** (WhatsApp, Email, SMS) al crear envío

### 👨‍💼 Administrador

Todas las funcionalidades de usuario, más:

- **Gestionar usuarios** (crear, editar, eliminar, ver panel de usuario)
- **Gestionar repartidores** (CRUD completo)
- **Asignar/Reasignar** repartidores a envíos
- **Cambiar estados** de envíos (con notificaciones automáticas)
- **Registrar incidencias** con descripción y fecha
- **Ver métricas** operativas con gráficos interactivos
- **Generar reportes** globales del sistema (Excel/PDF)
- **Configurar tarifas** del sistema (Express/Normal)
- **Ver panel de usuario** desde administración
- **Imprimir métricas** en PDF

---

## 🎨 Características de Diseño

### Interfaz Moderna

- **Gradientes atractivos** con colores corporativos
- **Efectos de sombra** para profundidad visual
- **Tarjetas con transparencia** para contenido
- **Botones con estados hover** (cursor: hand)
- **Iconos emoji** para mejor usabilidad
- **Diseño responsive** adaptable
- **Ventanas optimizadas** sin espacios vacíos

### Paleta de Colores

| Color | Uso | Hex |
|-------|-----|-----|
| 🔵 Azul | Acciones principales, confianza | #0072ff, #42A5F5 |
| 🟢 Verde | Confirmaciones, éxito | #43A047, #66BB6A |
| 🟠 Naranja | Urgencia, advertencias | #FF6F00, #FFB300 |
| 🔴 Rojo | Eliminaciones, incidencias | #D32F2F, #EF5350 |
| ⚫ Gris | Acciones secundarias | #757575, #424242 |
| 🔷 Cyan | Información, detalles | #26C6DA, #0097A7 |
| 🟣 Morado | Detalles, tarifas | #AB47BC, #8E24AA |

---

## 🔧 Tecnologías Utilizadas

### Core
- **Java 17** - Lenguaje de programación
- **JavaFX 17** - Framework de interfaz gráfica
- **Maven** - Gestión de dependencias

### Librerías
- **Apache POI** - Exportación de reportes Excel (.xlsx)
- **PDFBox** - Exportación de reportes PDF (.pdf)
- **ControlsFX** - Controles adicionales JavaFX
- **ValidatorFX** - Validación de formularios
- **Ikonli** - Iconos vectoriales
- **BootstrapFX** - Estilos CSS
- **TilesFX** - Componentes visuales
- **FXGL** - Utilidades gráficas

---

## 📦 Patrones de Diseño

### Creacionales
- **Singleton:** `DataStore`, `LogisticaFacade`
- **Builder:** `EnvioBuilder`
- **Factory:** `EntityFactory`

### Estructurales
- **Facade:** `LogisticaFacade` (punto único de acceso)
- **DTO:** 6 DTOs implementados para transferencia de datos
- **Mapper:** Conversión bidireccional DTO ↔ Entity

### Comportamentales
- **Strategy:** `MetodoPago`, cálculo de tarifas
- **Observer:** Sistema de notificaciones (`EnvioSubject`, `NotificationService`)
- **Template Method:** Controllers abstractos (`HistorialEnviosController`, `CrearEnvioController`)

---

## 🛡️ Principios SOLID

✅ **SRP:** Una responsabilidad por clase  
✅ **OCP:** Abierto a extensión, cerrado a modificación  
✅ **LSP:** Sustitución de tipos (DTOs, herencia)  
✅ **ISP:** Interfaces segregadas e implícitas  
✅ **DIP:** Dependencia de abstracciones (Facade)

---

## 📈 Datos de Ejemplo

El sistema incluye datos de ejemplo completos:

- **7 Usuarios** (2 administradores, 5 regulares)
- **7 Repartidores** (6 disponibles, zonas Norte/Centro/Sur)
- **30+ Envíos** (todos los estados: SOLICITADO, CONFIRMADO, ASIGNADO, EN_RUTA, ENTREGADO, CANCELADO, INCIDENCIA)
- **30+ Pagos** (todos los métodos: TARJETA_CREDITO, PSE, EFECTIVO, TRANSFERENCIA)
- **2 Tarifas** configuradas (Express y Normal)
- **Múltiples direcciones** por usuario

---

## 📊 Panel de Métricas

El panel de administración incluye:

- **Tiempo promedio de entrega** (calculado en horas)
- **Ingresos totales** del sistema
- **Total de envíos** y **usuarios**
- **Incidencias reportadas**
- **Gráficos interactivos:**
    - LineChart: Evolución de tiempos de entrega
    - BarChart: Servicios adicionales más usados
    - PieChart: Distribución de servicios
    - BarChart: Ingresos por período
    - BarChart: Incidencias por zona
- **Tablas de datos** con información detallada
- **Exportación a PDF** de métricas

---

## 📁 Estructura de Directorios

```
logistica/
├── src/main/java/co/edu/uniquindio/logistica/
│   ├── MainApp.java
│   ├── factory/           # Factory Pattern
│   │   └── EntityFactory.java
│   ├── facade/            # Facade Pattern (Singleton)
│   │   └── LogisticaFacade.java
│   ├── model/             # Entidades y DTOs
│   │   ├── DTO/           # Data Transfer Objects (6)
│   │   ├── EnvioBuilder.java
│   │   └── [Entidades: Usuario, Envio, Pago, etc.]
│   ├── observer/          # Observer Pattern
│   │   ├── Subject.java
│   │   ├── Observer.java
│   │   ├── EnvioSubject.java
│   │   └── TipoEvento.java
│   ├── service/           # Lógica de negocio (7 servicios)
│   │   ├── UsuarioService.java
│   │   ├── EnvioService.java
│   │   ├── PagoService.java
│   │   ├── TarifaService.java
│   │   ├── RepartidorService.java
│   │   ├── MetricasService.java
│   │   └── NotificationService.java
│   ├── store/             # DataStore (Singleton)
│   │   └── DataStore.java
│   ├── ui/                # Controladores JavaFX (25)
│   │   └── [25 controladores]
│   ├── util/              # Utilidades, Mappers, Validación
│   │   ├── Mappers/       # Conversión DTO ↔ Entity (6)
│   │   ├── PasswordUtil.java
│   │   ├── ReportUtil.java
│   │   ├── Sesion.java
│   │   └── ValidacionUtil.java
│   └── test/              # Pruebas
│       └── TestFXMLCargadores.java
├── src/main/resources/
│   └── fxml/              # 24 vistas FXML
├── pom.xml                # Configuración Maven
├── module-info.java       # Configuración de módulos Java
├── README.md              # Este archivo
├── CUMPLIMIENTO_REQUERIMIENTOS.md
└── PROYECTO_COMPLETADO.md
```

---

## 🎯 Casos de Uso Principales

### Como Usuario Regular

1. **Registrarse** en el sistema
2. **Iniciar sesión** con email y contraseña
3. **Crear un envío:**
    - Ingresar origen y destino (o seleccionar de direcciones guardadas)
    - Seleccionar peso y dimensiones (si es caja)
    - Elegir tipo de tarifa (Express o Normal)
    - Elegir servicios adicionales (prioridad, seguro, frágil, firma requerida)
    - **Cotizar** para ver el costo total estimado
    - **Ver detalle de tarifa** con desglose completo
    - Elegir canal de notificación (WhatsApp, Email, SMS)
    - Elegir método de pago
    - **Confirmar y pagar**
    - Opción de imprimir factura
4. **Ver historial** de envíos con filtros por fecha y estado
5. **Rastrear** estado de envíos
6. **Editar perfil** y gestionar direcciones (múltiples)
7. **Editar envíos** en estado SOLICITADO
8. **Ver detalle de tarifa** de envíos existentes
9. **Descargar reportes** personales

### Como Administrador

1. **Gestionar usuarios** del sistema
    - Crear, editar, eliminar usuarios
    - Ver panel de usuario seleccionado
2. **Gestionar repartidores:**
    - Crear, editar, eliminar
    - Ver disponibilidad por zonas
3. **Gestionar envíos:**
    - Asignar/Reasignar repartidores
    - Cambiar estados (con notificaciones automáticas)
    - Registrar incidencias
    - Editar envíos
    - Ver detalle de tarifa
    - Imprimir facturas
4. **Ver métricas operativas:**
    - Gráficos de rendimiento
    - Tiempos promedios
    - Ingresos totales
    - Tablas de datos
    - Exportar métricas a PDF
5. **Configurar tarifas** del sistema (Express/Normal)
6. **Generar reportes globales** (Excel/PDF)
7. **Gestionar pagos** del sistema

---

## 🔍 Rastreo Público

Cualquier persona puede rastrear un envío sin necesidad de login:

1. Desde la pantalla de login, clic en **"🔍 Rastrear Envío"**
2. Ingresar el ID del envío (ejemplos: 1, 2, 3, etc.)
3. Ver estado actual, origen, destino, repartidor asignado, fechas e incidencias

---

## 📊 Reportes Disponibles

### Tipos de Reporte
- **Usuarios:** Lista completa con todos los datos
- **Envíos:** Detalles completos de envíos (filtrable por fecha)
- **Pagos:** Registro completo de pagos (filtrable por fecha)
- **Repartidores:** Lista completa de personal
- **Reporte General:** Consolidado completo del sistema
- **Métricas:** Panel de métricas operativas en PDF

### Formatos
- **Excel (.xlsx):** Con Apache POI
- **PDF (.pdf):** Con PDFBox

### Filtros
- Rango de fechas (opcional)
- Por tipo de reporte
- Por estado de envío

---

## 🔔 Sistema de Notificaciones

Implementado con el patrón **Observer**:

- **Notificaciones al crear envío:**
    - Usuario recibe notificación
    - Administrador recibe notificación
    - Canal seleccionable (WhatsApp, Email, SMS)

- **Notificaciones al cambiar estado:**
    - Usuario recibe notificación
    - Administrador recibe notificación
    - Repartidor asignado recibe notificación
    - Impresión en consola de todas las notificaciones

- **Eventos notificados:**
    - Creación de envío
    - Cambio de estado
    - Registro de incidencia
    - Entrega completada

---

## 🛠️ Validaciones Implementadas

Todas las validaciones centralizadas en `ValidacionUtil`:

- Email: Formato válido (regex)
- Teléfono: 10 dígitos
- Nombre: 3-100 caracteres
- Password: Mínimo 4 caracteres
- Peso: 0-1000 kg
- Volumen: 0-10 m³
- Monto: Valores positivos
- Campos requeridos
- Validación de direcciones
- Validación de zonas (Sur, Centro, Norte)

---

## 📝 Estados de Envío

1. **SOLICITADO** - Creado, pendiente de pago
2. **CONFIRMADO** - Pagado, esperando asignación
3. **ASIGNADO** - Repartidor asignado automáticamente
4. **EN_RUTA** - En proceso de entrega
5. **ENTREGADO** - Completado exitosamente
6. **CANCELADO** - Cancelado por el usuario
7. **INCIDENCIA** - Problema reportado

---

## 💳 Métodos de Pago

- **TARJETA_CREDITO** - Tarjeta de crédito
- **PSE** - Pagos Seguros en Línea
- **EFECTIVO** - Pago en efectivo
- **TRANSFERENCIA** - Transferencia bancaria

---

## 📊 Servicios Adicionales

- **⚡ Prioridad:** Entrega más rápida (+recargo)
- **🛡️ Seguro:** Protección del paquete (+recargo)
- **⚠️ Frágil:** Manejo especial (+recargo)
- **✍️ Firma Requerida:** Requiere firma del destinatario (+recargo)

---

## 🎯 Tarifas

- **Express:** Entrega rápida con recargo
- **Normal:** Entrega estándar
- Configurables por administrador
- Cálculo automático de costos con desglose

---

## 🎓 Contexto Académico

**Universidad:** Universidad del Quindío  
**Materia:** Programación 2 Orientada a Objetos  
**Tema:** Aplicación de patrones de diseño y arquitectura en capas  
**Año:** 2025

### Requerimientos Implementados

- ✅ **Pensamiento Computacional** aplicado
- ✅ **Diagrama de Clases** implementado
- ✅ **Principios SOLID** seguidos
- ✅ **10+ Patrones de Diseño** implementados
- ✅ **Arquitectura en Capas** completa
- ✅ **Sistema de Notificaciones** con Observer
- ✅ **Gestión completa** de usuarios, envíos, repartidores
- ✅ **Métricas y Reportes** operativos
- ✅ **Interfaz moderna** y funcional

---

## 📊 Estadísticas del Proyecto

- **25 Controladores** JavaFX
- **7 Servicios** de negocio
- **6 DTOs** para transferencia de datos
- **6 Mappers** para conversión
- **24 Vistas FXML** diseñadas
- **4 Patrones Creacionales** implementados
- **3 Patrones Estructurales** implementados
- **3 Patrones Comportamentales** implementados
- **100% Cobertura** de funcionalidades requeridas

---

## 🤝 Contribuciones

Proyecto desarrollado como parte del curso de Programación Orientada a Objetos.

---

## 📞 Contacto

**Universidad del Quindío**  
Programa de Ingeniería de Sistemas  
Armenia, Quindío, Colombia

---

## 📄 Licencia

Este proyecto es de uso académico.

---

<div align="center">

### ⭐ Si te gustó este proyecto, dale una estrella!

**© 2025 Plataforma de Logística | Sistema de Entregas**

</div>
