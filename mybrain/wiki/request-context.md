# RequestContext

`RequestContext` encapsula toda la información relevante para una ejecución de tarea dentro del [[a2a-kernel]]. Es el objeto que recibe el `AgentExecutor` al procesar una solicitud.

## Contenido

- Parámetros del mensaje entrante.
- Identificadores de tarea y contexto.
- Estado actual de la tarea.
- Tareas relacionadas.
- Referencia al [[server-call-context]].

## Temas relacionados

- [[a2a-kernel]]
- [[server-call-context]]