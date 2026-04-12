package io.github.jtpadilla.a2a.server.lib.spec;

import com.google.lf.a2a.v1.Artifact;
import com.google.lf.a2a.v1.Message;
import com.google.lf.a2a.v1.Task;
import com.google.lf.a2a.v1.TaskStatus;
import com.google.protobuf.Struct;

import java.util.Optional;

public interface Emitter {

    Optional<String> taskId();
    Optional<String> contextId();

    void messageSend(Message message);

    void taskCreate(Task task);

    void taskStatusUpdate(TaskStatus taskStatus);
    void taskStatusUpdate(TaskStatus taskStatus, Struct metadata);

    void taskArtifactUpdate(Artifact artifact);
    void taskArtifactUpdate(Artifact artifact, Struct metadata);
    void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk);
    void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk, Struct metadata);

}