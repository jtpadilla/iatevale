# ServerCallContext

`ServerCallContext` mantiene el estado del lado del servidor para cada llamada activa dentro del [[a2a-kernel]]. Es accesible desde el [[request-context]] durante la ejecución de un `AgentExecutor`.

## Contenido

| Campo | Descripción |
|---|---|
| Detalles de autenticación | Identidad del usuario que realiza la llamada. |
| Mapa de estado mutable | Datos temporales para el ciclo de vida de la llamada. |
| Extensiones del cliente | Extensiones solicitadas y activadas por el cliente. |
| Versión de protocolo | Versión A2A negociada para la llamada. |
| Cancelación de consumidores | Mecanismo para cancelar consumidores de eventos, especialmente en operaciones streaming. |

## Temas relacionados

- [[a2a-kernel]]
- [[request-context]]