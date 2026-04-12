package io.github.jtpadilla.a2a.common.verifier.internal;

import com.google.lf.a2a.v1.Artifact;

public class ArtifactVerifier {

    static public Artifact verify(Artifact msg) {
        return new ArtifactVerifier(msg).verify();
    }

    final private Artifact msg;

    private ArtifactVerifier(Artifact msg) {
        this.msg = msg;
    }

    private Artifact getThis() {
        if (msg == null) {
            throw new IllegalArgumentException("Artifact is null");
        }
        return msg;
    }

    private void verifyArtifactId(Artifact.Builder builder) {
        if (getThis().getArtifactId().isBlank()) {
            throw new IllegalArgumentException("Artifact.artifact_id is required");
        }
        builder.setArtifactId(getThis().getArtifactId());
    }

    private void verifyParts(Artifact.Builder builder) {
        if (getThis().getPartsList().isEmpty()) {
            throw new IllegalArgumentException("Artifact.parts is required and must not be empty");
        }
        builder.addAllParts(getThis().getPartsList());
    }

    private Artifact verify() {
        Artifact.Builder builder = Artifact.newBuilder();
        verifyArtifactId(builder);
        verifyParts(builder);
        if (!getThis().getName().isBlank()) {
            builder.setName(getThis().getName());
        }
        if (!getThis().getDescription().isBlank()) {
            builder.setDescription(getThis().getDescription());
        }
        if (getThis().hasMetadata()) {
            builder.setMetadata(getThis().getMetadata());
        }
        builder.addAllExtensions(getThis().getExtensionsList());
        return builder.build();
    }

}
