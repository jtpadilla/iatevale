# A2A Java — Kernel del Servidor

El kernel del SDK de A2A Java es el centro de las operaciones del lado del servidor: gestiona el ciclo de vida de las tareas, el manejo de solicitudes entrantes, el procesamiento de eventos y las operaciones seguras y configurables. Se encuentra principalmente en el directorio `server-common`.

## Interfaz con la Capa de Transporte

Los `RequestHandler`s son la interfaz principal del kernel para las solicitudes que llegan a través de las capas de transporte. El `DefaultRequestHandler` orquesta las interacciones entre la lógica de negocio del agente, la persistencia de tareas y la gestión de eventos.

Cada transporte tiene su propio manejador que traduce mensajes del protocolo específico a operaciones A2A y los delega al `RequestHandler`:

- **gRPC** — `GrpcHandler`: traduce mensajes protobuf a objetos de dominio A2A. Ver [[transporte-grpc]].
- **JSON-RPC 2.0 sobre HTTP** — `JSONRPCHandler`: procesa métodos JSON-RPC y los convierte en operaciones A2A.
- **REST sobre HTTP+JSON** — `RestHandler`: traduce solicitudes HTTP en operaciones A2A.

## Piezas del Kernel

### 1. Lógica de Negocio — `AgentExecutor`

La lógica de negocio central se implementa a través de la interfaz `AgentExecutor` (en `server-common/.../agentexecution`). Es el punto de extensión principal para que los desarrolladores definan cómo un agente ejecuta y cancela tareas.

- `execute`: contiene la lógica principal de ejecución.
- `cancel`: gestiona las solicitudes de detención de tareas en curso.

Cada ejecución se encapsula en un `RequestContext`, que contiene parámetros de mensajes, identificadores de tarea y contexto, estado actual de la tarea, tareas relacionadas y el `ServerCallContext`. Ver [[request-context]].

### 2. Persistencia — `TaskStore` y `PushNotificationConfigStore`

- **`TaskStore`**: interfaz para la gestión del ciclo de vida y persistencia de objetos `Task`.
  - `InMemoryTaskStore`: implementación transitoria por defecto.
  - `JpaDatabaseTaskStore`: almacenamiento persistente en base de datos relacional (serializa `Task` a JSON; desnormaliza campos clave para optimizar consultas). Ver [[persistencia-tareas]].
- **`PushNotificationConfigStore`**: gestiona configuración de notificaciones push. Disponible `JpaDatabasePushNotificationConfigStore` para persistir en base de datos.

### 3. Estados de las Tareas

`TaskStore` expone `isTaskActive` e `isTaskFinalized`. `JpaDatabaseTaskStore` implementa estos métodos con un *grace period* configurable para manejar replicación y evitar el cierre prematuro en entornos distribuidos. Ver [[persistencia-tareas]].

### 4. Recepción y Emisión de Eventos

El sistema de eventos se centra en el `MainEventBus`, punto de entrada único y seguro para todos los eventos. Un hilo de fondo (`MainEventBusProcessor`) los consume:

1. Persiste eventos en el `TaskStore`.
2. Envía notificaciones push.
3. Distribuye eventos a `ChildQueue`s individuales para consumo del cliente.

La arquitectura garantiza que los eventos persisten antes de que los clientes los vean y que el procesamiento es serial.

| Clase | Rol |
|---|---|
| `EventQueue` | Clase base abstracta |
| `MainQueue` | Envía al `MainEventBus` |
| `ChildQueue` | Consumo del cliente |
| `QueueManager` | Gestiona las colas (`InMemoryQueueManager`, `ReplicatedQueueManager`) |
| `AgentEmitter` | API para actualizar estados, añadir artefactos y enviar mensajes |
| `ResultAggregator` | Conecta `EventConsumer` con manejadores de solicitudes (streaming y no-streaming) |

En entornos distribuidos, `ReplicatedQueueManager` intercepta, propaga y procesa eventos entre instancias usando brokers de mensajes. Usa un mecanismo de *poison pill* para la limpieza determinista de recursos tras la finalización de la tarea. La integración con MicroProfile Reactive Messaging (`ReactiveMessagingReplicationStrategy`) serializa/deserializa eventos a JSON. Ver [[cola-replicada]].

### 5. Contexto de Llamada — `ServerCallContext`

`ServerCallContext` mantiene el estado del lado del servidor para cada llamada activa:

- Detalles de autenticación del usuario.
- Mapa de estado mutable para datos temporales.
- Extensiones solicitadas y activadas por el cliente.
- Versión de protocolo negociada.
- Mecanismo de cancelación de consumidores de eventos (clave para operaciones streaming).

Ver [[server-call-context]].

## Temas relacionados

- [[transporte-grpc]]
- [[request-context]]
- [[persistencia-tareas]]
- [[cola-replicada]]
- [[server-call-context]]