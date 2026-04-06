# DateTime Skill Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a `datetime` A2A skill that returns the current UTC date/time in ISO 8601 format in response to any message, and wire it into `simpleagent`.

**Architecture:** New Bazel module `//java/skill/datetime` containing a single `DateTimeSkill` class implementing `SkillProvider`. Uses `java.time.Instant` + `DateTimeFormatter.ISO_INSTANT` from the JDK standard library — no new Maven dependencies. Installed into `simpleagent` by adding it to that binary's `deps`.

**Tech Stack:** Java 21, Bazel 8.6.0, Helidon 4 Service Registry, gRPC (proto A2A v1)

---

### Task 1: Create `DateTimeSkill.java`

**Files:**
- Create: `java/skill/datetime/src/main/java/io/github/jtpadilla/a2a/skill/datetime/DateTimeSkill.java`

- [ ] **Step 1: Create directory structure**

```bash
mkdir -p java/skill/datetime/src/main/java/io/github/jtpadilla/a2a/skill/datetime
```

- [ ] **Step 2: Write `DateTimeSkill.java`**

```java
package io.github.jtpadilla.a2a.skill.datetime;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.server.service.skill.SkillContext;
import io.github.jtpadilla.a2a.server.service.skill.SkillRequestSimple;
import io.github.jtpadilla.a2a.server.service.skill.SkillRequestStream;
import io.github.jtpadilla.a2a.server.service.skill.spi.SkillProvider;
import io.grpc.stub.StreamObserver;
import io.helidon.service.registry.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;

@Service.Singleton
public class DateTimeSkill implements SkillProvider {

    private static final Logger LOGGER = Logger.getLogger(DateTimeSkill.class.getName());

    @Override
    public AgentSkill getSkillCard() {
        return AgentSkill.newBuilder()
                .setId("b3a1c2d4-e5f6-7890-abcd-ef1234567890")
                .setName("DateTime")
                .setDescription("Returns current UTC date and time in ISO 8601 format")
                .addTags("datetime")
                .addTags("time")
                .addTags("date")
                .build();
    }

    @Override
    public void executeSkill(SkillContext context) {
        switch (context.request()) {
            case SkillRequestSimple simple -> sendMessage(simple.request(), simple.responseObserver());
            case SkillRequestStream stream -> sendStreamingMessage(stream.request(), stream.responseObsever());
        }
    }

    private String currentDateTimeUtc() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }

    private void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        LOGGER.info("Received sendMessage: " + request.getMessage().getMessageId());

        Message responseMessage = Message.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setContextId(request.getMessage().getContextId())
                .setRole(Role.ROLE_AGENT)
                .addParts(Part.newBuilder().setText(currentDateTimeUtc()).build())
                .build();

        responseObserver.onNext(SendMessageResponse.newBuilder()
                .setMessage(responseMessage)
                .build());
        responseObserver.onCompleted();
    }

    private void sendStreamingMessage(SendMessageRequest request, StreamObserver<StreamResponse> responseObserver) {
        LOGGER.info("Received sendStreamingMessage: " + request.getMessage().getMessageId());

        responseObserver.onNext(StreamResponse.newBuilder()
                .setStatusUpdate(TaskStatusUpdateEvent.newBuilder()
                        .setContextId(request.getMessage().getContextId())
                        .setStatus(TaskStatus.newBuilder()
                                .setState(TaskState.TASK_STATE_WORKING)
                                .build())
                        .build())
                .build());

        Message responseMessage = Message.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setContextId(request.getMessage().getContextId())
                .setRole(Role.ROLE_AGENT)
                .addParts(Part.newBuilder().setText(currentDateTimeUtc()).build())
                .build();

        responseObserver.onNext(StreamResponse.newBuilder()
                .setMessage(responseMessage)
                .build());
        responseObserver.onCompleted();
    }
}
```

---

### Task 2: Create `BUILD.bazel` for the datetime module

**Files:**
- Create: `java/skill/datetime/BUILD.bazel`

- [ ] **Step 1: Write `BUILD.bazel`**

```python
load("@rules_java//java:defs.bzl", "java_binary")

package(default_visibility = ["//visibility:public"])

java_library(
    name = "datetime",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "//java/a2a/server/base/service/skill",
        "//java/third_party/helidon:service",
        "//proto/lf/a2a/v1:a2a_java_grpc",
        "//proto/lf/a2a/v1:a2a_java_proto",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_stub",
        "@protobuf//:protobuf_java_util",
    ],
)
```

- [ ] **Step 2: Compile the new library to verify it builds**

```bash
bazel build //java/skill/datetime:datetime
```

Expected: `INFO: Build completed successfully`

- [ ] **Step 3: Commit**

```bash
git add java/skill/datetime/
git commit -m "feat: add DateTime skill (returns ISO 8601 UTC datetime)"
```

---

### Task 3: Wire datetime skill into simpleagent

**Files:**
- Modify: `java/product/simpleagent/BUILD.bazel`

- [ ] **Step 1: Add `//java/skill/datetime` to simpleagent deps**

In `java/product/simpleagent/BUILD.bazel`, add the new dep inside the `deps` list (after the echo entry):

```python
        "//java/skill/echo",
        "//java/skill/datetime",
```

- [ ] **Step 2: Build simpleagent to verify everything links**

```bash
bazel build //java/product/simpleagent:simpleagent
```

Expected: `INFO: Build completed successfully`

- [ ] **Step 3: Commit**

```bash
git add java/product/simpleagent/BUILD.bazel
git commit -m "feat: install datetime skill in simpleagent"
```

---

### Task 4: Manual smoke test

- [ ] **Step 1: Start the agent**

```bash
bazel run //java/product/simpleagent:simpleagent
```

Expected: server starts on port 8080.

- [ ] **Step 2: Verify skill appears in the agent card**

```bash
curl http://localhost:8080/.well-known/agent.json
```

Expected: JSON response includes a skill entry with `"name": "DateTime"`.

- [ ] **Step 3: Test sendMessage via HTTP**

```bash
curl -X POST http://localhost:8080/message:send \
  -H "Content-Type: application/json" \
  -d '{
    "message": {
      "messageId": "msg-dt-001",
      "contextId": "ctx-dt-001",
      "role": "ROLE_USER",
      "parts": [{"text": "now"}]
    }
  }'
```

Expected: response `parts[0].text` matches pattern `\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z`.

- [ ] **Step 4: Test sendStreamingMessage via gRPC**

```bash
grpcurl -plaintext \
  -d '{
    "message": {
      "messageId": "msg-dt-002",
      "contextId": "ctx-dt-001",
      "role": "ROLE_USER",
      "parts": [{"text": "now"}]
    }
  }' \
  localhost:8080 lf.a2a.v1.A2AService/SendStreamingMessage
```

Expected: two responses — first a `statusUpdate` with `TASK_STATE_WORKING`, then a `message` whose `parts[0].text` is an ISO 8601 UTC timestamp.
