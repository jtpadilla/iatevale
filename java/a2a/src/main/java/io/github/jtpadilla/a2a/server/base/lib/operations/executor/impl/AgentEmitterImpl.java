package io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl;

import com.google.lf.a2a.v1.*;
import com.google.protobuf.Struct;
import io.github.jtpadilla.a2a.server.base.lib.model.TaskStateUtil;
import io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl.event.*;
import io.github.jtpadilla.a2a.server.base.lib.spec.Emitter;
import io.github.jtpadilla.a2a.server.base.lib.spec.EmitterException;
import io.github.jtpadilla.a2a.server.base.lib.spec.RequestContext;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class AgentEmitterImpl implements Emitter {

    private final Consumer<EmitterEvent> emitter;
    private final String taskId;
    private final String contextId;
    private final AtomicBoolean terminalStateReached = new AtomicBoolean(false);

    public AgentEmitterImpl(RequestContext context, Consumer<EmitterEvent> emitter) {
        this.emitter = emitter;
        this.taskId = context.getTaskId();
        this.contextId = context.getContextId();
    }

    public Optional<String> taskId() {
        return Optional.ofNullable(taskId);
    }

    public Optional<String> contextId() {
        return Optional.ofNullable(contextId);
    }

    private String taskIdRequired() throws EmitterException {
        return taskId().orElseThrow(()->new EmitterException("TaskId is missing"));
    }

    private String contextIdRequired() throws EmitterException {
        return contextId().orElseThrow(()->new EmitterException("ContextId is missing"));
    }

    @Override
    public Message.Builder messageBuilder() {
        return Message.newBuilder()
                .setRole(Role.ROLE_AGENT)
                .setMessageId(UUID.randomUUID().toString());
    }

    @Override
    public void messageSend(Message message) throws EmitterException {
        // Se encola el evento
        emitter.accept(new EmitterMessageEvent(message));
    }

    @Override
    public void taskCreate(Task task) throws EmitterException {
        emitter.accept(new EmitterTaskCreateEvent(task));
    }

    @Override
    public void taskStatusUpdate(TaskStatus taskStatus) throws EmitterException {
        taskStatusUpdateImpl(taskStatus, null);
    }

    @Override
    public void taskStatusUpdate(TaskStatus taskStatus, Struct metadata) throws EmitterException {
        taskStatusUpdateImpl(taskStatus, metadata);
    }

    private void taskStatusUpdateImpl(TaskStatus taskStatus, @Nullable Struct metadata) throws EmitterException {

        final boolean isTerminal = TaskStateUtil.isTerminal(taskStatus.getState());

        // Comprobar estado terminal primero (fallo rápido)
        if (terminalStateReached.get()) {
            throw new IllegalStateException("Cannot update task status - terminal state already reached");
        }

        // Para estados finales, establecer el indicador de forma atómica
        if (isTerminal) {
            if (!terminalStateReached.compareAndSet(false, true)) {
                throw new IllegalStateException("Cannot update task status - terminal state already reached");
            }
        }

        // Se compone el mensaje dirigido al cliente
        final TaskStatusUpdateEvent.Builder taskStatusUpdateEvent = TaskStatusUpdateEvent.newBuilder()
                .setTaskId(taskIdRequired())
                .setContextId(contextIdRequired())
                .setStatus(taskStatus);
        if (metadata != null) {
            taskStatusUpdateEvent.setMetadata(metadata);
        }

        // Se encola el evento
        emitter.accept(new EmitterTaskStatusUpdateEvent(taskStatusUpdateEvent.build()));

    }

    @Override
    public void taskArtifactUpdate(Artifact artifact) throws EmitterException {
        taskArtifactUpdateImpl(artifact, false, true, null);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, Struct metadata) throws EmitterException {
        taskArtifactUpdateImpl(artifact, false, true, metadata);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk) throws EmitterException {
        taskArtifactUpdateImpl(artifact, append, lastChunk, null);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk, Struct metadata) throws EmitterException {
        taskArtifactUpdateImpl(artifact, append, lastChunk, metadata);
    }

    private void taskArtifactUpdateImpl(Artifact artifact, boolean append, boolean lastChunk, @Nullable Struct metadata) throws EmitterException {

        // Se compone el mensaje dirigido al cliente
        final TaskArtifactUpdateEvent.Builder taskArtifactUpdateEvent = TaskArtifactUpdateEvent.newBuilder()
                .setTaskId(taskIdRequired())
                .setContextId(contextIdRequired())
                .setArtifact(artifact)
                .setAppend(append)
                .setLastChunk(lastChunk);
        if (metadata != null) {
            taskArtifactUpdateEvent.setMetadata(metadata);
        }

        // Se encola el evento
        emitter.accept(new EmitterTaskArtifactUpdateEvent(taskArtifactUpdateEvent.build()));

    }

}
