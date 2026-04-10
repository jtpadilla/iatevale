# INDEX

| Artículo | Descripción |
|---|---|
| [[a2a-kernel]] | Centro de operaciones del servidor A2A: ciclo de vida de tareas, eventos y transporte. |
| [[transporte-grpc]] | Traducción de mensajes protobuf gRPC a operaciones A2A mediante `GrpcHandler`. |
| [[request-context]] | Objeto que encapsula toda la información de contexto para la ejecución de una tarea. |
| [[server-call-context]] | Estado del servidor por llamada activa: auth, extensiones, versión de protocolo y cancelación. |
| [[persistencia-tareas]] | `TaskStore` y `PushNotificationConfigStore`: implementaciones en memoria y JPA. |
| [[cola-replicada]] | `ReplicatedQueueManager` para gestión distribuida de colas de eventos entre instancias. |