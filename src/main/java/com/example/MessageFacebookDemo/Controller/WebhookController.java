package com.example.MessageFacebookDemo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    @Value("${messenger.verifyToken}")
    private String verifyToken;
    final String payload =
            "{\n"
                    + "  \"object\": \"page\",\n"
                    + "  \"entry\": [{\n"
                    + "    \"id\": \"1717527131834678\",\n"
                    + "    \"time\": 1475942721780,\n"
                    + "    \"messaging\": [{\n"
                    + "      \"sender\": {\n"
                    + "        \"id\": \"1256217357730577\"\n"
                    + "      },\n"
                    + "      \"recipient\": {\n"
                    + "        \"id\": \"1717527131834678\"\n"
                    + "      },\n"
                    + "      \"timestamp\": 1475942721741,\n"
                    + "      \"message\": {\n"
                    + "        \"mid\": \"mid.1475942721728:3b9e3646712f9bed52\",\n"
                    + "        \"seq\": 123,\n"
                    + "        \"text\": \"Hello Chatbot\"\n"
                    + "      }\n"
                    + "    }]\n"
                    + "  }]\n"
                    + "}";

    private final  ObjectMapper objectMapper;
@Autowired
    public WebhookController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    // Add support for GET requests to our webhook
   @GetMapping
    public ResponseEntity<String>  verifyWebhook(@RequestParam(name = "hub.mode") String hubMode, @RequestParam(name = "hub.verify_token") String hubVerifyToken, @RequestParam(name = "hub.challenge") String hubChallenge) {
       if (hubMode!=null &&hubVerifyToken!=null){
           if (hubMode.equals("subscribe") && hubVerifyToken.equals(verifyToken) ){
               System.out.println("Webhook verified");
               return ResponseEntity.ok(hubChallenge);
           }
       }
       System.out.println("Webhook not verified");
       System.out.println(getObjectOfBody(payload));
       return ResponseEntity.status(403).body("Forbidden");
    }
    @GetMapping("/object")
    public String getObject(){
        return getObjectOfBody(payload);
    }


   public  String getObjectOfBody(String body)  {
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(body);
            String object = rootNode.path("object").asText();
            System.out.println("Received object: " + object);
            return object;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestParam String body, HttpServletRequest request){
      if (getObjectOfBody(body)=="page"){
          System.out.println("Webhook verified");
          System.out.println("Received body: " + body);
          return ResponseEntity.ok("EVENT_RECEIVED");
      }
      else {
          return ResponseEntity.status(403).body("Forbidden");
      }
    }
}
