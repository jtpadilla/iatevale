# Cola Replicada (Distributed Queue Management)

En entornos distribuidos, el [[a2a-kernel]] utiliza `ReplicatedQueueManager` para interceptar, propagar y procesar eventos entre múltiples instancias del servidor usando brokers de mensajes.

## Mecanismo central

- `ReplicatedQueueManager`: intercepta eventos locales, los propaga al broker y los recibe de otras instancias.
- **Poison pill**: mecanismo para la limpieza determinista de recursos tras la finalización de una tarea.
- `ReactiveMessagingReplicationStrategy`: integración con MicroProfile Reactive Messaging para serializar/deserializar eventos a JSON y publicarlos en canales de mensajería.

## Comparativa de QueueManagers

| Implementación | Uso |
|---|---|
| `InMemoryQueueManager` | Proceso único, sin replicación. |
| `ReplicatedQueueManager` | Múltiples instancias, requiere broker de mensajes. |

## Temas relacionados

- [[a2a-kernel]]
- [[persistencia-tareas]]