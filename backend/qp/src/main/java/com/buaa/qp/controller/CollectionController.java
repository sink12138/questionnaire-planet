package com.buaa.qp.controller;

import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.ExtraMessageException;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class CollectionController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateService templateService;

    @GetMapping("/locked")
    public Map<String, Object> locked(@RequestBody Map<String, Object> requestMap) throws ParameterFormatException, ObjectNotFoundException {
        Map<String, Object> map = new HashMap<>();
        Integer templateId;
        try {
            // Parameter checks
            try {
                templateId = (Integer) requestMap.get("templateId");
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId < 0) {
                throw new ParameterFormatException();
            }
            Template template = templateService.getTemplate(templateId);
            if (template == null || template.getDeleted()) {
                throw new ObjectNotFoundException();
            } else if (!template.getReleased()) {
                throw new ExtraMessageException("问卷尚未发布, 无法访问");
            }
            map.put("success", true);
            map.put("locked", template.getPassword() != null && !template.getPassword().equals(""));

        } catch (ParameterFormatException | ObjectNotFoundException | ExtraMessageException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @GetMapping("/details")
    public Map<String, Object> details(@RequestBody Map<String, Object> requestMap) {
        return null;
    }
}
