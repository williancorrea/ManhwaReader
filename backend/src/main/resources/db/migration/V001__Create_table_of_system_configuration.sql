CREATE TABLE SYSTEM_CONFIGURATION_GROUP
(
    ID          CHAR(36)     NOT NULL DEFAULT UUID() PRIMARY KEY UNIQUE,
    NAME        VARCHAR(200) NOT NULL UNIQUE,
    DESCRIPTION VARCHAR(200),
    ACTIVE      BOOL         NOT NULL DEFAULT TRUE
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8;

INSERT INTO SYSTEM_CONFIGURATION_GROUP (ID, NAME, DESCRIPTION, ACTIVE)
VALUES ('53fa779d-a73c-4a28-a2c4-19bb2b5693c9', 'MINIO', 'Minio configurations', 1);
INSERT INTO SYSTEM_CONFIGURATION_GROUP (ID, NAME, DESCRIPTION, ACTIVE)
VALUES ('3df4db16-6c46-4473-98d7-5150e10bf871', 'EMAIL', 'Minio configurations', 1);


CREATE TABLE SYSTEM_CONFIGURATION
(
    ID                            CHAR(36)     NOT NULL DEFAULT UUID() PRIMARY KEY UNIQUE,
    ID_SYSTEM_CONFIGURATION_GROUP CHAR(36)     NOT NULL,
    FOREIGN KEY (ID_SYSTEM_CONFIGURATION_GROUP) REFERENCES SYSTEM_CONFIGURATION_GROUP (ID),
    DESCRIPTION                   VARCHAR(200),
    REFERENCE                     VARCHAR(200) NOT NULL UNIQUE,
    VALUE                         VARCHAR(200) NOT NULL,
    ACTIVE                        BOOL         NOT NULL DEFAULT TRUE
) ENGINE = INNODB
  DEFAULT CHARSET = UTF8;

INSERT INTO SYSTEM_CONFIGURATION (ID, ID_SYSTEM_CONFIGURATION_GROUP, DESCRIPTION, REFERENCE, VALUE, ACTIVE)
VALUES ('8f5f7916-2474-4feb-bec0-4dd5e19d2887', '53fa779d-a73c-4a28-a2c4-19bb2b5693c9',
        'Minio - Temporary bucket expiration in days', 'MINIO_TEMPORARY_BUCKET_EXPIRATION_IN_DAYS', '10', 1);

INSERT INTO SYSTEM_CONFIGURATION (ID, ID_SYSTEM_CONFIGURATION_GROUP, DESCRIPTION, REFERENCE, VALUE, ACTIVE)
VALUES (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Config enabled', 'EMAIL_ENABLED', 'false', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Transport protocol', 'EMAIL_SMTP_PROTOCOL', 'smtp', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp host', 'EMAIL_SMTP_HOST', 'smtp.gmail.com', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp port', 'EMAIL_SMTP_PORT', '587', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp username', 'EMAIL_SMTP_USERNAME', '', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp password', 'EMAIL_SMTP_PASSWORD', '', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp auth', 'EMAIL_SMTP_AUTH', 'true', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp start tls enabled', 'EMAIL_SMTP_STARTTLS_ENABLE', 'true', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp start tls required', 'EMAIL_SMTP_STARTTLS_REQUIRED', 'true', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp connection timeout', 'EMAIL_SMTP_CONNECTION_TIMEOUT', '5000', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp writ timeout', 'EMAIL_SMTP_WRITE_TIMEOUT', '5000', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Smtp timeout', 'EMAIL_SMTP_TIMEOUT', '5000', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Debug', 'EMAIL_DEBUG', 'false', 1),
       
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - from', 'EMAIL_FROM', 'noreply@manhwareader.com', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - from name', 'EMAIL_FROM_NAME', 'Manhwa Reader', 1),
       (UUID(), '3df4db16-6c46-4473-98d7-5150e10bf871', 'Email - Admin', 'EMAIL_ADMIN', 'willian.vag@gmail.com', 1);
