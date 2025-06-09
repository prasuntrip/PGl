package com.example.PGL.Controller;

import com.example.PGL.Model.Messages;
import com.example.PGL.Model.UserModel;
import com.example.PGL.Services.IDService;
import com.example.PGL.Services.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private IDService idService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/")
    public String home(HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username != null) {
            // User is logged in, send them directly to dashboard
            return "redirect:/dashboard";
        }

        // User not logged in, show login page
        return "index"; // loads index.html (login/start page)
    }


    @PostMapping("/start")
    public String start(@RequestParam String username, @RequestParam String email, HttpSession session, Model model) {
        session.setAttribute("username", username);
        session.setAttribute("email", email);

        if (idService.existsByUsername(username)) {
            UserModel existingUser = idService.findByUsername(username);
            if (!existingUser.getEmail().equalsIgnoreCase(email)) {
                model.addAttribute("error", "Username already taken by another email.");
                return "index";  // return to start page with error
            }
        }

        int code = idService.sendEmail(email);
        session.setAttribute("code", code);

        return "verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam int otp, HttpSession session, RedirectAttributes redirectAttributes) {
        int code = (int) session.getAttribute("code");
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");

        if (code != otp) {
            System.out.println("OTP NOT VERIFIED :- " + otp);
            return "verify";
        }

        UserModel existingUser = idService.findByUsername(username);
        if (existingUser != null) {
            if (!existingUser.getEmail().equals(email)) {
                redirectAttributes.addFlashAttribute("error", "Username already taken with a different email.");
                return "redirect:/verify"; // or a dedicated error page
            }
        } else {
            UserModel newUser = new UserModel();
            newUser.setUsername(username);
            newUser.setEmail(email);
            idService.save(newUser);
        }

        // Fetch messages again for dashboard or fetch on dashboard page
        List<Messages> messages = messageService.getMessagesByUsername(username);
        session.setAttribute("username", username);

        redirectAttributes.addFlashAttribute("username", username);
        redirectAttributes.addFlashAttribute("messages", messages);

        return "redirect:/dashboard";
    }



    @GetMapping("/send/{username}")
    public String send(@PathVariable String username, HttpSession session, Model model) {
        if (!idService.existsByUsername(username)) {
            model.addAttribute("error", "User does not exist.");
            return "error";
        }

        session.setAttribute("recipient", username);
        model.addAttribute("username", username);
        return "send";
    }

    @PostMapping("/send/{username}")
    public String verifyMessage(@PathVariable String username, Model model, @RequestParam String message) {
        if (!idService.existsByUsername(username)) {
            model.addAttribute("error", "User does not exist.");
            return "error";
        }

        Messages msg = new Messages();
        msg.setUsername(username);
        msg.setMessage(message);
        messageService.saveMessage(msg);

        model.addAttribute("success", "Message sent!");
        model.addAttribute("username", username);
        return "send";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, HttpServletRequest request) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/";
        }

        List<Messages> messages = messageService.getMessagesByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("messages", messages);

        // Build full URL (e.g., http://localhost:8080/send/username)
        String domain = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String fullLink = domain + "/send/" + username;
        model.addAttribute("shareLink", fullLink);

        return "dashboard";
    }


    @GetMapping("/view/sample")
    public String viewSample(@RequestParam String username, Model model) {
        String sampleText = "Send me an anonymous message!";
        model.addAttribute("message", sampleText);
        model.addAttribute("username", username);
        return "view"; // reuse same view.html
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/"; // back to login page
    }


    @PostMapping("/delete/{id}")
    public String deleteMessage(@PathVariable int id, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/";
        }

        Messages msg = messageService.getMessageById(id);
        if (msg != null && msg.getUsername().equals(username)) {
            messageService.deleteMessageById(id);
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/view/{id}")
    public String viewMessage(@PathVariable int id, Model model) {
        Messages msg = messageService.getMessageById(id);

        if (msg == null) {
            return "error";
        }

        model.addAttribute("message", msg.getMessage());
        return "view"; // this maps to templates/view.html
    }


}