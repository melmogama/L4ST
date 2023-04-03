# L4ST
## Creating A Connection To A Table
L4ST is a persistence library that uses the names of your source files and variables to connect to your database. It 
uses reflection to match file and variables names with that of the database (case senstitive).

#### Database Table Connection Example
---
>
```
public class user extends L4STImpl {
  public user(Connection connection, boolean allowNullValues) throws Exception{
    super(connection, allowNullValues);
  }
}
```
> `allowNullValues` tells the library if it is allowed to *__update__* any and all table fields to **null**
(Assuming entity value is set to null)
>
Just like that, you have established a connection to your database table named **_user_**, 
provided you give a `java.sql.Connection` to your database. 

<br>

## Creating An Entity To Map To Table
Assuming **_user_** has columns `| Name | Age | Contact |`, you 
can create an entity object to easily map your values to the table. 

#### Entity Object Example
```
public class UserEntity extends L4STEntity {
  public String Name;
  public int Age;
  public String Contact;
}
```
<br>

## Creating A Record In **user**
```
public static void main(String[] args) {
  user userTable = new user(connection);
  UserEntity user = new UserEntity();
  
  user.setName("Jake");
  user.setAge(23);
  user.setContact("jake@email.com");
  
  userTable.create(user);
}
```
[![](https://jitpack.io/v/melmogama/L4ST.svg)](https://jitpack.io/#melmogama/L4ST)
