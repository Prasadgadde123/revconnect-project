
## REVCONNECT - Application Architecture Diagram
### Layered Architecture Overview
```text
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                                                                             │
│                                REVCONNECT APPLICATION ARCHITECTURE                          │
│                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                                                                                             │
│                                  PRESENTATION LAYER                                         │
│                                  (Console User Interface)                                   │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│    ┌─────────────────────────────────────────────────────────────────────────────────┐      │
│    │                                MAIN APPLICATION                                 │      │
│    │                                   (Main.java)                                   │      │
│    ├─────────────────────────────────────────────────────────────────────────────────┤      │
│    │                                                                                 │      │
│    │   ┌──────────────────────────────────────────────────────────────────────┐      │      │
│    │   │                        CONSOLE INTERFACE                             │      │      │
│    │   ├──────────────────────────────────────────────────────────────────────┤      │      │
│    │   │  • Welcome Banner Display                                            │      │      │
│    │   │  • Guest Menu (Login/Register)                                       │      │      │
│    │   │  • User Dashboard Menu                                               │      │      │
│    │   │  • Post Interaction Menu                                             │      │      │
│    │   │  • Profile Management Menu                                           │      │      │
│    │   │  • Input/Output Handling                                             │      │      │
│    │   └──────────────────────────────────────────────────────────────────────┘      │      │
│    │                                                                                 │      │
│    └─────────────────────────────────────────────────────────────────────────────────┘      │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│                                 SERVICE LAYER                                               │
│                                (Business Logic)                                             │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│   │ AUTHENTICATION│  │   USER       │  │    POST      │  │  CONNECTION │  │ NOTIFICATION │  │
│   │   SERVICE     │  │   SERVICE    │  │   SERVICE    │  │   SERVICE   │  │   SERVICE    │  │
│   ├──────────────┤  ├──────────────┤  ├──────────────┤  ├──────────────┤  ├──────────────┤  │
│   │• User Login  │  │• View Profile│  │• Create Post │  │• Send Request│  │• Send Notif  │  │
│   │• Registration│  │• Edit Profile│  │• View Feed   │  │• Accept/Reject│ │• View Notif  │  │
│   │• Password    │  │• Search Users│  │• Like/Unlike │  │• View Conns  │  │• Mark Read   │  │
│   │  Management  │  │              │  │• Comment     │  │              │  │              │  │
│   │• Logout      │  │              │  │• Edit/Delete │  │              │  │              │  │
│   │              │  │              │  │• Search Posts│  │              │  │              │  │
│   └───────┬──────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│           │                 │                  │                 │                 │        │
│           └─────────────────┼──────────────────┼─────────────────┼─────────────────┘        │
│                             │                  │                 │                          │
│   ┌──────────────┐  ┌───────┴───────┐  ┌──────┴──────┐  ┌───────┴───────┐                   │
│   │    FOLLOW    │  │  VALIDATION   │  │  CONSOLE    │  │   LOGGING     │                   │
│   │   SERVICE    │  │     UTIL      │  │   COLORS    │  │   UTILITY     │                   │
│   ├──────────────┤  ├───────────────┤  ├─────────────┤  ├───────────────┤                   │
│   │• Follow User │  │• Email Valid. │  │• ANSI Colors│  │• AppLogger    │                   │
│   │• Unfollow    │  │• Username Valid│  │• Text Format│  │• File Logging│                   │
│   │• View Follows│  │• Password Valid│  │• UI Styling │  │• Console Log │                   │
│   │• Get Stats   │  │• Hashtag Extract│  │• Banners    │  │• Error Logging│                 │
│   └──────────────┘  └───────────────┘  └─────────────┘  └───────────────┘                   │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│                                DATA ACCESS LAYER                                            │
│                                 (DAO Pattern)                                               │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│   │    USER      │  │    POST      │  │   COMMENT    │  │     LIKE     │  │   FOLLOW     │  │
│   │     DAO      │  │     DAO      │  │     DAO      │  │     DAO      │  │     DAO      │  │
│   ├──────────────┤  ├──────────────┤  ├──────────────┤  ├──────────────┤  ├──────────────┤  │
│   │• Create User │  │• Create Post │  │• Create Comm │  │• Add Like    │  │• Follow User │  │
│   │• Get User    │  │• Get Post    │  │• Get Comments│  │• Remove Like │  │• Unfollow    │  │
│   │• Update User │  │• Update Post │  │• Delete Comm │  │• Check Like  │  │• Check Follow│  │
│   │• Search Users│  │• Delete Post │  │• Update Comm │  │• Get Count   │  │• Get Counts  │  │
│   │• Change Pass │  │• Search Posts│  │              │  │              │  │• Get Lists   │  │
│   │              │  │• Get Feed    │  │              │  │              │  │              │  │
│   └───────┬──────┘  └───────┬──────┘  └────────┬─────┘  └────────┬─────┘  └────────┬─────┘  │
│           │                 │                  │                 │                 │        │
│           └─────────────────┼──────────────────┼─────────────────┼─────────────────┘        │
│                             │                  │                 │                          │
│   ┌───────────────┐  ┌──────┴────────┐  ┌──────┴──────┐                                     │
│   │  CONNECTION   │  │ NOTIFICATION  │  │   BASE      │                                     │
│   │     DAO       │  │      DAO      │  │     DAO     │                                     │
│   ├───────────────┤  ├───────────────┤  ├─────────────┤                                     │
│   │• Send Request │  │• Create Notif │  │• Base Class │                                     │
│   │• Accept/Reject│  │• Get Notifs   │  │• Connection │                                     │
│   │• Get Conns    │  │• Mark Read    │  │  Management │                                     │
│   │• Check Status │  │• Delete Notif │  │• Resource   │                                     │
│   │               │  │• Get Count    │  │  Cleanup    │                                     │
│   └───────────────┘  └───────────────┘  └─────────────┘                                     │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│                              DATABASE CONNECTION LAYER                                      │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│    ┌─────────────────────────────────────────────────────────────────────────────────┐      │
│    │                          DATABASE CONNECTION                                    │      │
│    │                              Utility Class                                      │      │
│    ├─────────────────────────────────────────────────────────────────────────────────┤      │
│    │                                                                                 │      │
│    │   ┌───────────────────────────────────────────────────────────────────────┐     │      │
│    │   │                     DATABASE CONFIGURATION                            │     │      │
│    │   ├───────────────────────────────────────────────────────────────────────┤     │      │
│    │   │  • Connection Pooling                                                 │     │      │
│    │   │  • Connection Management                                              │     │      │
│    │   │  • Connection Reuse                                                   │     │      │
│    │   │  • Resource Cleanup                                                   │     │      │
│    │   │  • Error Handling                                                     │     │      │
│    │   └───────────────────────────────────────────────────────────────────────┘     │      │
│    │                                                                                 │      │
│    └─────────────────────────────────────────────────────────────────────────────────┘      │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│                                   DATABASE LAYER                                            │
│                                                                                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│    ┌─────────────────────────────────────────────────────────────────────────────────┐      │
│    │                             MySQL DATABASE                                      │      │
│    │                             (revconnect)                                        │      │
│    ├─────────────────────────────────────────────────────────────────────────────────┤      │
│    │                                                                                 │      │
│    │   ┌───────────────────────────────────────────────────────────────────────┐     │      │
│    │   │                            TABLES                                     │     │      │
│    │   ├───────────────────────────────────────────────────────────────────────┤     │      │
│    │   │  • users                • posts              • comments               │     │      │
│    │   │  • business_users       • likes              • follows                │     │      │
│    │   │  • creator_users        • connections        • notifications          │     │      │
│    │   └───────────────────────────────────────────────────────────────────────┘     │      │
│    │                                                                                 │      │
│    └─────────────────────────────────────────────────────────────────────────────────┘      │
│                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

```text
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    MAIN APP     │     │    SERVICES     │     │      DAOs       │     │   DATABASE      │
│   (Main.java)   │     │  (Business      │     │  (Data Access)  │     │    MySQL        │
│                 │     │    Logic)       │     │                 │     │                 │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ 1. Display Menu │     │                 │     │                 │     │                 │
│ 2. Get User     │────▶│                 │     │                 │     │                 │
│    Input        │     │                 │     │                 │     │                 │
│                 │     │                 │     │                 │     │                 │
│ 3. Process      │     │ 4. Validate     │     │                 │     │                 │
│    Choice       │────▶│    Input        │     │                 │     │                 │
│                 │     │                 │     │                 │     │                 │
│ 8. Display      │◀────│ 7. Return       │◀────│ 6. Return       │◀────│ 5. Execute      │
│    Results      │     │    Results      │     │    Data         │     │    Query        │
│                 │     │                 │     │                 │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘     └─────────────────┘
```
## Component Interaction Diagram
### Example: User Login Flow

- Main → Displays login screen, gets username/password
- Main → Calls AuthenticationService.login()
- AuthService → Validates input using ValidationUtil
- AuthService → Calls UserDAO.getUserByUsernameOrEmail()
- UserDAO → Executes SQL query via DatabaseConnection
- UserDAO → Returns User object to AuthService
- AuthService → Validates password, returns success/failure
- Main → Displays result to user

## Package Structure Architecture
```text
com.revconnect/
├── app/
│   └── Main.java                          # Application entry point
│
├── service/                               # Business Logic Layer
│   ├── AuthenticationService.java         # User auth & registration
│   ├── UserService.java                   # User profile management
│   ├── PostService.java                   # Post creation & interaction
│   ├── ConnectionService.java             # Connection management
│   ├── FollowService.java                 # Follow system
│   └── NotificationService.java           # Notification handling
│
├── dao/                                   # Data Access Layer
│   ├── BaseDAO.java                       # Base DAO with common methods
│   ├── UserDAO.java                       # User data operations
│   ├── PostDAO.java                       # Post data operations
│   ├── CommentDAO.java                    # Comment data operations
│   ├── LikeDAO.java                       # Like data operations
│   ├── FollowDAO.java                     # Follow data operations
│   ├── ConnectionDAO.java                 # Connection data operations
│   └── NotificationDAO.java               # Notification data operations
│
├── model/                                 # Domain Model Layer
│   ├── User.java                          # Base user entity
│   ├── PersonalUser.java                  # Personal user type
│   ├── BusinessUser.java                  # Business user type
│   ├── CreatorUser.java                   # Creator user type
│   ├── Post.java                          # Post entity
│   ├── Comment.java                       # Comment entity
│   ├── Like.java                          # Like entity
│   ├── Follow.java                        # Follow relationship
│   ├── Connection.java                    # Connection relationship
│   ├── Notification.java                  # Notification entity
│   └── Profile.java                       # User profile
│
├── util/                                  # Utility Layer
│   ├── DatabaseConnection.java            # DB connection management
│   ├── AppLogger.java                     # Logging utility
│   ├── ConsoleColors.java                 # Console UI styling
│   └── ValidationUtil.java                # Input validation
│
└── test/                                  # Testing Layer
├── DatabaseConnectionTest.java        # DB connection tests
├── AuthenticationServiceTest.java     # Auth service tests
├── UserServiceTest.java               # User service tests
├── PostServiceTest.java               # Post service tests
├── BaseDAOTest.java                   # Base DAO tests
├── log4jTest.java                     # Logging tests
├── TestConfig.java                    # Test configuration
├── TestRunner.java                    # Test runner
└── ... (other test files)
```

## Data Flow Architecture
```text
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                              DATA FLOW ARCHITECTURE                                          │
└─────────────────────────────────────────────────────────────────────────────────────────────┘

                                    ┌─────────────────┐
                                    │   USER INPUT    │
                                    │  (Console/CLI)  │
                                    └────────┬────────┘
                                             │
                                    ┌────────▼────────┐
                                    │  INPUT PARSING  │
                                    │  & VALIDATION   │
                                    └────────┬────────┘
                                             │
                               ┌─────────────▼─────────────┐
                               │   BUSINESS LOGIC LAYER    │
                               │  (Service Classes)        │
                               │                           │
                               │  • Authentication Rules   │
                               │  • Post Creation Rules    │
                               │  • Follow/Connection Rules│
                               │  • Notification Triggers  │
                               └─────────────┬─────────────┘
                                             │
                               ┌─────────────▼─────────────┐
                               │   DATA ACCESS LAYER       │
                               │  (DAO Classes)            │
                               │                           │
                               │  • SQL Query Construction │
                               │  • Result Mapping         │
                               │  • Transaction Management │
                               └─────────────┬─────────────┘
                                             │
                                    ┌────────▼────────┐
                                    │  DB CONNECTION  │
                                    │     POOL        │
                                    └────────┬────────┘
                                             │
                                    ┌────────▼────────┐
                                    │   MYSQL DB      │
                                    │   (revconnect)  │
                                    └─────────────────┘
                                             │
                                    ┌────────▼────────┐
                                    │   RESPONSE      │
                                    │   PROCESSING    │
                                    └────────┬────────┘
                                             │
                               ┌─────────────▼─────────────┐
                               │   OUTPUT RENDERING        │
                               │  (Console Formatting)     │
                               │                           │
                               │  • Color Coding           │
                               │  • Table Formatting       │
                               │  • Error Messages         │
                               └─────────────┬─────────────┘
                                             │
                                    ┌────────▼────────┐
                                    │   USER OUTPUT   │
                                    │  (Console)      │
                                    └─────────────────┘
```

## Deployment Architecture
```text
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                          DEPLOYMENT ARCHITECTURE                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘

                    ┌─────────────────────────────────────────────────┐
                    │            CLIENT/DEVICE LAYER                  │
                    │                                                 │
                    │  • Console/Command Line Interface               │
                    │  • Java Runtime Environment (JRE 8+)            │
                    │  • Application JAR File                         │
                    └─────────────────────────────────────────────────┘
                                            │
                                            │ (Network/SSH)
                                            ▼
                    ┌─────────────────────────────────────────────────┐
                    │          APPLICATION SERVER LAYER               │
                    │                                                 │
                    │  • Java Application (RevConnect.jar)            │
                    │  • Service Layer Components                     │
                    │  • DAO Layer Components                         │
                    │  • Utility Classes                              │
                    │  • Configuration Files                          │
                    └──────────────┬──────────────────────────────────┘
                                   │ (JDBC Connection)
                                   ▼
                    ┌─────────────────────────────────────────────────┐
                    │            DATABASE SERVER LAYER                │
                    │                                                 │
                    │  • MySQL Database Server                        │
                    │  • revconnect Database                          │
                    │  • Tables: users, posts, comments, etc.         │
                    │  • Stored Procedures (if any)                   │
                    │  • Backup & Recovery System                     │
                    └─────────────────────────────────────────────────┘
                                   │
                    ┌──────────────┼──────────────────────────────────┐
                    │       SUPPORTING INFRASTRUCTURE                 │
                    │                                                 │
                    │  • Log Files (app.log, error.log)               │
                    │  • Configuration Files                          │
                    │  • Backup Systems                               │
                    │  • Monitoring Tools                             │
                    └─────────────────────────────────────────────────┘
```