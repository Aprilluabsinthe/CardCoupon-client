package com.cardcoupon.passbook.dao;

import com.cardcoupon.passbook.entity.Merchants;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * The interface of Merchants Dao
 */
public interface MerchantsDao extends JpaRepository<Merchants,Integer> {
    /**
     * get Merchants by given Id
     * @param id the id to be found
     * @return {@link Merchants}
     */
    Optional<Merchants> findById(Integer id);

    /**
     * get Merchants by given name
     * @param name the id to be found
     * @return {@link Merchants}
     */
    Merchants findByName(String name);

    /**
     * get Merchants by given name
     * @param ids the ids List to be found
     * @return List of {@link Merchants}
     */
    List<Optional<Merchants> > findByIdIn(List<Integer> ids);
}
