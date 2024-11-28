# ChatoChat

Este proyecto es una aplicación de chat DPM para Android que utiliza Firebase para autenticación y almacenamiento de datos en tiempo real.

## Requisitos previos

- **Android Studio**: Necesitarás Android Studio instalado en tu máquina. Si no lo tienes, descárgalo desde [aquí](https://developer.android.com/studio).
- **Cuenta de Firebase**: Necesitarás una cuenta de Firebase para poder configurar el proyecto.

## Configuración de Firebase

### Paso 1: Crear un Proyecto en Firebase

1. **Ir a Firebase Console**: Ve a [Firebase Console](https://console.firebase.google.com/).
2. **Crear un nuevo proyecto**:
   - Haz clic en "Agregar proyecto" y sigue los pasos para crear un proyecto nuevo en Firebase.
   - Asegúrate de habilitar Google Analytics si deseas trackear las estadisticas de desempeño de tu fork.

### Paso 2: Agregar tu aplicación Android a Firebase
Hay 3 maneras en las que puedes iniciar tu cuenta de Firebase en el proyecto:

1. **Registrar la aplicación**:
   - En el panel de Firebase, haz clic en "Agregar aplicación" y selecciona Android.
   - En el formulario que aparece, ingresa el **nombre del paquete** de tu aplicación (este se encuentra en el archivo `AndroidManifest.xml` de tu proyecto).
   - Ingresa el **nombre del proyecto de Firebase** y sigue los pasos para descargar el archivo `google-services.json`.
   - Entra en el apartado "Tools" en AndroidStudio y selecciona Firebase, agrega Cloud Firestore [Java] y Authentication [Java]

   
2. **Agregar el archivo `google-services.json` a tu proyecto**:
   - Copia el archivo `google-services.json` descargado y pégalo en el directorio `app/` de tu proyecto de Android.
   - Entra en el apartado "Tools" en AndroidStudio y selecciona Firebase, agrega Cloud Firestore [Java] y Authentication [Java]
     
3. **Descarga el binario precompilado**:
   - Entra al siguiente link https://github.com/Glazeddberrie/ChatoChat/releases y descarga el binario precompilado más reciente
