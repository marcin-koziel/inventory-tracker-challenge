package ca.shopify.inventorytrackerchallenge.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * The type Error handler controller to redirect errors.
 *
 * @author Marcin Koziel
 */
@Controller
public class ErrorHandlerController implements ErrorController {

    /**
     * Handle errors and redirect to appropriate dynamic error page.
     *
     * @param request the request
     * @param model   the model
     * @return the string
     */
    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // handle HTTP 404 Not Found error
                model
                        .addAttribute("errorMessage", "Not Found")
                        .addAttribute("errorCode", "404");

            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // handle HTTP 403 Forbidden error
                model
                        .addAttribute("errorMessage", "Access Forbidden")
                        .addAttribute("errorCode", "403");

            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // handle HTTP 500 Internal Server error
                model
                        .addAttribute("errorMessage", "Internal Server Error")
                        .addAttribute("errorCode", "500");
            }

        }

        return "/error/index";
    }

    public String getErrorPath() {
        return "/error";
    }

}
