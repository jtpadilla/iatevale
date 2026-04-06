# iatevale

Implementación del protocolo **A2A (Agent-to-Agent)** sobre **Helidon 4** y **gRPC**, orientada a evaluar y experimentar
con la interoperabilidad entre agentes de IA.

## Requisitos

- Bazel 8.6.0 (ver `.bazelversion`)
- Java 21

## Arrancar el agente

```bash
bazel run //java/product/simpleagent:simpleagent
```

El servidor arranca en el puerto **8080** y expone:

| Endpoint                         | Descripción           |
|----------------------------------|-----------------------|
| `/.well-known/agent.json` (HTTP) | Agent card del agente |
| `lf.a2a.v1.A2AService` (gRPC)    | Servicio A2A          |

## Compilar y ejecutar tests

```bash
# Compilar todo
bazel build //...

# Ejecutar todos los tests
bazel test //...

# Ejecutar un test concreto
bazel test //java/bootstrap/unit:test
```

## Integración con Gemini CLI

El directorio `java/product/simpleagent/src/resources/` contiene plantillas para registrar el agente en Gemini CLI:

**Opción 1 — con servidor arriba** (copia `sandbox-url.md` como `.gemini/agents/sandbox.md`):

```yaml
---
kind: remote
name: a2a-sandbox
agent_card_url: http://localhost:8080/.well-known/agent.json
---
```

**Opción 2 — sin servidor** (copia `sandbox-inline.md` como `.gemini/agents/sandbox.md`):

```yaml
---
kind: remote
name: a2a-sandbox
agent_card_json: |
  { ... }   # agent card incrustada
---
```

Comandos útiles en Gemini CLI:

```
/agents list
/agents enable a2a-sandbox
/agents disable a2a-sandbox
```

## Estructura del proyecto

```
proto/lf/a2a/v1/          # Definición Protobuf del protocolo A2A
java/a2a/                 # Framework servidor A2A
  server/base/            #   Implementaciones base (gRPC, HTTP, servicios)
  server/vanilla/         #   Implementaciones vacías por defecto
  client/                 #   Cliente A2A (en desarrollo)
java/skill/               # Implementaciones de skills (p.ej. echo)
java/product/simpleagent/ # Binario ejecutable: ensambla el servidor
java/bootstrap/           # Librerías de soporte (logging, Gson, Protobuf, GenAI)
```

## Implementar un skill

1. Crea una clase que implemente `SkillProvider` con `@Service.Singleton`:

```java
@Service.Singleton
public class MiSkill implements SkillProvider {

    @Override
    public AgentSkill getSkillCard() {
        return AgentSkill.newBuilder()
                .setId("mi-skill")
                .setName("Mi Skill")
                .setDescription("Descripción")
                .build();
    }

    @Override
    public void executeSkill(SkillContext context) {
        // lógica del skill
    }
}
```

2. Añade la dependencia en el `BUILD.bazel` de `//java/product/simpleagent`.

Ver `//java/skill/echo` como referencia mínima.
