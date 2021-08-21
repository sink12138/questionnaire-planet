package com.buaa.qp.controller;

import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class CollectionController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateService templateService;

    @GetMapping("/locked")
    public Map<String, Object> locked(@RequestBody Map<String, Object> requestMap) {
        return null;
    }

    @GetMapping("/details")
    public Map<String, Object> details(@RequestBody Map<String, Object> requestMap) {
        return null;
    }
}
