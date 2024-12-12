
### API SPEC
https://documenter.getpostman.com/view/16768841/2s9YXe6NtU

### HOW TO RUN THIS PROGRAM

1. setup database
    - docker start <mysql-container>
    - docker exec -it <mysql-container-name> bash
    - mysql -u root -p
2. run spring application
    - mvn spring-boot:run
3. run ngrok
    - ngrok start <ngrok-tunnel-name>
4. copy ngrok link to react application (.env1)
5. push commit react application
6. wait for changes from react application [React-app](https://my-lppaik.netlify.app/)
