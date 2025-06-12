package com.steverhoton.poc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventBridgeLambdaHandlerTest {

  private EventBridgeLambdaHandler handler;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @Mock private Context context;

  @BeforeEach
  void setUp() {
    handler = new EventBridgeLambdaHandler();
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void handleRequest_ShouldReturnSuccessMessage() {
    ScheduledEvent event = new ScheduledEvent();
    event.setSource("aws.events");
    event.setDetailType("Scheduled Event");

    String result = handler.handleRequest(event, context);

    assertEquals("Event processed successfully", result);
  }

  @Test
  void handleRequest_ShouldLogEventToStdout() {
    ScheduledEvent event = new ScheduledEvent();
    event.setSource("aws.events");
    event.setDetailType("Scheduled Event");

    handler.handleRequest(event, context);

    String output = outputStream.toString();
    assertTrue(output.contains("EventBridge event received:"));
    assertTrue(output.contains("aws.events"));
    assertTrue(output.contains("Scheduled Event"));
  }

  @Test
  void handleRequest_WithNullEvent_ShouldProcessSuccessfully() {
    String result = handler.handleRequest(null, context);

    assertEquals("Event processed successfully", result);
  }

  private void assertTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError("Expected condition to be true");
    }
  }
}
