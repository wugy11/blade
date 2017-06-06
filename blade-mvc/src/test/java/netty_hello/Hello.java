package netty_hello;

import com.blade.Blade;

/**
 * @author biezhi
 *         2017/6/5
 */
public class Hello {

    public static void main(String[] args) {
        Blade.me()
                .devMode(false)
                .get("/rest/hello", ((request, response) -> response.text("Hello World.")))
                .listen(8080).start();
    }
}
