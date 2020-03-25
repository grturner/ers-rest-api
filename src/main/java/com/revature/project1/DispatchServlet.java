package com.revature.project1;

import com.revature.project1.service.ReimbService;
import com.revature.project1.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class DispatchServlet extends HttpServlet {
    private static final String REIMBURSEMENTS = "remimbursements";
    private final Logger logger = LogManager.getLogger(DispatchServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        String jsonResponse = null;
        try {
            if (uriDecon.length >= 3) {
                switch (uriDecon[2]) {
                    case "users":
                        UserService uService = new UserService();
                        jsonResponse = uService.processGet(Arrays.copyOfRange(uriDecon, 2, uriDecon.length));
                        break;
                    case REIMBURSEMENTS:
                        ReimbService rService = new ReimbService();
                        jsonResponse = rService.processGet(Arrays.copyOfRange(uriDecon, 2, uriDecon.length));
                        break;
                    default:
                        response.sendError(404);
                }
            } else {
                response.sendError(404, "Path not found");
            }
            PrintWriter out = response.getWriter();
            out.println(jsonResponse);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String json = br.readLine();
            br.close();
            int returnCode = -1;
            String returnStr = "";
            if (uriDecon.length >= 3) {
                switch (uriDecon[2]) {
                    case "users":
                        UserService uService = new UserService();
                        returnStr = uService.processPost(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), json);
                        if (returnStr != null) {
                            if (returnStr.equals("200")) {
                                returnCode = 200;
                            } else {
                                returnCode = 200;
                                PrintWriter out = response.getWriter();
                                out.println(returnStr);
                            }
                        }
                        break;
                    case REIMBURSEMENTS:
                        ReimbService rService = new ReimbService();
                        returnCode = rService.processPost(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), json);
                        break;
                    default:
                        response.sendError(404);
                }
            }
            if (returnCode == -1)
                response.sendError(404);
            else
                response.setStatus(returnCode);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int returnCode = -1;
        String[] uriDecon = request.getRequestURI().toLowerCase().split("/");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String json = br.readLine();
            br.close();
            if (uriDecon.length >= 3) {
                if (REIMBURSEMENTS.equals(uriDecon[2])) {
                    ReimbService service = new ReimbService();
                    returnCode = service.processPut(Arrays.copyOfRange(uriDecon, 2, uriDecon.length), json);
                } else {
                    response.sendError(404);
                }
            }
            if (returnCode == -1)
                response.sendError(400);
            else
                response.setStatus(returnCode);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
}
