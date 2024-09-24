FROM openjdk:17
ADD target/ExpenseTracker-0.0.1-SNAPSHOT.jar ExpenseTracker.jar
ENTRYPOINT [ "java", "-jar", "ExpenseTracker.jar" ]
