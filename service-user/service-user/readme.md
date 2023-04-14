# User service
## Synopsis
A microservice that stores users, each user linked via a global domain identifier to a domain. Here you will find backoffice users and players alike, the only difference being the domain that they are linked to. Players interact with this service to register accounts, update profile details, reset passwords, set user preferences etc.

### Installation
TODO

### Configuration
TODO

 Additional domain settings
 
 ##### allow-duplicate-cellnumber
 Set it "true" to allow registering new user without checking cell phone number for duplicates.  If it not set default value will be "false". 
 
 ##### allow-duplicate-email
 Set it "true" to allow registering new user without checking email for duplicates.  If it not set default value will be "false". 

### API Reference
TODO

### Propagate User field to another service
 As we are working with DB only by using Hibernate there is a way how to be notified of all updates/inserts from a single place. The idea is to intercept all User entity updates/inserts, check if any of the fields other services are interested in were changed and propagate changes to the other services through the message broker, see implementation details in UserUpdatesPropagator and NewUsersPropagator classes.
 ##### Steps to propagate:
1. Add the necessary field to UserAttributesData
2. Add User field name to propagate to UserUpdatesPropagator.PROPAGATED_FIELDS
3. Each saves command to user-services repositories will handle via NewUsersPropagator/UserUpdatesPropagator
4. If the changed entity is User and the changed field name contains in UserUpdatesPropagator.PROPAGATED_FIELDS - at UserAttributesChangesPropagator created a new UserAttributesData object and send him to UserAttributesTriggerStream.
5. Make sure all tests at UserUpdatesPropagatorTest are working successfully
  

