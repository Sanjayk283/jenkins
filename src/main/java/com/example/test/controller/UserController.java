    package com.example.test.controller;

    import com.example.test.serviceImpl.UserServiceImpl;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

    import java.io.IOException;

    @Controller
    public class UserController {
    
        @Autowired
        private UserServiceImpl userService;

        @GetMapping("/")
        public String showForm() {
            return "index";
        }

        @PostMapping("/generate")
        public StreamingResponseBody generateProject(@RequestParam String projectName,
                                                     @RequestParam String ait,
                                                     HttpServletResponse response) throws IOException {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + projectName + ".zip\"");
            return outputStream -> userService.generateAndDownloadProject(projectName, ait, outputStream);
        }


    
    }
