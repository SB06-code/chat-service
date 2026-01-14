package codeit.sb06.ws.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/chat/{roomId}")
    public String chatRoom(@PathVariable String roomId,
                           @RequestParam(required = false) String username,
                           Model model) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("username", username != null ? username : "");
        return "chat";
    }
}