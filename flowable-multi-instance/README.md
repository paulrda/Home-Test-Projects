## Unit Tests for Flowable multi-instance issue.
I have created a user task and want to enable multi-instance feature for it. a List collection variable is created and save it as process instance variable. This instance variable is passed to multi-instance collection element and gave a name for a element variable. After running the process i should expect values of list in each form but i only get last index value of list in every form.

To run this unit tests first need run this flowable embedded spring boot application.

```bash
mvn spring-boot:run
```

This user task is assigned to a group call "staff". To access forms,  create the group in flowable idm and add a user. Login to flowable-task ui from that user login.