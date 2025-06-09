package org.example.entity.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String rawPromt = """
            --- PRIMARY GOAL START ---
            Your SOLE task is to analyze the USER INPUT and the 'number_of_test_cases_requested' parameter.
            If the USER INPUT is clear enough to generate test cases, generate a VALID JSON ARRAY containing the requested number of detailed test cases. Each test case object in the array MUST be valid.
            If the USER INPUT is ambiguous, unclear, or insufficient to create meaningful test cases, generate a SINGLE, VALID JSON object containing ONLY an "error_message" field explaining the issue in English and an "ai_confidence" of 0 (this will be a single object, not an array, even if multiple test cases were requested).
            Output NOTHING ELSE. No explanations, no introductory text, no comments, just the JSON (either an array of test cases or a single error object).
            All string values inside the JSON (including error messages) MUST be in ENGLISH.
            Strictly follow the JSON structure defined in the INSTRUCTIONS section.
            --- PRIMARY GOAL END ---
                        
            --- USER INPUT START ---
            {здесь_будет_текст_введенный_пользователем}
            --- USER INPUT END ---
                        
            --- SYSTEM PARAMETERS START ---
            number_of_test_cases_requested: {здесь_будет_запрошенное_количество_тестов}
            --- SYSTEM PARAMETERS END ---
            
            --- AI STRATEGY SETTING START ---
            ai_strategy: {здесь_будет_ии_стратегия}
            --- AI STRATEGY SETTING END ---
                        
            --- INSTRUCTIONS START ---
            Based *only* on the text provided in the USER INPUT section and the 'number_of_test_cases_requested' from SYSTEM PARAMETERS:
            1.  If the input is sufficient and clear, generate a JSON array containing 'number_of_test_cases_requested' distinct, detailed test cases. Each test case should be a unique scenario if possible, or variations if a single core idea is being tested.
            2.  If the input is ambiguous, too vague, or lacks necessary information to form even one meaningful test case, generate a single JSON object with only "error_message" (explaining the problem in English) and "ai_confidence" (set to 0).
                        
            **Your entire output MUST be a single, valid JSON structure (either an array of test case objects or a single error object).** Do not include any text or explanations before or after the JSON. All string values within the JSON must be in **English**.
                        
            **JSON Structure for each Test Case Object (within the array for successful generation):**
            ```json
            {
              "id": "TC-XXX", // Auto-generated unique ID, e.g., TC-001, TC-002
              "type": "Functional/Positive/Negative/Boundary/Security/etc.", // Test type in English
              "component": "Relevant System Component/Module", // e.g., Login, Cart, API
              "priority": "High/Medium/Low", // Test priority in English
              "status": "New", // Default status, always "New"
              "name": "Short, descriptive English title",
              "description": "Brief English explanation of the test case objective",
              "preconditions": [
                "English precondition 1"
              ],
              "test_steps": [
                "English step 1"
              ],
              "input_data": [
                { "parameter": "Parameter Name 1", "value": "Value 1" }
              ],
              "expected_outcome": "Clear English statement of the expected result",
              "notes_ai_analysis": "Optional English notes. For boundary tests, explain chosen boundaries.",
              "ai_confidence": 0-100,
              "related_requirements": [
                "REQ-ID-1"
              ]
            }
            JSON Structure (for an unclear/insufficient input - single object, not an array):
            {
              "error_message": "Specific error message in English.",
              "ai_confidence": 0
            }
            Field Explanations for Test Case Object:
            id: (String) Auto-generated unique identifier for the test case. Start with "TC-" followed by a number (e.g., TC-001, TC-002). Increment for each test case in the array.
            type: (String) Type of test (e.g., "Functional", "Positive", "Negative", "Boundary", "Security", "Usability").
            component: (String) The system component or module being tested (e.g., "Authentication", "Shopping Cart", "User Profile API").
            priority: (String) Priority of the test case (e.g., "High", "Medium", "Low").
            status: (String) Initial status of the test case. Always set this to "New".
            name, description, preconditions, test_steps, input_data, expected_outcome, notes_ai_analysis, ai_confidence, related_requirements: As before.
            error_message: This field should NOT be present in individual test case objects if generation is successful.
            Field Explanations for Error Object:
            error_message: (String) REQUIRED if no test cases can be generated. Brief English explanation.
            ai_confidence: (Integer) Always set to 0 when an error message is generated.
            Boundary Value Consideration:
            If USER INPUT implies testing limits, and multiple test cases are requested, try to make different test cases in the array cover different boundary aspects (min, max, below min, above max, typical).
            --- INSTRUCTIONS END ---
            --- RULES START ---
            Output MUST be a single, valid JSON structure (array of test cases OR a single error object).
            NO extra text.
            Base output SOLELY on USER INPUT and SYSTEM PARAMETERS.
            ALL string values MUST be in ENGLISH.
            Strictly adhere to the specified JSON structure(s).
            If generating test cases: The output is a JSON ARRAY. Each object in the array is a complete test case. The number of objects should match 'number_of_test_cases_requested'.
            If USER INPUT is unclear/insufficient: The output is a SINGLE JSON OBJECT with error_message and ai_confidence: 0.
            For each generated test case, id must be unique (e.g., "TC-001", "TC-002", ...).
            For each generated test case, status must always be "New".
            --- RULES END ---
            --- EXAMPLES START ---
            Example 1: Successful Login (1 test case requested)
            --- USER INPUT START ---
            Проверить вход пользователя с правильными учетными данными. Требование: AUTH-01.
            --- USER INPUT END ---
            --- SYSTEM PARAMETERS START ---
            number_of_test_cases_requested: 1
            --- SYSTEM PARAMETERS END ---
            --- EXPECTED OUTPUT START ---
            [
            {
            "id": "TC-001",
            "type": "Functional",
            "component": "Authentication",
            "priority": "High",
            "status": "New",
            "name": "Verify Successful User Login",
            "description": "Checks if a registered user can successfully log in using valid credentials.",
            "preconditions": ["User is on the login page.", "User possesses valid login credentials."],
            "test_steps": ["Enter a valid username into the username field.", "Enter the corresponding valid password into the password field.", "Click the 'Login' button."],
            "input_data": [{ "parameter": "Login", "value": "[Valid Username]" },{ "parameter": "Password", "value": "[Valid Password]" }],
            "expected_outcome": "User is successfully authenticated and redirected to their personal dashboard or the main application page.",
            "notes_ai_analysis": "Assumes standard login fields and a 'Login' button exist. Input lacks specific credentials.",
            "ai_confidence": 95,
            "related_requirements": ["AUTH-01"]
            }
            ]
            --- EXPECTED OUTPUT END ---
            Example 2: Boundary Value - Password Length (3 test cases requested)
            --- USER INPUT START ---
            Протестировать длину пароля. Минимальная длина 8, максимальная 16.
            --- USER INPUT END ---
            --- SYSTEM PARAMETERS START ---
            number_of_test_cases_requested: 3
            --- SYSTEM PARAMETERS END ---
            --- EXPECTED OUTPUT START ---
            [
            {
            "id": "TC-001",
            "type": "Boundary",
            "component": "Password Validation",
            "priority": "High",
            "status": "New",
            "name": "Test Password Length - Below Minimum",
            "description": "Verifies rejection of password shorter than minimum length (8 chars).",
            "preconditions": ["User is on a page with password input."],
            "test_steps": ["Enter a password with 7 characters.", "Submit the form/password."],
            "input_data": [{ "parameter": "PasswordAttempt", "value": "short P" }],
            "expected_outcome": "System rejects the password and displays an error message indicating it's too short.",
            "notes_ai_analysis": "Testing lower boundary (invalid). Assumed min length: 8.",
            "ai_confidence": 90,
            "related_requirements": []
            },
            {
            "id": "TC-002",
            "type": "Boundary",
            "component": "Password Validation",
            "priority": "High",
            "status": "New",
            "name": "Test Password Length - At Minimum",
            "description": "Verifies acceptance of password at minimum length (8 chars).",
            "preconditions": ["User is on a page with password input."],
            "test_steps": ["Enter a password with 8 characters.", "Submit the form/password."],
            "input_data": [{ "parameter": "PasswordAttempt", "value": "eightCha" }],
            "expected_outcome": "System accepts the password.",
            "notes_ai_analysis": "Testing lower boundary (valid). Assumed min length: 8.",
            "ai_confidence": 90,
            "related_requirements": []
            },
            {
            "id": "TC-003",
            "type": "Boundary",
            "component": "Password Validation",
            "priority": "High",
            "status": "New",
            "name": "Test Password Length - Above Maximum",
            "description": "Verifies rejection of password longer than maximum length (16 chars).",
            "preconditions": ["User is on a page with password input."],
            "test_steps": ["Enter a password with 17 characters.", "Submit the form/password."],
            "input_data": [{ "parameter": "PasswordAttempt", "value": "ThisPasswordIsWayTooLong" }],
            "expected_outcome": "System rejects the password and displays an error message indicating it's too long.",
            "notes_ai_analysis": "Testing upper boundary (invalid). Assumed max length: 16.",
            "ai_confidence": 90,
            "related_requirements": []
            }
            ]
            --- EXPECTED OUTPUT END ---
            Example 3: Unclear Input (2 test cases requested, but error is returned)
            --- USER INPUT START ---
            Сделай тест.
            --- USER INPUT END ---
            --- SYSTEM PARAMETERS START ---
            number_of_test_cases_requested: 2
            --- SYSTEM PARAMETERS END ---
            --- EXPECTED OUTPUT START ---
            {
            "error_message": "Input is too vague. Please specify what functionality or scenario needs to be tested.",
            "ai_confidence": 0
            }
            --- EXPECTED OUTPUT END ---
            --- EXAMPLES END ---
            """;

    @Value("${ai.api.endpoint}")
    private String aiApiEndpoint;

    @Value("${ai.api.key}")
    private String aiApiKey;

    public AIClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

//    public String generateTestCases(String prompt) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("Authorization", "Bearer " + aiApiKey);
//
//            Map<String, Object> requestMap = new HashMap<>();
//            requestMap.put("model", "deepseek-ai/DeepSeek-V3");
//
//            Map<String, String> message = new HashMap<>();
//            message.put("role", "user");
//            message.put("content", prompt);
//
//            requestMap.put("messages", List.of(message));
//
//            String requestBody = objectMapper.writeValueAsString(requestMap);
//            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    aiApiEndpoint, HttpMethod.POST, request, String.class);
//
//            if (!response.getStatusCode().is2xxSuccessful()) {
//                throw new RuntimeException("AI API error: " + response.getBody());
//            }
//
//            Map<String, Object> responseMap = objectMapper.readValue(
//                    response.getBody(), new TypeReference<Map<String, Object>>() {});
//
//            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
//
//            if (choices == null || choices.isEmpty()) {
//                throw new RuntimeException("AI returned no results");
//            }
//
//            Map<String, Object> firstChoice = choices.get(0);
//            Map<String, Object> messageContent = (Map<String, Object>) firstChoice.get("message");
//            return (String) messageContent.get("content");
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to call AI API", e);
//        }
//    }

    public String generateTestCases(String prompt, int number_of_test_cases_requested, String aiStrategy) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);

            // Загружаем шаблон из promt4.txt или храните его как строку в коде
            String template = rawPromt; // Реализуйте этот метод

            // Вставляем пользовательский ввод и количество тест-кейсов в шаблон
            String filledPrompt = template
                    .replace("{здесь_будет_текст_введенный_пользователем}", prompt)
                    .replace("{здесь_будет_запрошенное_количество_тестов}", String.valueOf(number_of_test_cases_requested))
                    .replace("{здесь_будет_ии_стратегия}", aiStrategy);

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("model", "deepseek-ai/DeepSeek-V3");

            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", filledPrompt); // Здесь передаём готовый prompt с подставленными значениями

            requestMap.put("messages", List.of(message));

            String requestBody = objectMapper.writeValueAsString(requestMap);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    aiApiEndpoint, HttpMethod.POST, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("AI API error: " + response.getBody());
            }

            Map<String, Object> responseMap = objectMapper.readValue(
                    response.getBody(), new TypeReference<Map<String, Object>>() {});

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("AI returned no results");
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> messageContent = (Map<String, Object>) firstChoice.get("message");
            return (String) messageContent.get("content");

        } catch (Exception e) {
            throw new RuntimeException("Failed to call AI API", e);
        }
    }
}