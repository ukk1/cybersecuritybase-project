package sec.project.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.DatabaseQueries;
import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;

@Controller
public class SignupController {
    
    
    @RequestMapping("*")
    public String defaultMapping(Model model, HttpSession httpSession) {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm(Model model, HttpSession httpSession) {
        return "form";
    }
    
    @RequestMapping(value = "/supersecretpage", method = RequestMethod.GET)
    public String redirectURL(@RequestParam String url) throws SQLException {
        return "redirect:" + url;
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String username, @RequestParam String password) throws SQLException {
        DatabaseQueries db = new DatabaseQueries();
        try {
            Signup login = db.getAccount(username, password);
            if (login != null) {
                return "supersecretpage";
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}