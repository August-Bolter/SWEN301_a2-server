package nz.ac.vuw.swen301.a2.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LogsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int limit = Integer.parseInt(req.getParameter("limit"));
            if (limit > Integer.MAX_VALUE || limit < 0) {
                resp.sendError(400);
            }
        }
        catch (NumberFormatException x) {
            resp.sendError(400);
        }
        String level = req.getParameter("level");
        resp.setContentType("application/json");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
