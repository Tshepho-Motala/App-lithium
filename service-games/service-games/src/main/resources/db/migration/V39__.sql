ALTER TABLE `game`
    ADD COLUMN `supplier_game_reward_guid` VARCHAR(255)  NULL, ALGORITHM=INPLACE, LOCK=NONE;