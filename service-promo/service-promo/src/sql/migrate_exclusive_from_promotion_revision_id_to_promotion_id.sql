UPDATE IGNORE `lithium_promo`.`promo_exclusive_players` pep
    SET pep.promotion_id = (
        SELECT pr.promotion_id FROM `lithium_promo`.`promotion_revision` pr
        WHERE pr.id = pep.promotion_revision_id
        LIMIT 1
    )
WHERE pep.promotion_id IS NULL;