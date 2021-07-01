package com.cardcoupon.passbook.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * The Model of Merchants
 *     `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
 *     `name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT 'Name of Merchant',
 *     `logo_url` varchar(256) COLLATE utf8_bin NOT NULL COMMENT 'logo of Merchant',
 *     `business_license_url` varchar(256 ) COLLATE utf8_bin NOT NULL COMMENT 'complement license of Merchant',
 *     `phone` varchar(64) COLLATE utf8_bin NOT NULL COMMENT 'Contact Phone',
 *     `address` varchar(64) COLLATE utf8_bin NOT NULL COMMENT 'Contact Address',
 *     `is_audit` BOOLEAN COLLATE utf8_bin NOT NULL COMMENT 'Is audited or not',
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="merchants")
public class Merchants {
    /** the id of the merchant*/
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Integer id;

    /** the name of the merchant, Globally unique*/
    @Basic
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    /** the logo url of the merchant */
    @Basic
    @Column(name = "logo_url", nullable = false)
    private String logoUrl;

    /** the license url of the merchant */
    @Basic
    @Column(name = "business_license_url", nullable = false)
    private String businessLicenseUrl;

    /** Contact phone */
    @Basic
    @Column(name = "phone", nullable = false)
    private String phone;

    /** Contact address */
    @Basic
    @Column(name = "address", nullable = false)
    private String address;

    /** The merchant has been audited or not */
    @Basic
    @Column(name = "is_audit", unique = true, nullable = false)
    private Boolean isAudit = false;
}
