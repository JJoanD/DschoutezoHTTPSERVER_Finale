package httpserver;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        HttpServer httpServer = new HttpServer();
        
        httpServer.start();
    }
}
