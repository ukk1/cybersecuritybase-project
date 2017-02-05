# Cyber Security Base - Course Project I

Credentials:

seppo:password
pentti:123456
niilo:87654321

### A1-Injection

There is an SQL injection vulnerability within the application login form. It is possible to use trivial injection attacks to bypass the login.

####Steps to reproduce:

1. Open browser and go to http://localhost:8080
2. Both username and password form fields are vulnerable to SQLi
3. For the username parameter set the following value: admin' OR 1=1-- (remember, there is a space after the --)
4. You can leave the password field empty
5. Click "Submit"
6. You are redirected to the super secret page and have successfully exploited the SQL injection vulnerability in the application

####How to fix the issue?

The code uses raw SQL queries to check if the user exists in the database:

    ResultSet resultSet = connection.createStatement().executeQuery("SELECT id, username, password FROM accounts WHERE username='" +username+ "' AND password='" +password+ "'");

Instead of raw SQL queries, it is recommended to use prepared statements. 

Change the code in the DatabaseQueries class getAccount method as follows:

    public Signup getAccount(String username, String password) throws SQLException {
        String query = "SELECT id, username, password FROM accounts WHERE username= ? AND password= ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        ResultSet resultSet = pstmt.executeQuery();
        while (resultSet.next()) {
            return new Signup(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("password"));
    }

When the application is restarted the SQL injection attacks will not work anymore.

More information about Prepared Statements etc. can be found from OWASP:

https://www.owasp.org/index.php/SQL_Injection_Prevention_Cheat_Sheet

### A5-Security Misconfiguration

The Application uses an outdated version of the Spring Framework (1.4.2). Latest version is 1.4.3.

####Steps to reproduce:

1. Open the pom.xml file in NetBeans
2. From the list we can see the org.springframework.boot version number is 1.4.2

        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>1.4.2.RELEASE</version>
        </parent>

####How to fix the issue?

We can fix the issue by modifying the version to 1.4.3.RELEASE and then re-building the application. 

        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>1.4.3.RELEASE</version>
        </parent>

After that it will use the latest version.

### A6-Sensitive Data Exposure

The application stores password in cleartext in the database. Also the application does not currently support HTTPS, which allows anyone to eavesdrop user credentials if located in the same network.

####Steps to reproduce:

1. Open browser and go to http://localhost:8080
2. Login to the application with existing credentials
3. Pay attention to the URL - all traffic goes through unencrypted HTTP protocol and does not provide sufficient protection against attacks where attackers may be eavesdropping the communications.
4. We can also look into the data.sql file in the project
5. All the user credentials are stored in cleartext

        INSERT INTO accounts (id, username, password) VALUES ('1', 'seppo', 'password');
        INSERT INTO accounts (id, username, password) VALUES ('2', 'pentti', '123456');
        INSERT INTO accounts (id, username, password) VALUES ('3', 'niilo', '87654321');

####How to fix the issue?

All sensitive data, such as CC information or passwords should be stored in encrypted form. For passwords it is recommended to use strong hashing algorithms with unique salts. So, even in a case where the password data gets leaked, if strong and secure hashing algorithms has been used it is quite impossible for the attacker to retrieve the cleartext passwords

For example, OWASP recommends using PBDKF2, scrypt or bcrypt functions for encrypting passwords.

OWASP has offered the following pseudo code for implementing password hashing:

    return [salt] + pbkdf2([salt], [credential], c=10000);
    
Also, the SecurityConfiguration class has some examples how to implement password encryption with BCrypt:

     public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

### A7-Missing Function Level Access Control

The web application does not properly enforce access control to sensitive pages. For example, anyone can access the h2-console, which can be used to gain unauthorized access into the backend database.

####Steps to reproduce:

1. Open browser and go to http://localhost:8080/h2-console

####How to fix the issue?

It is recommended to prevent access to sensitive resources from the public Interent. This specific vulnerability can be mitigated by restricting access based on IP addresses.

We can modify the SecurityConfiguration.java class by implementing IP-restriction to the /h2-console resource as follows:

        http.authorizeRequests()
                .antMatchers("/h2-console/*").hasIpAddress("10.10.10.10") //access is only allowed from IP-address 10.10.10.10
                .anyRequest().permitAll();
    }

### A8-Cross-Site Request Forgery (CSRF)

The web application currently does not have any anti-CSRF protection mechanisms. This means that users can be tricked to send requests on the attackers behalf if they are tricked into visiting malicious sites that contains attack code that uses the victims credentials or session data to send requests to the targeted application.

Currently the web application does not have any functionalities that would create a security risk.

####How to fix the issue?

CSRF protection can be enabled for the application by removing the http.csrf().disable(); from the SecurityConfiguration class:

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); //Disable CSRF protection
        http.authorizeRequests()
                .antMatchers("/h2-console/*").hasIpAddress("10.10.10.10")
                .anyRequest().permitAll();
    }
    
When it is removed, Spring Framework will automatically add randomized anti-CSRF tokens into every HTTP request.

    POST /form HTTP/1.1
    Host: localhost:8080
    User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:51.0) Gecko/20100101 Firefox/51.0
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
    Accept-Language: en-US,en;q=0.5
    Referer: http://localhost:8080/form
    Cookie: SESSIONID=A2B55A949C1F96376A2C544A0268C189
    Connection: close
    Upgrade-Insecure-Requests: 1
    Content-Type: application/x-www-form-urlencoded
    Content-Length: 69

    username=seppo&password=password&_csrf=45b2790b-ac9f-44d9-bd8c-53da1a3a50a3

### A10-Unvalidated Redirects and Forwards

The application has an open redirect vulnerability, which allows users to manipulate a link to point to other locations. This could be used for example to trick other users visit malicious sites by sending the URL with a malicious link to the victims.

Steps to reproduce:

1. Open browser and go to http://localhost:8080
2. Use the SQLi vulnerability or login with valid credentials.
3. The supersecretpage page has a link that when clicked will take the user back to http://localhost:8080/form
4. The url parameter within the link can be manipulated so that it will redirect the user to any site.
5. Click the link and intercept the request with OWASP ZAP or Burp Suite.
6. Modify the "url" parameter as follows: GET /supersecretpage?url=http%3a%2f%2fgoogle.com
7. Send the request and you will be redirected to Google

####How to fix the issue?

As said, the code takes any user supplied input as a value for the redirection.

    @RequestMapping(value = "/supersecretpage", method = RequestMethod.GET)
    public String redirectURL(@RequestParam String url) throws SQLException {
        return "redirect:" + url;
    }

There are few ways to mitigate this issue:
1. Do not use redirects and forwards
2. Do not allow user supplied input as destination

The vulnerability can be fixed as follows:

    @RequestMapping(value = "/supersecretpage", method = RequestMethod.GET)
    public String redirectURL(@RequestParam String url) throws SQLException {
        return "redirect:/form";
    }
