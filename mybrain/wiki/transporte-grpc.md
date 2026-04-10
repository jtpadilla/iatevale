# Transporte gRPC

El transporte gRPC traduce mensajes protobuf entrantes a objetos de dominio A2A y delega la lógica de negocio al `RequestHandler` del kernel. En el SDK de referencia se implementa en `GrpcHandler`.

## Funcionamiento

1. El cliente envía una llamada gRPC al servidor.
2. `GrpcHandler` deserializa el mensaje protobuf a un objeto de dominio A2A.
3. La operación se delega al `DefaultRequestHandler`.
4. La respuesta se serializa de vuelta a protobuf y se devuelve al cliente.

## Temas relacionados

- [[a2a-kernel]]
- [[request-context]]