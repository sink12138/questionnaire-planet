package com.buaa.qp.service.impl;

import com.buaa.qp.dao.AccountDao;
import com.buaa.qp.dao.CheckDao;
import com.buaa.qp.entity.Account;
import com.buaa.qp.entity.Check;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import com.buaa.qp.service.AccountService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountDao accountDao;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    CheckDao checkDao;

    @Override
    public void addAccount(Account account) {
        accountDao.insert(account);
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountDao.selectByEmail(email);
    }

    @Value("${check.valid-time}")
    private Integer VALID_TIME;

    @Override
    public Account checkCode(String code) throws ObjectNotFoundException {
        Check check = checkDao.selectByCode(code);
        Date deadline = new Date(new Date().getTime() - VALID_TIME);
        if (check == null || check.getCheckingTime().getTime() < deadline.getTime()) {
            throw new ObjectNotFoundException();
        }
        checkDao.deleteById(check.getCheckId());
        return accountDao.selectById(check.getAccountId());
    }

    @Override
    public void modifyInfos(Account newAccountInfos) throws ObjectNotFoundException {
        if (accountDao.update(newAccountInfos) == 0) {
            throw new ObjectNotFoundException();
        }
    }

    @Override
    public Boolean isPasswordValid(String password) {
        if (password.length() > 20 || password.length() < 6) {
            return false;
        }
        boolean containCharacter = false;
        boolean containNumber = false;
        for (char character : password.toCharArray()) {
            if (character >= '0' && character <= '9') {
                containNumber = true;
            } else if ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z') ) {
                containCharacter = true;
            } else {
                return false;
            }
        }
        return containCharacter && containNumber;
    }

    @Override
    public void sendCheckEmail(Integer accountId, String email) throws MailException {
        new Thread(new CheckEmailThread(accountId, email)).start();
    }

    @Override
    public Account getAccountBySession(HttpSession session) throws LoginVerificationException {
        Integer accountId = (Integer) session.getAttribute("accountId");
        Account account = accountDao.selectById(accountId);
        if (account == null) {
            throw new LoginVerificationException();
        }
        return account;
    }

    @Value("${spring.mail.username}")
    private String FROM_ADDRESS;

    @Value("${check.code-bits}")
    private Integer CODE_BITS;

    private void basicSetting(MimeMessage mimeMessage, String subject, String toAddress, String process) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("问卷星球 | " + subject);
        helper.setFrom(FROM_ADDRESS);
        helper.setTo(toAddress);
        helper.setSentDate(new Date());
        helper.setText(process, true);
    }

    private String generateCode(Integer accountId) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < CODE_BITS; i++) {
            int number = random.nextInt(62);
            stringBuffer.append(str.charAt(number));
        }
        return stringBuffer.toString() + '_' +accountId;
    }

    private String formatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public class CheckEmailThread implements Runnable {
        Integer accountId;
        String email;

        public CheckEmailThread(Integer accountId, String email) {
            this.accountId = accountId;
            this.email = email;
        }

        @Value("${check.ip-address}")
        private String IP_ADDRESS;

        @SneakyThrows
        @Override
        public void run() {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            String subject = "注册邮箱验证";
            String action = "注册";
            String code = generateCode(accountId);
            Check check = new Check(accountId, code, new Date());
            checkDao.insert(check);
            Context context = new Context();
            context.setVariable("action", action);
            context.setVariable("checkLink",  IP_ADDRESS +"/verify?code=" + code);
            context.setVariable("email", email);
            context.setVariable("createTime", formatDate());
            String process = templateEngine.process("CheckEmail.html", context);
            basicSetting(mimeMessage, subject, email, process);
            javaMailSender.send(mimeMessage);
        }
    }
}
