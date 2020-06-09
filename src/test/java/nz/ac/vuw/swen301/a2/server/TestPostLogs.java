package nz.ac.vuw.swen301.a2.server;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.text.ParseException;

public class TestPostLogs {

    @Test
    /** This tests that an invalid response code is generated when a request is sent with no parameters */
    public void testInvalidResponseCodeNoParam() throws IOException {
        /* Creating mock request and response */
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.doPost(request, response);
        assertEquals(400, response.getStatus()); //400 is the failure response code
    }

    @Test
    /** This tests that an invalid response code is generated when the requests log event is missing a field (level field) */
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when the requests log event has a field which is a JsonNull (level field in this case) */
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when the requests log event has too many fields (excluding optional error details field) */
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when the requests log event has too many fields (including optional error details field) */
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when a field (level in this case) of the log event of the request is not a JsonPrimitive */
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when a field (level in this case) of the log event of the request is not a String (but is a JsonPrimitive) */
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when the id field of the log event of the request doesn't follow UUID format*/
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code (409) is generated when the id of the log event of the request is the same as an existing log in the LogsServlet database*/
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        request.removeAllParameters();
        response.reset();
        JsonObject newObj1 = new JsonObject();
        newObj1.addProperty("id", "d290f1ee-6c54-4b01-90e6-d701748f0851"); //This id is identical to the one above
        newObj1.addProperty("message", "Everything running smoothly");
        newObj1.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");
        request.addParameter("LogEvent", newObj1.toString());
        service.doPost(request, response);
        assertEquals(409, response.getStatus());
    }

    /** This tests that an invalid response code is generated when timestamp of the log event of the request does not follow the specified date format */
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
        assertEquals(400, response.getStatus());
    }

    /** This tests that an invalid response code is generated when level of the log event of the request is invalid (isn't one of the 'levels' enum values in LogsServlet) */
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
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
        request.addParameter("LogEvent", newObj.toString());
        service.doPost(request, response);
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
        request.addParameter("LogEvent", newObj.toString());

        JsonObject newObj1 = new JsonObject();
        newObj1.addProperty("id", "a290f1ee-6c54-4b01-90e6-d701748f0851");
        newObj1.addProperty("message", "Everything running smoothly");
        newObj1.addProperty("timestamp", "2019-07-29T09:12:33.001Z");
        newObj1.addProperty("thread", "main");
        newObj1.addProperty("logger", "com.example.Foo");
        newObj1.addProperty("level", "WARN");
        request.addParameter("LogEvent", newObj1.toString());
        service.doPost(request, response);
        assertEquals(201, response.getStatus());
    }

}
