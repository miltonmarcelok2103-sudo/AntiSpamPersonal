# AntiSpam Personal — prototipo para Android

Proyecto inicial de una aplicación de filtrado de llamadas para Android (minSdk 26). No es un APK ya compilado ni una aplicación probada en el Samsung del usuario.

## Funciones incluidas en el prototipo
- Reglas de bloqueo por número completo.
- Reglas por prefijo/código de área.
- Excepciones por número y contactos (requiere permiso de contactos).
- Historial local de llamadas bloqueadas.
- Regla heurística experimental opcional para identificadores muy cortos.

## Limitaciones importantes
- La heurística no consulta bases de reputación y NO identifica de forma fiable todas las llamadas de spam. Está desactivada por defecto.
- El formato de los números puede variar; probar cuidadosamente cada prefijo antes de usarlo.
- Android debe designar esta app como aplicación de filtrado de llamadas. El usuario debe aceptar el diálogo del sistema.
- La app no escucha ni graba conversaciones y no sube datos.
- El historial de esta versión registra los bloqueos que decide esta app; no es un registro completo de todas las llamadas recibidas.
- Esta es una base de desarrollo. Debe compilarse y probarse antes de confiarle el bloqueo de llamadas importantes.

## Crear APK desde el propio teléfono
La vía más realista sin PC es subir estos archivos a un repositorio GitHub y usar GitHub Actions para compilar en la nube:
1. En el navegador del teléfono, iniciar sesión o crear una cuenta GitHub.
2. Crear un repositorio nuevo, por ejemplo `AntiSpamPersonal`.
3. Subir el contenido de este ZIP conservando carpetas y archivos.
4. Abrir la pestaña **Actions** del repositorio y ejecutar **Build Android APK** (o hacer un cambio en `main` para disparar la compilación).
5. Abrir la ejecución terminada, entrar en **Artifacts** y descargar `AntiSpamPersonal-debug-apk`.
6. Descomprimir el artefacto y tocar `app-debug.apk`. Android puede pedir habilitar temporalmente “Instalar apps desconocidas” para el navegador o gestor de archivos utilizado. Habilitalo solo para esa fuente de confianza y desactivalo luego.
7. Abrir la app, conceder acceso a contactos si querés esa excepción y tocar “Activar como filtro de llamadas”. Aceptar el diálogo del sistema.
8. Probar con un número propio o de confianza antes de cargar reglas reales.

## Archivos principales
- `app/src/main/AndroidManifest.xml`: permisos y servicio Android.
- `SpamScreeningService.kt`: decisión de permitir o bloquear.
- `Rules.kt`: reglas e historial local.
- `MainActivity.kt`: interfaz de configuración.
- `.github/workflows/build.yml`: compilación automática y publicación del APK como artefacto temporal.

## Privacidad y seguridad
No cargues listas de contactos ni números personales en repositorios públicos. El código no necesita tus contraseñas, acceso remoto al teléfono ni credenciales de tu operador.
