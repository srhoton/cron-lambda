package com.steverhoton.poc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class EventBridgeLambdaHandler implements RequestHandler<ScheduledEvent, String> {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule());

  @Override
  public String handleRequest(ScheduledEvent event, Context context) {
    try {
      String eventJson = objectMapper.writeValueAsString(event);
      System.out.println("EventBridge event received: " + eventJson);
      return "Event processed successfully";
    } catch (JsonProcessingException e) {
      System.err.println("Error serializing event: " + e.getMessage());
      throw new RuntimeException("Failed to process event", e);
    }
  }
}
