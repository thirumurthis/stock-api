### For development and deploying to heroku

- Clone the project to local
- Create a branch, make the enhancement changes
- Push the changes to branch
```
$ git checkout -b <branch-name>
$ git push -u origin <branch-name>
```

- In the github, submit the pull request from the web ui.

- Create an user account in heroku
- Download heroku cli and install.

- Create App using the below command

```
$ heroku create
## The command will create a random, app name. 
```

- The above will update the local git repo, use below command to view the repos
```
$ git remote -v 

## This lists the fetch and push url, and heroku and origin listed
```

- In order to deploy the code to heroku, push the code

```
$ git push heroku main

### in the above case, main is the (master branch) github the new repository project
### created main branch (old master)
```

- When the above command prompts for username and password for 
- https://git.heroku.com, pass the API key
- To get the API key use below command (refer link - https://devcenter.heroku.com/articles/authentication#retrieving-the-api-token)
  - When the user name is not required.
```
## The https://git.heroku.com/, can be accessed only using API 
$ heroku auth:token

### Generates the API token
```

- Development notes
  - Add the `Procfile` with below content
  ```
  web: java -jar target/finance-1.0.0.jar
  ```
  
   - Add the `system.properties` with the java version
  ```
  java.runtime.version=11
  ```
  
  - If the maven build command reports issue during `git push heroku main`, comment java version in the pom.xml
  - In order to expose the port, in application.properties use `server.port=${PORT:8080}` configuration
   