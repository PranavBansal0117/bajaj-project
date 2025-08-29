import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class BfhlApiApplication {
    
    private static final String USER_ID = "pranav_bansal_17012004";
    private static final String EMAIL = "pranav.22bce9204@vitapstudent.ac.in"; 
    private static final String ROLL_NUMBER = "22BCE9204";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/bfhl", new BfhlHandler());
        server.createContext("/", new HealthHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
        System.out.println("BFHL endpoint: http://localhost:8080/bfhl");
    }

    static class BfhlHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                handlePost(exchange);
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            try {
                String requestBody = readRequestBody(exchange);
                String jsonData = extractDataArray(requestBody);
                
                List<String> data = parseJsonArray(jsonData);
                String response = processData(data);
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                
            } catch (Exception e) {
                String errorResponse = "{\"is_success\":false,\"error\":\"" + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, errorResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(errorResponse.getBytes());
                os.close();
            }
        }

        private String readRequestBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }

        private String extractDataArray(String json) {
            int start = json.indexOf("[");
            int end = json.lastIndexOf("]");
            return json.substring(start + 1, end);
        }

        private List<String> parseJsonArray(String arrayContent) {
            List<String> result = new ArrayList<>();
            String[] items = arrayContent.split(",");
            
            for (String item : items) {
                String cleaned = item.trim().replaceAll("\"", "");
                if (!cleaned.isEmpty()) {
                    result.add(cleaned);
                }
            }
            return result;
        }

        private String processData(List<String> data) {
            List<String> oddNumbers = new ArrayList<>();
            List<String> evenNumbers = new ArrayList<>();
            List<String> alphabets = new ArrayList<>();
            List<String> specialCharacters = new ArrayList<>();
            List<String> alphabetChars = new ArrayList<>();
            
            int sum = 0;

            for (String item : data) {
                if (isNumber(item)) {
                    int num = Integer.parseInt(item);
                    sum += num;
                    
                    if (num % 2 == 0) {
                        evenNumbers.add(item);
                    } else {
                        oddNumbers.add(item);
                    }
                } else if (isAlphabet(item)) {
                    String upperItem = item.toUpperCase();
                    alphabets.add(upperItem);
                    
                    for (char c : item.toCharArray()) {
                        if (Character.isLetter(c)) {
                            alphabetChars.add(String.valueOf(c));
                        }
                    }
                } else {
                    specialCharacters.add(item);
                }
            }

            String concatString = buildConcatString(alphabetChars);

            return buildJsonResponse(true, oddNumbers, evenNumbers, alphabets, 
                                   specialCharacters, String.valueOf(sum), concatString);
        }

        private boolean isNumber(String str) {
            try {
                Integer.parseInt(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private boolean isAlphabet(String str) {
            if (str == null || str.isEmpty()) return false;
            for (char c : str.toCharArray()) {
                if (!Character.isLetter(c)) return false;
            }
            return true;
        }

        private String buildConcatString(List<String> alphabetChars) {
            if (alphabetChars.isEmpty()) {
                return "";
            }

            Collections.reverse(alphabetChars);
            
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < alphabetChars.size(); i++) {
                String ch = alphabetChars.get(i);
                if (i % 2 == 0) {
                    result.append(ch.toUpperCase());
                } else {
                    result.append(ch.toLowerCase());
                }
            }
            
            return result.toString();
        }

        private String buildJsonResponse(boolean isSuccess, List<String> oddNumbers, 
                                       List<String> evenNumbers, List<String> alphabets,
                                       List<String> specialCharacters, String sum, String concatString) {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"is_success\":").append(isSuccess).append(",");
            json.append("\"user_id\":\"").append(USER_ID).append("\",");
            json.append("\"email\":\"").append(EMAIL).append("\",");
            json.append("\"roll_number\":\"").append(ROLL_NUMBER).append("\",");
            json.append("\"odd_numbers\":").append(listToJson(oddNumbers)).append(",");
            json.append("\"even_numbers\":").append(listToJson(evenNumbers)).append(",");
            json.append("\"alphabets\":").append(listToJson(alphabets)).append(",");
            json.append("\"special_characters\":").append(listToJson(specialCharacters)).append(",");
            json.append("\"sum\":\"").append(sum).append("\",");
            json.append("\"concat_string\":\"").append(concatString).append("\"");
            json.append("}");
            return json.toString();
        }

        private String listToJson(List<String> list) {
            if (list.isEmpty()) {
                return "[]";
            }
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                json.append("\"").append(list.get(i)).append("\"");
                if (i < list.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            return json.toString();
        }
    }

    static class HealthHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"message\":\"VIT BFHL API is running\",\"status\":\"active\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}