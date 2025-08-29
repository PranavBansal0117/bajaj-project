import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestApi {
    private static final String BASE_URL = "http://localhost:8080";
    
    public static void main(String[] args) {
        System.out.println("Testing VIT BFHL API...\n");
        
        testExampleA();
        testExampleB();
        testExampleC();
    }
    
    private static void testExampleA() {
        String payload = "{\"data\":[\"a\",\"1\",\"334\",\"4\",\"R\",\"$\"]}";
        System.out.println("=== Example A Test ===");
        System.out.println("Input: " + payload);
        makeRequest(payload);
        System.out.println("Expected concat_string: Ra\n");
    }
    
    private static void testExampleB() {
        String payload = "{\"data\":[\"2\",\"a\",\"y\",\"4\",\"&\",\"-\",\"*\",\"5\",\"92\",\"b\"]}";
        System.out.println("=== Example B Test ===");
        System.out.println("Input: " + payload);
        makeRequest(payload);
        System.out.println("Expected concat_string: ByA\n");
    }
    
    private static void testExampleC() {
        String payload = "{\"data\":[\"A\",\"ABcD\",\"DOE\"]}";
        System.out.println("=== Example C Test ===");
        System.out.println("Input: " + payload);
        makeRequest(payload);
        System.out.println("Expected concat_string: EoDdCbAa\n");
    }
    
    private static void makeRequest(String payload) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bfhl"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
                
            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
                
            System.out.println("Response: " + response.body());
            System.out.println("Status Code: " + response.statusCode());
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}