package com.buaa.qp.service;

import com.buaa.qp.entity.Account;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import org.springframework.mail.MailException;

import javax.servlet.http.HttpSession;

public interface AccountService {
    Account getAccountById(Integer accountId);

    void addAccount(Account account);

    /**
     * Select Account by email,
     * Check whether the email has been used when login and register
     * @param email Target email
     * @return An account with appropriate email
     */
    Account getAccountByEmail(String email);

    /**
     * Check whether the code is valid
     * @param code The check code
     * @return The Account Object
     */
    Account checkCode(String code) throws ObjectNotFoundException;

    /**
     * Modify infos of current account,
     * If the attribute is null, just not modify
     * @param newAccountInfos An object of Account with modified infos
     */
    void modifyInfos(Account newAccountInfos) throws ObjectNotFoundException;

    /**
     * Judge whether password is valid,
     * the password must contain character and number at the mean time, the length must be in range [6, 20]
     * @param password The password needed to judge
     * @return Ture if valid or false
     */
    Boolean isPasswordValid(String password);

    /**
     * Send a check email to the account Email
     * @param accountId The id of account, if the param is null = new account register
     * @param email The email address of account
     * @throws MailException The Exceptions of mail sending
     */
    void sendCheckEmail(Integer accountId, String email) throws MailException;

    Account getAccountBySession(HttpSession session) throws LoginVerificationException;

    boolean isShuffleIdMatched(Integer shuffleId, Integer accountId, Integer templateId);

}
