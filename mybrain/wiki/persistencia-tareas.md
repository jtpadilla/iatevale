# Persistencia de Tareas

El [[a2a-kernel]] gestiona la persistencia de tareas a través de la interfaz `TaskStore` y la configuración de notificaciones push a través de `PushNotificationConfigStore`.

## TaskStore

Interfaz central para el ciclo de vida y persistencia de objetos `Task`.

| Implementación | Descripción |
|---|---|
| `InMemoryTaskStore` | Transitoria, por defecto. Adecuada para desarrollo y pruebas. |
| `JpaDatabaseTaskStore` | Persistencia en base de datos relacional (módulo extra). Serializa `Task` a JSON; desnormaliza campos clave para optimizar consultas. |

### Estados de las Tareas

- `isTaskActive`: determina si una tarea está activa.
- `isTaskFinalized`: determina si una tarea ha finalizado.

`JpaDatabaseTaskStore` implementa ambos métodos con un **grace period** configurable para manejar la replicación y evitar el cierre prematuro de tareas en entornos distribuidos.

## PushNotificationConfigStore

Interfaz para persistir la configuración de notificaciones push.

- `JpaDatabasePushNotificationConfigStore`: reemplaza la tienda en memoria por defecto con almacenamiento en base de datos.

## Temas relacionados

- [[a2a-kernel]]
- [[cola-replicada]]