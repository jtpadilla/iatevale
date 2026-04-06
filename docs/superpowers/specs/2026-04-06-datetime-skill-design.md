# DateTime Skill — Design Spec

**Date:** 2026-04-06

## Overview

Add a new A2A skill `datetime` that returns the current UTC date and time in ISO 8601 format (`2026-04-06T14:32:05Z`) in
response to any incoming message.

## Architecture

New Bazel module at `java/skill/datetime/`, following the same structure as the reference skill `java/skill/echo/`.

### Files to create

- `java/skill/datetime/BUILD.bazel`
- `java/skill/datetime/src/main/java/io/github/jtpadilla/a2a/skill/datetime/DateTimeSkill.java`

### Files to modify

- `java/product/simpleagent/BUILD.bazel` — add `//java/skill/datetime` to deps

## Component Design

### DateTimeSkill

- Implements `SkillProvider`, annotated `@Service.Singleton`
- Skill card:
    - `id`: fresh UUID
    - `name`: `"DateTime"`
    - `description`: `"Returns current UTC date and time in ISO 8601 format"`
    - `tags`: `["datetime", "time", "date"]`
- Uses `java.time.Instant.now()` formatted with `DateTimeFormatter.ISO_INSTANT` (no external dependencies)
- `sendMessage`: captures instant → returns a `Message` (role `ROLE_AGENT`) with the formatted string as the sole `Part`
- `sendStreamingMessage`: emits `TASK_STATE_WORKING` status update first, then the same datetime `Message`, mirroring
  the echo streaming pattern

## Data Flow

```
Client → SendMessage / SendStreamingMessage
           → DateTimeSkill.executeSkill()
               → Instant.now() formatted as ISO 8601 UTC
               → Message { role: ROLE_AGENT, parts: [{ text: "2026-04-06T14:32:05Z" }] }
           → Response / StreamResponse
```

## BUILD Dependencies

Same as `echo`:

- `//java/a2a/server/base/service/skill`
- `//java/third_party/helidon:service`
- `//proto/lf/a2a/v1:a2a_java_grpc`
- `//proto/lf/a2a/v1:a2a_java_proto`
- `@maven//:io_grpc_grpc_api`
- `@maven//:io_grpc_grpc_stub`
- `@protobuf//:protobuf_java_util`

## Testing

Verify with grpcurl after `bazel run //java/product/simpleagent:simpleagent`:

```bash
grpcurl -plaintext \
  -d '{"message":{"messageId":"msg-dt","contextId":"ctx-dt","role":"ROLE_USER","parts":[{"text":"now"}]}}' \
  localhost:8080 lf.a2a.v1.A2AService/SendMessage
```

Expected: response `parts[0].text` matches ISO 8601 UTC pattern `\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z`.
