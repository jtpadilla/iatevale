# CLAUDE.md

Este fichero proporciona orientación a Claude Code (claude.ai/code) cuando trabaja con el código de este repositorio.

## Sistema de build

El proyecto usa **Bazel 8.6.0** con Java 21. Todo pasa por Bazel — no hay Maven ni Gradle.

```bash
# Compilar todo
bazel build //...

# Ejecutar todos los tests
bazel test //...

# Ejecutar un test concreto
bazel test //java/bootstrap/unit:test
bazel test //java/bootstrap/base/gson:test   # (si existe el target de test)

# Arrancar el binario simpleagent
bazel run //java/product/simpleagent:simpleagent

# Compilar una librería concreta
bazel build //java/a2a/server/base/grpc:grpc
```

Hay una caché de disco compartida en `~/.cache/ai-helidon-labs-cache` (usada tanto por el IDE como por Claude Code).

## Visión general de la arquitectura

El proyecto implementa el **protocolo A2A (Agent-to-Agent)** — un estándar gRPC + HTTP para la interoperabilidad entre
agentes. La especificación oficial está en:

- Sitio principal: https://a2a-protocol.org/
- Especificación: https://a2a-protocol.org/latest/specification/
- Definiciones: https://a2a-protocol.org/latest/definitions/

El único producto ejecutable es `//java/product/simpleagent`, que expone:

- Un endpoint gRPC (`lf.a2a.v1.A2AService`) en el puerto 8080
- Un endpoint HTTP en `/.well-known/agent.json` que sirve la agent card

### Estructura de módulos

```
proto/lf/a2a/v1/         # Definiciones Protobuf del protocolo A2A (A2AService, AgentCard, etc.)
java/a2a/                # Framework servidor A2A
  server/base/grpc/      # A2AServiceImpl — servicio gRPC (delega en SkillService)
  server/base/http/      # WellKnownHandler — sirve /.well-known/agent.json
  server/base/service/   # Interfaces de servicio principales:
    agentcard/           #   AgentCardService — devuelve la AgentCard del agente
    skill/               #   SkillService (lista), SPI SkillProvider, SkillContext/Request
    persistence/         #   A2AServerPersistenceService
  server/vanilla/        # Implementaciones vacías por defecto (VanillaSkillService devuelve [])
java/skill/              # Implementaciones concretas de skills (p.ej. echo/)
java/product/simpleagent/ # Ensambla todo; Main arranca el WebServer de Helidon
java/bootstrap/          # Librerías compartidas (no específicas de A2A)
  unit/                  # Identificación de unidad y framework de logging estructurado
  base/gson/             # Utilidades Gson con adaptadores de fecha/hora
  base/protobuf/         # Utilidades Protobuf ↔ JSON/Struct
  base/util/             # Utilidades Java generales
  gcloud/context/        # Servicio de contexto de Google Cloud
  gcloud/genai/          # Wrapper Gemini/VertexAI (GenerateContent, FunctionGateway, caché)
```

### Inyección de dependencias

El proyecto usa **Helidon 4 Service Registry** — no Spring ni CDI. Anotaciones clave:

- `@Service.Singleton` — marca una clase como servicio singleton
- `@Service.Inject` — inyección por constructor
- `@Service.Contract` — marca una interfaz como contrato de servicio (p.ej. `SkillProvider`)

### Añadir un skill

Para añadir un nuevo skill:

1. Implementar `SkillProvider` (de `//java/a2a/server/base/service/skill`) anotado con `@Service.Singleton`
2. Añadirlo como dependencia de `//java/product/simpleagent:simpleagent` en su `BUILD.bazel`
3. Sobreescribir `VanillaSkillService` o inyectar directamente — el registro de Helidon descubre automáticamente las
   implementaciones de `@Service.Contract`

Ver `//java/skill/echo` como implementación de referencia mínima.

### Interacción desde línea de comando

#### HTTP con curl

```bash
# Obtener la agent card pública
curl http://localhost:8080/.well-known/agent.json

# Obtener la agent card extendida (vía HTTP transcoding)
curl http://localhost:8080/extendedAgentCard

# Enviar un mensaje (sendMessage)
curl -X POST http://localhost:8080/message:send \
  -H "Content-Type: application/json" \
  -d '{
    "message": {
      "messageId": "msg-001",
      "contextId": "ctx-001",
      "role": "ROLE_USER",
      "parts": [{"text": "Hola agente"}]
    }
  }'
```

#### gRPC con grpcurl

```bash
# Instalar grpcurl (si no está disponible)
# https://github.com/fullstorydev/grpcurl

# Listar servicios disponibles (requiere reflexión gRPC habilitada)
grpcurl -plaintext localhost:8080 list

# Listar métodos del servicio A2A
grpcurl -plaintext localhost:8080 list lf.a2a.v1.A2AService

# Obtener la agent card extendida
grpcurl -plaintext -d '{}' localhost:8080 lf.a2a.v1.A2AService/GetExtendedAgentCard

# Enviar un mensaje
grpcurl -plaintext \
  -d '{
    "message": {
      "messageId": "msg-001",
      "contextId": "ctx-001",
      "role": "ROLE_USER",
      "parts": [{"text": "Hola agente"}]
    }
  }' \
  localhost:8080 lf.a2a.v1.A2AService/SendMessage

# Enviar un mensaje en modo streaming
grpcurl -plaintext \
  -d '{
    "message": {
      "messageId": "msg-002",
      "contextId": "ctx-001",
      "role": "ROLE_USER",
      "parts": [{"text": "Hola streaming"}]
    }
  }' \
  localhost:8080 lf.a2a.v1.A2AService/SendStreamingMessage

# Consultar el estado de una tarea
grpcurl -plaintext \
  -d '{"id": "<task-id>"}' \
  localhost:8080 lf.a2a.v1.A2AService/GetTask
```

> Si el servidor no tiene reflexión gRPC habilitada, hay que pasar el proto con `-proto proto/lf/a2a/v1/a2a.proto` y
`-import-path .` a grpcurl.

### Paquete Proto

El proto A2A está en `proto/lf/a2a/v1/a2a.proto`. Targets generados:

- `//proto/lf/a2a/v1:a2a_java_proto` — mensajes protobuf
- `//proto/lf/a2a/v1:a2a_java_grpc` — stubs gRPC

Paquete Java del código generado: `com.google.lf.a2a.v1`.
