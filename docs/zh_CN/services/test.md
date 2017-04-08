---
root: false
title: test
sort: 9
---

```java
public class Application {

	public static void main(String[] args) {
	    $().get("/", (request, response) -> {
	        response.html("<h1>Hello blade!</h1>");
	    }).start();
	}

}
```


```java
@Route(value = "users", method = HttpMethod.GET)
@JSON
public List<User> users(){
	List<User> users = new ArrayList<>();
	users.add(new User("jack", 20));
	users.add(new User("rose", 18));
    return users;
}
```

```java
public static void main(String[] args) {

    $().before("/.*", (request, response) -> {
        System.out.println("before...");
    }).start();

}
```

```java
public static void main(String[] args) {
    $().get("user/:uid", (request, response) -> {
        Integer uid = request.paramAsInt("uid");
        response.text("uid : " + uid);
    }).start();
    
}
```