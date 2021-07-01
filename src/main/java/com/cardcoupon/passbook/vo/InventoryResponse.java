package com.cardcoupon.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * response for requesting inventory
 * should be different according to Users
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    /** User's ID */
    private Long userId;

    /** Pieces of PassTemplate Information */
    private List<PassTemplateInfo> passTemplateInfos;
}
