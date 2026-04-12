package io.github.jtpadilla.a2a.common.verifier;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.common.verifier.internal.*;

/**
 * Facade para la verificacion de mensajes protobuf del protocolo A2A.
 *
 * Cada metodo estatico verify() valida los campos marcados como REQUIRED en
 * proto/lf/a2a/v1/a2a.proto y lanza {@link IllegalArgumentException} si alguno
 * de ellos no esta informado. Los campos String se comprueban con
 * {@link String#isBlank()}, los campos mensaje con hasXxx(), y los campos
 * repeated con isEmpty().
 */
public class A2AVerifier {

    // -------------------------------------------------------------------------
    // Mensajes de dominio
    // -------------------------------------------------------------------------

    static public Message verify(Message msg) {
        return MessageVerifier.verify(msg);
    }

    static public TaskStatus verify(TaskStatus msg) {
        return TaskStatusVerifier.verify(msg);
    }

    static public Task verify(Task msg) {
        return TaskVerifier.verify(msg);
    }

    static public Artifact verify(Artifact msg) {
        return ArtifactVerifier.verify(msg);
    }

    // -------------------------------------------------------------------------
    // Eventos de streaming
    // -------------------------------------------------------------------------

    static public TaskStatusUpdateEvent verify(TaskStatusUpdateEvent msg) {
        return TaskStatusUpdateEventVerifier.verify(msg);
    }

    static public TaskArtifactUpdateEvent verify(TaskArtifactUpdateEvent msg) {
        return TaskArtifactUpdateEventVerifier.verify(msg);
    }

    // -------------------------------------------------------------------------
    // Push notifications
    // -------------------------------------------------------------------------

    static public PushNotificationConfig verify(PushNotificationConfig msg) {
        return PushNotificationConfigVerifier.verify(msg);
    }

    static public AuthenticationInfo verify(AuthenticationInfo msg) {
        return AuthenticationInfoVerifier.verify(msg);
    }

    static public TaskPushNotificationConfig verify(TaskPushNotificationConfig msg) {
        return TaskPushNotificationConfigVerifier.verify(msg);
    }

    // -------------------------------------------------------------------------
    // Agent card
    // -------------------------------------------------------------------------

    static public AgentInterface verify(AgentInterface msg) {
        return AgentInterfaceVerifier.verify(msg);
    }

    static public AgentProvider verify(AgentProvider msg) {
        return AgentProviderVerifier.verify(msg);
    }

    static public AgentSkill verify(AgentSkill msg) {
        return AgentSkillVerifier.verify(msg);
    }

    static public AgentCard verify(AgentCard msg) {
        return AgentCardVerifier.verify(msg);
    }

    static public AgentCardSignature verify(AgentCardSignature msg) {
        return AgentCardSignatureVerifier.verify(msg);
    }

    // -------------------------------------------------------------------------
    // Security schemes
    // -------------------------------------------------------------------------

    static public APIKeySecurityScheme verify(APIKeySecurityScheme msg) {
        return APIKeySecuritySchemeVerifier.verify(msg);
    }

    static public HTTPAuthSecurityScheme verify(HTTPAuthSecurityScheme msg) {
        return HTTPAuthSecuritySchemeVerifier.verify(msg);
    }

    static public OAuth2SecurityScheme verify(OAuth2SecurityScheme msg) {
        return OAuth2SecuritySchemeVerifier.verify(msg);
    }

    static public OpenIdConnectSecurityScheme verify(OpenIdConnectSecurityScheme msg) {
        return OpenIdConnectSecuritySchemeVerifier.verify(msg);
    }

    static public AuthorizationCodeOAuthFlow verify(AuthorizationCodeOAuthFlow msg) {
        return AuthorizationCodeOAuthFlowVerifier.verify(msg);
    }

    static public ClientCredentialsOAuthFlow verify(ClientCredentialsOAuthFlow msg) {
        return ClientCredentialsOAuthFlowVerifier.verify(msg);
    }

    static public DeviceCodeOAuthFlow verify(DeviceCodeOAuthFlow msg) {
        return DeviceCodeOAuthFlowVerifier.verify(msg);
    }

    // -------------------------------------------------------------------------
    // Requests / responses
    // -------------------------------------------------------------------------

    static public SendMessageRequest verify(SendMessageRequest msg) {
        return SendMessageRequestVerifier.verify(msg);
    }

    static public GetTaskRequest verify(GetTaskRequest msg) {
        return GetTaskRequestVerifier.verify(msg);
    }

    static public CancelTaskRequest verify(CancelTaskRequest msg) {
        return CancelTaskRequestVerifier.verify(msg);
    }

    static public SubscribeToTaskRequest verify(SubscribeToTaskRequest msg) {
        return SubscribeToTaskRequestVerifier.verify(msg);
    }

    static public GetTaskPushNotificationConfigRequest verify(GetTaskPushNotificationConfigRequest msg) {
        return GetTaskPushNotificationConfigRequestVerifier.verify(msg);
    }

    static public DeleteTaskPushNotificationConfigRequest verify(DeleteTaskPushNotificationConfigRequest msg) {
        return DeleteTaskPushNotificationConfigRequestVerifier.verify(msg);
    }

    static public CreateTaskPushNotificationConfigRequest verify(CreateTaskPushNotificationConfigRequest msg) {
        return CreateTaskPushNotificationConfigRequestVerifier.verify(msg);
    }

    static public ListTaskPushNotificationConfigsRequest verify(ListTaskPushNotificationConfigsRequest msg) {
        return ListTaskPushNotificationConfigsRequestVerifier.verify(msg);
    }

}
