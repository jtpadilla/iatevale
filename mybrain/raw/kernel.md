El kernel del SDK de A2A Java, ubicado principalmente en el directorio `/a2aproject/a2a-java/server-common`, es el centro de las operaciones del lado del servidor. Se encarga de la gestión del ciclo de vida de las tareas, el manejo de solicitudes entrantes, el procesamiento de eventos y las operaciones seguras y configurables, como se detalla en [Server-Side Core Components](#server-side-core-components).

**Esquema del Kernel e Interfaz con la Capa de Transporte:**

Los `RequestHandler`s actúan como la interfaz principal del kernel para las solicitudes que llegan a través de las capas de transporte. El `RequestHandler` define métodos para interacciones A2A comunes, como buscar, listar y cancelar tareas, enviar mensajes (individuales y en streaming) y suscribirse a eventos de tareas. El `DefaultRequestHandler` es una implementación estándar que orquesta las interacciones entre la lógica de negocio del agente, la persistencia de tareas y la gestión de eventos, según [Server-Side Core Components](#server-side-core-components).

Los diferentes transportes (gRPC, JSON-RPC 2.0 sobre HTTP, REST sobre HTTP+JSON) tienen sus propios manejadores que traducen los mensajes específicos del protocolo a operaciones A2A y luego los delegan al `RequestHandler`. Por ejemplo:
*   **gRPC:** El `GrpcHandler` en `/a2aproject/a2a-java/transport/grpc/src/main/java/io/a2a/transport/grpc/handler/GrpcHandler.java` traduce mensajes protobuf de gRPC en objetos de dominio A2A y delega la lógica de negocio al `RequestHandler`, como se describe en [Transport Layer Implementations](#transport-layer-implementations) y [gRPC Transport Implementation Details](#transport-layer-implementations-grpc-transport-implementation-details).
*   **JSON-RPC:** El `JSONRPCHandler` en `/a2aproject/a2a-java/transport/jsonrpc/src/main/java/io/a2a/transport/jsonrpc/handler/JSONRPCHandler.java` procesa métodos JSON-RPC, los convierte en operaciones A2A y delega al `RequestHandler`, como se menciona en [Transport Layer Implementations](#transport-layer-implementations) y [JSON-RPC 2.0 over HTTP Transport Implementation Details](#transport-layer-implementations-json-rpc-20-over-http-transport-implementation-details).
*   **REST:** El `RestHandler` en `/a2aproject/a2a-java/transport/rest/src/main/java/io/a2a/transport/rest/handler/RestHandler.java` traduce las solicitudes HTTP en operaciones A2A, como se detalla en [Transport Layer Implementations](#transport-layer-implementations) y [REST over HTTP+JSON Transport Implementation Details](#transport-layer-implementations-rest-over-httpjson-transport-implementation-details).

**Piezas del Kernel para la Gestión:**

1.  **Lógica de Negocio del Agente (`AgentExecutor`):**
    *   La lógica de negocio central de un agente A2A se implementa a través de la interfaz `AgentExecutor`, ubicada en `/a2aproject/a2a-java/server-common/src/main/java/io/a2a/server/agentexecution`. Es el punto de extensión principal para que los desarrolladores definan cómo un agente ejecuta y cancela tareas.
    *   El método `execute` contiene la lógica principal, mientras que `cancel` gestiona las solicitudes de detención de tareas en curso.
    *   Cada contexto de ejecución se encapsula dentro de un `RequestContext`, que contiene toda la información relevante para una tarea, incluyendo parámetros de mensajes, identificadores de tareas y contexto, estado actual de la tarea, tareas relacionadas y el `ServerCallContext`. Esto se explica en [Agent Execution and Business Logic Extension](#server-side-core-components-agent-execution-and-business-logic-extension).

2.  **Gestión de Persistencia (`TaskStore` y `PushNotificationConfigStore`):**
    *   El kernel utiliza la interfaz `TaskStore` para la gestión del ciclo de vida y la persistencia de los objetos `Task`.
    *   Existe una implementación transitoria por defecto, `InMemoryTaskStore`, y una implementación JPA, `JpaDatabaseTaskStore` en `/a2aproject/a2a-java/extras/task-store-database-jpa/src/main/java/io/a2a/extras/taskstore/database/jpa/JpaDatabaseTaskStore.java`, para el almacenamiento persistente en una base de datos relacional. Esta última serializa los objetos `Task` en JSON para su almacenamiento y desnormaliza campos clave para optimizar las consultas, como se describe en [Server-Side Core Components](#server-side-core-components) y [Persistent Task Storage](#persistent-task-storage).
    *   Para la configuración de notificaciones push, la interfaz `PushNotificationConfigStore` es gestionada por el kernel. Una implementación JPA, `JpaDatabasePushNotificationConfigStore`, está disponible para persistir estas configuraciones en una base de datos, reemplazando la tienda en memoria por defecto.

3.  **Estados de las Tareas:**
    *   La persistencia de tareas a través de `TaskStore` incluye métodos para determinar si una tarea está activa (`isTaskActive`) o finalizada (`isTaskFinalized`).
    *   `JpaDatabaseTaskStore` implementa estos métodos, considerando un "grace period" configurable para manejar la replicación y evitar el cierre prematuro de las tareas en entornos distribuidos. Esto se detalla en [Task State Management with Replication Awareness](#persistent-task-storage-task-state-management-with-replication-awareness) y en `/a2aproject/a2a-java/extras/task-store-database-jpa/src/main/java/io/a2a/extras/taskstore/database/jpa/JpaDatabaseTaskStore.java`.

4.  **Recepción y Emisión de Eventos:**
    *   El sistema de procesamiento de eventos se centra en el `MainEventBus`, que sirve como punto de entrada central y seguro para todos los eventos. Un hilo de fondo único, `MainEventBusProcessor`, consume eventos del bus.
    *   Este procesador persiste eventos en el `TaskStore`, envía notificaciones push y distribuye eventos a instancias de `ChildQueue` individuales para el consumo del cliente.
    *   La arquitectura garantiza que los eventos persisten antes de que los clientes los vean y que el procesamiento es serial.
    *   `EventQueue` es una clase base abstracta con implementaciones `MainQueue` (para el envío al `MainEventBus`) y `ChildQueue` (para el consumo del cliente).
    *   El `QueueManager` gestiona estas colas, con implementaciones como `InMemoryQueueManager` y `ReplicatedQueueManager` (para escenarios distribuidos).
    *   El `AgentEmitter` proporciona APIs para que los agentes actualicen estados de tareas, añadan artefactos y envíen mensajes.
    *   El `ResultAggregator` conecta el `EventConsumer` (que sondea una `ChildQueue`) con los manejadores de solicitudes, ofreciendo estrategias de consumo para solicitudes en streaming y no en streaming.
    *   Estos conceptos se explican en [Server-Side Core Components](#server-side-core-components).
    *   En entornos distribuidos, el `ReplicatedQueueManager` en `/a2aproject/a2a-java/extras/queue-manager-replicated/core/src/main/java/io/a2a/extras/queuemanager/replicated/core/ReplicatedQueueManager.java` intercepta, propaga y procesa eventos a través de instancias utilizando brokers de mensajes, como se describe en [Distributed Queue Management](#distributed-queue-management) y [Core Replication Mechanism and Event Handling](#distributed-queue-management-core-replication-mechanism-and-event-handling). Utiliza un mecanismo de "poison pill" para la limpieza determinista de recursos tras la finalización de la tarea. La integración con MicroProfile Reactive Messaging (`ReactiveMessagingReplicationStrategy`) se usa para serializar y deserializar eventos a JSON y publicarlos en canales de mensajería, como se detalla en [MicroProfile Reactive Messaging Integration](#distributed-queue-management-microprofile-reactive-messaging-integration).

5.  **Contexto de Llamada del Servidor (`ServerCallContext`):**
    *   El `ServerCallContext` en `/a2aproject/a2a-java/server-common/src/main/java/io/a2a/server/ServerCallContext.java` mantiene el estado crucial del lado del servidor para cada llamada activa. Proporciona acceso a los detalles de autenticación del usuario, un mapa de estado mutable para datos temporales, las extensiones solicitadas y activadas por el cliente, y la versión de protocolo negociada.
    *   También ofrece un mecanismo para la cancelación de consumidores de eventos, especialmente importante para gestionar operaciones de streaming, como se detalla en [Server Call Context and State Management](#server-side-core-components-server-call-context-and-state-management).

En resumen, el kernel de A2A Java proporciona una arquitectura modular que permite la flexibilidad en la elección de transportes y mecanismos de persistencia, al tiempo que centraliza la lógica de negocio, la gestión de tareas y el procesamiento de eventos a través de interfaces bien definidas.

