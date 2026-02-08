```text
┌──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                          REVCONNECT DATABASE SCHEMA                                                  │
├──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                                      │
│  ┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐  │
│  │                                                USER TABLE                                                      │  │
│  ├────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤  │
│  │  PRIMARY KEY: user_id                                                                                          │  │
│  │  UNIQUE: username, email                                                                                       │  │
│  ├────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤  │
│  │  + user_id           : INT          (Auto-increment)                                                           │  │
│  │  + username          : VARCHAR(50)  (Unique, Not Null)                                                         │  │
│  │  + email             : VARCHAR(100) (Unique, Not Null)                                                         │  │
│  │  + password_hash     : VARCHAR(255) (Not Null)                                                                 │  │
│  │  + user_type         : ENUM         ('PERSONAL', 'BUSINESS', 'CREATOR')                                        │  │
│  │  + full_name         : VARCHAR(100) (Not Null)                                                                 │  │
│  │  + bio               : TEXT                                                                                    │  │
│  │  + profile_pic_path  : VARCHAR(255)                                                                            │  │
│  │  + location          : VARCHAR(100)                                                                            │  │
│  │  + website           : VARCHAR(255)                                                                            │  │
│  │  + is_private        : BOOLEAN      (Default: false)                                                           │  │
│  │  + created_at        : TIMESTAMP    (Default: CURRENT_TIMESTAMP)                                               │  │
│  │  + updated_at        : TIMESTAMP    (Default: CURRENT_TIMESTAMP ON UPDATE)                                     │  │
│  └────────────┬───────────────────────────────────────────────────────────────────────────────────────────────────┘  │
│               │                                                                                                      │
│               │                                                                                                      │
│  ┌────────────▼─────────────────────┐  ┌────────────▼──────────────────────┐  ┌────────────▼──────────────┐          │               
│  │   BUSINESS_USERS TABLE           │  │   CREATOR_USERS TABLE             │  │   PERSONAL_USERS TABLE    │          │               
│  ├──────────────────────────────────┤  ├───────────────────────────────────┤  ├───────────────────────────┤          │               
│  │  PRIMARY KEY: user_id            │  │  PRIMARY KEY: user_id             │  │  (No separate table -     │          │               
│  │  FOREIGN KEY: user_id            │  │  FOREIGN KEY: user_id             │  │   uses base USER table)   │          │               
│  │      → USERS.user_id             │  │      → USERS.user_id              │  │                           │          │               
│  ├──────────────────────────────────┤  ├───────────────────────────────────┤  ├───────────────────────────┤           │              
│  │  + category          VARCHAR(100)│  │  + category          VARCHAR(100) │  │                           │           │            
│  │  + industry          VARCHAR(100)│  │  + detailed_bio      TEXT         │  │                           │           │            
│  │  + detailed_bio      TEXT        │  │  + contact_info      VARCHAR(255) │  │                           │           │             
│  │  + business_address  TEXT        │  │  + social_media_links TEXT        │  │                           │           │             
│  │  + contact_info      VARCHAR(255)│  │  + endorsement_links TEXT         │  │                           │           │             
│  │  + business_hours    VARCHAR(100)│  │                                   │  │                           │           │             
│  │  + social_media_links TEXT       │  │                                   │  │                           │           │             
│  │  + endorsement_links TEXT        │  │                                   │  │                           │           │             
│  └──────────────────────────────────┘  └───────────────────────────────────┘  └───────────────────────────┘           │             
│                                                                                                                       │
├───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                           CONTENT CREATION RELATIONSHIPS                                              │
├───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                                       │
│         ┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐       │
│         │                                      POSTS TABLE                                                    │       │
│         ├─────────────────────────────────────────────────────────────────────────────────────────────────────┤       │
│         │  PRIMARY KEY: post_id                                                                               │       │
│         │  FOREIGN KEY: user_id → USERS.user_id                                                               │       │
│         ├─────────────────────────────────────────────────────────────────────────────────────────────────────┤       │
│         │  + post_id          : INT          (Auto-increment)                                                 │       │
│         │  + user_id          : INT          (Not Null)                                                       │       │
│         │  + content          : TEXT          (Not Null)                                                      │       │
│         │  + hashtags         : VARCHAR(500)                                                                  │       │
│         │  + post_type        : ENUM          ('TEXT', 'PROMOTIONAL', 'ANNOUNCEMENT')                         │       │
│         │  + call_to_action   : VARCHAR(100)                                                                  │       │
│         │  + tagged_products  : VARCHAR(500)                                                                  │       │
│         │  + scheduled_time   : TIMESTAMP                                                                     │       │
│         │  + is_pinned        : BOOLEAN        (Default: false)                                               │       │
│         │  + reach_count      : INT            (Default: 0)                                                   │       │
│         │  + created_at       : TIMESTAMP      (Default: CURRENT_TIMESTAMP)                                   │       │
│         │  + updated_at       : TIMESTAMP      (Default: CURRENT_TIMESTAMP ON UPDATE)                         │       │
│         └────────────┬────────────────────────────────┬────────────────────────────────┬──────────────────────┘       │
│                      │                                │                                │                              │
│                      │                                │                                │                              │
│        ┌─────────────▼──────────────┐   ┌─────────────▼──────────────┐   ┌─────────────▼──────────────┐               │
│        │       COMMENTS TABLE       │   │         LIKES TABLE        │   │        SHARES TABLE        │               │
│        ├────────────────────────────┤   ├────────────────────────────┤   ├────────────────────────────┤               │
│        │  PRIMARY KEY: comment_id   │   │  PRIMARY KEY: like_id      │   │  PRIMARY KEY: share_id     │               │
│        │  FOREIGN KEYS:             │   │  FOREIGN KEYS:             │   │  FOREIGN KEYS:             │               │
│        │    • post_id → POSTS       │   │    • post_id → POSTS       │   │    • post_id → POSTS       │               │
│        │    • user_id → USERS       │   │    • user_id → USERS       │   │    • user_id → USERS       │               │
│        ├────────────────────────────┤   ├────────────────────────────┤   ├────────────────────────────┤               │
│        │  + comment_id : INT        │   │  + like_id    : INT        │   │  + share_id   : INT        │               │
│        │  + post_id    : INT        │   │  + post_id    : INT        │   │  + post_id    : INT        │               │
│        │  + user_id    : INT        │   │  + user_id    : INT        │   │  + user_id    : INT        │               │
│        │  + content    : TEXT       │   │  + created_at : TIMESTAMP  │   │  + created_at : TIMESTAMP  │               │
│        │  + created_at : TIMESTAMP  │   │  + UNIQUE(post_id, user_id)│   │                            │               │
│        │  + updated_at : TIMESTAMP  │   │                            │   │                            │               │
│        └────────────────────────────┘   └────────────────────────────┘   └────────────────────────────┘               │
│                                                                                                                       │
├───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                        USER RELATIONSHIP TABLES                                                       │
├───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                                       │
│      ┌────────────────────────────────────────────────────────────────────────────────────────────────────┐          │
│      │                                  FOLLOWS TABLE                                                     │          │
│      ├────────────────────────────────────────────────────────────────────────────────────────────────────┤          │
│      │  PRIMARY KEY: follow_id                                                                            │         │
│      │  FOREIGN KEYS:                                                                                     │         │
│      │    • follower_id  → USERS.user_id                                                                  │         │
│      │    • following_id → USERS.user_id                                                                  │         │
│      │  UNIQUE: (follower_id, following_id)                                                               │         │
│      │  CHECK: follower_id != following_id (Cannot follow self)                                           │         │
│      ├────────────────────────────────────────────────────────────────────────────────────────────────────┤         │
│      │  + follow_id    : INT          (Auto-increment)                                                    │         │
│      │  + follower_id  : INT          (Not Null)                                                          │         │
│      │  + following_id : INT          (Not Null)                                                          │         │
│      │  + created_at   : TIMESTAMP    (Default: CURRENT_TIMESTAMP)                                        │         │
│      └────────────────────────────────────────────────────────────────────────────────────────────────────┘         │
│                                                                                                                     │
│      ┌────────────────────────────────────────────────────────────────────────────────────────────────────┐         │
│      │                                CONNECTIONS TABLE                                                   │         │
│      ├────────────────────────────────────────────────────────────────────────────────────────────────────┤         │
│      │  PRIMARY KEY: connection_id                                                                        │         │
│      │  FOREIGN KEYS:                                                                                     │         │
│      │    • user_id1     → USERS.user_id                                                                  │         │
│      │    • user_id2     → USERS.user_id                                                                  │         │
│      │    • requested_by → USERS.user_id                                                                  │         │
│      │  UNIQUE: (user_id1, user_id2)                                                                      │         │
│      │  CHECK: user_id1 < user_id2 (Prevents duplicate connections)                                       │         │
│      ├────────────────────────────────────────────────────────────────────────────────────────────────────┤         │
│      │  + connection_id : INT          (Auto-increment)                                                   │         │
│      │  + user_id1      : INT          (Not Null)                                                         │         │
│      │  + user_id2      : INT          (Not Null)                                                         │         │
│      │  + status        : ENUM         ('PENDING', 'ACCEPTED', 'REJECTED')                                │         │
│      │  + requested_by  : INT          (Not Null)                                                         │         │
│      │  + created_at    : TIMESTAMP    (Default: CURRENT_TIMESTAMP)                                       │         │
│      │  + updated_at    : TIMESTAMP    (Default: CURRENT_TIMESTAMP ON UPDATE)                             │         │
│      └────────────────────────────────────────────────────────────────────────────────────────────────────┘         │
│                                                                                                                     │
├─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                      NOTIFICATION SYSTEM                                                            │
├─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                                     │
│  ┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                      NOTIFICATIONS TABLE                                                       │ │
│  ├────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤ │
│  │  PRIMARY KEY: notification_id                                                                                  │ │
│  │  FOREIGN KEYS:                                                                                                 │ │
│  │    • user_id        → USERS.user_id                                                                            │ │
│  │    • source_user_id → USERS.user_id (Nullable)                                                                 │ │
│  │    • post_id        → POSTS.post_id (Nullable)                                                                 │ │
│  │    • comment_id     → COMMENTS.comment_id (Nullable)                                                           │ │
│  │    • connection_id  → CONNECTIONS.connection_id (Nullable)                                                     │ │
│  ├────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤ │
│  │  + notification_id : INT          (Auto-increment)                                                             │ │
│  │  + user_id         : INT          (Not Null)                                                                   │ │
│  │  + type            : ENUM         ('CONNECTION_REQUEST', 'CONNECTION_ACCEPTED', 'NEW_FOLLOWER',                │ │
│  │                                    'LIKE', 'COMMENT', 'SHARE', 'NEW_POST')                                     │ │
│  │  + source_user_id  : INT          (Nullable)                                                                   │ │
│  │  + post_id         : INT          (Nullable)                                                                   │ │
│  │  + comment_id      : INT          (Nullable)                                                                   │ │
│  │  + connection_id   : INT          (Nullable)                                                                   │ │
│  │  + is_read         : BOOLEAN      (Default: false)                                                             │ │
│  │  + created_at      : TIMESTAMP    (Default: CURRENT_TIMESTAMP)                                                 │ │
│  └────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```
## RELATIONSHIP DIAGRAM
```text
                       ┌─────────────────────────────────────────────────┐
                       │                    USERS                        │
                       │   (user_id, username, email, user_type, ...)    │
                       └──────────────┬─────────────────┬────────────────┘
                                      │                 │
                        ┌─────────────▼─────┐   ┌──────▼─────────────┐
                        │   BUSINESS_USERS  │   │   CREATOR_USERS    │
                        │   (Specialized)   │   │   (Specialized)    │
                        └───────────────────┘   └────────────────────┘

                                      │
                              ┌───────▼─────────────────────────────────────────┐
                              │                    POSTS                        │
                              │   (post_id, user_id, content, hashtags, ...)    │
                              └──────────────┬─────────────────┬────────────────┘
                            ┌────────────────┼─────────────────┼────────────────┐
                      ┌─────▼─────┐    ┌─────▼─────┐    ┌─────▼─────┐    ┌─────▼─────┐
                      │ COMMENTS  │    │   LIKES   │    │  FOLLOWS  │    │CONNECTIONS│
                      │ (1:M with │    │ (M:N with │    │ (M:N Self)│    │ (M:N Self)│
                      │   POSTS)  │    │   POSTS)  │    │           │    │           │
                      └───────────┘    └───────────┘    └───────────┘    └───────────┘
                            │                 │                 │                 │
                            └─────────────────┼─────────────────┼─────────────────┘
                                              │                 │
                                    ┌─────────▼─────────────────▼─────────┐
                                    │          NOTIFICATIONS              │
                                    │ (Receives from all above relations) │
                                    └─────────────────────────────────────┘
```

## CARDINALITY SUMMARY
1. USER → POSTS
   - One USER can create many POSTS (1:N)
   - Each POST belongs to exactly one USER

2. POST → COMMENTS
   - One POST can have many COMMENTS (1:N)
   - Each COMMENT belongs to exactly one POST

3. USER → COMMENTS
   - One USER can write many COMMENTS (1:N)
   - Each COMMENT is written by exactly one USER

4. USER ↔ POSTS (via LIKES)
   - One USER can like many POSTS (M:N)
   - One POST can be liked by many USERS (M:N)

5. USER ↔ USER (via FOLLOWS)
   - One USER can follow many USERS (M:N)
   - One USER can be followed by many USERS (M:N)

6. USER ↔ USER (via CONNECTIONS)
   - One USER can connect with many USERS (M:N)
   - Connection requires mutual acceptance

7. USER → NOTIFICATIONS
   - One USER can receive many NOTIFICATIONS (1:N)
   - Each NOTIFICATION is sent to exactly one USER

8. VARIOUS → NOTIFICATIONS
   - POSTS, COMMENTS, LIKES, FOLLOWS, CONNECTIONS can all generate NOTIFICATIONS