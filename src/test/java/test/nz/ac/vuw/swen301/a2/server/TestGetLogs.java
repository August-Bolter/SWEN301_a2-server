package test.nz.ac.vuw.swen301.a2.server;
import nz.ac.vuw.swen301.a2.server.LogsServlet;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class TestGetLogs {
    @Test
    public void testInvalidResponseCode1() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }
}
