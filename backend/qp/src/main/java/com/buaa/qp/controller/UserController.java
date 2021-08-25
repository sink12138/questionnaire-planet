package com.buaa.qp.controller;

import com.buaa.qp.entity.Account;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.exception.RepetitiveOperationException;
import com.buaa.qp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> request) {
        Map<String, Object> map = new HashMap<>();
        try {
            String email;
            String username;
            String password;
            try {
                email = (String) request.get("email");
                username = (String) request.get("username");
                password = (String) request.get("password");
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            Account account = accountService.getAccountByEmail(email);
            if (account == null) {
                if (password == null || !accountService.isPasswordValid(password)) {
                    throw new ParameterFormatException();
                }
                Account newAccount = new Account(email, username, password, false);
                accountService.addAccount(newAccount);
                accountService.sendCheckEmail(newAccount.getAccountId(), email);
            } else if (account.getVerified()) {
                throw new RepetitiveOperationException();
            } else {
                if (password == null || !accountService.isPasswordValid(password)) {
                    throw new ParameterFormatException();
                }
                account.setPassword(password);
                account.setUsername(username);
                accountService.modifyInfos(account);
                accountService.sendCheckEmail(account.getAccountId(), email);
            }
            map.put("success", true);
        } catch (ParameterFormatException | RepetitiveOperationException e) {
            map.put("success", false);
            map.put("message", e.toString());
        } catch (Exception otherException) {
            map.put("success", false);
            map.put("message", "操作失败");
            otherException.printStackTrace();
        }
        return map;
    }

    @Autowired
    private HttpServletRequest httpServletRequest;

    @GetMapping("/verify")
    public ModelAndView verify(String code) {
        try {
            Account account = accountService.checkCode(code);
            account.setVerified(true);
            accountService.modifyInfos(account);
            httpServletRequest.setAttribute("email", account.getEmail());
            return new ModelAndView("CheckSuccess");
        } catch (ObjectNotFoundException e) {
            return new ModelAndView("CheckFailure");
        } catch (Exception otherException) {
            otherException.printStackTrace();
            return new ModelAndView("CheckFailure");
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = httpServletRequest.getSession();
        try {
            String email;
            String password;
            try {
                email = (String) request.get("email");
                password = (String) request.get("password");
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            Account account = accountService.getAccountByEmail(email);
            if (account == null || !account.getPassword().equals(password) || !account.getVerified()) {
                throw new LoginVerificationException();
            }
            map.put("success", true);
            session.setAttribute("accountId", account.getAccountId());
        } catch (ParameterFormatException | LoginVerificationException exception) {
            map.put("success", false);
            map.put("message", exception.toString());
        } catch (Exception otherException) {
            map.put("success", false);
            map.put("message", "操作失败");
            otherException.printStackTrace();
        }
        return map;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = httpServletRequest.getSession();
        try {
            Enumeration<String> em = session.getAttributeNames();
            while (em.hasMoreElements()) {
                session.removeAttribute(em.nextElement());
            }
            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
            map.put("message", "操作失败");
            e.printStackTrace();
        }
        return map;
    }

    @GetMapping("/home")
    public Map<String, Object> home() {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = httpServletRequest.getSession();
        try {
            Account account = accountService.getAccountBySession(session);
            map.put("success", true);
            map.put("email", account.getEmail());
            map.put("password", account.getPassword());
            map.put("username", account.getUsername());
        } catch (LoginVerificationException e) {
            map.put("success", false);
            map.put("message", e.toString());
        } catch (Exception e) {
            map.put("success", false);
            map.put("message", "操作失败");
            e.printStackTrace();
        }
        return map;
    }

    @PostMapping("/home/modify")
    public Map<String, Object> modify(@RequestBody Map<String, Object> request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = httpServletRequest.getSession();
        try {
            String password;
            String username;
            Account account = accountService.getAccountBySession(session);
            try {
                password = (String) request.get("password");
                username = (String) request.get("username");
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            if (password == null || !accountService.isPasswordValid(password) || username == null) {
                throw new ParameterFormatException();
            }
            Account newAccountInfos = new Account(account.getEmail(), username, password, true);
            newAccountInfos.setAccountId(account.getAccountId());
            accountService.modifyInfos(newAccountInfos);
            map.put("success", true);
            session.setAttribute("password", password);
        } catch (ParameterFormatException | LoginVerificationException | ObjectNotFoundException exception) {
            map.put("success", false);
            map.put("message", exception.toString());
        } catch (Exception e) {
            map.put("success", false);
            map.put("message", "操作失败");
            e.printStackTrace();
        }
        return map;
    }
}
