# E-shop example project

This project is an example created using various java tools and libraries.
This project is incomplete but working.

## Description

Multi-module approach used to separate web layer from database layer.

Maven dependency management tool used to manage this multi-module project.

Lombok used to reduce boilerplate code making it cleaner.

PostgreSQL JDBC driver with Hikari connection pooling used for database $ Hibernate for object-relational mapping.

Spring Security used to protect web page URL's by roles & authorities. 
Thymeleaf Spring Security Extras used to show information and buttons based on user authentication status (ex. in navigation) . Cart checkout button available only for fully authenticated(remember-me token doesn't count) user.

Persistent remember-me token used for better protection. It expires and is created new every time user logs in from certain device.

Thymeleaf used as templating engine for its flexibility. Header, navigation and footer split into fragments. There can even be used thymeleaf's decoupled template logic to separate HTML templates from thymeleaf attributes.

Hibernate validator used to validate entities when new user registers. There is even custom UniqueEmail validator created that checks if email doesn't exist in database already.

Bootstrap js, bootstrap css, jQuery webjars used for adding & managing client side dependencies.

Spring JPA used in database module for easier work with data in DB. No-code repositories, reduced boilerplate code, auto-generated queries. PagingAndSorting repository used for items to get elements by page(used in main application view).

For items Rest GET endpoint created. JQuery with AJAX used in main page to call application REST endpoint and get items list by page. Then popolate view elements. To use REST and to convert POJO's to JSON and vice versa, Jackson Databind dependency(libraries) is used.

Pure JS DOM used in main page to provide full functionality for pagination buttons. Works from 1 to n pages. If there is not a full page of elements - not used HTML elements is hidden to prevent product info from last page.

Moneta(JSR 354 reference implementation) used for Item entity price field & 'numeric' type in postgreSQL.

Java Mail is used when sending emails for password reset & new registration confirmation. Tested with "MailDev" locally.

Internationalization(I18N) configured and used in main page.

![UML DB tables diagram](https://raw.githubusercontent.com/MindaugasJasiunas/multiModuleWebApplication/MindaugasJ/presentation%20images/tables_UML.png)

## Not implemented yet

- item sorting by category, size, gender
- item images
- cart functionality
- discount functionality
- checkout functionality
- admin functionality (view, disable users)
- employee functionality (add new items, edit existing)
- Unit + Integration tests 
