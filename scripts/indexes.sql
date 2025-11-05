-- Tip 7: Never Create a Primary Index in Production
CREATE COLLECTION `sms_app`.`_default`.`users`;
CREATE COLLECTION `sms_app`.`_default`.`sms`;
CREATE COLLECTION `sms_app`.`_default`.`limiter-conditions`;
CREATE COLLECTION `sms_app`.`_default`.`suspicious-words`;
CREATE COLLECTION `sms_app`.`_default`.`suspicious-categories`;

CREATE PRIMARY INDEX ON `sms_app`.`_default`.`_default`;
CREATE PRIMARY INDEX ON `sms_app`.`_default`.`users`;
CREATE PRIMARY INDEX ON `sms_app`.`_default`.`sms`;
CREATE PRIMARY INDEX ON `sms_app`.`_default`.`limiter-conditions`;
CREATE PRIMARY INDEX ON `sms_app`.`_default`.`suspicious-words`;
CREATE PRIMARY INDEX ON `sms_app`.`_default`.`suspicious-categories`;

CREATE INDEX idx_actives ON `sms_app`.`_default`.`limiter-conditions`(priority)
    WHERE active = true;

CREATE INDEX idx_caught_sms ON `sms_app`.`_default`.`sms`(appliedFilterId);

CREATE INDEX idx_sms_receive_times ON `sms_app`.`_default`.`sms`(receivedTime);

CREATE INDEX idx_sms_senders ON `sms_app`.`_default`.`sms`(sender, receivedTime);

CREATE INDEX idx_sms_senders_receivers ON `sms_app`.`_default`.`sms`(sender, receiver, receivedTime);

-- TODO: Tip 10: Avoid the use of SELECT *
-- TODO: Tip 12: Pushdown Pagination to the Index
-- TODO: Tip 15: Use Named Prepared statements
-- TODO: Tip 17: Avoid LIKE Statements