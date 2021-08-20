package com.buaa.qp.dao;

import com.buaa.qp.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountDao {
    Account selectByEmail(String email);

    Account selectById(Integer Id);

    Integer insert(Account account);

    /**
     * Modify password and username
     * @param account Object of Account
     * @return Whether success
     */
    Integer update(Account account);
}
