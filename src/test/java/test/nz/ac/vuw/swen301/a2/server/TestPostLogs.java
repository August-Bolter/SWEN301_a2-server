package test.nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Class that contains tests for testing doPost() in LogsServlet */
public class TestPostLogs {

    @Test
    /** This tests that an invalid response code is generated when a request is sent with no content body */
    public void testInvalidResponseCodeRequestNoBody() throws IOException {
        /* Creating mock request and response */
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus()); //400 is the failure response code
    }

    @Test
    /** This tests that an invalid response code is generated when the request has no content type */
    public void testInvalidResponseCodeRequestNoContentType() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "WARN");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear(); //Clearing logs since logs is static
        assertEquals(400, response.getStatus());
    }

    @Test
    /** This tests that an invalid response code is generated when the request has an invalid content type */
    public void testInvalidResponseCodeRequestInvalidContentType() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("text/plain");
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "WARN");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear(); //Clearing logs since logs is static
        assertEquals(400, response.getStatus());
    }

    @Test
    /** This tests that an invalid response code is generated when the requests body content is missing a field (level field) */
    public void testInvalidResponseCodeMissingField() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject(); //Creating the log
        /* Adding fields to the log, but missing field is level */
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when the requests body content has a field which is a JsonNull (level field in this case) */
    @Test
    public void testInvalidResponseCodeJsonNull() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.add("level", JsonNull.INSTANCE); //Adding a JsonNull as one of the fields
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when the requests body content has too many fields (excluding optional error details field) */
    @Test
    public void testInvalidResponseCodeTooManyFieldsWithoutErrorDetails() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "WARN");
        newObj.addProperty("test", "testing"); //Not a valid field
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when the requests body content has too many fields (including optional error details field) */
    @Test
    public void testInvalidResponseCodeTooManyFieldsWithErrorDetails() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "ERROR");
        newObj.addProperty("errorDetails", "Failure");
        newObj.addProperty("test", "testing");
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when a field (level in this case) of the requests body content is not a JsonPrimitive */
    @Test
    public void testInvalidResponseCodeFieldNotPrimitive() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.add("level", new JsonObject()); //Not a JsonPrimitive
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when a field (level in this case) of the requests body content is not a String (but is a JsonPrimitive) */
    @Test
    public void testInvalidResponseCodeFieldNotString() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", true); //A JsonPrimitive (boolean) but not a string.
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when errorDetails of the requests body content is not a JsonPrimitive */
    @Test
    public void testInvalidResponseCodeErrorDetailsNotPrimitive() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "WARN");
        newObj.add("errorDetails", new JsonObject());
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when errorDetails of the requests body content is not a String (but is a JsonPrimitive) */
    @Test
    public void testInvalidResponseCodeErrorDetailsNotString() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "WARN");
        newObj.addProperty("errorDetails", true);
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when the id field of the requests body content doesn't follow UUID format*/
    @Test
    public void testInvalidResponseCodeIncorrectIDFormat() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "f1ee-6c54748f0851"); //This ID doesn't follow the specified UUID format
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "ERROR");
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code (409) is generated when the id of the requests body content is the same as an existing log in the LogsServlet database*/
    @Test
    public void testInvalidResponseCode409() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "ERROR");
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        assertEquals(201, response.getStatus());

        MockHttpServletResponse response1 = new MockHttpServletResponse();
        JsonObject newObj1 = new JsonObject();
        newObj1.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851"); //This id is identical to the one above
        newObj1.addProperty("message", "Everything running smoothly");
        newObj1.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");
        request.setContent(newObj1.toString().getBytes());
        service.doPost(request, response1);
        LogsServlet.logs.clear();
        assertEquals(409, response1.getStatus());
    }

    /** This tests that an invalid response code is generated when timestamp of the requests body content does not follow the specified date format */
    @Test
    public void testInvalidResponseCodeTimeStampInvalidFormat() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2020"); //Timestamp doesn't match specified date format
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "ERROR");
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when level of the requests body content is invalid (isn't one of the 'levels' enum values in LogsServlet) */
    @Test
    public void testInvalidResponseCodeInvalidLevel() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "test"); //'test' isn't part of levels enum in LogsServlet
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        LogsServlet.logs.clear();
        assertEquals(400, response.getStatus());
    }

    /** This tests that a valid response code is generated one log event is posted with completely valid fields */
    @Test
    public void testValidResponseCodePostingOneLog() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "WARN");
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);
        assertEquals(1, LogsServlet.logs.size());
        LogsServlet.logs.clear();
        assertEquals(201, response.getStatus()); //201 is the success response code
    }

    /** This tests that a valid response code is generated multiple log event are posted with completely valid fields */
    @Test
    public void testValidResponseCodePostingMultipleLogs() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        JsonObject newObj = new JsonObject();
        newObj.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj.addProperty("message", "Everything running smoothly");
        newObj.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj.addProperty("thread", "main");
        newObj.addProperty("logger", "com.example.Foo");
        newObj.addProperty("level", "WARN");
        request.setContentType("application/json");
        request.setContent(newObj.toString().getBytes());
        service.doPost(request, response);

        MockHttpServletResponse response1 = new MockHttpServletResponse();
        JsonObject newObj1 = new JsonObject();
        newObj1.addProperty("id", "a290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj1.addProperty("message", "Everything running smoothly");
        newObj1.addProperty("timestamp", "2020-07-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");
        request.setContent(newObj1.toString().getBytes());
        service.doPost(request, response1);
        assertEquals(2, LogsServlet.logs.size());
        LogsServlet.logs.clear();
        assertEquals(201, response1.getStatus());
    }

}
